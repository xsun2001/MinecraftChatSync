package io.xsun.minecraft.chatsync.insidenode.forge.v1122;

import io.xsun.minecraft.chatsync.common.LogManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(modid = InsideNodeForgeMod.MODID, name = InsideNodeForgeMod.NAME, version = InsideNodeForgeMod.VERSION)
public class InsideNodeForgeMod {
    public static final String MODID = "chatsync-forge";
    public static final String NAME = "Chatsync Inside Node for Forge";
    public static final String VERSION = "1.0";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        LogManager.setLogManagerFactory(Log4jLogManagerAdapter::new);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        org.slf4j.Logger sl4jLogger = LogManager.getInstance().getLogger(InsideNodeForgeMod.class);
        sl4jLogger.info("TESTTESTTEST");
    }

    private class Log4jLogManagerAdapter extends LogManager {

        @Override
        public org.slf4j.Logger getLogger(Class<?> aClass) {
            return LoggerFactory.getLogger(MODID);
        }
    }
}
