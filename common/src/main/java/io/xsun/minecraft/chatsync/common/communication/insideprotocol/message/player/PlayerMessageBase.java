package io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.player;

import io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.MessageBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class PlayerMessageBase extends MessageBase {
    private String playerName;
}
