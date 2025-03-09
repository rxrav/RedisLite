package com.github.rxrav.redislite.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Memory {
    private Map<String, ValueWrapper> mainMemory;
    private Map<String, ExpiryMetaData> expiryDetails;

    public Memory() {
        this.mainMemory = new ConcurrentHashMap<>();
        this.expiryDetails = new ConcurrentHashMap<>();
    }

    public Map<String, ValueWrapper> getMainMemory() {
        return mainMemory;
    }

    public void setMainMemory(Map<String, ValueWrapper> mainMemory) {
        this.mainMemory = mainMemory;
    }

    public Map<String, ExpiryMetaData> getExpiryDetails() {
        return expiryDetails;
    }

    public void setExpiryDetails(Map<String, ExpiryMetaData> expiryDetails) {
        this.expiryDetails = expiryDetails;
    }
}
