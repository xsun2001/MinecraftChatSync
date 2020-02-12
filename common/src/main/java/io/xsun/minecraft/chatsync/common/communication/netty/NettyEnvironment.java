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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Supplier;

public final class NettyEnvironment implements CommunicationEnvironment {

    private final static Logger LOG = LoggerFactory.getLogger(NettyEnvironment.class);
    private final Supplier<EventLoopGroup> groupConstructor;
    private final Class<? extends SocketChannel> scType;
    private final Class<? extends ServerSocketChannel> sscType;
    private EventLoopGroup group;

    private NettyEnvironment(Supplier<EventLoopGroup> groupConstructor,
                             Class<? extends SocketChannel> scType,
                             Class<? extends ServerSocketChannel> sscType) {
        this.groupConstructor = groupConstructor;
        this.scType = scType;
        this.sscType = sscType;
    }

    public static NettyEnvironment defaultEnv() {
        LOG.info("Use default netty environment");
        if (Epoll.isAvailable()) {
            LOG.info("Use Epoll");
            return new NettyEnvironment(EpollEventLoopGroup::new, EpollSocketChannel.class, EpollServerSocketChannel.class);
        } else if (KQueue.isAvailable()) {
            LOG.info("Use KQueue");
            return new NettyEnvironment(KQueueEventLoopGroup::new, KQueueSocketChannel.class, KQueueServerSocketChannel.class);
        } else {
            LOG.info("Use Java NIO");
            return new NettyEnvironment(NioEventLoopGroup::new, NioSocketChannel.class, NioServerSocketChannel.class);
        }
    }

    public static NettyEnvironment customEnv(Supplier<EventLoopGroup> groupConstructor,
                                             Class<? extends SocketChannel> scType,
                                             Class<? extends ServerSocketChannel> sscType) {
        LOG.info("Use custom netty environment");
        return new NettyEnvironment(groupConstructor, scType, sscType);
    }

    @Override
    public void init() {
        LOG.info("NettyEnvironment is initializing");
        group = groupConstructor.get();
        LOG.info("NettyEnvironment is using {}", group.getClass().getSimpleName());
    }

    @Override
    public void shutdown() {
        LOG.info("NettyEnvironment is closing");
        group.shutdownGracefully();
        group = null;
    }

    private void checkInit() {
        Objects.requireNonNull(group, "Environment isn't initialized");
    }

    @Override
    public ClientFactory getClientFactory() {
        LOG.info("Creating new NettyClientFactory");
        checkInit();
        return new NettyClientFactory(group, scType);
    }

    @Override
    public ServerFactory getServerFactory() {
        LOG.info("Creating new NettyServerFactory");
        checkInit();
        return new NettyServerFactory(group, sscType);
    }
}
