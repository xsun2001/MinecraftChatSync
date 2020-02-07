package io.xsun.minecraft.chatsync.common.communication;

import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class AbstractChannel<MessageType> implements IChannel<MessageType> {
    protected volatile Consumer<MessageType> onMessage = message -> {
    };
    protected volatile Predicate<Throwable> onException = exception -> true;
    protected volatile CloseHandler onClose = () -> {
    };

    @Override
    public void setMessageHandler(Consumer<MessageType> onMessage) {
        this.onMessage = onMessage;
    }

    @Override
    public void setExceptionHandler(Predicate<Throwable> onException) {
        this.onException = onException;
    }

    @Override
    public void setCloseHandler(CloseHandler onClose) {
        this.onClose = onClose;
    }
}
