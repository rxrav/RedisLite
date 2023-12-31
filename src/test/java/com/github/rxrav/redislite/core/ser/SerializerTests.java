package com.github.rxrav.redislite.core.ser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.github.rxrav.redislite.core.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerializerTests {

    static Resp2Serializer serializer;

    @BeforeAll
    static void beforeAll() {
        serializer = new Resp2Serializer();
    }

    @Test
    void shouldSerializeSimpleString() {
        var simpleStr = "hello world";
        var expected = STR."+hello world\{CRLF}";
        var actual = serializer.serialize(simpleStr, false);
        assertEquals(expected, actual);
    }

    @Test
    void shouldSerializeBulkString() {
        var bulkStr = STR."hello\{CR}wo\{LF}rld";
        var expected = STR."$12\{CRLF}hello\{CR}wo\{LF}rld\{CRLF}";
        var actual = serializer.serialize(bulkStr, true);
        assertEquals(expected, actual);
    }

    @Test
    void shouldSerializeAnotherBulkString() {
        var bulkStr = "hello";
        var expected = STR."$5\{CRLF}hello\{CRLF}";
        var actual = serializer.serialize(bulkStr, true);
        assertEquals(expected, actual);
    }

    @Test
    void shouldSerializeNullString() {
        var expected = STR."$-1\{CRLF}";
        var actual = serializer.serialize((String) null, true);
        assertEquals(expected, actual);
    }

    @Test
    void shouldSerializeEmptyString() {
        var expected = STR."$0\{CRLF}";
        var actual = serializer.serialize("", true);
        assertEquals(expected, actual);
    }

    @Test
    void shouldNotSerializeBulkStrAsSimpleStr() {
        var bulkStr = STR."hello\{CR}wo\{LF}rld";
        String expected = STR."-this is a bulk str, can't serialize as simple str\{CRLF}";
        String actual = serializer.serialize(bulkStr, false);
        assertEquals(expected, actual);
    }

    @Test
    void shouldSerializeSimpleError() {
        var simpleError = new RuntimeException("test exception");
        var expected = STR."-test exception\{CRLF}";
        var actual = serializer.serialize(simpleError);
        assertEquals(expected, actual);
    }

    @Test
    void shouldSerializePositiveInteger() {
        var integer = 99;
        var expected = STR.":99\{CRLF}";
        var actual = serializer.serialize(integer);
        assertEquals(expected, actual);
    }

    @Test
    void shouldSerializeNegativeInteger() {
        var integer = -99;
        var expected = STR.":-99\{CRLF}";
        var actual = serializer.serialize(integer);
        assertEquals(expected, actual);
    }

    @Test
    void shouldSerializeStrArray() {
        var arr = new Object[] {"hello", "world"};
        var expected = STR."*2\{CRLF}$5\{CRLF}hello\{CRLF}$5\{CRLF}world\{CRLF}";
        var actual = serializer.serialize(arr);
        assertEquals(expected, actual);
    }

    @Test
    void shouldSerializeIntArray() {
        var arr = new Object[] {1, 2};
        var expected = STR."*2\{CRLF}:1\{CRLF}:2\{CRLF}";
        var actual = serializer.serialize(arr);
        assertEquals(expected, actual);
    }

    @Test
    void shouldSerializeMixedArray() {
        var arr = new Object[] {"hello", 2};
        var expected = STR."*2\{CRLF}$5\{CRLF}hello\{CRLF}:2\{CRLF}";
        var actual = serializer.serialize(arr);
        assertEquals(expected, actual);
    }

    @Test
    void shouldSerializeNullArray() {
        var expected = STR."*-1\{CRLF}";
        var actual = serializer.serialize((Object[]) null);
        assertEquals(expected, actual);
    }

    @Test
    void shouldSerializeEmptyArray() {
        var arr = new Object[] {};
        var expected = STR."*0\{CRLF}";
        var actual = serializer.serialize(arr);
        assertEquals(expected, actual);
    }
}