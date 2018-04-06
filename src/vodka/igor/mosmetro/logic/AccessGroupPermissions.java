package vodka.igor.mosmetro.logic;

import java.util.Set;

public class AccessGroupPermissions {
    Set<String> permissions;

    public AccessGroupPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public boolean can(String permission) {
        return permissions.contains(permission);
    }
}
