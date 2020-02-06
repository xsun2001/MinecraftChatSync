package io.xsun.minecraft.chatsync.common.communication.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.xsun.minecraft.chatsync.common.communication.ClientFactory;
import io.xsun.minecraft.chatsync.common.communication.CommunicationEnvironment;
import io.xsun.minecraft.chatsync.common.communication.ServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class NettyEnvironment implements CommunicationEnvironment {

    private final static Logger LOG = LoggerFactory.getLogger(NettyEnvironment.class);
    private EventLoopGroup boss, worker;

    public NettyEnvironment() {
        LOG.info("Netty environment is used");
    }

    @Override
    public void init() {
        LOG.info("NettyEnvironment is initializing");
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
    }

    @Override
    public void shutdown() {
        LOG.info("NettyEnvironment is closing");
        boss.shutdownGracefully();
        worker.shutdownGracefully();
        boss = null;
        worker = null;
    }

    private void checkInit() {
        Objects.requireNonNull(boss, "Environment isn't initialized");
        Objects.requireNonNull(worker, "Environment isn't initialized");
    }

    @Override
    public ClientFactory getClientFactory() {
        LOG.info("Creating new NettyClientFactory");
        checkInit();
        return new NettyClientFactory(boss, worker);
    }

    @Override
    public ServerFactory getServerFactory() {
        LOG.info("Creating new NettyServerFactory");
        checkInit();
        return new NettyServerFactory(boss, worker);
    }
}
