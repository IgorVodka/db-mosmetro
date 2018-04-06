package vodka.igor.mosmetro.listener;

import javax.persistence.Entity;

public interface DatabaseRowLoadListener<T> {
    Object[] rowDisplayed(T entity);
}
