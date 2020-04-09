package io.xsun.minecraft.chatsync.common.communication;

import com.google.gson.JsonObject;

import java.net.InetSocketAddress;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface IChannel {

    InetSocketAddress getLocalAddress();

    InetSocketAddress getRemoteAddress();

    void close();

    void send(JsonObject message);

    void setMessageHandler(Consumer<JsonObject> onMessage);

    void setExceptionHandler(Predicate<Throwable> onException);

    void setCloseHandler(CloseHandler onClose);

}
