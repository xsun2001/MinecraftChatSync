package io.xsun.minecraft.chatsync.common.communication.insideprotocol.message;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class MessageBase {
    private String type;
    private MessageSourceInfo source;
}
