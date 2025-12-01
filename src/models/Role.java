package com.cmpe343.project2.model;

public enum Role {
    TESTER,
    JUNIOR_DEVELOPER,
    SENIOR_DEVELOPER,
    MANAGER;

    public static Role fromString(String value) {
        if (value == null) return null;
        switch (value.toUpperCase()) {
            case "TESTER":
                return TESTER;
            case "JUNIOR DEVELOPER":
            case "JUNIOR_DEVELOPER":
            case "JUNIOR":
                return JUNIOR_DEVELOPER;
            case "SENIOR DEVELOPER":
            case "SENIOR_DEVELOPER":
            case "SENIOR":
                return SENIOR_DEVELOPER;
            case "MANAGER":
                return MANAGER;
            default:
                throw new IllegalArgumentException("Unknown role: " + value);
        }
    }
}
