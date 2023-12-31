package com.github.rxrav.redislite.core.cmd;

import com.github.rxrav.redislite.AppTest;
import com.github.rxrav.redislite.client.RedisLiteTestClient;
import com.github.rxrav.redislite.core.ser.Resp2Deserializer;
import com.github.rxrav.redislite.core.ser.Resp2Serializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RedisLiteTestClientTest {

    @BeforeAll
    static void _startServer() {
        AppTest.startServer();
    }

    @AfterAll
    static void _stopServer() throws IOException {
        AppTest.stopServer();
    }

    @Test
    void shouldPing() throws InterruptedException {
        RedisLiteTestClient client = new RedisLiteTestClient("alpha");
        client.tryConnect();
        String response1 = client.send(new Resp2Serializer().serialize(new String[]{"ping"}));
        assertEquals("PONG", new Resp2Deserializer().deserializeString(response1));
        client.disconnect();
    }

    @Test
    void shouldEcho() throws InterruptedException {
        RedisLiteTestClient client = new RedisLiteTestClient("delta");
        client.tryConnect();
        String response3 = client.send(new Resp2Serializer().serialize(new String[]{"echo", "\"hello world\""}));
        assertEquals("\"hello world\"", new Resp2Deserializer().deserializeString(response3));
        client.disconnect();
    }
}
