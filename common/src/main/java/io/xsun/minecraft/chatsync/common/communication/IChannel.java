package io.xsun.minecraft.chatsync.common.communication;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

public interface IChannel<MessageType> {

    InetSocketAddress getLocalAddress();

    InetSocketAddress getRemoteAddress();

    boolean isReady();

    void close();

    void send(MessageType message);

    void setMessageHandler(Consumer<MessageType> onMessage);

    void setExceptionHandler(Consumer<Throwable> onException);

    void setCloseHandler(Consumer<CloseReason> onClose);

}
