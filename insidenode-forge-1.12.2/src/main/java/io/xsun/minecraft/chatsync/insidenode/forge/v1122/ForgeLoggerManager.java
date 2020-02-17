package io.xsun.minecraft.chatsync.insidenode.forge.v1122;

import io.xsun.minecraft.chatsync.common.logging.CSLogger;
import io.xsun.minecraft.chatsync.common.logging.LogManager;
import org.apache.logging.log4j.Logger;

public class ForgeLoggerManager extends LogManager {

    private final CSLogger log;

    public ForgeLoggerManager(Logger modLogger) {
        log = new Log4j2CSLoggerAdapter(modLogger);
    }

    @Override
    public CSLogger getLogger(Class<?> aClass) {
        return log;
    }
}
