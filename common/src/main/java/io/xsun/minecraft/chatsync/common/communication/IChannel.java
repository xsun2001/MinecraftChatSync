package io.xsun.minecraft.chatsync.common.communication;

import java.net.InetSocketAddress;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface IChannel<MessageType> {

    InetSocketAddress getLocalAddress();

    InetSocketAddress getRemoteAddress();

    void close();

    void send(MessageType message);

    void setMessageHandler(Consumer<MessageType> onMessage);

    void setExceptionHandler(Predicate<Throwable> onException);

    void setCloseHandler(CloseHandler onClose);

}
