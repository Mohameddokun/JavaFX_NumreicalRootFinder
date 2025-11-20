package com.numerical.rootfinder.model;

public enum RootFindingMethod {
    BISECTION("Bisection Method"),
    FALSE_POSITION("False Position Method"),
    FIXED_POINT("Fixed Point Iteration"),
    NEWTON_RAPHSON("Newton-Raphson Method"),
    SECANT("Secant Method"),
    ALL("All Methods");

    private final String displayName;

    RootFindingMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}