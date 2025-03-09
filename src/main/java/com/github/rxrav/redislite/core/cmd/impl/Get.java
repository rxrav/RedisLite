package com.github.rxrav.redislite.core.cmd.impl;

import com.github.rxrav.redislite.core.ExpiryMetadata;
import com.github.rxrav.redislite.core.Memory;
import com.github.rxrav.redislite.core.ValueType;
import com.github.rxrav.redislite.core.ValueWrapper;
import com.github.rxrav.redislite.core.cmd.Command;
import com.github.rxrav.redislite.core.error.ValidationError;
import com.github.rxrav.redislite.core.error.WrongTypeError;

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
    protected ValueWrapper execute(Memory memoryRef) {
        String key = super.getArgs()[0];
        ValueWrapper obj = memoryRef.get(key);
        ExpiryMetadata expiryMetadata = memoryRef.getExpMd(key);
        boolean itHasExpired = (expiryMetadata != null) && hasExpired(expiryMetadata);

        if (obj == null) return null;
        else if (itHasExpired) {
            memoryRef.remove(key);
            return null;
        }
        else if (obj.getValueType() == ValueType.STRING) return new ValueWrapper(obj.getValue().toString(), ValueType.STRING);
        else if (obj.getValueType() == ValueType.NUMBER) return new ValueWrapper(Integer.valueOf(obj.getValue().toString()), ValueType.NUMBER);
        else throw new WrongTypeError();
    }

    private static boolean hasExpired(ExpiryMetadata expiryMetadata) {
        return new Date().getTime() - expiryMetadata.getSetAt() > expiryMetadata.getValidFor();
    }
}