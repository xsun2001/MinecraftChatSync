package io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.player;

import io.xsun.minecraft.chatsync.common.communication.MessageTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@MessageTypeName("player_dead")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, includeFieldNames = true)
public class PlayerDeadMessage extends PlayerMessageBase {
    private String deadReason;
}
