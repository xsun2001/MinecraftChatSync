package io.xsun.minecraft.chatsync.insidenode.forge.v1122;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.xsun.minecraft.chatsync.common.communication.CommunicationEnvironment;
import io.xsun.minecraft.chatsync.common.communication.netty.NettyEnvironment;
import io.xsun.minecraft.chatsync.common.logging.CSLogger;
import io.xsun.minecraft.chatsync.insidenode.common.IMinecraftServerApi;
import net.minecraft.network.NetworkSystem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.text.TextComponentString;

public class ForgeMinecraftServerApi implements IMinecraftServerApi {

    private final MinecraftServer mcServer;
    private final CSLogger log;

    public ForgeMinecraftServerApi(MinecraftServer mcServer, CSLogger log) {
        this.mcServer = mcServer;
        this.log = log;
    }

    @Override
    public CommunicationEnvironment getCommunicationEnvironment() {
        Class<? extends SocketChannel> scClass;
        Class<? extends ServerSocketChannel> sscClass;
        LazyLoadBase<? extends EventLoopGroup> lazyloadbase;

        if (Epoll.isAvailable() && mcServer.shouldUseNativeTransport()) {
            scClass = EpollSocketChannel.class;
            sscClass = EpollServerSocketChannel.class;
            lazyloadbase = NetworkSystem.SERVER_EPOLL_EVENTLOOP;
        } else {
            scClass = NioSocketChannel.class;
            sscClass = NioServerSocketChannel.class;
            lazyloadbase = NetworkSystem.SERVER_NIO_EVENTLOOP;
        }

        return NettyEnvironment.customEnv(lazyloadbase::getValue, scClass, sscClass);
    }

    @Override
    public boolean isRunning() {
        return mcServer.isServerRunning();
    }

    @Override
    public void broadcastMessage(String s) {
        mcServer.getPlayerList().sendMessage(new TextComponentString(s));
    }

    @Override
    public String getFormatHint(String s) {
        return null;
    }
}
