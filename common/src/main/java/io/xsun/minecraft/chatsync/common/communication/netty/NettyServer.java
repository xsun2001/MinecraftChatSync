package io.xsun.minecraft.chatsync.common.communication.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.xsun.minecraft.chatsync.common.communication.IChannel;
import io.xsun.minecraft.chatsync.common.communication.IServer;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

class NettyServer<MessageType> implements IServer<MessageType> {

    private final Channel parentChannel;
    private final CopyOnWriteArrayList<IChannel<MessageType>> connectedChannels = new CopyOnWriteArrayList<>();
    private volatile Consumer<IChannel<MessageType>> onChannelConnected, onChannelDisconnected;

    protected NettyServer(EventLoopGroup boss, EventLoopGroup worker, int port,
                          ByteToMessageDecoder decoder, MessageToByteEncoder<MessageType> encoder) {
        this(boss, worker, port, ch -> ch.pipeline().addLast(decoder, encoder));
    }

    protected NettyServer(EventLoopGroup boss, EventLoopGroup worker, int port,
                          Consumer<SocketChannel> preInit) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        preInit.accept(ch);
                        NettyChannel<MessageType> myChannel = new NettyChannel<>(ch, true);
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                super.channelInactive(ctx);
                                connectedChannels.remove(myChannel);
                                onChannelDisconnected.accept(myChannel);
                            }
                        });
                        connectedChannels.add(myChannel);
                        onChannelConnected.accept(myChannel);
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        parentChannel = bootstrap.bind(port).syncUninterruptibly().channel();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) parentChannel.localAddress();
    }

    @Override
    public void close() {
        try {
            connectedChannels.forEach(IChannel::close);
            parentChannel.close().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOnChannelConnected(Consumer<IChannel<MessageType>> onChannelConnected) {
        this.onChannelConnected = onChannelConnected;
    }

    @Override
    public void setOnChannelDisconnected(Consumer<IChannel<MessageType>> onChannelDisconnected) {
        this.onChannelDisconnected = onChannelDisconnected;
    }

    @Override
    public List<IChannel<MessageType>> getConnectedChannels() {
        return Collections.unmodifiableList(connectedChannels);
    }
}
