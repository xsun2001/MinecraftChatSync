package io.xsun.minecraft.chatsync.common.communication.insideprotocol;

import com.google.gson.JsonObject;
import io.xsun.minecraft.chatsync.common.communication.AbstractChannel;
import io.xsun.minecraft.chatsync.common.communication.IChannel;
import io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.MessageBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class InsideChannel extends AbstractChannel<MessageBase> implements IChannel<MessageBase> {

    private final static Logger LOG = LoggerFactory.getLogger(InsideChannel.class);
    private final IChannel<JsonObject> impl;
    private final ProtocolResolver resolver;

    public InsideChannel(IChannel<JsonObject> impl) {
        LOG.info("Create InsideChannel with impl[{}]", impl);
        this.impl = impl;
        resolver = new ProtocolResolver();

        impl.setMessageHandler(
                json -> {
                    MessageBase msg = resolver.toMessage(json);
                    onMessage.accept(msg);
                }
        );
        impl.setExceptionHandler(
                cause -> {
                    if (cause instanceof InvalidMessageTypeException) {
                        LOG.warn(cause.getMessage());
                        return false;
                    }
                    return true;
                }
        );
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return impl.getLocalAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return impl.getRemoteAddress();
    }

    @Override
    public void close() {
        impl.close();
    }

    @Override
    public void send(MessageBase message) {
        JsonObject json = resolver.toJson(message);
        impl.send(json);
    }
}
