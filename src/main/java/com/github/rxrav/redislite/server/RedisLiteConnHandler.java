package com.github.rxrav.redislite.server;

import com.github.rxrav.redislite.core.Memory;
import com.github.rxrav.redislite.core.cmd.CommandHandler;
import com.github.rxrav.redislite.core.ser.Resp2Deserializer;
import com.github.rxrav.redislite.core.ser.Resp2Serializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class RedisLiteConnHandler {
    private static final int MAX_BUFFER_SIZE = 4096;
    private final Logger logger = LogManager.getLogger(RedisLiteConnHandler.class);
    private final Socket clientSocket;
    private final Memory memoryRef;

    public RedisLiteConnHandler(Socket clientSocket, Memory memoryRef) {
        this.clientSocket = clientSocket;
        this.memoryRef = memoryRef;
    }

    public void handle() throws IOException {
        logger.debug("Client connected!");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream(), StandardCharsets.UTF_8))) {

            char[] incoming = new char[MAX_BUFFER_SIZE];
            int nosOfBytesRead;

            while((nosOfBytesRead = reader.read(incoming)) > 0) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < nosOfBytesRead; i ++) {
                    builder.append(incoming[i]);
                }
                logger.debug(STR."Client sent: \{builder.toString()}");
                Object cmdResp = new CommandHandler(
                        new Resp2Serializer(),
                        new Resp2Deserializer(),
                        memoryRef
                ).handleCommand(builder.toString());
                writer.write(cmdResp.toString());
                writer.flush();
            }
        } catch (SocketException e) {
            logger.info("Client disconnection requested");
            clientSocket.close();
        } catch (IOException e) {
            logger.error("RedisLite server unable to handle connection");
        }
    }
}
