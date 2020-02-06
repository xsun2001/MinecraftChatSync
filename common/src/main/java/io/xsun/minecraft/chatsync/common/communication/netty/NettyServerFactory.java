package io.xsun.minecraft.chatsync.common.communication.netty;

import com.google.gson.JsonObject;
import io.netty.channel.EventLoopGroup;
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

    private final EventLoopGroup boss, worker;

    public NettyServerFactory(EventLoopGroup boss, EventLoopGroup worker) {
        this.boss = boss;
        this.worker = worker;
    }

    @Override
    public IServer<JsonObject> newTcpJsonServer(int port) {
        LOG.info("Create new netty tcp json server on port {}.", port);
        return new NettyServer<>(boss, worker, port, CodecUtility.newJsonDecoder(), CodecUtility.newJsonEncoder());
    }

    @Override
    public IServer<JsonObject> newWebsocketJsonServer(int port) {
        LOG.info("Create new netty websocket json server on port {}", port);
        return new NettyServer<>(boss, worker, port,
                ch -> ch.pipeline()
                        .addLast(new HttpServerCodec())
                        .addLast(new HttpObjectAggregator(65536))
                        .addLast(WebSocketClientCompressionHandler.INSTANCE)
                        .addLast(new WebSocketServerProtocolHandler("/", null, true))
                        .addLast(CodecUtility.newWebsocketJsonCodec()));
    }

}
