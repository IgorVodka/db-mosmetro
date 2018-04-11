package vodka.igor.mosmetro.listener;

import java.util.Map;

public interface DatabaseRowSaveListener<T> {
    T rowSaved(T entityBound, Map<String, Object> row);
}
