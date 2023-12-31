package com.github.rxrav.redislite;

import com.github.rxrav.redislite.server.RedisLiteServer;

import java.io.IOException;

public class App {
    public static void main( String[] args ) throws IOException {
        RedisLiteServer server = new RedisLiteServer();
        server.start();
    }
}
