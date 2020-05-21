package cavern.config;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;

import cavern.block.BlockCave;
import cavern.block.CaveBlocks;
import cavern.config.manager.CaveBiome;
import cavern.config.manager.CaveBiomeManager;
import cavern.config.manager.CaveVein;
import cavern.config.manager.CaveVeinManager;
import cavern.core.Cavern;
import cavern.util.BlockMeta;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockStone;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class CaveniaConfig
{
	public static Configuration config;

	public static int dimensionId;
	public static int worldHeight;

	public static boolean generateCaves;
	public static boolean generateLakes;

	public static int monsterSpawn;
	public static double crazySpawnChance;
	public static double caveBrightness;

	public static final CaveBiomeManager BIOMES = new CaveBiomeManager();
	public static final CaveVeinManager VEINS = new CaveVeinManager();

	public static void syncConfig()
	{
		String category = "dimension";
		Property prop;
		String comment;
		List<String> propOrder = Lists.newArrayList();

		if (config == null)
		{
			config = Config.loadConfig("cavenia", category);
		}

		prop = config.get(category, "dimension", -54);
		prop.setRequiresMcRestart(true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		dimensionId = prop.getInt(dimensionId);

		prop = config.get(category, "worldHeight", 128);
		prop.setMinValue(64).setMaxValue(256);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		worldHeight = prop.getInt(worldHeight);

		prop = config.get(category, "generateCaves", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateCaves = prop.getBoolean(generateCaves);

		prop = config.get(category, "generateLakes", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateLakes = prop.getBoolean(generateLakes);

		prop = config.get(category, "monsterSpawn", 200);
		prop.setMinValue(0).setMaxValue(5000);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		monsterSpawn = prop.getInt(monsterSpawn);

		prop = config.get(category, "crazySpawnChance", 0.1D);
		prop.setMinValue(0.0D).setMaxValue(1.0D);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		crazySpawnChance = prop.getDouble(crazySpawnChance);

		prop = config.get(category, "caveBrightness", 0.125D);
		prop.setMinValue(0.0D).setMaxValue(1.0D);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		caveBrightness = prop.getDouble(caveBrightness);

		config.setCategoryPropertyOrder(category, propOrder);

		Config.saveConfig(config);
	}

	public static void syncBiomesConfig()
	{
		if (BIOMES.config == null)
		{
			BIOMES.config = Config.loadConfig("cavenia", "biomes");
		}
		else
		{
			BIOMES.getCaveBiomes().clear();
		}

		if (BIOMES.config.getCategoryNames().isEmpty())
		{
			List<CaveBiome> biomes = Lists.newArrayList();

			biomes.add(new CaveBiome(Biomes.OCEAN, 15, null, new BlockMeta(Blocks.GRAVEL.getDefaultState())));
			biomes.add(new CaveBiome(Biomes.PLAINS, 100, null, new BlockMeta(Blocks.GRASS.getDefaultState())));
			biomes.add(new CaveBiome(Biomes.DESERT, 70, null, new BlockMeta(Blocks.SAND.getDefaultState())));
			biomes.add(new CaveBiome(Biomes.DESERT_HILLS, 10, null, new BlockMeta(Blocks.SANDSTONE.getDefaultState())));
			biomes.add(new CaveBiome(Biomes.FOREST, 80, null, new BlockMeta(Blocks.GRAVEL.getDefaultState())));
			biomes.add(new CaveBiome(Biomes.FOREST_HILLS, 10, null, new BlockMeta(Blocks.GRAVEL.getDefaultState())));
			biomes.add(new CaveBiome(Biomes.TAIGA, 80));
			biomes.add(new CaveBiome(Biomes.TAIGA_HILLS, 10));
			biomes.add(new CaveBiome(Biomes.JUNGLE, 80, null, new BlockMeta(Blocks.GRAVEL.getDefaultState())));
			biomes.add(new CaveBiome(Biomes.JUNGLE_HILLS, 10, null, new BlockMeta(Blocks.GRAVEL.getDefaultState())));
			biomes.add(new CaveBiome(Biomes.SWAMPLAND, 60, null, new BlockMeta(Blocks.GRASS.getDefaultState())));
			biomes.add(new CaveBiome(Biomes.EXTREME_HILLS, 50));
			biomes.add(new CaveBiome(Biomes.SAVANNA, 50, null, new BlockMeta(Blocks.GRASS.getDefaultState())));
			biomes.add(new CaveBiome(Biomes.MESA, 50, null, new BlockMeta(Blocks.RED_SANDSTONE.getDefaultState())));

			CavernConfig.generateBiomesConfig(BIOMES, biomes);
		}
		else
		{
			CavernConfig.addBiomesFromConfig(BIOMES);
		}

		Config.saveConfig(BIOMES.config);
	}

	public static void syncVeinsConfig()
	{
		if (VEINS.config == null)
		{
			VEINS.config = Config.loadConfig("cavenia", "veins");
		}
		else
		{
			VEINS.getCaveVeins().clear();
		}

		if (VEINS.config.getCategoryNames().isEmpty())
		{
			List<CaveVein> veins = Lists.newArrayList();

			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.GRANITE.getMetadata()), 28, 25, 1, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.DIORITE.getMetadata()), 28, 25, 1, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.ANDESITE.getMetadata()), 30, 25, 1, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.COAL_ORE, 0), 50, 17, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.IRON_ORE, 0), 40, 10, 1, 127));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.AQUAMARINE_ORE.getMetadata()), 12, 8, 20, 127, Type.COLD, Type.WATER, Type.WET));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.MAGNITE_ORE.getMetadata()), 30, 10, 1, 127));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.RANDOMITE_ORE.getMetadata()), 24, 4, 1, 127));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.HEXCITE_ORE.getMetadata()), 4, 5, 1, 30));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.FISSURED_STONE.getMetadata()), 150, 2, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.DIRT, 0), 20, 25, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.GRAVEL, 0), 10, 20, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.SAND, BlockSand.EnumType.SAND.getMetadata()), 10, 20, 1, 127, Type.SANDY));

			CavernConfig.generateVeinsConfig(VEINS, veins);
		}
		else
		{
			if (CavernConfig.addVeinsFromConfig(VEINS))
			{
				try
				{
					FileUtils.forceDelete(new File(VEINS.config.toString()));

					VEINS.getCaveVeins().clear();
					VEINS.config = null;

					syncVeinsConfig();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		Config.saveConfig(VEINS.config);
	}
}