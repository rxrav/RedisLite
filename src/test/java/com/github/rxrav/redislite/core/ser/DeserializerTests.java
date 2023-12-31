package com.github.rxrav.redislite.core.ser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.github.rxrav.redislite.core.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DeserializerTests {

    static Resp2Deserializer deserializer;

    @BeforeAll
    static void beforeAll() {
        deserializer = new Resp2Deserializer();
    }

    @Test
    void shouldDeserializeSimpleString() {
        var msg = STR."+OK\{CRLF}";
        var expected = "OK";
        String actual = deserializer.deserializeString(msg);
        assertEquals(expected, actual);
    }

    @Test
    void shouldDeserializeBulkString() {
        var msg = STR."$12\{CRLF}hello\{CR}wo\{LF}rld\{CRLF}";
        var expected = STR."hello\{CR}wo\{LF}rld";
        String actual = deserializer.deserializeString(msg);
        assertEquals(expected, actual);
    }

    @Test
    void shouldDeserializeAnotherBulkString() {
        var msg = STR."$5\{CRLF}hello\{CRLF}";
        var expected = "hello";
        String actual = deserializer.deserializeString(msg);
        assertEquals(expected, actual);
    }

    @Test
    void shouldDeserializeNullString() {
        var actual = deserializer.deserializeString(STR."$-1\{CRLF}");
        assertNull(actual);
    }

    @Test
    void shouldDeserializeEmptyString() {
        var actual = deserializer.deserializeString(STR."$0\{CRLF}");
        assertEquals("", actual);
    }

    @Test
    void shouldDeserializeSimpleError() {
        var msg = STR."-test exception\{CRLF}";
        var expected = new RuntimeException("test exception");
        var actual = deserializer.deserializeError(msg);
        assertEquals(expected.getMessage(), actual.getMessage());
    }

    @Test
    void shouldDeserializePositiveInteger() {
        var integer = STR.":99\{CRLF}";
        var actual = deserializer.deserializeInteger(integer);
        assertEquals(99, actual);
    }

    @Test
    void shouldDeserializeNegativeInteger() {
        var integer = STR.":-99\{CRLF}";
        var actual = deserializer.deserializeInteger(integer);
        assertEquals(-99, actual);
    }

    @Test
    void shouldDeserializeStrArray() {
        var msg = STR."*2\{CRLF}$5\{CRLF}hello\{CRLF}$5\{CRLF}world\{CRLF}";
        var expected = new String[] {"hello", "world"};
        var actual = deserializer.deserializeArray(msg);
        for(int i = 0; i < expected.length; i ++) {
            assertEquals(expected[i], actual[i].toString());
        }
    }

    @Test
    void shouldDeserializeIntArray() {
        var msg = STR."*2\{CRLF}:1\{CRLF}:2\{CRLF}";
        var expected = new int[] {1, 2};
        var actual = deserializer.deserializeArray(msg);
        for(int i = 0; i < expected.length; i ++) {
            assertEquals(expected[i], (int) actual[i]);
        }
    }

    @Test
    void shouldSerializeMixedArray() {
        var msg = STR."*2\{CRLF}$5\{CRLF}hello\{CRLF}:2\{CRLF}";
        var actual = deserializer.deserializeArray(msg);
        assertEquals("hello", actual[0].toString());
        assertEquals(2, (int) actual[1]);
    }

    @Test
    void shouldDeserializeNullArray() {
        var actual = deserializer.deserializeArray(STR."*-1\{CRLF}");
        assertNull(actual);
    }

    @Test
    void shouldDeserializeEmptyArray() {
        var actual = deserializer.deserializeArray(STR."*0\{CRLF}");
        assertEquals(0, actual.length);
    }
}
