package io.xsun.minecraft.chatsync.common.communication.insideprotocol.message.player;

import io.xsun.minecraft.chatsync.common.communication.MessageTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@MessageTypeName("player_enter_server")
@ToString(callSuper=true, includeFieldNames=true)
public class PlayerEnterServerMessage extends PlayerMessageBase {
}