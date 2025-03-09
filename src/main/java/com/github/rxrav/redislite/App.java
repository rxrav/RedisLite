package com.github.rxrav.redislite;

import com.github.rxrav.redislite.core.Constants;
import com.github.rxrav.redislite.server.RedisLiteServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class App {
    public static void main( String[] args ) throws IOException {
        RedisLiteServer server = new RedisLiteServer();
        server.start();
    }
}
