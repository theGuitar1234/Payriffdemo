package org.payriff.springboot.utilities.constants;

public enum roles {
    ADMIN(1L, "ADMIN"),
    USER(2L, "USER"),
    TEACHER(3L, "TEACHER"),
    STUDENT(4L, "STUDENT");

    private Long roleId;
    private String roleNameString;

    private roles(Long roleId, String roleNameString) {
        this.roleId = roleId;
        this.roleNameString = roleNameString;
    }

    public Long getRoleId() {
        return this.roleId;
    }

    public String getRoleNameString() {
        return this.roleNameString;
    }
}
