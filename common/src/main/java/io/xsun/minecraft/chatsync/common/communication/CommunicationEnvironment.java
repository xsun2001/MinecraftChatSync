package io.xsun.minecraft.chatsync.common.communication;

import java.net.InetSocketAddress;

public interface CommunicationEnvironment {
    void shutdown();

    IChannel connect(TransferProtocol protocol, InetSocketAddress destination);

    IServer bind(TransferProtocol protocol, InetSocketAddress bindAddress);
}
