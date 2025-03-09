package com.github.rxrav.redislite.core;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Memory {

    enum Op {
        INC,
        DEC,
    }

    private long totalSizeInBytes;
    private Map<String, ValueWrapper> mainMemory;
    private Map<String, ExpiryMetadata> expiryMetadataRef;

    public Memory() {
        this.totalSizeInBytes = 0;
        this.mainMemory = new ConcurrentHashMap<>();
        this.expiryMetadataRef = new ConcurrentHashMap<>();
    }

    private synchronized void updateSize(int size, Op op) {
        if (op == Op.INC) {
            this.totalSizeInBytes += size;
        } else if (op == Op.DEC) {
            this.totalSizeInBytes -= size;
            if (this.totalSizeInBytes < 0) {
                this.totalSizeInBytes = 0;
            }
        }
    }

    public void setMainMemory(Map<String, ValueWrapper> mainMemory) {
        this.mainMemory = mainMemory;
    }

    public void setExpiryMetadataRef(Map<String, ExpiryMetadata> expiryMetadataRef) {
        this.expiryMetadataRef = expiryMetadataRef;
    }

    public void putData(String key, ValueWrapper value) throws UnsupportedEncodingException {
        int sizeInBytes = getSizeInBytes(value);

        if (isMemoryLeft(sizeInBytes)) {
            this.mainMemory.put(key, value);
            this.updateSize(sizeInBytes, Op.INC);
        } else {
            throw new RuntimeException("Memory full!");
        }
    }

    private static int getSizeInBytes(ValueWrapper value) {
        int sizeInBytes = 0;
        if (value.getValueType() == ValueType.NUMBER) {
            sizeInBytes = 8;
        } else if (value.getValueType() == ValueType.STRING) {
            sizeInBytes = value.getValue().toString().getBytes(StandardCharsets.UTF_8).length;
        } else if (value.getValueType() == ValueType.LIST) {
            var list = (ArrayList<?>) value.getValue();
            for (Object o: list) {
                sizeInBytes += o.toString().getBytes(StandardCharsets.UTF_8).length;
            }
        }
        return sizeInBytes;
    }

    private boolean isMemoryLeft(int sizeInBytes) {
        return Constants.PERMITTED_MAIN_MEMORY_SIZE_IN_BYTES - this.totalSizeInBytes >= sizeInBytes;
    }

    public void putExpiryData(String key, ExpiryMetadata expiryMd) {
        this.expiryMetadataRef.put(key, expiryMd);
    }

    public boolean has(String key) {
        return this.mainMemory.containsKey(key);
    }

    public ValueWrapper get(String key) {
        return this.mainMemory.get(key);
    }

    public ValueWrapper remove(String key) {
        ValueWrapper value = this.mainMemory.get(key);
        int sizeInBytes = getSizeInBytes(value);

        this.expiryMetadataRef.remove(key);
        ValueWrapper removedValue = this.mainMemory.remove(key);
        this.expiryMetadataRef.remove(key);
        this.updateSize(sizeInBytes, Op.DEC);
        return removedValue;
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
