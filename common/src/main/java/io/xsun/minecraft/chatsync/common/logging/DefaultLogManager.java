package io.xsun.minecraft.chatsync.common.logging;

import java.util.logging.Logger;

final class DefaultLogManager extends LogManager {

    @Override
    public CSLogger getLogger(Class<?> clazz) {
        return new JULAdapter(Logger.getLogger(clazz.getName()));
    }
}