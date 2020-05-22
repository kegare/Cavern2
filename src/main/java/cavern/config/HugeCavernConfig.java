package cavern.config;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;

import cavern.block.BlockCave;
import cavern.block.CaveBlocks;
import cavern.client.config.CaveConfigEntries;
import cavern.config.manager.CaveBiome;
import cavern.config.manager.CaveBiomeManager;
import cavern.config.manager.CaveVein;
import cavern.config.manager.CaveVeinManager;
import cavern.config.property.ConfigItems;
import cavern.core.Cavern;
import cavern.util.BlockMeta;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockStone;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class HugeCavernConfig
{
	public static Configuration config;

	public static int dimensionId;
	public static int worldHeight;
	public static ConfigItems triggerItems = new ConfigItems();

	public static boolean generateCaves;
	public static boolean generateLakes;

	public static int monsterSpawn;
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
			config = Config.loadConfig("hugecavern", category);
		}

		prop = config.get(category, "dimension", -51);
		prop.setRequiresMcRestart(true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		dimensionId = prop.getInt(dimensionId);

		prop = config.get(category, "worldHeight", 256);
		prop.setMinValue(64).setMaxValue(256);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		worldHeight = prop.getInt(worldHeight);

		prop = config.get(category, "triggerItems", new String[0]);
		prop.setConfigEntryClass(CaveConfigEntries.selectBlocksAndItems);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		triggerItems.setValues(prop.getStringList());

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

		prop = config.get(category, "monsterSpawn", 100);
		prop.setMinValue(0).setMaxValue(5000);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		monsterSpawn = prop.getInt(monsterSpawn);

		prop = config.get(category, "caveBrightness", 0.095D);
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
			BIOMES.config = Config.loadConfig("hugecavern", "biomes");
		}
		else
		{
			BIOMES.getCaveBiomes().clear();
		}

		if (BIOMES.config.getCategoryNames().isEmpty())
		{
			List<CaveBiome> biomes = Lists.newArrayList();

			biomes.add(new CaveBiome(Biomes.OCEAN, 15));
			biomes.add(new CaveBiome(Biomes.PLAINS, 100));
			biomes.add(new CaveBiome(Biomes.DESERT, 70));
			biomes.add(new CaveBiome(Biomes.DESERT_HILLS, 10));
			biomes.add(new CaveBiome(Biomes.FOREST, 80));
			biomes.add(new CaveBiome(Biomes.FOREST_HILLS, 10));
			biomes.add(new CaveBiome(Biomes.TAIGA, 80));
			biomes.add(new CaveBiome(Biomes.TAIGA_HILLS, 10));
			biomes.add(new CaveBiome(Biomes.JUNGLE, 80, null, new BlockMeta(Blocks.GRAVEL.getDefaultState())));
			biomes.add(new CaveBiome(Biomes.JUNGLE_HILLS, 10, null, new BlockMeta(Blocks.GRAVEL.getDefaultState())));
			biomes.add(new CaveBiome(Biomes.SWAMPLAND, 60));
			biomes.add(new CaveBiome(Biomes.EXTREME_HILLS, 50));
			biomes.add(new CaveBiome(Biomes.SAVANNA, 50));
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
			VEINS.config = Config.loadConfig("hugecavern", "veins");
		}
		else
		{
			VEINS.getCaveVeins().clear();
		}

		if (VEINS.config.getCategoryNames().isEmpty())
		{
			List<CaveVein> veins = Lists.newArrayList();

			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.GRANITE.getMetadata()), 15, 25, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.GRANITE.getMetadata()), 28, 25, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.DIORITE.getMetadata()), 15, 25, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.DIORITE.getMetadata()), 28, 25, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.ANDESITE.getMetadata()), 18, 25, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.ANDESITE.getMetadata()), 30, 25, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.COAL_ORE, 0), 30, 17, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.COAL_ORE, 0), 55, 17, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.IRON_ORE, 0), 35, 10, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.IRON_ORE, 0), 60, 10, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.GOLD_ORE, 0), 5, 5, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.GOLD_ORE, 0), 10, 5, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.REDSTONE_ORE, 0), 8, 6, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.REDSTONE_ORE, 0), 20, 6, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.LAPIS_ORE, 0), 10, 5, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.LAPIS_ORE, 0), 22, 5, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.DIAMOND_ORE, 0), 2, 5, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.DIAMOND_ORE, 0), 4, 5, 50, 255));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.AQUAMARINE_ORE.getMetadata()), 10, 8, 1, 50, Type.COLD, Type.WATER, Type.WET));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.AQUAMARINE_ORE.getMetadata()), 12, 8, 50, 255, Type.COLD, Type.WATER, Type.WET));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.MAGNITE_ORE.getMetadata()), 30, 10, 1, 50));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.MAGNITE_ORE.getMetadata()), 50, 10, 50, 255));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.RANDOMITE_ORE.getMetadata()), 15, 4, 1, 50));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.RANDOMITE_ORE.getMetadata()), 24, 4, 50, 255));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.HEXCITE_ORE.getMetadata()), 2, 5, 1, 50));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.HEXCITE_ORE.getMetadata()), 4, 5, 50, 255));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.FISSURED_STONE.getMetadata()), 40, 2, 1, 50));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.FISSURED_STONE.getMetadata()), 80, 2, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.DIRT, 0), 16, 25, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.DIRT, 0), 20, 25, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.GRAVEL, 0), 8, 20, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.GRAVEL, 0), 10, 20, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.SAND, BlockSand.EnumType.SAND.getMetadata()), 8, 20, 1, 50, Type.SANDY));
			veins.add(new CaveVein(new BlockMeta(Blocks.SAND, BlockSand.EnumType.SAND.getMetadata()), 10, 20, 50, 255, Type.SANDY));

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