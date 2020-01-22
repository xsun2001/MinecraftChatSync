package io.xsun.minecraft.chatsync.common.communication;

import com.google.gson.JsonObject;

import java.net.InetSocketAddress;

public final class ClientFactory {
    public static IChannel<JsonObject> connectToTcpJsonServer(InetSocketAddress address) {
        return null;
    }

    public static IChannel<JsonObject> connectToWebsocketJsonServer(InetSocketAddress address) {
        return null;
    }
}
