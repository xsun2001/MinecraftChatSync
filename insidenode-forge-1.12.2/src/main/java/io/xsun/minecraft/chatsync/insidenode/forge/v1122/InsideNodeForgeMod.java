package io.xsun.minecraft.chatsync.insidenode.forge.v1122;

import io.xsun.minecraft.chatsync.common.logging.CSLogger;
import io.xsun.minecraft.chatsync.common.logging.LogManager;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = InsideNodeForgeMod.MODID, name = InsideNodeForgeMod.NAME, version = InsideNodeForgeMod.VERSION)
public class InsideNodeForgeMod {
    public static final String MODID = "chatsync-forge";
    public static final String NAME = "Chatsync Inside Node for Forge";
    public static final String VERSION = "1.0";

    private static CSLogger log;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LogManager.setLogManagerFactory(() -> new ForgeLoggerManager(event.getModLog()));
        log = LogManager.getInstance().getLogger(InsideNodeForgeMod.class);
        MinecraftForge.EVENT_BUS.register(this);
        PlayerList
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        log.info("Hello Minecraft");
    }

    @SubscribeEvent
    public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MODID)) {
            ConfigManager.sync(MODID, Config.Type.INSTANCE);
        }
    }

}
