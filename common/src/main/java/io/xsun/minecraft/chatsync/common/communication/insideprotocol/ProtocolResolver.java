package io.xsun.minecraft.chatsync.common.communication.insideprotocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.MessageBase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ProtocolResolver {

    private static final Gson GSON = new GsonBuilder().create();
    private Map<String, Class<? extends MessageBase>> typeNameMapping;

    public ProtocolResolver() {
        try {
            Properties mapping = new Properties();
            mapping.load(ProtocolResolver.class.getResource("message/MessageTypeMapping.properties").openStream());
            typeNameMapping = new HashMap<>();
            mapping.forEach((o1, o2) -> {
                try {
                    String typeName = o1.toString();
                    String fullQualifiedName = o2.toString();
                    Class<? extends MessageBase> typeClass = Class.forName(fullQualifiedName).asSubclass(MessageBase.class);
                    typeNameMapping.put(typeName, typeClass);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonObject toJson(MessageBase rawMsg) {
        return (JsonObject) GSON.toJsonTree(rawMsg);
    }

    public MessageBase toMessage(JsonObject rawJson) {
        String typeName = rawJson.get("type").getAsString();
        Class<? extends MessageBase> typeClass = typeNameMapping.get(typeName);
        if (typeClass == null) {
            throw new InvalidMessageTypeException(typeName);
        }
        return GSON.fromJson(rawJson, typeClass);
    }

}
