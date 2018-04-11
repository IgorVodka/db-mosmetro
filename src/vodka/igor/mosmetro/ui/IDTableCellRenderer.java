package vodka.igor.mosmetro.ui;

import vodka.igor.mosmetro.logic.CustomTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

class IDTableCellRenderer extends DefaultTableCellRenderer {
    private CustomTableModel tableModel;

    public IDTableCellRenderer(CustomTableModel tableModel) {
        this.tableModel = tableModel;
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
    ) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        String valueText = String.valueOf(value);

        if (tableModel.isMarkedAsDeleted(row)) {
            setBorder(BorderFactory.createLineBorder(Color.RED));
            setText("✘ " + valueText);
        } else {
            setBorder(BorderFactory.createEmptyBorder());
            setText(valueText);
        }
        return this;
    }
}