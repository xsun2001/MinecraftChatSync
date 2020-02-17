package io.xsun.minecraft.chatsync.common.communication.insideprotocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.xsun.minecraft.chatsync.common.communication.IProtocolResolver;
import io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.MessageBase;
import io.xsun.minecraft.chatsync.common.logging.CSLogger;
import io.xsun.minecraft.chatsync.common.logging.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class InsideProtocolResolver implements IProtocolResolver<MessageBase> {

    private static final Gson GSON = new GsonBuilder().create();
    private final CSLogger log;
    private Map<String, Class<? extends MessageBase>> typeNameMapping;

    public InsideProtocolResolver() {
        log = LogManager.getInstance().getLogger(InsideProtocolResolver.class);
        try {
            Properties mapping = new Properties();
            URL resource = InsideProtocolResolver.class.getResource("message/MessageTypeMapping.properties");
            InputStream inStream = resource.openStream();
            mapping.load(inStream);
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

    @Override
    public JsonObject toJson(MessageBase rawMsg) {
        return (JsonObject) GSON.toJsonTree(rawMsg);
    }

    @Override
    public MessageBase fromJson(JsonObject rawJson) {
        String typeName = rawJson.get("type").getAsString();
        Class<? extends MessageBase> typeClass = typeNameMapping.get(typeName);
        if (typeClass == null) {
            throw new InvalidMessageTypeException(typeName);
        }
        return GSON.fromJson(rawJson, typeClass);
    }

}
