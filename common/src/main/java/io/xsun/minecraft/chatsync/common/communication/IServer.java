package io.xsun.minecraft.chatsync.common.communication;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.Consumer;

public interface IServer<MessageType> {

    InetSocketAddress getLocalAddress();

    void close();

    void setOnChannelConnected(Consumer<IChannel<MessageType>> onChannelConnected);

    void setOnChannelDisconnected(Consumer<IChannel<MessageType>> onChannelDisconnected);

    List<IChannel<MessageType>> getConnectedChannels();

    default void broadcastMessage(MessageType message) {
        getConnectedChannels().forEach(channel -> channel.send(message));
    }

}
