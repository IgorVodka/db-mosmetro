package vodka.igor.mosmetro.logic;

import vodka.igor.mosmetro.ui.TableDatabaseBinding;
import vodka.igor.mosmetro.ui.item.IDItem;

import javax.swing.table.DefaultTableModel;
import java.util.*;

public class CustomTableModel extends DefaultTableModel {
    private TableDatabaseBinding binding;
    private HashMap<String, Class> columnClasses;
    private boolean editEnabled = true;

    public CustomTableModel(TableDatabaseBinding binding) {
        this.binding = binding;
        this.columnClasses = new HashMap<>();
        this.editEnabled = true;
    }

    // TODO: move entities bound here

    @Override
    public Class getColumnClass(int col) {
        String columnName = getColumnName(col);

        if (columnClasses.containsKey(columnName)) {
            return columnClasses.get(columnName);
        }

        if (findColumn("ID") == col) {
            return IDItem.class;
        }

        return String.class;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        String columnName = getColumnName(col);
        return editEnabled && !(col == 0 || binding.isColumnFixed(columnName));
    }

    public boolean isMarkedAsDeleted(int row) {
        int goodIndex = binding.getTableControl().convertRowIndexToModel(row);
        return ((IDItem) getValueAt(goodIndex, 0)).isDeleted();
    }

    public void markAsDeleted(int row) {
        int goodIndex = binding.getTableControl().convertRowIndexToModel(row);
        IDItem value = (IDItem) getValueAt(goodIndex, 0);
        value.markDeleted(true);
        setValueAt(value, goodIndex, 0);
    }

    public void unmarkAsDeleted(int row) {
        int goodIndex = binding.getTableControl().convertRowIndexToModel(row);
        IDItem value = (IDItem) getValueAt(goodIndex, 0);
        value.markDeleted(false);
        setValueAt(value, goodIndex, 0);
    }

    public void disableEdit() {
        editEnabled = false;
    }

    public void overrideColumnClass(String columnName, Class c) {
        columnClasses.put(columnName, c);
    }
}
