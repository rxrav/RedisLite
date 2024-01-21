package com.github.rxrav.redislite.core.cmd.impl;

import com.github.rxrav.redislite.core.Memory;
import com.github.rxrav.redislite.core.cmd.Command;
import com.github.rxrav.redislite.core.error.ValidationError;
import com.github.rxrav.redislite.core.error.WrongTypeError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lpush extends Command {
    @Override
    protected void validate() throws ValidationError {
        if (!"LPUSH".equalsIgnoreCase(super.getCmd())) throw new ValidationError("Not correct use of 'lpush' command!");
        if (super.getArgs().length < 2) throw new ValidationError("Wrong number of arguments for 'lpush' command");
    }

    @Override
    protected Object execute(Memory memoryRef) {
        String key = super.getArgs()[0];
        String[] args = new String[super.getArgs().length-1];
        for (int i = 1; i < super.getArgs().length; i++) {
            args[i-1] = super.getArgs()[i];
        }

        ArrayList<String> newList;
        if (memoryRef.getMainMemory().containsKey(key)) {
            Object obj = memoryRef.getMainMemory().get(key);
            if (obj instanceof ArrayList) {
                List<String> list = ((ArrayList<?>) obj).stream().map(String::valueOf).toList();
                newList = new ArrayList<>(Arrays.asList(args).reversed());
                newList.addAll(list);
                memoryRef.getMainMemory().put(key, newList);
            } else {
                throw new WrongTypeError("Operation against a key holding the wrong kind of value");
            }
        } else {
            newList = new ArrayList<>(Arrays.asList(args).reversed());
            memoryRef.getMainMemory().put(key, newList);
        }
        return newList.size();
    }
}

