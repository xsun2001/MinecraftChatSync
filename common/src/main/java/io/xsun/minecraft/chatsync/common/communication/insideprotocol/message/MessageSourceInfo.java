package io.xsun.minecraft.chatsync.common.communication.insideprotocol.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageSourceInfo {
    private SourceSide side;
    private String name;
    private String version;

    public enum SourceSide {
        MASTER, INSIDE_NODE
    }
}
