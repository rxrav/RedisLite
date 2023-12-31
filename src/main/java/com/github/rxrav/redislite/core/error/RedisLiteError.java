package com.github.rxrav.redislite.core.error;

public class RedisLiteError extends RuntimeException {
    public RedisLiteError(String message) {  super(STR."ERR \{message}"); }
}
