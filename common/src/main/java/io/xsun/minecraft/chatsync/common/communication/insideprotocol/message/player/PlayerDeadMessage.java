package io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.player;

import io.xsun.minecraft.chatsync.common.communication.MessageTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@MessageTypeName("player_dead")
@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerDeadMessage extends PlayerMessageBase {
    private String deadReason;
}
