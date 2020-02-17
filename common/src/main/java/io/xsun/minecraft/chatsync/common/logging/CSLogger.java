package io.xsun.minecraft.chatsync.common.logging;

import io.xsun.minecraft.chatsync.common.FormatUtil;

// CSLogger stands for ChatSync Logger
public interface CSLogger {

    void trace(String msg);

    void debug(String msg);

    void info(String msg);

    void warn(String msg);

    void warn(String msg, Throwable error);

    void error(String msg);

    void error(String msg, Throwable error);

    default void trace(String format, Object... args) {
        trace(FormatUtil.format(format, args));
    }

    default void debug(String format, Object... args) {
        debug(FormatUtil.format(format, args));
    }

    default void info(String format, Object... args) {
        info(FormatUtil.format(format, args));
    }

    default void warn(String format, Object... args) {
        warn(FormatUtil.format(format, args));
    }

    default void error(String format, Object... args) {
        error(FormatUtil.format(format, args));
    }

}
