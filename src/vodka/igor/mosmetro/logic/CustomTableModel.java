package vodka.igor.mosmetro.logic;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomTableModel extends DefaultTableModel {
    private Set<Integer> markedAsDeletedRows;

    public CustomTableModel() {
        this.markedAsDeletedRows = new HashSet<>();
    }

    // TODO: move entities bound here

    public boolean isMarkedAsDeleted(int row) {
        return markedAsDeletedRows.contains(row);
    }

    public void markAsDeleted(int row) {
        markedAsDeletedRows.add(row);
    }

    public void unmarkAsDeleted(int row) {
        markedAsDeletedRows.remove(row);
    }
}
