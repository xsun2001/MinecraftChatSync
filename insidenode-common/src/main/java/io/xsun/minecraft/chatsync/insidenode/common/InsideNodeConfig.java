package io.xsun.minecraft.chatsync.insidenode.common;

import lombok.Data;

@Data
public final class InsideNodeConfig {
    private String masterHost;
    private int masterPort;
    private boolean autoReconnect;
    private int reconnectInterval;
}
