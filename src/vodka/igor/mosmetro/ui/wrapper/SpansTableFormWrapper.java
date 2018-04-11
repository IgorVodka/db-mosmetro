package vodka.igor.mosmetro.ui.wrapper;

import javax.persistence.Query;

public class SpansTableFormWrapper extends StationLinkTableFormWrapper {
    @Override
    public String getName() {
        return "Перегоны";
    }

    @Override
    public Query getQuery() {
        return getSession().createQuery("select s from spans s");
    }

}
