package io.xsun.minecraft.chatsync.common.communication.netty;

import com.google.gson.JsonObject;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import io.xsun.minecraft.chatsync.common.communication.IServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServerBuilder {

    public static final NettyServerBuilder INSTANCE = new NettyServerBuilder();
    private static final Logger LOG = LoggerFactory.getLogger(NettyServerBuilder.class);

    static {
        InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
    }

    private final EventLoopGroup boss = new NioEventLoopGroup(), worker = new NioEventLoopGroup();

    public IServer<JsonObject> newNettyTcpJsonServer(int port) {
        LOG.debug("Creating new netty tcp json server on port {}.", port);
        return new NettyServer<>(boss, worker, port, CodecUtility.newJsonDecoder(), CodecUtility.newJsonEncoder());
    }

    public IServer<JsonObject> newNettyWebsocketJsonServer(int port) {
        LOG.debug("Creating new netty websocket json server on port {}", port);
        return new NettyServer<>(boss, worker, port,
                ch -> ch.pipeline()
                        .addLast(new HttpServerCodec())
                        .addLast(new HttpObjectAggregator(65536))
                        .addLast(WebSocketClientCompressionHandler.INSTANCE)
                        .addLast(new WebSocketServerProtocolHandler("/", null, true))
                        .addLast(CodecUtility.newWebsocketJsonCodec()));
    }

    public final void close() {
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }

}
