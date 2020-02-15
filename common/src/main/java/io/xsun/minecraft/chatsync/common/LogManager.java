package io.xsun.minecraft.chatsync.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class LogManager {
    private static Supplier<LogManager> factory = Slf4jLogManagerDelegate::new;

    private static LogManager INSTANCE;

    public static void setLogManagerFactory(Supplier<LogManager> factory) {
        LogManager.factory = factory;
    }

    public synchronized static LogManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = factory.get();
        }
        return Objects.requireNonNull(INSTANCE);
    }

    public abstract Logger getLogger(Class<?> clazz);

    private static class Slf4jLogManagerDelegate extends LogManager {
        @Override
        public Logger getLogger(Class<?> clazz) {
            return LoggerFactory.getLogger(clazz);
        }
    }
}
