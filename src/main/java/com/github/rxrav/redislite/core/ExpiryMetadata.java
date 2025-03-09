package com.github.rxrav.redislite.core;

public class ExpiryMetadata {
    private long setAt;
    private long validFor;

    public ExpiryMetadata() {}

    public ExpiryMetadata(long setAt, long validFor) {
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
