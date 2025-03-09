package com.github.rxrav.redislite.core.cmd.impl;

import com.github.rxrav.redislite.core.Memory;
import com.github.rxrav.redislite.core.ValueType;
import com.github.rxrav.redislite.core.ValueWrapper;
import com.github.rxrav.redislite.core.cmd.Command;
import com.github.rxrav.redislite.core.error.ValidationError;

public class Ping extends Command {
    @Override
    protected void validate() {
        if (!"PING".equalsIgnoreCase(super.getCmd())) throw new ValidationError("Not correct use of 'ping' command!");
    }
    @Override
    protected ValueWrapper execute(Memory memoryRef) {
        return new ValueWrapper("PONG", ValueType.STRING);
    }
}
