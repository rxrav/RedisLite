package com.github.rxrav.redislite.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rxrav.redislite.core.ExpiryMetaData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.StringTemplate.STR;

public class RedisLiteServer {
    public static final int PORT = 6379;
    public static final String VN = "1.0";
    private static Map<String, Object> memory;
    private static Map<String, ExpiryMetaData> expiryDetails;
    private final Logger logger = LogManager.getLogger(RedisLiteServer.class);
    private ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final List<Socket> connectedClientList = new ArrayList<>();
    private boolean shouldAcceptConnections = true;

    public RedisLiteServer() {
        logger.info("Building server memory...");
        memory = new ConcurrentHashMap<>();
        expiryDetails = new ConcurrentHashMap<>();
        logger.info("Trying to restored db...");
        this.restoreDb();
        logger.info("Creating executor service...");
        ThreadFactory tf = Thread.ofVirtual().name("client-handler-", 1).factory();
        executorService = Executors.newThreadPerTaskExecutor(tf);
    }

    private void restoreDb() {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("redisLiteDb.rdb"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            String[] data = builder.toString().split("__SEPARATOR__");
            ObjectMapper mapper = new ObjectMapper();
            memory = mapper.readValue(data[0], new TypeReference<>() {});
            expiryDetails = mapper.readValue(data[1], new TypeReference<>() {});
            if (!memory.isEmpty() || !expiryDetails.isEmpty()) {
                logger.info("Data restored");
            } else {
                logger.info("rdb file found, but nothing to restore");
            }
        } catch (FileNotFoundException e) {
            logger.info("Nothing to restore");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() throws IOException {
        registerShutdownHook();
        this.serverSocket = new ServerSocket(PORT);
        logger.info(STR."Server started, RedisLite version \{VN}");
        try {
            logger.info(STR."The RedisLite server is now ready to accept connections on port \{PORT}");
            logger.info("Listening for connections...");
            while (shouldAcceptConnections) {
                Socket client = this.serverSocket.accept();
                this.connectedClientList.add(client);
                executorService.submit(() -> {
                    try {
                        new RedisLiteConnHandler(client).handle();
                    } catch (IOException _) {}
                });
            }
        } catch (IOException e) {
            logger.info("Exited connection handler loop");
        }
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown hook triggered. Stopping RedisLite server...");
            try {
                logger.info("Terminating client connections...");
                for(Socket connectedClient: this.connectedClientList) {
                    connectedClient.close();
                }
                logger.info("Shutting down executor service...");
                this.executorService.shutdown();
                this.stop();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public void stop() throws IOException {
        shouldAcceptConnections = false;
        logger.info("Broke out of connection handler loop");
        serverSocket.close();
        logger.info("Connection closed. Bye.");
    }

    public static Map<String, Object> getMemoryMap() {
        return memory;
    }
    public static Map<String, ExpiryMetaData> getExpiryDetailsMap() {
        return expiryDetails;
    }
}
