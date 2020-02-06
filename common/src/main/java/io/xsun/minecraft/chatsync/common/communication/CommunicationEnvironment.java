package io.xsun.minecraft.chatsync.common.communication;

public interface CommunicationEnvironment {
    void init();

    void shutdown();

    ClientFactory getClientFactory();

    ServerFactory getServerFactory();
}
