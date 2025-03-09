package com.github.rxrav.redislite.core.cmd.impl;

import com.github.rxrav.redislite.core.Memory;
import com.github.rxrav.redislite.core.ValueType;
import com.github.rxrav.redislite.core.ValueWrapper;
import com.github.rxrav.redislite.core.cmd.Command;
import com.github.rxrav.redislite.core.error.ValidationError;

public class Exists extends Command {
    @Override
    protected void validate() throws ValidationError {
        if (!"EXISTS".equalsIgnoreCase(super.getCmd())) throw new ValidationError("Not correct use of 'exists' command!");
        if (super.getArgs().length == 0) throw new ValidationError("Need to pass key(s)");
    }

    @Override
    protected ValueWrapper execute(Memory memoryRef) {
        int i = 0;
        for (String key: super.getArgs()) {
            if (memoryRef.has(key)) ++i;
        }
        return new ValueWrapper(i, ValueType.NUMBER);
    }
}
