package io.xsun.minecraft.chatsync.common.communication;

import com.google.gson.JsonObject;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.Consumer;

public interface IServer {

    InetSocketAddress getLocalAddress();

    void close();

    void setOnChannelConnected(Consumer<IChannel> onChannelConnected);

    void setOnChannelDisconnected(Consumer<IChannel> onChannelDisconnected);

    List<IChannel> getConnectedChannels();

    default void broadcastMessage(JsonObject message) {
        getConnectedChannels().forEach(channel -> channel.send(message));
    }

}
