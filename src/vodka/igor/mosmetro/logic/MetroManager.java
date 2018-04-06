package vodka.igor.mosmetro.logic;

import org.hibernate.Session;

public class MetroManager {
    private static MetroManager ourInstance = new MetroManager();

    public static MetroManager getInstance() {
        return ourInstance;
    }

    private MetroManager() {
    }

    private AccessGroup accessGroup;
    private Session session;

    public AccessGroup getAccessGroup() {
        return accessGroup;
    }

    public void setAccessGroup(AccessGroup accessGroup) {
        this.accessGroup = accessGroup;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
