package com.github.rxrav.redislite.core;

public class ExpiryMetaData {
    private long setAt;
    private long validFor;

    public ExpiryMetaData() {}

    public ExpiryMetaData(long setAt, long validFor) {
        this.setAt = setAt;
        this.validFor = validFor;
    }

    public long getSetAt() {
        return setAt;
    }

    public long getValidFor() {
        return validFor;
    }
}
