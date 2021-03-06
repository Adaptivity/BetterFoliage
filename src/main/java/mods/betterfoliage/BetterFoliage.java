package mods.betterfoliage;

import java.io.File;
import java.util.Map;

import mods.betterfoliage.client.BetterFoliageClient;
import mods.betterfoliage.common.config.BetterFoliageConfig;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;

@Mod(name=BetterFoliage.MOD_NAME, modid=BetterFoliage.MOD_ID, acceptedMinecraftVersions=BetterFoliage.MC_VERSIONS, guiFactory=BetterFoliage.GUI_FACTORY)
public class BetterFoliage {

	public static final String MOD_ID = "BetterFoliage";
	public static final String MOD_NAME = "Better Foliage";
	public static final String MC_VERSIONS = "[1.7.2,1.7.10]";
	public static final String GUI_FACTORY = "mods.betterfoliage.client.gui.ConfigGuiFactory";
	
	@Mod.Instance
	public static BetterFoliage instance;
	
	public static BetterFoliageConfig config = new BetterFoliageConfig();
	
	public static Logger log;
	
	public static File configDir;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)  {
		log = event.getModLog();
		if (event.getSide() == Side.CLIENT) {
			configDir = new File(event.getModConfigurationDirectory(), MOD_ID);
			configDir.mkdir();
			config.load(new File(configDir, "betterfoliage.cfg"));
			BetterFoliageClient.preInit();
		}
	}
	
	@NetworkCheckHandler
	public boolean checkVersion(Map<String, String> mods, Side side) {
		return true;
	}
}
