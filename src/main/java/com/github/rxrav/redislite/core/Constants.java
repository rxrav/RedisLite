package com.github.rxrav.redislite.core;

public class Constants {
    public static final char CR = '\r';
    public static final char LF = '\n';
    public static final String CRLF = STR."\{CR}\{LF}";

    // first bytes of different data types
    public static final char SIMPLE_STR = '+';
    public static final char SIMPLE_ERR = '-';
    public static final char INTEGER = ':';
    public static final char BULK_STR = '$';
    public static final char ARRAY = '*';

    // others
    public static final String SEPARATOR = "\n__SEP__\n";
    public static final String DAT_FILE_NAME_AT_CURRENT_PATH = "./redisLiteDb.bytes";
}
