package com.github.rxrav.redislite.core;

public class ValueWrapper {
    private Object value;
    private ValueType valueType;

    public ValueWrapper() {}

    public ValueWrapper(Object value, ValueType type) {
        this.value = value;
        this.valueType = type;
    }

    public ValueType getValueType() {
        return this.valueType;
    }

    public Object getValue() {
        return this.value;
    }
}
