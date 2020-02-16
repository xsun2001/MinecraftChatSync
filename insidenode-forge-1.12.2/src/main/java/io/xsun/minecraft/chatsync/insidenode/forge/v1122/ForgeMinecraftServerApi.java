package io.xsun.minecraft.chatsync.insidenode.forge.v1122;

import io.xsun.minecraft.chatsync.common.communication.CommunicationEnvironment;
import io.xsun.minecraft.chatsync.insidenode.common.IMinecraftServerApi;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;

public class ForgeMinecraftServerApi implements IMinecraftServerApi {

//    private final MinecraftServer mcServer;

    @Override
    public CommunicationEnvironment getCommunicationEnvironment() {
        return null;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void broadcastMessage(String s) {

    }

    @Override
    public String getFormatHint(String s) {
        return null;
    }
}
