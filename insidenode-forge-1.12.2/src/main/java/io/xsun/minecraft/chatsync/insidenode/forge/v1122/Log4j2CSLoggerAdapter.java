package io.xsun.minecraft.chatsync.insidenode.forge.v1122;

import io.xsun.minecraft.chatsync.common.logging.CSLogger;
import org.apache.logging.log4j.Logger;

public final class Log4j2CSLoggerAdapter implements CSLogger {

    private final Logger log4j2Impl;

    public Log4j2CSLoggerAdapter(Logger log4j2Impl) {
        this.log4j2Impl = log4j2Impl;
    }

    @Override
    public void trace(String msg) {
        log4j2Impl.trace(msg);
    }

    @Override
    public void debug(String msg) {
        log4j2Impl.debug(msg);
    }

    @Override
    public void info(String msg) {
        log4j2Impl.info(msg);
    }

    @Override
    public void warn(String msg) {
        log4j2Impl.warn(msg);
    }

    @Override
    public void warn(String msg, Throwable error) {
        log4j2Impl.warn(msg, error);
    }

    @Override
    public void error(String msg) {
        log4j2Impl.error(msg);
    }

    @Override
    public void error(String msg, Throwable error) {
        log4j2Impl.error(msg, error);
    }
}