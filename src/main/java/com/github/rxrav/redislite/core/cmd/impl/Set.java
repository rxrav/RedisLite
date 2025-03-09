package com.github.rxrav.redislite.core.cmd.impl;

import com.github.rxrav.redislite.core.ExpiryMetadata;
import com.github.rxrav.redislite.core.Memory;
import com.github.rxrav.redislite.core.ValueType;
import com.github.rxrav.redislite.core.ValueWrapper;
import com.github.rxrav.redislite.core.cmd.Command;
import com.github.rxrav.redislite.core.error.RedisLiteError;
import com.github.rxrav.redislite.core.error.ValidationError;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.function.Predicate;

public class Set extends Command {
    public static final long REALLY_BIG_TIME_VAL = 100_000_000_000L;
    private String optNxXx = "NONE";
    private String optExPx = "NONE";
    private long optTimeVal = REALLY_BIG_TIME_VAL;

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
                if (isNxXx.test(super.getArgs()[i])) {
                    this.optNxXx = super.getArgs()[i];
                }
                if (isExPx.test(super.getArgs()[i])) {
                    this.optExPx = super.getArgs()[i];

                    try {
                        this.optTimeVal = Long.parseLong(super.getArgs()[i + 1]);
                        if (this.optTimeVal <= 0) {
                            throw new ValidationError(STR."Time is in past, can't use with \{super.getArgs()[i]}");
                        }
                    } catch (ArrayIndexOutOfBoundsException a) {
                        throw new ValidationError("Hmm.. seems like a syntax error");
                    } catch (NumberFormatException n) {
                        throw new ValidationError("Time value is not a number");
                    }
                }
            } else if (IsNumber.test(super.getArgs()[i])) {
                if (!isExPx.test(super.getArgs()[i - 1])) { // because a number can only be preceded by ex, px
                    throw new ValidationError("Misplaced number value");
                }
            } else {
                throw new ValidationError("Hmm.. seems like a syntax error");
            }
        }
    }

    @Override
    protected ValueWrapper execute(Memory memoryRef) throws UnsupportedEncodingException {
        String key = super.getArgs()[0];
        String val = super.getArgs()[1];

        long timeout = switch (this.optExPx) {
            case "EX", "ex" -> this.optTimeVal * 1000;
            case "PX", "px" -> this.optTimeVal;
            default -> REALLY_BIG_TIME_VAL;
        };

        int iVal = 0;
        String sVal = "";
        ValueType valType;
        try {
            iVal = Integer.parseInt(val);
            valType = ValueType.NUMBER; 
        } catch (NumberFormatException e) {
            sVal = val;
            valType = ValueType.STRING;
        }

        switch (this.optNxXx) {
            case "XX", "xx" -> {
                if (memoryRef.has(key)) {
                    set(memoryRef, valType, key, iVal, sVal, timeout);
                } else {
                    return null;
                }
            }
            case "NX", "nx" -> {
                if (!memoryRef.has(key)) {
                    set(memoryRef, valType, key, iVal, sVal, timeout);
                } else {
                    return null;
                }
            }
            default -> {
                set(memoryRef, valType, key, iVal, sVal, timeout);
            }
        }
        return new ValueWrapper("OK", ValueType.STRING);
    }

    private static void set(Memory memoryRef, ValueType valType, String key, int iVal, String sVal, long timeout) throws UnsupportedEncodingException {
        if (valType == ValueType.NUMBER) {
            memoryRef.putData(key, new ValueWrapper(iVal, ValueType.NUMBER));
        } else {
            memoryRef.putData(key, new ValueWrapper(sVal, ValueType.STRING));
        }
        memoryRef.putExpiryData(key, new ExpiryMetadata(new Date().getTime(), timeout));
    }
}
