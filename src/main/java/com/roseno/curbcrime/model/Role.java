package com.roseno.curbcrime.model;

public enum Role {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String id;

    Role(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }
}
