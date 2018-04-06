package vodka.igor.mosmetro.ui;

import org.hibernate.Session;
import vodka.igor.mosmetro.listener.DatabaseRowLoadListener;
import vodka.igor.mosmetro.listener.DatabaseRowSaveListener;
import vodka.igor.mosmetro.logic.CustomTableModel;
import vodka.igor.mosmetro.logic.MetroManager;

import javax.persistence.Query;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableDatabaseBinding<T> {
    protected Session session;
    protected Query query;
    protected JTable tableControl;
    protected CustomTableModel tableModel;
    protected List<T> entitiesBound; // TODO: move to model
    private DatabaseRowLoadListener<T> rowLoadListener;
    private DatabaseRowSaveListener<T> rowSaveListener;

    public TableDatabaseBinding(Query query, JTable tableControl) {
        this.session = MetroManager.getInstance().getSession();
        this.query = query;
        this.tableControl = tableControl;

        this.tableModel = new CustomTableModel();
        tableControl.setModel(tableModel);

        this.entitiesBound = new ArrayList<>();
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
        return entitiesBound.get(row);
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public int getRowByEntity(T entity) {
        return entitiesBound.indexOf(entity);
    }

    public void setLoadListener(DatabaseRowLoadListener<T> listener) {
        this.rowLoadListener = listener;
    }

    public void setSaveListener(DatabaseRowSaveListener<T> listener) {
        this.rowSaveListener = listener;
    }

    private void clearModel() {
        tableModel.getDataVector().removeAllElements();
        tableControl.clearSelection();
    }

    public T getSelectedEntity() {
        return getEntityByRow(tableControl.getSelectedRow());
    }

    public void loadAll() {
        clearModel();

        List result = query.getResultList();
        entitiesBound.clear();
        for (Object o : result) {
            entitiesBound.add((T) o);
            tableModel.addRow(rowLoadListener.rowDisplayed((T) o));
        }

        resizeColumnWidth(tableControl);

        tableControl.getColumn(tableControl.getColumnName(0)).setCellRenderer(new CustomTableCellRenderer());
    }

    public void saveAll() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            session.beginTransaction();
            if (tableModel.isMarkedAsDeleted(i)) {
                try {
                    session.delete(getEntityByRow(i));
                    unmarkRowAsDeleted(i);
                    session.getTransaction().commit();
                } catch (Exception exc) {
                    session.getTransaction().rollback();
                    exc.printStackTrace();
                    UIUtils.error(exc.getMessage(), exc.getClass().getName());
                }
                continue;
            }

            Map<String, Object> data = new HashMap<>();
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                data.put(tableModel.getColumnName(j), tableModel.getValueAt(i, j));
            }
            rowSaveListener.rowSaved(entitiesBound.get(i), data);
            session.saveOrUpdate(entitiesBound.get(i));
            session.getTransaction().commit();
        }
        this.loadAll();
    }

    public JTable getTableControl() {
        return tableControl;
    }

    class CustomTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
        ) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (tableModel.isMarkedAsDeleted(row)) {
                setBorder(BorderFactory.createLineBorder(Color.RED));
                setText("✘ " + value.toString());
            } else {
                setBorder(BorderFactory.createEmptyBorder());
                setText(value.toString());
            }
            return this;
        }
    }
}
