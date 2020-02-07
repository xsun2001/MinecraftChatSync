package io.xsun.minecraft.chatsync.common.communication.insideprotocol.message;

import lombok.Data;

@Data
public abstract class MessageBase {
    private int protocolVersion;
    private String type;
    private MessageSourceInfo source;
}
