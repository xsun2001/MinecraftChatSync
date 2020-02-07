package io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.player;

import io.xsun.minecraft.chatsync.common.communication.MessageTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@MessageTypeName("player_exit_server")
@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerExitServerMessage extends PlayerMessageBase {
    private String exitReason;
}
