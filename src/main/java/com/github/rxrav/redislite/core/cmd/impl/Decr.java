package com.github.rxrav.redislite.core.cmd.impl;

import com.github.rxrav.redislite.core.Memory;
import com.github.rxrav.redislite.core.ValueType;
import com.github.rxrav.redislite.core.ValueWrapper;
import com.github.rxrav.redislite.core.cmd.Command;
import com.github.rxrav.redislite.core.error.ValidationError;
import com.github.rxrav.redislite.core.error.WrongTypeError;

import java.io.UnsupportedEncodingException;

public class Decr extends Command {
    @Override
    protected void validate() throws ValidationError {
        if (!"DECR".equalsIgnoreCase(super.getCmd())) throw new ValidationError("Not correct use of 'decr' command!");
        if (super.getArgs().length != 1) throw new ValidationError("Need to pass exactly one key");
    }

    @Override
    protected ValueWrapper execute(Memory memoryRef) throws UnsupportedEncodingException {
        int iVal = 0;
        String key = super.getArgs()[0];

        if (memoryRef.has(super.getArgs()[0])) {
            try {
                ValueWrapper valW = memoryRef.get(key);
                if (valW.getValueType() == ValueType.NUMBER) {
                    iVal = (int) valW.getValue();
                    memoryRef.putData(key, new ValueWrapper(--iVal, ValueType.NUMBER));
                    return new ValueWrapper(iVal, ValueType.NUMBER);
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException | UnsupportedEncodingException e) {
                throw new WrongTypeError("Not a valid number type");
            }
        } else {
            memoryRef.putData(key, new ValueWrapper(--iVal, ValueType.NUMBER));
            return new ValueWrapper(iVal, ValueType.NUMBER);
        }
    }
}

