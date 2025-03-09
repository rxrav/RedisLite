package com.github.rxrav.redislite.core.cmd;

import com.github.rxrav.redislite.core.Memory;
import com.github.rxrav.redislite.core.ValueType;
import com.github.rxrav.redislite.core.ValueWrapper;
import com.github.rxrav.redislite.core.cmd.impl.*;
import com.github.rxrav.redislite.core.error.UnknownCommandError;
import com.github.rxrav.redislite.core.ser.Resp2Deserializer;
import com.github.rxrav.redislite.core.ser.Resp2Serializer;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import static com.github.rxrav.redislite.core.Constants.*;

public class CommandHandler {
    private final Resp2Serializer serializer;
    private final Resp2Deserializer deserializer;
    private final Memory memoryRef;

    public CommandHandler(Resp2Serializer serializer, Resp2Deserializer deserializer, Memory memoryRef) {
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.memoryRef = memoryRef;
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
                    case "PING" -> serializer.serialize(new Ping().builder(deserializedArray).handle(null).getValue().toString(), false);
                    case "ECHO" -> serializer.serialize(new Echo().builder(deserializedArray).handle(null).getValue().toString(), true);
                    case "SET" -> {
                        ValueWrapper resp = new Set().builder(deserializedArray).handle(memoryRef);
                        if (resp == null) yield serializer.serialize(null, true);
                        else yield serializer.serialize(resp.getValue().toString(), false);
                    }
                    case "GET" -> {
                        ValueWrapper resp = new Get().builder(deserializedArray).handle(memoryRef);
                        if (resp == null) yield serializer.serialize(null, true);
                        if (resp.getValueType() == ValueType.NUMBER) yield serializer.serialize((int) resp.getValue());
                        else yield serializer.serialize(resp.getValue().toString(), true);
                    }
                    case "EXISTS" -> serializer.serialize((int) new Exists().builder(deserializedArray).handle(memoryRef).getValue());
                    case "DEL" -> serializer.serialize((int) new Del().builder(deserializedArray).handle(memoryRef).getValue());
                    case "INCR" -> serializer.serialize((int) new Incr().builder(deserializedArray).handle(memoryRef).getValue());
                    case "DECR" -> serializer.serialize((int) new Decr().builder(deserializedArray).handle(memoryRef).getValue());
                    case "LPUSH" -> serializer.serialize((int) new Lpush().builder(deserializedArray).handle(memoryRef).getValue());
                    case "LRANGE" -> {
                        ValueWrapper obj = new Lrange().builder(deserializedArray).handle(memoryRef);
                        List<String> list = ((ArrayList<?>) obj.getValue()).stream().map(String::valueOf).toList();
                        String[] arr = new String[list.size()];
                        for(int i = 0; i < arr.length; i ++) {
                            arr[i] = list.get(i);
                        }
                        yield serializer.serialize(arr);
                    }
                    case "RPUSH" -> serializer.serialize((int) new Rpush().builder(deserializedArray).handle(memoryRef).getValue());
                    case "FLUSHALL" -> serializer.serialize(new FlushAll().builder(deserializedArray).handle(memoryRef).getValue().toString(), false);
                    case "SAVE" -> serializer.serialize(new Save().builder(deserializedArray).handle(memoryRef).getValue().toString(), false);
                    default -> throw new UnknownCommandError("unknown command");
                };
            } catch (RuntimeException e) {
                return serializer.serialize(e);
            }
        }
    }
}
