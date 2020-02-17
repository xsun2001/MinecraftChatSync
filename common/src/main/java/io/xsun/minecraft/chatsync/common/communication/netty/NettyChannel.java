package io.xsun.minecraft.chatsync.common.communication.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.xsun.minecraft.chatsync.common.communication.AbstractChannel;
import io.xsun.minecraft.chatsync.common.communication.IChannel;
import io.xsun.minecraft.chatsync.common.logging.CSLogger;
import io.xsun.minecraft.chatsync.common.logging.LogManager;

import java.net.InetSocketAddress;

public class NettyChannel<MessageType> extends AbstractChannel<MessageType> implements IChannel<MessageType> {

    private final CSLogger log;
    private final SocketChannel nettyChannel;

    public NettyChannel(SocketChannel nettyChannel) {
        log = LogManager.getInstance().getLogger(NettyChannel.class);
        log.info("Creating new NettyChannel of {}", nettyChannel);
        nettyChannel.pipeline()
                .addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.debug("NettyChannel received a message [{}]", msg);
                        onMessage.accept((MessageType) msg);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        log.warn("Exception caught in NettyChannel", cause);
                        boolean shouldClose = onException.test(cause);
                        if (shouldClose) {
                            log.error("NettyChannel is closing because of an unexpected error");
                            NettyChannel.this.close();
                        }
                    }

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        log.info("Channel disconnected");
                        super.channelInactive(ctx);
                        onClose.onClose();
                    }
                });
        this.nettyChannel = nettyChannel;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return nettyChannel.localAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return nettyChannel.remoteAddress();
    }

    @Override
    public void close() {
        log.info("NettyChannel is closing");
        try {
            nettyChannel.close().sync();
        } catch (InterruptedException e) {
            log.warn("NettyChannel's closing process is interrupted", e);
        }
        log.info("NettyChannel is closed");
    }

    @Override
    public void send(MessageType message) {
        log.debug("NettyChannel is sending message [{}]", message);
        nettyChannel.writeAndFlush(message).syncUninterruptibly();
    }
}
