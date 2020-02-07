package io.xsun.minecraft.chatsync.common.communication.insideprotocol.message;

import lombok.Data;

@Data
public class MessageSourceInfo {
    private SourceSide side;
    private String name;
    private String version;

    public enum SourceSide {
        MASTER, INSIDE_NODE
    }
}
