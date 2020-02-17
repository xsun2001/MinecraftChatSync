package io.xsun.minecraft.chatsync.insidenode.common;

import io.xsun.minecraft.chatsync.common.communication.CommunicationEnvironment;

import static io.xsun.minecraft.chatsync.common.FormatUtil.format;

public interface IMinecraftServerApi {

    CommunicationEnvironment getCommunicationEnvironment();

    boolean isRunning();

    void broadcastMessage(String message);

    String getFormatHint(String messageType);

    default void broadcastFormattedMessage(String messageType, String... args) {
        broadcastMessage(format(getFormatHint(messageType), args));
    }
}
