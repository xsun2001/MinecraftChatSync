package io.xsun.minecraft.chatsync.common.communication.insideprotocol.message;

import io.xsun.minecraft.chatsync.common.communication.MessageTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@MessageTypeName("chat")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true, includeFieldNames=true)
public class ChatMessage extends MessageBase {
    private String senderName;
    private String chatMessage;
}
