package vodka.igor.mosmetro.ui.item;

import org.hibernate.Session;
import vodka.igor.mosmetro.models.Line;

import javax.swing.*;
import java.util.List;

public class IDItem implements Comparable<IDItem> {
    private Integer id;
    private boolean forDelete;

    public IDItem(Integer id) {
        this.id = id;
        this.forDelete = false;
    }

    public Integer getId() {
        return id;
    }

    public void markDeleted(boolean forDelete) {
        this.forDelete = forDelete;
    }

    public boolean isDeleted() {
        return forDelete;
    }

    @Override
    public String toString() {
        return id == null ? "+" : id.toString();
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof IDItem) {
            return this.getId().equals(((IDItem) that).getId());
        } else {
            return this == that;
        }
    }

    @Override
    public int compareTo(IDItem that) {
        return this.getId().compareTo(that.getId());
    }
}
