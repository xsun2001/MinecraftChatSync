package io.xsun.minecraft.chatsync.common.communication.netty;

import com.google.gson.JsonObject;
import io.netty.channel.EventLoopGroup;
import io.xsun.minecraft.chatsync.common.communication.ClientFactory;
import io.xsun.minecraft.chatsync.common.communication.IChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

final class NettyClientFactory implements ClientFactory {

    private static final Logger LOG = LoggerFactory.getLogger(NettyClientFactory.class);

    private final EventLoopGroup boss, worker;

    NettyClientFactory(EventLoopGroup boss, EventLoopGroup worker) {
        this.boss = boss;
        this.worker = worker;
    }

    @Override
    public IChannel<JsonObject> connectToTcpJsonServer(InetSocketAddress address) {
        return null;
    }

    @Override
    public IChannel<JsonObject> connectToWebsocketJsonServer(InetSocketAddress address) {
        return null;
    }
}
