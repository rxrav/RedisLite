package com.github.rxrav.redislite.core.cmd.impl;

import com.github.rxrav.redislite.core.cmd.Command;
import com.github.rxrav.redislite.core.error.ValidationError;
import com.github.rxrav.redislite.core.error.WrongTypeError;
import com.github.rxrav.redislite.server.RedisLiteServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lrange extends Command {
    @Override
    protected void validate() throws ValidationError {
        if (!"LRANGE".equalsIgnoreCase(super.getCmd())) throw new ValidationError("Not correct use of 'lrange' command!");
        if (super.getArgs().length != 3) throw new ValidationError("Wrong number of arguments for 'lrange' command");
        try {
            Integer.parseInt(super.getArgs()[1]);
            Integer.parseInt(super.getArgs()[2]);
        } catch (NumberFormatException e) {
            throw new ValidationError("'lrange' works on a startIdx and endIdx which are numbers");
        }
    }

    @Override
    protected Object execute() {
        String key = super.getArgs()[0];
        String startIdx = super.getArgs()[1];
        String endIdx = super.getArgs()[2];

        if (RedisLiteServer.getMemoryMap().containsKey(key)) {
            Object obj = RedisLiteServer.getMemoryMap().get(key);
            if (obj instanceof ArrayList) {
                List<String> list = ((ArrayList<?>) obj).stream().map(String::valueOf).toList();
                ArrayList<String> aList = new ArrayList<>(list);
                int _startIdx = Integer.parseInt(startIdx);
                int _endIdx = Integer.parseInt(endIdx);
                if (_startIdx > aList.size() || _startIdx > _endIdx) _startIdx = 0;
                if (_endIdx < _startIdx || _endIdx > aList.size()) _endIdx = aList.size();
                return new ArrayList<>(aList.subList(_startIdx, _endIdx));
            }
            return new WrongTypeError("Key is not mapped to a list");
        } else {
            return new ArrayList<>();
        }
    }
}
