package com.github.rxrav.redislite.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Memory {

    private final Logger logger = LogManager.getLogger(Memory.class);

    private final long allowedMemory;

    private Map<String, ValueWrapper> mainMemory;
    private Map<String, ExpiryMetadata> expiryMetadataRef;

    public Memory() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        logger.info(STR."Runtime started with [\{maxMemory}] bytes of memory");

        allowedMemory = (long) (maxMemory * Constants.PERMITTED_MAIN_MEMORY_THRESHOLD);
        logger.info(STR."Server allowed to store data till [\{allowedMemory}] bytes of memory");

        this.mainMemory = new ConcurrentHashMap<>();
        this.expiryMetadataRef = new ConcurrentHashMap<>();
    }

    public void setMainMemory(Map<String, ValueWrapper> mainMemory) {
        this.mainMemory = mainMemory;
    }

    public void setExpiryMetadataRef(Map<String, ExpiryMetadata> expiryMetadataRef) {
        this.expiryMetadataRef = expiryMetadataRef;
    }

    public void putData(String key, ValueWrapper value) throws UnsupportedEncodingException {
        if (isMemoryLeft()) {
            this.mainMemory.put(key, value);
        } else {
            throw new RuntimeException("Memory full!");
        }
    }

    private boolean isMemoryLeft() {
        return this.allowedMemory > Runtime.getRuntime().freeMemory();
    }

    public void putExpiryData(String key, ExpiryMetadata expiryMetadata) {
        this.expiryMetadataRef.put(key, expiryMetadata);
    }

    public boolean has(String key) {
        return this.mainMemory.containsKey(key);
    }

    public ValueWrapper get(String key) {
        return this.mainMemory.get(key);
    }

    public ValueWrapper remove(String key) {
        this.expiryMetadataRef.remove(key);
        return this.mainMemory.remove(key);
    }

    public void fullFlush() {
        this.mainMemory.clear();
        this.expiryMetadataRef.clear();
    }

    public ExpiryMetadata getExpMd(String key) {
        return this.expiryMetadataRef.get(key);
    }

    public Map<String, ValueWrapper> getMainMemorySnapshot() {
        return this.mainMemory;
    }

    public Map<String, ExpiryMetadata> getExpiryMetadataRefSnapshot() {
        return this.expiryMetadataRef;
    }
}
