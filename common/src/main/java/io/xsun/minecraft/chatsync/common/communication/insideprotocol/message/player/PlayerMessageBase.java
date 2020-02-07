package io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.player;

import io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.MessageBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true, includeFieldNames=true)
public abstract class PlayerMessageBase extends MessageBase {
    private String playerName;
}
