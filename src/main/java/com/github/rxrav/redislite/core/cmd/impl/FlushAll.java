package com.github.rxrav.redislite.core.cmd.impl;

import com.github.rxrav.redislite.core.cmd.Command;
import com.github.rxrav.redislite.core.error.RedisLiteError;
import com.github.rxrav.redislite.core.error.ValidationError;
import com.github.rxrav.redislite.server.RedisLiteServer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class FlushAll extends Command {
    @Override
    protected void validate() throws ValidationError {
        if (!"FLUSHALL".equalsIgnoreCase(super.getCmd())) throw new ValidationError("Not correct use of 'flushall' command!");
        if (super.getArgs().length != 0) throw new ValidationError("Doesn't need additional args, only run 'flushall'");
    }

    @Override
    protected Object execute() {
        try{
            RedisLiteServer.getMemoryMap().clear();
            RedisLiteServer.getExpiryDetailsMap().clear();
        } catch(Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getClass());
        }

        return "OK (Note, this doesn't clear any rdb files. After 'flushall', run 'save' to save current memory state";
    }
}
