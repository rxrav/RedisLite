package com.github.rxrav.redislite.core.cmd.impl;

import com.github.rxrav.redislite.core.cmd.Command;
import com.github.rxrav.redislite.core.error.ValidationError;
import com.github.rxrav.redislite.core.error.WrongTypeError;
import com.github.rxrav.redislite.server.RedisLiteServer;

public class Decr extends Command {
    @Override
    protected void validate() throws ValidationError {
        if (!"DECR".equalsIgnoreCase(super.getCmd())) throw new ValidationError("Not correct use of 'decr' command!");
        if (super.getArgs().length != 1) throw new ValidationError("Need to pass exactly one key");
    }

    @Override
    protected Object execute() {
        int iVal = 0;
        if (RedisLiteServer.getMemoryMap().containsKey(super.getArgs()[0])) {
            try {
                iVal = Integer.parseInt(RedisLiteServer.getMemoryMap().get(super.getArgs()[0]).toString());
                RedisLiteServer.getMemoryMap().put(super.getArgs()[0], --iVal);
                return iVal;
            } catch (NumberFormatException e) {
                throw new WrongTypeError("Not a valid number type");
            }
        } else {
            // according to this command's documentation, the key is set to 0 first, then decreased by 1
            RedisLiteServer.getMemoryMap().put(super.getArgs()[0], iVal);
            RedisLiteServer.getMemoryMap().put(super.getArgs()[0], --iVal);
            return iVal;
        }
    }
}

