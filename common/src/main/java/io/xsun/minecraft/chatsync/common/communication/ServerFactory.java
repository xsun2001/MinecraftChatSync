package io.xsun.minecraft.chatsync.common.communication;

import com.google.gson.JsonObject;
import io.xsun.minecraft.chatsync.common.communication.netty.NettyServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ServerFactory.class);

    public static IServer<JsonObject> newTcpJsonServer(int port) {
        return NettyServerBuilder.INSTANCE.newNettyTcpJsonServer(port);
    }

}
