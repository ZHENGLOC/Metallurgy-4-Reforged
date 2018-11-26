 package it.hurts.metallurgy_reforged;

 import it.hurts.metallurgy_reforged.fluid.ModFluids;
 import it.hurts.metallurgy_reforged.gui.GuiHandler;
 import it.hurts.metallurgy_reforged.material.ModMetals;
 import it.hurts.metallurgy_reforged.proxy.CommonProxy;
 import it.hurts.metallurgy_reforged.util.handler.TileEntityHandler;
 import it.hurts.metallurgy_reforged.util.recipe.ModRecipes;
 import it.hurts.metallurgy_reforged.world.ModWorldGen;
 import net.minecraftforge.fluids.FluidRegistry;
 import net.minecraftforge.fml.common.Mod;
 import net.minecraftforge.fml.common.SidedProxy;
 import net.minecraftforge.fml.common.event.FMLInitializationEvent;
 import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
 import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
 import net.minecraftforge.fml.common.network.NetworkRegistry;
 import net.minecraftforge.fml.common.registry.GameRegistry;
 import org.apache.logging.log4j.Logger;

 /***************************
*
* Author : ItHurtsLikeHell
* Project: Metallurgy-5
* Date   : 28 ago 2018
* Time   : 18:24:07
*
***************************/

@Mod(modid = Metallurgy.MODID, name = Metallurgy.NAME, version = Metallurgy.VERSION, dependencies = "required-after:forge@[11.16.0.2768,)", acceptedMinecraftVersions = "[1.12]")
public class Metallurgy {

	public static final String MODID = "m5";
	public static final String NAME = "Metallurgy 4: Reforged";
	public static final String VERSION = "0.0.1";

	public static Logger logger;

	@Mod.Instance(MODID)
	public static Metallurgy instance;

	@SidedProxy(serverSide = "it.hurts.metallurgy_reforged.proxy.CommonProxy", clientSide = "it.hurts.metallurgy_reforged.proxy.ClientProxy")
	public static CommonProxy proxy;

	static {
		FluidRegistry.enableUniversalBucket();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger =  event.getModLog();
		System.out.println(NAME + " is loading!");
		ModMetals.registerFluids();
		ModFluids.registerFluids();
		GameRegistry.registerWorldGenerator(new ModWorldGen(),3);
		TileEntityHandler.registerTileEntities();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		ModRecipes.init();
	}


	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}
}