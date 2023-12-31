package com.github.rxrav.redislite.core.ser;

import static com.github.rxrav.redislite.core.Constants.*;

public class Resp2Serializer {
    public String serialize(String str, boolean isBulk) {
        if (isBulk) {
            if (str == null) {
                return STR."\{BULK_STR}-1\{CRLF}";
            } else if (str.isEmpty()) {
                return STR."\{BULK_STR}0\{CRLF}";
            } else {
                return STR."\{BULK_STR}\{str.length()}\{CRLF}\{str}\{CRLF}";
            }
        } else {
            return (str.indexOf(CR) > 0 || str.indexOf(LF) > 0) ?
            serialize(new RuntimeException("this is a bulk str, can't serialize as simple str")) :
            STR."\{SIMPLE_STR}\{str}\{CRLF}";
        }
    }
    public String serialize(RuntimeException error) {
        return STR."\{SIMPLE_ERR}\{error.getMessage()}\{CRLF}";
    }
    public String serialize(int integer) {
        return STR."\{INTEGER}\{integer}\{CRLF}";
    }
    public String serialize(Object[] arr) {
        String des = STR."\{ARRAY}";
        if (arr == null) {
            des = STR."\{des}-1\{CRLF}";
        } else if (arr.length == 0) {
            des = STR."\{des}0\{CRLF}";
        } else {
            des = STR."\{des}\{arr.length}\{CRLF}";
            for (Object obj : arr) {
                if (obj instanceof String) {
                    des = STR."\{des}\{serialize((String) obj, true)}";
                } else if (obj instanceof Integer) {
                    des = STR."\{des}\{serialize((Integer) obj)}";
                }
            }
        }
        return des;
    }
}
