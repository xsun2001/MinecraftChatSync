package io.xsun.minecraft.chatsync.common.communication.netty;

import com.google.gson.JsonObject;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.xsun.minecraft.chatsync.common.communication.IServer;
import io.xsun.minecraft.chatsync.common.communication.ServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class NettyServerFactory implements ServerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(NettyServerFactory.class);
    private final EventLoopGroup group;
    private final Class<? extends ServerSocketChannel> sscType;

    public NettyServerFactory(EventLoopGroup group, Class<? extends ServerSocketChannel> sscType) {
        this.group = group;
        this.sscType = sscType;
    }

    @Override
    public IServer<JsonObject> newTcpJsonServer(int port) {
        LOG.info("Create new netty tcp json server on port {}.", port);
        return new NettyServer<>(group, sscType, port, CodecUtility.newJsonDecoder(), CodecUtility.newJsonEncoder());
    }

    @Override
    public IServer<JsonObject> newWebsocketJsonServer(int port) {
        LOG.info("Create new netty websocket json server on port {}", port);
        return new NettyServer<>(group, sscType, port,
                ch -> ch.pipeline()
                        .addLast(new HttpServerCodec())
                        .addLast(new HttpObjectAggregator(65536))
                        .addLast(WebSocketClientCompressionHandler.INSTANCE)
                        .addLast(new WebSocketServerProtocolHandler("/", null, true))
                        .addLast(CodecUtility.newWebsocketJsonCodec()));
    }

}
