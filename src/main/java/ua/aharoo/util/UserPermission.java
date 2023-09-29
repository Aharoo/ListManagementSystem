package ua.aharoo.util;

public enum UserPermission {
    READ("read"),
    WRITE("write"),
    DELETE("delete");

    private final String permission;

    UserPermission(String permission){this.permission = permission;}

    public String getPermission() {
        return permission;
    }
}
