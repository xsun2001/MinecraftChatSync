package io.xsun.minecraft.chatsync.common.communication.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.xsun.minecraft.chatsync.common.communication.IServer;
import io.xsun.minecraft.chatsync.common.logging.CSLogger;
import io.xsun.minecraft.chatsync.common.logging.LogManager;

import java.net.InetSocketAddress;

final class NettyServerFactory {

    private final CSLogger log;
    private final EventLoopGroup group;
    private final Class<? extends ServerSocketChannel> sscType;

    public NettyServerFactory(EventLoopGroup group, Class<? extends ServerSocketChannel> sscType) {
        this.log = LogManager.getInstance().getLogger(NettyServerFactory.class);
        this.group = group;
        this.sscType = sscType;
    }

    public IServer newTcpJsonServer(InetSocketAddress bindAddress) {
        log.info("Create new netty tcp json server on {}.", bindAddress);
        return new NettyServer(group, sscType, bindAddress, CodecUtility.newJsonDecoder(), CodecUtility.newJsonEncoder());
    }

    public IServer newWebsocketJsonServer(InetSocketAddress bindAddress) {
        log.info("Create new netty websocket json server on {}", bindAddress);
        return new NettyServer(group, sscType, bindAddress,
                ch -> ch.pipeline()
                        .addLast(new HttpServerCodec())
                        .addLast(new HttpObjectAggregator(65536))
                        .addLast(WebSocketClientCompressionHandler.INSTANCE)
                        .addLast(new WebSocketServerProtocolHandler("/", null, true))
                        .addLast(CodecUtility.newWebsocketJsonCodec()));
    }

}
