package com.github.rxrav.redislite.core.error;

public class UnknownCommandError extends RuntimeException {
    public UnknownCommandError(String message) {
        super(STR."UNKCMDERR \{message}");
    }
}
