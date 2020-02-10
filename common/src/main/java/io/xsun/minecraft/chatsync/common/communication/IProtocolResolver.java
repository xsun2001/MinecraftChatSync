package io.xsun.minecraft.chatsync.common.communication;

import com.google.gson.JsonObject;

public interface IProtocolResolver<ProtocolMessage> {
    ProtocolMessage fromJson(JsonObject json);

    JsonObject toJson(ProtocolMessage msg);
}
