package org.copper.users.common;

public enum StatusCode {
    ACTIVE("1"),
    INACTIVE("2");

    private final String code;
    StatusCode(String code) {
        this.code = code;
    }
}
