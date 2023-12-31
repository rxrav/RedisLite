package com.github.rxrav.redislite.core.error;

public class WrongTypeError extends RuntimeException {
    public WrongTypeError(String message) {  super(STR."WRONGTYPE \{message}"); }
}
