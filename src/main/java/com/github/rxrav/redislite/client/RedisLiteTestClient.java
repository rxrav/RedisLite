package com.github.rxrav.redislite.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RedisLiteTestClient {
    private final Logger logger = LogManager.getLogger(RedisLiteTestClient.class);
    private Socket server;
    private BufferedWriter writer;
    private BufferedReader reader;
    private final String name;
    private final String host;
    private final int port;

    public RedisLiteTestClient(String name) {
        this.name = name;
        this.host = "127.0.0.1";
        this.port = 6379;
    }
    public RedisLiteTestClient(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    public void tryConnect() throws InterruptedException {
        int i = 1;
        while (i <= 3) {
            try {
                this.server = new Socket(this.host, this.port);
                this.writer = new BufferedWriter(new OutputStreamWriter(this.server.getOutputStream()));
                this.reader = new BufferedReader(new InputStreamReader(this.server.getInputStream(), StandardCharsets.UTF_8));
                break;
            } catch (IOException e) {
                logger.error(STR."Client \{this.name} unable to connect at \{this.host}:\{this.port} on trial [\{i}]");
                Thread.sleep(500);
                if (i == 3) {
                    throw new RuntimeException(STR."Max tryouts exceeded for client [\{name}]");
                } else {
                    ++ i;
                }
            }
        }
    }

    public String send(String message) {
        String response = null;
        try {
            // logger.debug(STR."Client \{this.name} sending message: \{message}");
            this.writer.write(message);
            this.writer.flush();

            char[] incoming = new char[1024];
            int nosOfBytesRead = reader.read(incoming);

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < nosOfBytesRead; i++) {
                builder.append(incoming[i]);
            }
            response = builder.toString();
        } catch(IOException e) {
            logger.error(STR."Client \{this.name} couldn't send message to server");
        }
        return response;
    }

    public void disconnect() {
        try {
            this.reader.close();
            this.writer.close();
            this.server.close();
        } catch (IOException e) {
            logger.error(STR."Client \{this.name} failed to disconnect gracefully");
        }
    }
}
