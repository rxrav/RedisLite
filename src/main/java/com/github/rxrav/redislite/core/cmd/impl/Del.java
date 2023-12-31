package com.github.rxrav.redislite.core.cmd.impl;

import com.github.rxrav.redislite.core.cmd.Command;
import com.github.rxrav.redislite.core.error.ValidationError;
import com.github.rxrav.redislite.server.RedisLiteServer;

public class Del extends Command {
    @Override
    protected void validate() throws ValidationError {
        if (!"DEL".equalsIgnoreCase(super.getCmd())) throw new ValidationError("Not correct use of 'del' command!");
        if (super.getArgs().length == 0) throw new ValidationError("Need to pass key(s)");
    }

    @Override
    protected Object execute() {
        int i = 0;
        for (String key: super.getArgs()) {
            Object obj = RedisLiteServer.getMemoryMap().remove(key);
            if (obj != null) ++i;
        }
        return i;
    }
}

