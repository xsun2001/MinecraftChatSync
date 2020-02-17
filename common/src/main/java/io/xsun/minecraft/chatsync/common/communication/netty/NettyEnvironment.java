package io.xsun.minecraft.chatsync.common.communication.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.xsun.minecraft.chatsync.common.communication.ClientFactory;
import io.xsun.minecraft.chatsync.common.communication.CommunicationEnvironment;
import io.xsun.minecraft.chatsync.common.communication.ServerFactory;
import io.xsun.minecraft.chatsync.common.logging.CSLogger;
import io.xsun.minecraft.chatsync.common.logging.LogManager;

import java.util.Objects;
import java.util.function.Supplier;

public final class NettyEnvironment implements CommunicationEnvironment {

    private final CSLogger log;
    private final Supplier<EventLoopGroup> groupConstructor;
    private final Class<? extends SocketChannel> scType;
    private final Class<? extends ServerSocketChannel> sscType;
    private EventLoopGroup group;

    private NettyEnvironment(Supplier<EventLoopGroup> groupConstructor,
                             Class<? extends SocketChannel> scType,
                             Class<? extends ServerSocketChannel> sscType) {
        this.log = LogManager.getInstance().getLogger(NettyEnvironment.class);
        this.groupConstructor = groupConstructor;
        this.scType = scType;
        this.sscType = sscType;

        log.info("NettyEnvironment is created with [{},{},{}]",
                groupConstructor.getClass().getSimpleName(),
                scType.getSimpleName(), sscType.getSimpleName());
    }

    public static NettyEnvironment defaultEnv() {
        if (Epoll.isAvailable()) {
            return new NettyEnvironment(EpollEventLoopGroup::new, EpollSocketChannel.class, EpollServerSocketChannel.class);
        } else if (KQueue.isAvailable()) {
            return new NettyEnvironment(KQueueEventLoopGroup::new, KQueueSocketChannel.class, KQueueServerSocketChannel.class);
        } else {
            return new NettyEnvironment(NioEventLoopGroup::new, NioSocketChannel.class, NioServerSocketChannel.class);
        }
    }

    public static NettyEnvironment customEnv(Supplier<EventLoopGroup> groupConstructor,
                                             Class<? extends SocketChannel> scType,
                                             Class<? extends ServerSocketChannel> sscType) {
        return new NettyEnvironment(groupConstructor, scType, sscType);
    }

    @Override
    public void init() {
        log.info("NettyEnvironment is initializing");
        group = groupConstructor.get();
        log.info("NettyEnvironment is using {}", group.getClass().getSimpleName());
    }

    @Override
    public void shutdown() {
        log.info("NettyEnvironment is closing");
        group.shutdownGracefully();
        group = null;
    }

    private void checkInit() {
        Objects.requireNonNull(group, "Environment isn't initialized");
    }

    @Override
    public ClientFactory getClientFactory() {
        log.info("Creating new NettyClientFactory");
        checkInit();
        return new NettyClientFactory(group, scType);
    }

    @Override
    public ServerFactory getServerFactory() {
        log.info("Creating new NettyServerFactory");
        checkInit();
        return new NettyServerFactory(group, sscType);
    }
}
