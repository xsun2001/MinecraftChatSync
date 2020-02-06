package io.xsun.minecraft.chatsync.common.communication;

import com.google.gson.JsonObject;

import java.net.InetSocketAddress;

public interface ClientFactory {
    IChannel<JsonObject> connectToTcpJsonServer(InetSocketAddress address);

    IChannel<JsonObject> connectToWebsocketJsonServer(InetSocketAddress address);
}
