package com.example.titanic1.model.enums;

public enum PClass {
    FIRST(1),
    SECOND(2),
    THIRD(3);

    private final int value;

    PClass(int value) {
        this.value = value;
    }

    public static PClass fromInt(int value) {
        for (PClass pClass : values()) {
            if (pClass.value == value) {
                return pClass;
            }
        }
        throw new IllegalArgumentException("Invalid PClass value: " + value);
    }
}
