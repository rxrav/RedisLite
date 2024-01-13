package com.github.rxrav.redislite.server;

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
    private final Logger logger = LogManager.getLogger(RedisLiteConnHandler.class);
    private final Socket clientSocket;
    public RedisLiteConnHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    public void handle() throws IOException {
        logger.debug("Client connected!");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream(), StandardCharsets.UTF_8))) {

            char[] incoming = new char[1024];
            int nosOfBytesRead;

            while((nosOfBytesRead = reader.read(incoming)) > 0) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < nosOfBytesRead; i ++) {
                    builder.append(incoming[i]);
                }
                logger.debug(STR."Client sent: \{builder.toString()}");
                Object cmdResp = new CommandHandler(
                        new Resp2Serializer(),
                        new Resp2Deserializer()
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
