package com.github.rxrav.redislite.core.error;

public class WrongTypeError extends RuntimeException {
    public WrongTypeError() { super ("WRONGTYPE Operation against a key holding the wrong kind of value"); }
    public WrongTypeError(String message) {  super(STR."WRONGTYPE \{message}"); }
}
