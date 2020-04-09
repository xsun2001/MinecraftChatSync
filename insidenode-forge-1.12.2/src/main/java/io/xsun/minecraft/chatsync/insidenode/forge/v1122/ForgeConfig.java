package io.xsun.minecraft.chatsync.insidenode.forge.v1122;

import io.xsun.minecraft.chatsync.insidenode.common.InsideNodeConfig;
import net.minecraftforge.common.config.Config;

@Config(modid = InsideNodeForgeMod.MODID, category = "")
public class ForgeConfig {

    public static InsideNodeConfigAdaptor common = new InsideNodeConfigAdaptor();

    public static class InsideNodeConfigAdaptor {
        public String masterHost = "127.0.0.1";
        public int masterPort = 23333;
        public boolean autoReconnect = true;
        public int reconnectInterval = 10;

        public InsideNodeConfig getAsConfig() {
            return new InsideNodeConfig(masterHost, masterPort, autoReconnect, reconnectInterval);
        }
    }
}
