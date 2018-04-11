package vodka.igor.mosmetro.ui.item;

import org.hibernate.Session;
import vodka.igor.mosmetro.models.Line;

import javax.swing.*;
import java.util.List;

public class LineItem implements Comparable<LineItem> {
    private Line line;

    public LineItem(Line line) {
        this.line = line;
    }

    public Line getLine() {
        return line;
    }

    @Override
    public String toString() {
        return line == null ? "" : line.getTag() + ": " + line.getLineName();
    }

    @Override
    public boolean equals(Object that) {
        if (this.getLine() == null)
            return ((LineItem) that).getLine() == null;

        if (that instanceof LineItem) {
            return this.getLine().equals(((LineItem) that).getLine());
        } else {
            return this == that;
        }
    }

    public static JComboBox createLinesComboBox(Session session) {
        JComboBox comboBox = new JComboBox();
        List lines = session.createQuery("select l from lines l").getResultList();
        for (Line line : (List<Line>) lines) {
            LineItem item = new LineItem(line);
            comboBox.addItem(item);
        }
        return comboBox;
    }

    @Override
    public int compareTo(LineItem that) {
        if (this.getLine() == null)
            return 1;
        return this.getLine().getLineName().compareTo(that.getLine().getLineName());
    }
}