package vodka.igor.mosmetro.listener;

import java.util.Map;

public interface DatabaseRowSaveListener<T> {
    void rowSaved(T entityBound, Map<String, Object> row);
}
