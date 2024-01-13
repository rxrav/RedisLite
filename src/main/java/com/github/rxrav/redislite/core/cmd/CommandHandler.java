package com.github.rxrav.redislite.core.cmd;

import com.github.rxrav.redislite.core.cmd.impl.*;
import com.github.rxrav.redislite.core.error.UnknownCommandError;
import com.github.rxrav.redislite.core.ser.Resp2Deserializer;
import com.github.rxrav.redislite.core.ser.Resp2Serializer;
import com.github.rxrav.redislite.server.RedisLiteServer;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import static com.github.rxrav.redislite.core.Constants.*;

public class CommandHandler {
    private final Resp2Serializer serializer;
    private final Resp2Deserializer deserializer;
    public CommandHandler(Resp2Serializer serializer, Resp2Deserializer deserializer) {
        this.serializer = serializer;
        this.deserializer = deserializer;
    }
    public String handleCommand(String command) throws SocketException {
        char firstByte = command.charAt(0);
        if (firstByte != ARRAY) {
            return serializer.serialize(new RuntimeException("Command should be an array of bulk str"));
        } else {
            try {
                Object[] deserializedArray = deserializer.deserializeArray(command);
                return switch (deserializedArray[0].toString().toUpperCase()) {
                    case "EXIT" -> throw new SocketException("Disconnecting client");
                    case "PING" -> serializer.serialize(String.valueOf(new Ping().builder(deserializedArray).handle()), false);
                    case "ECHO" -> serializer.serialize(String.valueOf(new Echo().builder(deserializedArray).handle()), true);
                    case "SET" -> {
                        Object resp = new Set().builder(deserializedArray).handle();
                        if (resp == null) yield serializer.serialize(null, true);
                        else yield serializer.serialize(String.valueOf(resp), false);
                    }
                    case "GET" -> {
                        Object resp = new Get().builder(deserializedArray).handle();
                        if (resp == null) yield serializer.serialize(null, true);
                        if (resp instanceof Integer) yield serializer.serialize((int) resp);
                        else yield serializer.serialize(String.valueOf(resp), true);
                    }
                    case "EXISTS" -> serializer.serialize((int) new Exists().builder(deserializedArray).handle());
                    case "DEL" -> serializer.serialize((int) new Del().builder(deserializedArray).handle());
                    case "INCR" -> serializer.serialize((int) new Incr().builder(deserializedArray).handle());
                    case "DECR" -> serializer.serialize((int) new Decr().builder(deserializedArray).handle());
                    case "LPUSH" -> serializer.serialize((int) new Lpush().builder(deserializedArray).handle());
                    case "LRANGE" -> {
                        Object obj = new Lrange().builder(deserializedArray).handle();
                        List<String> list = ((ArrayList<?>) obj).stream().map(String::valueOf).toList();
                        String[] arr = new String[list.size()];
                        for(int i = 0; i < arr.length; i ++) {
                            arr[i] = list.get(i);
                        }
                        yield serializer.serialize(arr);
                    }
                    case "RPUSH" -> serializer.serialize((int) new Rpush().builder(deserializedArray).handle());
                    case "FLUSHALL" -> serializer.serialize(String.valueOf(new FlushAll().builder(deserializedArray).handle()), false);
                    case "SAVE" -> serializer.serialize(String.valueOf(new Save().builder(deserializedArray).handle()), false);
                    default -> throw new UnknownCommandError("unknown command");
                };
            } catch (RuntimeException e) {
                return serializer.serialize(e);
            }
        }
    }
}
