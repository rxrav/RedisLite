package com.github.rxrav.redislite.core.error;

public class ValidationError extends RuntimeException {
    public ValidationError(String message) {
        super(STR."VALERR \{message}");
    }
}
