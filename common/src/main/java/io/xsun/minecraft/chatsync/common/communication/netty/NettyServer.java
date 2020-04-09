package io.xsun.minecraft.chatsync.common.communication.netty;

import com.google.gson.JsonObject;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.xsun.minecraft.chatsync.common.communication.IChannel;
import io.xsun.minecraft.chatsync.common.communication.IServer;
import io.xsun.minecraft.chatsync.common.logging.CSLogger;
import io.xsun.minecraft.chatsync.common.logging.LogManager;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

class NettyServer implements IServer {

    private final CSLogger log;
    private final Channel parentChannel;
    private final CopyOnWriteArrayList<IChannel> connectedChannels = new CopyOnWriteArrayList<>();
    private volatile Consumer<IChannel> onChannelConnected = ch -> {
    };
    private volatile Consumer<IChannel> onChannelDisconnected = ch -> {
    };

    protected NettyServer(EventLoopGroup group, Class<? extends ServerSocketChannel> sscType, InetSocketAddress bindAddress,
                          ByteToMessageDecoder decoder, MessageToByteEncoder<JsonObject> encoder) {
        this(group, sscType, bindAddress, ch -> ch.pipeline().addLast(decoder, encoder));
    }

    protected NettyServer(EventLoopGroup group, Class<? extends ServerSocketChannel> sscType,
                          InetSocketAddress bindAddress, Consumer<SocketChannel> preInit) {
        this.log = LogManager.getInstance().getLogger(NettyServer.class);
        log.info("NettyServer is creating on {}", bindAddress);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(sscType)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        final String remoteAddress = ch.remoteAddress().toString();
                        log.info("Accepting new connection from {}", remoteAddress);
                        preInit.accept(ch);
                        NettyChannel myChannel = new NettyChannel(ch);
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                log.info("Channel from {} disconnected", remoteAddress);
                                super.channelInactive(ctx);
                                connectedChannels.remove(myChannel);
                                onChannelDisconnected.accept(myChannel);
                            }
                        });
                        connectedChannels.add(myChannel);
                        onChannelConnected.accept(myChannel);
                        log.info("Channel from {} connected", remoteAddress);
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        parentChannel = bootstrap.bind(bindAddress).syncUninterruptibly().channel();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) parentChannel.localAddress();
    }

    @Override
    public void close() {
        log.info("NettyServer on port {} is closing", ((InetSocketAddress) parentChannel.localAddress()).getPort());
        try {
            connectedChannels.forEach(IChannel::close);
            parentChannel.close().sync();
        } catch (InterruptedException e) {
            log.warn("NettyServer's closing process is interrupted", e);
        }
        log.info("NettyServer is closed");
    }

    @Override
    public void setOnChannelConnected(Consumer<IChannel> onChannelConnected) {
        this.onChannelConnected = onChannelConnected;
    }

    @Override
    public void setOnChannelDisconnected(Consumer<IChannel> onChannelDisconnected) {
        this.onChannelDisconnected = onChannelDisconnected;
    }

    @Override
    public List<IChannel> getConnectedChannels() {
        return Collections.unmodifiableList(connectedChannels);
    }
}
