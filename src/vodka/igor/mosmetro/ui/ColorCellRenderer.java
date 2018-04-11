package vodka.igor.mosmetro.ui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public abstract class ColorCellRenderer implements TableCellRenderer {
    abstract public Color getColorForCell(int row, int column);

    @Override
    public Component getTableCellRendererComponent(JTable tab, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = new JLabel();
        label.setText(value == null ? "" : value.toString());
        Color color = getColorForCell(row, column);
        label.setBackground(color);
        label.setOpaque(true);

        if (color.getRed() + color.getGreen() * 1.2 + color.getBlue() * 0.8 > 300)
            label.setForeground(Color.BLACK);
        else
            label.setForeground(Color.WHITE);

        return label;
    }
}
