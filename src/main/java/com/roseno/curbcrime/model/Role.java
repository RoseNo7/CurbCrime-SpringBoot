package com.roseno.curbcrime.model;

public enum Role {
    ADMIN("ADMIN"),
    USER("USER");

    private final String id;

    Role(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }
}
