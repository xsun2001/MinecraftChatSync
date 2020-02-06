package io.xsun.minecraft.chatsync.common.communication.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.xsun.minecraft.chatsync.common.communication.CloseHandler;
import io.xsun.minecraft.chatsync.common.communication.IChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class NettyChannel<MessageType> implements IChannel<MessageType> {

    private static final Logger LOG = LoggerFactory.getLogger(NettyChannel.class);
    private final SocketChannel nettyChannel;
    private volatile Consumer<MessageType> onMessage = message -> {
    };
    private volatile Predicate<Throwable> onException = exception -> true;
    private volatile CloseHandler onClose = () -> {
    };

    public NettyChannel(SocketChannel nettyChannel) {
        LOG.info("Creating new NettyChannel of {}", nettyChannel);
        nettyChannel.pipeline()
                .addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        LOG.debug("NettyChannel received a message [{}]", msg);
                        onMessage.accept((MessageType) msg);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        LOG.warn("Exception caught in NettyChannel", cause);
                        boolean shouldClose = onException.test(cause);
                        if (shouldClose) {
                            LOG.error("NettyChannel is closing because of an unexpected error");
                            NettyChannel.this.close();
                        }
                    }

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        LOG.info("Channel disconnected");
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
        LOG.info("NettyChannel is closing");
        try {
            nettyChannel.close().sync();
        } catch (InterruptedException e) {
            LOG.warn("NettyChannel's closing process is interrupted", e);
        }
        LOG.info("NettyChannel is closed");
    }

    @Override
    public void send(MessageType message) {
        LOG.debug("NettyChannel is sending message [{}]", message);
        nettyChannel.writeAndFlush(message).syncUninterruptibly();
    }

    @Override
    public void setMessageHandler(Consumer<MessageType> onMessage) {
        this.onMessage = onMessage;
    }

    @Override
    public void setExceptionHandler(Predicate<Throwable> onException) {
        this.onException = onException;
    }

    @Override
    public void setCloseHandler(CloseHandler onClose) {
        this.onClose = onClose;
    }
}
