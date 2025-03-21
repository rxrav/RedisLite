package com.github.rxrav.redislite.core.cmd.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rxrav.redislite.core.Memory;
import com.github.rxrav.redislite.core.ValueType;
import com.github.rxrav.redislite.core.ValueWrapper;
import com.github.rxrav.redislite.core.cmd.Command;
import com.github.rxrav.redislite.core.error.ValidationError;
import com.github.rxrav.redislite.core.Constants;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Save extends Command {

    @Override
    protected void validate() throws ValidationError {
        if (!"SAVE".equalsIgnoreCase(super.getCmd())) throw new ValidationError("Not correct use of 'save' command!");
        if (super.getArgs().length != 0) throw new ValidationError("Doesn't need additional args, only run 'save'");
    }

    @Override
    protected ValueWrapper execute(Memory memoryRef) {
        String memData;
        String expMetaData;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            memData = objectMapper.writeValueAsString(memoryRef.getMainMemorySnapshot());
            expMetaData = objectMapper.writeValueAsString(memoryRef.getExpiryMetadataRefSnapshot());
            String dataToSave = STR."\{memData}\{Constants.SEPARATOR}\{expMetaData}";
            try (FileOutputStream fos = new FileOutputStream(Constants.DAT_FILE_NAME_AT_CURRENT_PATH)) {
                fos.write(dataToSave.getBytes(StandardCharsets.UTF_8));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ValueWrapper("OK", ValueType.STRING);
    }
}

