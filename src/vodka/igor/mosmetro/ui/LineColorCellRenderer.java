package vodka.igor.mosmetro.ui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public abstract class LineColorCellRenderer implements TableCellRenderer {
    abstract public Color getColorForCell(int row, int column);

    @Override
    public Component getTableCellRendererComponent(JTable tab, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = new JLabel();
        label.setText(value.toString());
        Color color = getColorForCell(row, column);
        label.setBackground(color);
        label.setOpaque(true);

        if(color.getRed() + color.getGreen() + color.getBlue() > 500)
            label.setForeground(Color.BLACK);
        else
            label.setForeground(Color.WHITE);

        return label;
    }

    public static Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16));
    }
}
