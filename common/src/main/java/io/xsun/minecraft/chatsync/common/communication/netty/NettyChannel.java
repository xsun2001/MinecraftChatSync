package io.xsun.minecraft.chatsync.common.communication.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.xsun.minecraft.chatsync.common.communication.CloseReason;
import io.xsun.minecraft.chatsync.common.communication.IChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

public class NettyChannel<MessageType> implements IChannel<MessageType> {

    private static final Logger LOG = LoggerFactory.getLogger(NettyChannel.class);
    private final SocketChannel nettyChannel;
    private volatile Consumer<MessageType> onMessage = message -> LOG.debug(message.toString());
    private volatile Consumer<Throwable> onException = exception -> LOG.error("Error", exception);
    private volatile Consumer<CloseReason> onClose = closeReason -> LOG.debug("Channel is closed for reason {}", closeReason);

    public NettyChannel(SocketChannel nettyChannel, boolean isServerSide) {
        nettyChannel.pipeline()
                .addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        onMessage.accept((MessageType) msg);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        onException.accept(cause);
                    }

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        super.channelInactive(ctx);
                        onClose.accept(isServerSide ? CloseReason.CLIENT : CloseReason.SERVER);
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
    public boolean isReady() {
        return nettyChannel.isActive();
    }

    @Override
    public void close() {
        try {
            nettyChannel.close().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(MessageType message) {
        nettyChannel.writeAndFlush(message).syncUninterruptibly();
    }

    @Override
    public void setMessageHandler(Consumer<MessageType> onMessage) {
        this.onMessage = onMessage;
    }

    @Override
    public void setExceptionHandler(Consumer<Throwable> onException) {
        this.onException = onException;
    }

    @Override
    public void setCloseHandler(Consumer<CloseReason> onClose) {
        this.onClose = onClose;
    }
}
