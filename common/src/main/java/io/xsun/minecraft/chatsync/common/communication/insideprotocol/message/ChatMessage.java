package io.xsun.minecraft.chatsync.common.communication.insideprotocol.message;

import io.xsun.minecraft.chatsync.common.communication.MessageTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@MessageTypeName("chat")
@Data
@EqualsAndHashCode(callSuper = true)
public class ChatMessage extends MessageBase {
    private String senderName;
    private String chatMessage;
}
