package com.github.rxrav.redislite.core.cmd.impl;

import com.github.rxrav.redislite.core.ExpiryMetaData;
import com.github.rxrav.redislite.core.cmd.Command;
import com.github.rxrav.redislite.core.error.RedisLiteError;
import com.github.rxrav.redislite.core.error.ValidationError;
import com.github.rxrav.redislite.server.RedisLiteServer;

import java.util.Date;
import java.util.function.Predicate;

public class Set extends Command {
    public static final String REALLY_BIG_TIME_VAL = "100000000000";
    private final Predicate<String> isNxXx = (opt) -> "NX".equalsIgnoreCase(opt) || "XX".equalsIgnoreCase(opt);
    private final Predicate<String> isExPx = (opt) -> {
        if ("EX".equalsIgnoreCase(opt) || "PX".equalsIgnoreCase(opt)) return true;
        if ("EXAT".equalsIgnoreCase(opt) || "PXAT".equalsIgnoreCase(opt))
            throw new RedisLiteError("EXAT & PXAT are not supported yet");
        else return false;
    };

    private final Predicate<String> IsNumber = (numStr) -> {
        try {
            Integer.parseInt(numStr);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    };

    @Override
    protected void validate() throws ValidationError {
        if (!"SET".equalsIgnoreCase(super.getCmd())) throw new ValidationError("Not correct use of 'set' command!");
        int len = super.getArgs().length;
        if (len < 2) throw new ValidationError("Too few arguments, example: set name john [nx|xx] [ex|px] timeVal"); // set name john
        if (len > 5)
            throw new ValidationError("Too many arguments, example: set name john [nx|xx] [ex|px] timeVal"); // set name john [nx|xx] [ex|px] timeVal

        // 0 is key
        // 1 is val
        // hence starting from 2
        for (int i = 2; i < super.getArgs().length; i++) {
            if (isNxXx.test(super.getArgs()[i]) || isExPx.test(super.getArgs()[i])) {
                if (isExPx.test(super.getArgs()[i])) {
                    try {
                        long timeVal = Long.parseLong(super.getArgs()[i + 1]);
                        if (timeVal <= 0) {
                            throw new ValidationError(STR."Time is in past, can't use with \{super.getArgs()[i]}");
                        }
                    } catch (ArrayIndexOutOfBoundsException a) {
                        throw new ValidationError("Hmm.. seems like a syntax error");
                    } catch (NumberFormatException n) {
                        throw new ValidationError("Time value is not a number");
                    }
                }
            } else if (IsNumber.test(super.getArgs()[i])) {
                if (!isExPx.test(super.getArgs()[i - 1])) {
                    throw new ValidationError("Misplaced number value");
                }
            } else {
                throw new ValidationError("Hmm.. seems like a syntax error");
            }
        }
    }

    @Override
    protected Object execute() {
        String key = super.getArgs()[0];
        String val = super.getArgs()[1];

        String optNxXx = "NONE";
        String optExPx = "NONE";
        String optTimeVal = REALLY_BIG_TIME_VAL;

        if (super.getArgs().length > 2) {
            for (int i = 2; i < super.getArgs().length; i++) {
                if (isNxXx.test(super.getArgs()[i])) {
                    optNxXx = super.getArgs()[i];
                }
                if (isExPx.test(super.getArgs()[i])) {
                    optExPx = super.getArgs()[i];
                    optTimeVal = super.getArgs()[i + 1];
                }
            }
        }

        long timeout = switch (optExPx) {
            case "EX", "ex" -> Long.parseLong(optTimeVal) * 1000;
            case "PX", "px" -> Long.parseLong(optTimeVal);
            default -> Long.parseLong(REALLY_BIG_TIME_VAL);
        };

        switch (optNxXx) {
            case "XX", "xx" -> {
                if (RedisLiteServer.getMemoryMap().containsKey(key)) {
                    RedisLiteServer.getMemoryMap().put(key, val);
                    RedisLiteServer.getExpiryDetailsMap().put(key, new ExpiryMetaData(new Date().getTime(), timeout));
                } else {
                    return null;
                }
            }
            case "NX", "nx" -> {
                if (!RedisLiteServer.getMemoryMap().containsKey(key)) {
                    RedisLiteServer.getMemoryMap().put(key, val);
                    RedisLiteServer.getExpiryDetailsMap().put(key, new ExpiryMetaData(new Date().getTime(), timeout));
                } else {
                    return null;
                }
            }
            default -> {
                RedisLiteServer.getMemoryMap().put(key, val);
                RedisLiteServer.getExpiryDetailsMap().put(key, new ExpiryMetaData(new Date().getTime(), timeout));
            }
        }
        return "OK";
    }
}
