package com.github.rxrav.redislite.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Memory {
    private Map<String, Object> mainMemory;
    private Map<String, ExpiryMetaData> expiryDetails;

    public Memory() {
        this.mainMemory = new ConcurrentHashMap<>();
        this.expiryDetails = new ConcurrentHashMap<>();
    }

    public Map<String, Object> getMainMemory() {
        return mainMemory;
    }

    public void setMainMemory(Map<String, Object> mainMemory) {
        this.mainMemory = mainMemory;
    }

    public Map<String, ExpiryMetaData> getExpiryDetails() {
        return expiryDetails;
    }

    public void setExpiryDetails(Map<String, ExpiryMetaData> expiryDetails) {
        this.expiryDetails = expiryDetails;
    }
}
