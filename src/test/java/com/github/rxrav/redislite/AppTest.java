package com.github.rxrav.redislite;

import com.github.rxrav.redislite.client.RedisLiteTestClient;
import com.github.rxrav.redislite.server.RedisLiteServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class AppTest {
    public static RedisLiteServer server;
    public static void startServer() {
        server = new RedisLiteServer();
        Runnable runnable = () -> {
            try {
                server.start();
            } catch (RuntimeException | IOException e) {
                throw new RuntimeException(e);
            }
        };
        Thread serverThread = new Thread(runnable);
        serverThread.start();
    }

    public static void stopServer() throws IOException {
        server.stop();
    }

    @BeforeAll
    static void _startServer() {
        startServer();
    }

    @AfterAll
    static void _stopServer() throws IOException {
        stopServer();
    }

    @Test
    void shouldConnectAndDisconnect() throws InterruptedException {
        RedisLiteTestClient client = new RedisLiteTestClient("beta");
        client.tryConnect();
        client.disconnect();
    }

}