package vodka.igor.mosmetro.ui;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import vodka.igor.mosmetro.listener.DatabaseRowLoadListener;
import vodka.igor.mosmetro.listener.DatabaseRowSaveListener;
import vodka.igor.mosmetro.logic.CustomTableModel;
import vodka.igor.mosmetro.logic.MetroManager;
import vodka.igor.mosmetro.main.GenericTableForm;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

public class TableDatabaseBinding<T> {
    protected Class entityClass;
    protected Session session;
    protected Query query;
    protected JTable tableControl;
    protected CustomTableModel tableModel;
    protected List<T> entitiesBound;
    protected Map<String, Object> fixedCols;
    protected GenericTableForm form;
    private DatabaseRowLoadListener<T> rowLoadListener;
    private DatabaseRowSaveListener<T> rowSaveListener;

    public TableDatabaseBinding(Class<?> entityClass, Query query, JTable tableControl, GenericTableForm form) {
        this.entityClass = entityClass;
        this.session = MetroManager.getInstance().getSession();
        this.query = query;
        this.tableControl = tableControl;
        this.form = form;

        this.tableModel = new CustomTableModel(this);
        tableControl.setModel(tableModel);
        tableControl.setAutoCreateRowSorter(true);

        this.entitiesBound = new ArrayList<>();
        this.fixedCols = new HashMap<>();
    }

    private void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 10;
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 1, width);
            }
            if (width > 300)
                width = 300;
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    public void setHeaders(String[] headers) {
        tableModel.setColumnIdentifiers(headers);
    }

    public void markRowAsDeleted(int row) {
        tableModel.markAsDeleted(row);
        tableControl.updateUI();
    }

    public void unmarkRowAsDeleted(int row) {
        tableModel.unmarkAsDeleted(row);
        tableControl.updateUI();
    }

    public T getEntityByRow(int row) {
        return (entitiesBound.size() > row && row >= 0) ?
                entitiesBound.get(tableControl.convertRowIndexToModel(row)) : null;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public int getRowByEntity(T entity) {
        return tableControl.convertRowIndexToView(entitiesBound.indexOf(entity));
    }

    public void setLoadListener(DatabaseRowLoadListener<T> listener) {
        this.rowLoadListener = listener;
    }

    public void setSaveListener(DatabaseRowSaveListener<T> listener) {
        this.rowSaveListener = listener;
    }

    public void updateFilter(String filter) {
        TableRowSorter sorter = (TableRowSorter) tableControl.getRowSorter();

        sorter.setStringConverter(new TableStringConverter() {
            @Override
            public String toString(TableModel tableModel, int row, int col) {
                return tableModel.getValueAt(row, col).toString().toLowerCase();
            }
        });
        sorter.setRowFilter(RowFilter.regexFilter(filter.toLowerCase()));
    }

    private void clearModel() {
        tableModel.getDataVector().removeAllElements();
        tableControl.clearSelection();
    }

    public T getSelectedEntity() {
        return getEntityByRow(tableControl.getSelectedRow());
    }

    public void addFixedColumn(String columnName, Object value) {
        fixedCols.put(columnName, value);
    }

    public boolean isColumnFixed(String columnName) {
        return fixedCols.containsKey(columnName);
    }

    public Object getColumnFixedValue(String columnName) {
        assert isColumnFixed(columnName);
        return fixedCols.get(columnName);
    }

    public void addEmptyEntity(int selectedRow) {
        T entity = null;
        try {
            entity = (T) entityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            UIUtils.error("Ошибка добавления сущности.");
            e.printStackTrace();
        }
        tableModel.addRow(rowLoadListener.rowDisplayed(entity));
        entitiesBound.add(entity);
        for (String columnName : fixedCols.keySet()) {
            tableModel.setValueAt(
                    getColumnFixedValue(columnName),
                    getModel().getRowCount() - 1,
                    getModel().findColumn(columnName)
            );
        }
    }

    public void loadAll() {
        try {
            clearModel();

            List result = query.getResultList();
            entitiesBound.clear();
            for (Object o : result) {
                entitiesBound.add((T) o);
                tableModel.addRow(rowLoadListener.rowDisplayed((T) o));
            }

            resizeColumnWidth(tableControl);

            tableControl.getColumn(tableControl.getColumnName(0)).setCellRenderer(
                    new IDTableCellRenderer(tableModel)
            );
        } catch (IllegalStateException exc) {
            UIUtils.error("Невозможно получить данные для данной сущности.");
            form.closeForm();
        } catch (Exception exc) {
            UIUtils.error("Ошибка получения данных!", exc.getClass().getSimpleName());
        }
    }

    public void saveAll() {
        try {
            session.beginTransaction();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.isMarkedAsDeleted(i)) {
                    try {
                        session.delete(getEntityByRow(i));
                        unmarkRowAsDeleted(i);
                        session.getTransaction().commit();
                    } catch (EntityNotFoundException | ConstraintViolationException exc) {
                        UIUtils.error("Невозможно удалить сущность, т.к. существуют связанные сущности.");
                    } catch (Exception exc) {
                        UIUtils.error("Ошибка удаления!");
                    } finally {
                        if (session.getTransaction().isActive()) {
                            session.getTransaction().rollback();
                        }
                    }
                    continue;
                }

                Map<String, Object> data = new HashMap<>();
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    data.put(tableModel.getColumnName(j), tableModel.getValueAt(i, j));
                }
                entitiesBound.set(i, rowSaveListener.rowSaved(entitiesBound.get(i), data));
                session.saveOrUpdate(entitiesBound.get(i));
            }
            if (session.getTransaction().isActive()) {
                session.getTransaction().commit();
            }
            this.loadAll();
        } catch (Exception exc) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
                exc.printStackTrace();
                form.reloadEverything(this);
                UIUtils.error("Ошибка обновления!", exc.getClass().getSimpleName());
            }
        }
    }

    public JTable getTableControl() {
        return tableControl;
    }

    public CustomTableModel getModel() {
        return (CustomTableModel) tableControl.getModel();
    }
}
