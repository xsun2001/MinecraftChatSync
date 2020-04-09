package io.xsun.minecraft.chatsync.common.communication.netty;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.xsun.minecraft.chatsync.common.communication.CloseHandler;
import io.xsun.minecraft.chatsync.common.communication.IChannel;
import io.xsun.minecraft.chatsync.common.logging.CSLogger;
import io.xsun.minecraft.chatsync.common.logging.LogManager;

import java.net.InetSocketAddress;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class NettyChannel implements IChannel {

    private final CSLogger log;
    private final SocketChannel nettyChannel;
    protected volatile Consumer<JsonObject> onMessage = message -> {
    };
    protected volatile Predicate<Throwable> onException = exception -> true;
    protected volatile CloseHandler onClose = () -> {
    };

    public NettyChannel(SocketChannel nettyChannel) {
        log = LogManager.getInstance().getLogger(NettyChannel.class);
        log.info("Creating new NettyChannel of {}", nettyChannel);
        nettyChannel.pipeline()
                .addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.debug("NettyChannel received a message [{}]", msg);
                        onMessage.accept((JsonObject) msg);
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
    public void setMessageHandler(Consumer<JsonObject> onMessage) {
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
    public void send(JsonObject message) {
        log.debug("NettyChannel is sending message [{}]", message);
        nettyChannel.writeAndFlush(message).syncUninterruptibly();
    }
}
