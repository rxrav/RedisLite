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

    // commands
    public static final String PING = "PING";
    public static final String ECHO = "ECHO";
    public static final String SET = "SET";
    public static final String GET = "GET";
}
