package io.xsun.minecraft.chatsync.common.communication;

import com.google.gson.JsonObject;

public interface ServerFactory {
    IServer<JsonObject> newTcpJsonServer(int port);

    IServer<JsonObject> newWebsocketJsonServer(int port);
}
