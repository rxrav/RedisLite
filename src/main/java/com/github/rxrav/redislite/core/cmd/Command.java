package com.github.rxrav.redislite.core.cmd;

import com.github.rxrav.redislite.core.Memory;
import com.github.rxrav.redislite.core.ValueWrapper;
import com.github.rxrav.redislite.core.error.ValidationError;

import java.io.UnsupportedEncodingException;

public abstract class Command {
    private String cmd;
    private String[] args;

    protected Command() {}

    protected abstract void validate() throws ValidationError;
    protected abstract ValueWrapper execute(Memory memoryRef) throws UnsupportedEncodingException;

    protected final ValueWrapper handle(Memory memoryRef) throws ValidationError, UnsupportedEncodingException {
        this.validate();
        return this.execute(memoryRef);
    }

    public String getCmd() {
        return this.cmd;
    }

    public String[] getArgs() {
        return this.args;
    }

    public final Command builder(Object[] deserializedArray) {
        if (deserializedArray != null) {
            this.cmd = String.valueOf(deserializedArray[0]);
            this.args = new String[deserializedArray.length - 1];
            for (int i = 1; i < deserializedArray.length; i++) {
                this.args[i - 1] = String.valueOf(deserializedArray[i]);
            }
        }
        return this;
    }
}
