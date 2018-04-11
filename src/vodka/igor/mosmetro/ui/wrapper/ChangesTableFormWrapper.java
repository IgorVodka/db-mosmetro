package vodka.igor.mosmetro.ui.wrapper;

import javax.persistence.Query;

public class ChangesTableFormWrapper extends StationLinkTableFormWrapper {
    @Override
    public String getName() {
        return "Пересадки";
    }

    @Override
    public Query getQuery() {
        return getSession().createQuery("select c from changes c");
    }

}
