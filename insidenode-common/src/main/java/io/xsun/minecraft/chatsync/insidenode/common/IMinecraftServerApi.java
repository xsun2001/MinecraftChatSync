package io.xsun.minecraft.chatsync.insidenode.common;

import io.xsun.minecraft.chatsync.common.communication.CommunicationEnvironment;

public interface IMinecraftServerApi {
    static String format(String formatHint, String... args) {
        StringBuilder builder = new StringBuilder();
        char[] chars = new char[formatHint.length()];
        formatHint.getChars(0, formatHint.length(), chars, 0);
        boolean good = true;
        int last = 0, argIndex = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '{') {
                if (chars[i + 1] == '}') {
                    if (argIndex >= args.length) {
                        good = false;
                        break;
                    }
                    builder.append(chars, last, i);
                    builder.append(args[argIndex++]);
                    last = i + 2;
                    ++i;
                }
            }
        }
        return good ? builder.toString() : "Format Error";
    }

    CommunicationEnvironment getCommunicationEnvironment();

    boolean isRunning();

    void broadcastMessage(String message);

    String getFormatHint(String messageType);

    default void broadcastFormattedMessage(String messageType, String... args) {
        broadcastMessage(format(getFormatHint(messageType), args));
    }
}
