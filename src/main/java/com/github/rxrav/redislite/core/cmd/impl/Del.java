package com.github.rxrav.redislite.core.cmd.impl;

import com.github.rxrav.redislite.core.Memory;
import com.github.rxrav.redislite.core.ValueType;
import com.github.rxrav.redislite.core.ValueWrapper;
import com.github.rxrav.redislite.core.cmd.Command;
import com.github.rxrav.redislite.core.error.ValidationError;

public class Del extends Command {
    @Override
    protected void validate() throws ValidationError {
        if (!"DEL".equalsIgnoreCase(super.getCmd())) throw new ValidationError("Not correct use of 'del' command!");
        if (super.getArgs().length == 0) throw new ValidationError("Need to pass key(s)");
    }

    @Override
    protected ValueWrapper execute(Memory memoryRef) {
        int i = 0;
        for (String key: super.getArgs()) {
            ValueWrapper obj = memoryRef.getMainMemory().remove(key);
            if (obj != null) ++i;
        }
        return new ValueWrapper(i, ValueType.NUMBER);
    }
}

