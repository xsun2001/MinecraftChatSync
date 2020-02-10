package io.xsun.minecraft.chatsync.common.communication;

import com.google.gson.JsonObject;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class ProtocolUtility {
    private ProtocolUtility() {
    }

    public static <T> IChannel<T> wrapJsonChannel(IChannel<JsonObject> impl, IProtocolResolver<T> resolver) {
        return new ChannelWrapper<>(impl, resolver);
    }

    public static <T> IServer<T> wrapJsonServer(IServer<JsonObject> impl, IProtocolResolver<T> resolver) {
        return new ServerWrapper<>(impl, resolver);
    }

    private static class ChannelWrapper<MsgType> implements IChannel<MsgType> {
        private final IChannel<JsonObject> impl;
        private final IProtocolResolver<MsgType> resolver;

        public ChannelWrapper(IChannel<JsonObject> impl, IProtocolResolver<MsgType> resolver) {
            this.impl = impl;
            this.resolver = resolver;
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
        public void send(MsgType message) {
            impl.send(resolver.toJson(message));
        }

        @Override
        public void setMessageHandler(Consumer<MsgType> onMessage) {
            impl.setMessageHandler(rawJson -> onMessage.accept(resolver.fromJson(rawJson)));
        }

        @Override
        public void setExceptionHandler(Predicate<Throwable> onException) {
            impl.setExceptionHandler(onException);
        }

        @Override
        public void setCloseHandler(CloseHandler onClose) {
            impl.setCloseHandler(onClose);
        }


    }

    private static class ServerWrapper<MsgType> implements IServer<MsgType> {
        private final IServer<JsonObject> impl;
        private final IProtocolResolver<MsgType> resolver;
        // Pretty ugly design :(
        private final Map<IChannel<JsonObject>, IChannel<MsgType>> channelMapping;

        public ServerWrapper(IServer<JsonObject> impl, IProtocolResolver<MsgType> resolver) {
            this.impl = impl;
            this.resolver = resolver;
            this.channelMapping = new HashMap<>();
        }

        @Override
        public InetSocketAddress getLocalAddress() {
            return impl.getLocalAddress();
        }

        @Override
        public void close() {
            impl.close();
        }

        @Override
        public void setOnChannelConnected(Consumer<IChannel<MsgType>> onChannelConnected) {
            impl.setOnChannelConnected(rawChannel -> {
                IChannel<MsgType> channel = wrapJsonChannel(rawChannel, resolver);
                channelMapping.put(rawChannel, channel);
                onChannelConnected.accept(channel);
            });
        }

        @Override
        public void setOnChannelDisconnected(Consumer<IChannel<MsgType>> onChannelDisconnected) {
            impl.setOnChannelDisconnected(rawChannel -> {
                IChannel<MsgType> channel = channelMapping.remove(rawChannel);
                onChannelDisconnected.accept(channel);
            });
        }

        @Override
        public List<IChannel<MsgType>> getConnectedChannels() {
            // f**k this code
            return Collections.unmodifiableList(new ArrayList<>(channelMapping.values()));
        }
    }
}
