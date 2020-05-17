package cavern.world;

import javax.annotation.Nullable;

import com.google.common.base.Strings;

import cavern.api.CavernAPI;
import cavern.config.AquaCavernConfig;
import cavern.config.CavelandConfig;
import cavern.config.CaveniaConfig;
import cavern.config.CavernConfig;
import cavern.config.HugeCavernConfig;
import cavern.config.MirageWorldsConfig;
import cavern.core.Cavern;
import cavern.item.CaveItems;
import cavern.world.mirage.WorldProviderCaveland;
import cavern.world.mirage.WorldProviderCavenia;
import cavern.world.mirage.WorldProviderCrownCliffs;
import cavern.world.mirage.WorldProviderDarkForest;
import cavern.world.mirage.WorldProviderFrostMountains;
import cavern.world.mirage.WorldProviderSkyland;
import cavern.world.mirage.WorldProviderVoid;
import cavern.world.mirage.WorldProviderWideDesert;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

public class CaveDimensions
{
	public static DimensionType CAVERN;
	public static DimensionType HUGE_CAVERN;
	public static DimensionType AQUA_CAVERN;

	public static DimensionType CAVELAND;
	public static DimensionType CAVENIA;
	public static DimensionType FROST_MOUNTAINS;
	public static DimensionType WIDE_DESERT;
	public static DimensionType THE_VOID;
	public static DimensionType DARK_FOREST;
	public static DimensionType CROWN_CLIFFS;
	public static DimensionType SKYLAND;

	@Nullable
	private static DimensionType register(String name, int id, Class<? extends WorldProvider> provider)
	{
		if (Strings.isNullOrEmpty(name) || id == 0 || DimensionManager.isDimensionRegistered(id))
		{
			return null;
		}

		DimensionType type = DimensionType.register(name, "_" + name.replace(" ", "_").toLowerCase(), id, provider, false);

		DimensionManager.registerDimension(id, type);

		return type;
	}

	public static void registerDimensions()
	{
		CAVERN = register("cavern", CavernConfig.dimensionId, WorldProviderCavern.class);
		HUGE_CAVERN = register("huge_cavern", HugeCavernConfig.dimensionId, WorldProviderHugeCavern.class);
		AQUA_CAVERN = register("aqua_cavern", AquaCavernConfig.dimensionId, WorldProviderAquaCavern.class);

		CAVELAND = register("caveland", CavelandConfig.dimensionId, WorldProviderCaveland.class);
		CAVENIA = register("cavenia", CaveniaConfig.dimensionId, WorldProviderCavenia.class);
		FROST_MOUNTAINS = register("frost_mountains", MirageWorldsConfig.frostMountains, WorldProviderFrostMountains.class);
		WIDE_DESERT = register("wide_desert", MirageWorldsConfig.wideDesert, WorldProviderWideDesert.class);
		THE_VOID = register("the_void", MirageWorldsConfig.theVoid, WorldProviderVoid.class);
		DARK_FOREST = register("dark_forest", MirageWorldsConfig.darkForest, WorldProviderDarkForest.class);
		CROWN_CLIFFS = register("crown_cliffs", MirageWorldsConfig.crownCliffs, WorldProviderCrownCliffs.class);
		SKYLAND = register("skyland", MirageWorldsConfig.skyland, WorldProviderSkyland.class);
	}

	public static String getLocalizedName(@Nullable DimensionType type)
	{
		if (type == null)
		{
			return null;
		}

		if (type == CAVERN)
		{
			return Cavern.proxy.translate("dimension.cavern.name");
		}

		if (type == HUGE_CAVERN)
		{
			return Cavern.proxy.translate("dimension.hugeCavern.name");
		}

		if (type == AQUA_CAVERN)
		{
			return Cavern.proxy.translate("dimension.aquaCavern.name");
		}

		String suffix = CaveItems.MIRAGE_BOOK.getUnlocalizedName();

		if (type == CAVELAND)
		{
			return Cavern.proxy.translate(suffix + ".caveland.name");
		}

		if (type == CAVENIA)
		{
			return Cavern.proxy.translate(suffix + ".cavenia.name");
		}

		if (type == FROST_MOUNTAINS)
		{
			return Cavern.proxy.translate(suffix + ".frostMountains.name");
		}

		if (type == WIDE_DESERT)
		{
			return Cavern.proxy.translate(suffix + ".wideDesert.name");
		}

		if (type == THE_VOID)
		{
			return Cavern.proxy.translate(suffix + ".theVoid.name");
		}

		if (type == DARK_FOREST)
		{
			return Cavern.proxy.translate(suffix + ".darkForest.name");
		}

		if (type == CROWN_CLIFFS)
		{
			return Cavern.proxy.translate(suffix + ".crownCliffs.name");
		}

		if (type == SKYLAND)
		{
			return Cavern.proxy.translate(suffix + ".skyland.name");
		}

		if (CavernAPI.dimension.isMirageWorlds(type))
		{
			return Cavern.proxy.translate("dimension.mirageWorlds.name");
		}

		return type.getName();
	}
}