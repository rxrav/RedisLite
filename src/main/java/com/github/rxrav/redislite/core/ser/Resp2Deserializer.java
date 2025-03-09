package com.github.rxrav.redislite.core.ser;

import static com.github.rxrav.redislite.core.Constants.*;

public class Resp2Deserializer {

    public String deserializeString(String msg) {
        var type = msg.charAt(0);
        return switch (type) {
            case SIMPLE_STR -> parseSimpleString(msg);
            case BULK_STR -> parseBulkString(msg);
            default -> throw new IllegalStateException(STR."Unexpected value: \{type}");
        };
    }

    public RuntimeException deserializeError(String msg) {
        var type = msg.charAt(0);
        if (type == SIMPLE_ERR) return parseSimpleError(msg);
        throw new IllegalStateException(STR."Unexpected value: \{type}");
    }

    public int deserializeInteger(String msg) {
        var type = msg.charAt(0);
        if (type == INTEGER) return parseInteger(msg);
        throw new IllegalStateException(STR."Unexpected value: \{type}");
    }

    public Object[] deserializeArray(String msg) {
        var type = msg.charAt(0);
        if (type == ARRAY) return parseArray(msg);
        throw new IllegalStateException(STR."Unexpected value: \{type}");
    }

    private int parseInteger(String msg) {
        return Integer.parseInt(msg.substring(1, msg.lastIndexOf(CRLF)));
    }

    private static Object[] parseArray(String msg) {
        var tokens = msg.split(CRLF);
        var arrSize = Integer.parseInt(tokens[0].substring(1));

        if (arrSize == -1) return null;
        if (arrSize == 0) return new Object[]{};

        Object[] items = new Object[arrSize];
        for(int i = 1, j = 0; i < tokens.length; j++) {
            if (tokens[i].startsWith(String.valueOf(BULK_STR))) {
                items[j] = tokens[i+1];
                i = i+2;
            } else if (tokens[i].startsWith(String.valueOf(INTEGER))) {
                items[j] = Integer.parseInt(tokens[i].substring(1));
                i = i+1;
            }
        }
        return items;
    }

    private RuntimeException parseSimpleError(String msg) {
        return new RuntimeException(msg.substring(1, msg.lastIndexOf(CRLF)));
    }

    private String parseBulkString(String msg) {
        if (STR."$-1\{CRLF}".equals(msg)) return null;
        return msg.substring(msg.indexOf(CRLF), msg.lastIndexOf(CRLF)).trim();
    }

    private String parseSimpleString(String msg) {
        System.out.println(msg);
        return msg.substring(1, msg.indexOf(CRLF));
    }
}
