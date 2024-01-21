package com.github.rxrav.redislite.core.cmd.impl;

import com.github.rxrav.redislite.core.ExpiryMetaData;
import com.github.rxrav.redislite.core.Memory;
import com.github.rxrav.redislite.core.cmd.Command;
import com.github.rxrav.redislite.core.error.ValidationError;

import java.util.Date;

public class Get extends Command {
    @Override
    protected void validate() throws ValidationError {
        if (!"GET".equalsIgnoreCase(super.getCmd())) throw new ValidationError("Not correct use of 'get' command!");
        if (super.getArgs().length != 1) throw new ValidationError("Incorrect number of args");
    }

    /**
     * Has side effects on memory, if value is expired will remove them from memory
     * @return Value from the memory, or null
     */
    @Override
    protected Object execute(Memory memoryRef) {
        String key = super.getArgs()[0];
        Object val = memoryRef.getMainMemory().get(key);
        ExpiryMetaData expiryMetaData = memoryRef.getExpiryDetails().get(key);
        boolean itHasExpired = (expiryMetaData != null) && hasExpired(expiryMetaData);

        if (val == null) return null;
        else if (itHasExpired) {
            memoryRef.getMainMemory().remove(key);
            memoryRef.getExpiryDetails().remove(key);
            return null;
        }
        else if (val instanceof String) return val.toString();
        else if (val instanceof Integer) return Integer.parseInt(val.toString());
        else return null;
    }

    private static boolean hasExpired(ExpiryMetaData expiryMetaData) {
        return new Date().getTime() - expiryMetaData.getSetAt() > expiryMetaData.getValidFor();
    }
}