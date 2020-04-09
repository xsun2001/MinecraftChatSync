package io.xsun.minecraft.chatsync.common.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

final class JULAdapter implements CSLogger {

    private final Logger logger;

    public JULAdapter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void trace(String msg) {
        logger.finer(msg);
    }

    @Override
    public void debug(String msg) {
        logger.fine(msg);
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void warn(String msg) {
        logger.warning(msg);
    }

    @Override
    public void warn(String msg, Throwable error) {
        logger.log(Level.WARNING, msg, error);
    }

    @Override
    public void error(String msg) {
        logger.severe(msg);
    }

    @Override
    public void error(String msg, Throwable error) {
        logger.log(Level.SEVERE, msg, error);
    }
}
