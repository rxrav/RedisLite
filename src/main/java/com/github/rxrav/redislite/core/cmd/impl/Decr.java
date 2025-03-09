package com.github.rxrav.redislite.core.cmd.impl;

import com.github.rxrav.redislite.core.Memory;
import com.github.rxrav.redislite.core.ValueType;
import com.github.rxrav.redislite.core.ValueWrapper;
import com.github.rxrav.redislite.core.cmd.Command;
import com.github.rxrav.redislite.core.error.ValidationError;
import com.github.rxrav.redislite.core.error.WrongTypeError;

public class Decr extends Command {
    @Override
    protected void validate() throws ValidationError {
        if (!"DECR".equalsIgnoreCase(super.getCmd())) throw new ValidationError("Not correct use of 'decr' command!");
        if (super.getArgs().length != 1) throw new ValidationError("Need to pass exactly one key");
    }

    @Override
    protected ValueWrapper execute(Memory memoryRef) {
        int iVal = 0;
        String key = super.getArgs()[0];

        if (memoryRef.getMainMemory().containsKey(super.getArgs()[0])) {
            try {
                ValueWrapper valW = memoryRef.getMainMemory().get(key);
                if (valW.getValueType() == ValueType.NUMBER) {
                    iVal = (int) valW.getValue();
                    memoryRef.getMainMemory().put(key, new ValueWrapper(--iVal, ValueType.NUMBER));
                    return new ValueWrapper(iVal, ValueType.NUMBER);
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                throw new WrongTypeError("Not a valid number type");
            }
        } else {
            memoryRef.getMainMemory().put(key, new ValueWrapper(--iVal, ValueType.NUMBER));
            return new ValueWrapper(iVal, ValueType.NUMBER);
        }
    }
}

