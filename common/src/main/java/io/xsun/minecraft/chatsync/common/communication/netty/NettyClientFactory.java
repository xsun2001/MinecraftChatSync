package io.xsun.minecraft.chatsync.common.communication.netty;

import com.google.gson.JsonObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.xsun.minecraft.chatsync.common.communication.ClientFactory;
import io.xsun.minecraft.chatsync.common.communication.IChannel;
import io.xsun.minecraft.chatsync.common.logging.CSLogger;
import io.xsun.minecraft.chatsync.common.logging.LogManager;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

final class NettyClientFactory implements ClientFactory {

    private final CSLogger log;

    private final EventLoopGroup worker;
    private final Class<? extends SocketChannel> scType;

    NettyClientFactory(EventLoopGroup worker, Class<? extends SocketChannel> scType) {
        this.log = LogManager.getInstance().getLogger(NettyClientFactory.class);
        this.worker = worker;
        this.scType = scType;
    }

    private Bootstrap createBootstrap(ChannelInitializer<SocketChannel> initializer) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker)
                .channel(scType)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(initializer);
        return bootstrap;
    }

    @Override
    public IChannel<JsonObject> connectToTcpJsonServer(InetSocketAddress address) {
        log.info("Try to connect to {} and create tcp json channel", address);
        try {
            Bootstrap bootstrap = createBootstrap(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(CodecUtility.newJsonDecoder(), CodecUtility.newJsonEncoder());
                }
            });
            SocketChannel ch = (SocketChannel) bootstrap.connect(address).sync().channel();
            log.info("Channel connected");
            return new NettyChannel<>(ch);
        } catch (InterruptedException e) {
            log.error("Channel connection is interrupted", e);
        }
        return null;
    }

    @Override
    public IChannel<JsonObject> connectToWebsocketJsonServer(InetSocketAddress address) {
        log.info("Try to connect to {} and create websocket json channel", address);
        try {
            final WebSocketClientProtocolHandler wsProtocolHandler = new WebSocketClientProtocolHandler(
                    WebSocketClientHandshakerFactory.newHandshaker(
                            new URI("ws://" + address.getHostName() + ":" + address.getPort()),
                            WebSocketVersion.V13,
                            null,
                            true,
                            new DefaultHttpHeaders()
                    )
            );
            final WebsocketClientHandshakeHandler wsHandshakeHandler = new WebsocketClientHandshakeHandler();
            Bootstrap bootstrap = createBootstrap(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline()
                            .addLast(new HttpClientCodec())
                            .addLast(new HttpObjectAggregator(65536))
                            .addLast(WebSocketClientCompressionHandler.INSTANCE)
                            .addLast(wsProtocolHandler)
                            .addLast(wsHandshakeHandler)
                            .addLast(CodecUtility.newWebsocketJsonCodec());
                }
            });
            SocketChannel ch = (SocketChannel) bootstrap.connect(address).sync().channel();
            wsHandshakeHandler.getHandshakeFuture().sync();
            log.info("Channel connected");
            return new NettyChannel<>(ch);
        } catch (InterruptedException e) {
            log.error("Channel connection is interrupted", e);
        } catch (URISyntaxException e) {
            log.error("Wrong URI syntax", e);
        }
        return null;
    }

    public static class WebsocketClientHandshakeHandler extends ChannelInboundHandlerAdapter {
        private ChannelPromise handshakeFuture;

        public ChannelPromise getHandshakeFuture() {
            return handshakeFuture;
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            super.userEventTriggered(ctx, evt);
            if (evt == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE) {
                handshakeFuture.setSuccess();
                ctx.pipeline().remove(this);
            }
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            super.handlerAdded(ctx);
            handshakeFuture = ctx.newPromise();
        }
    }
}
