package vodka.igor.mosmetro.ui;

import org.hibernate.Session;
import vodka.igor.mosmetro.listener.DatabaseRowLoadListener;
import vodka.igor.mosmetro.listener.DatabaseRowSaveListener;
import vodka.igor.mosmetro.logic.MetroManager;
import vodka.igor.mosmetro.main.GenericTableForm;

import javax.persistence.Query;
import javax.swing.*;

public abstract class TableFormWrapper<T> {
    GenericTableForm form;

    public Session getSession() {
        return MetroManager.getInstance().getSession();
    }

    public void setForm(GenericTableForm<T> form) {
        this.form = form;
    }

    protected GenericTableForm getForm() {
        return form;
    }

    abstract public String getName();
    abstract public Query getQuery();
    abstract public String[] getHeaders();
    abstract public DatabaseRowSaveListener<T> getSaveListener();
    abstract public DatabaseRowLoadListener<T> getLoadListener();

    abstract public void customize(TableDatabaseBinding<T> binding);
}
