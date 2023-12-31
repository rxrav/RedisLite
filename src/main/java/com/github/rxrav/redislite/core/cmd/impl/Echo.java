package com.github.rxrav.redislite.core.cmd.impl;

import com.github.rxrav.redislite.core.cmd.Command;
import com.github.rxrav.redislite.core.error.ValidationError;

public class Echo extends Command {
    @Override
    protected void validate() {
        if (!"ECHO".equalsIgnoreCase(super.getCmd())) throw new ValidationError("Not correct use of 'echo' command!");
        if (super.getArgs().length != 1) throw new ValidationError("Too many arguments, for multiple tokens wrap them in double quotes");
    }

    @Override
    protected Object execute() {
        return super.getArgs()[0];
    }
}
