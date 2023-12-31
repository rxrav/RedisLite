package com.github.rxrav.redislite.core.cmd.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rxrav.redislite.core.ExpiryMetaData;
import com.github.rxrav.redislite.core.cmd.Command;
import com.github.rxrav.redislite.core.error.RedisLiteError;
import com.github.rxrav.redislite.core.error.ValidationError;
import com.github.rxrav.redislite.server.RedisLiteServer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Save extends Command {
    @Override
    protected void validate() throws ValidationError {
        if (!"SAVE".equalsIgnoreCase(super.getCmd())) throw new ValidationError("Not correct use of 'save' command!");
        if (super.getArgs().length != 0) throw new ValidationError("Doesn't need additional args, only run 'save'");
    }

    @Override
    protected Object execute() {
        String memData;
        String expMetaData;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            memData = objectMapper.writeValueAsString(RedisLiteServer.getMemoryMap());
            expMetaData = objectMapper.writeValueAsString(RedisLiteServer.getExpiryDetailsMap());
            BufferedWriter writer = new BufferedWriter(new FileWriter("redisLiteDb.rdb"));
            writer.write(STR."\{memData}__SEPARATOR__\{expMetaData}");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "OK";
    }
}

