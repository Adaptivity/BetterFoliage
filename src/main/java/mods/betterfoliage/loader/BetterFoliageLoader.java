package mods.betterfoliage.loader;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.TransformerExclusions({"mods.betterfoliage.loader"})
public class BetterFoliageLoader implements IFMLLoadingPlugin {

	public String[] getASMTransformerClass() {
		return new String[] {"mods.betterfoliage.loader.BetterFoliageTransformer"};
	}

	public String getModContainerClass() {
		return null;
	}

	public String getSetupClass() {
		return null;
	}

	public void injectData(Map<String, Object> data) {
	}

	public String getAccessTransformerClass() {
		return null;
	}

}
