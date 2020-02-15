package io.xsun.minecraft.chatsync.insidenode.forge.v1122;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = InsideNodeForgeMod.MODID, name = InsideNodeForgeMod.NAME, version = InsideNodeForgeMod.VERSION)
public class InsideNodeForgeMod {
    public static final String MODID = "chatsync-forge";
    public static final String NAME = "Chatsync Inside Node for Forge";
    public static final String VERSION = "1.0";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }
}
