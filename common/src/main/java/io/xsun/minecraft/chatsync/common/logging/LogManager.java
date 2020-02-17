package io.xsun.minecraft.chatsync.common.logging;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class LogManager {
    private static Supplier<LogManager> factory;

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

    public abstract CSLogger getLogger(Class<?> clazz);

}
