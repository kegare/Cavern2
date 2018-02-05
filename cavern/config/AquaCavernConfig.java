package cavern.config;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cavern.block.BlockCave;
import cavern.block.CaveBlocks;
import cavern.client.config.CaveConfigEntries;
import cavern.config.manager.CaveBiome;
import cavern.config.manager.CaveBiomeManager;
import cavern.config.manager.CaveVein;
import cavern.config.manager.CaveVeinManager;
import cavern.config.property.ConfigBiomeType;
import cavern.config.property.ConfigEntities;
import cavern.config.property.ConfigItems;
import cavern.core.Cavern;
import cavern.entity.EntityCavenicSkeleton;
import cavern.entity.EntityCavenicSpider;
import cavern.entity.EntityCavenicZombie;
import cavern.util.BlockMeta;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockStone;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class AquaCavernConfig
{
	public static Configuration config;

	public static int dimensionId;
	public static int worldHeight;
	public static ConfigBiomeType biomeType = new ConfigBiomeType();
	public static ConfigItems triggerItems = new ConfigItems();

	public static double floodDepth;

	public static boolean generateCaves;
	public static boolean generateRavine;
	public static boolean generateDungeons;
	public static boolean generateTowerDungeons;

	public static ConfigEntities dungeonMobs = new ConfigEntities();
	public static ConfigEntities towerDungeonMobs = new ConfigEntities();

	public static double caveBrightness;

	public static CaveBiomeManager biomeManager = new CaveBiomeManager();
	public static CaveVeinManager veinManager = new CaveVeinManager();

	public static void syncConfig()
	{
		String category = "dimension";
		Property prop;
		String comment;
		List<String> propOrder = Lists.newArrayList();

		if (config == null)
		{
			config = Config.loadConfig("aquacavern", category);
		}

		prop = config.get(category, "dimension", -52);
		prop.setRequiresMcRestart(true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		dimensionId = prop.getInt(dimensionId);

		prop = config.get(category, "worldHeight", Config.highProfiles ? 256 : 128);
		prop.setMinValue(64).setMaxValue(256);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		worldHeight = prop.getInt(worldHeight);

		prop = config.get(category, "biomeType", ConfigBiomeType.Type.NATURAL.ordinal());
		prop.setMinValue(0).setMaxValue(ConfigBiomeType.Type.values().length - 1).setConfigEntryClass(CaveConfigEntries.cycleInteger);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";

		int min = Integer.parseInt(prop.getMinValue());
		int max = Integer.parseInt(prop.getMaxValue());

		for (int i = min; i <= max; ++i)
		{
			comment += Configuration.NEW_LINE + i + ": " + Cavern.proxy.translate(prop.getLanguageKey() + "." + i);

			if (i < max)
			{
				comment += ",";
			}
		}

		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		biomeType.setValue(prop.getInt(biomeType.getValue()));

		prop = config.get(category, "triggerItems", new String[0]);
		prop.setConfigEntryClass(CaveConfigEntries.selectBlocksAndItems);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		triggerItems.setValues(prop.getStringList());

		prop = config.get(category, "floodDepth", 1.0D);
		prop.setMinValue(0.0D).setMaxValue(1.0D);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		floodDepth = prop.getDouble(floodDepth);

		prop = config.get(category, "generateCaves", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateCaves = prop.getBoolean(generateCaves);

		prop = config.get(category, "generateRavine", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateRavine = prop.getBoolean(generateRavine);

		prop = config.get(category, "generateDungeons", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateDungeons = prop.getBoolean(generateDungeons);

		prop = config.get(category, "generateTowerDungeons", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateTowerDungeons = prop.getBoolean(generateTowerDungeons);

		Set<Class<? extends Entity>> mobs = Sets.newHashSet();

		mobs.add(EntityZombie.class);
		mobs.add(EntitySkeleton.class);
		mobs.add(EntitySpider.class);

		prop = config.get(category, "dungeonMobs", mobs.stream().map(EntityList::getKey).map(ResourceLocation::toString).sorted().toArray(String[]::new));
		prop.setConfigEntryClass(CaveConfigEntries.selectMobs);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		dungeonMobs.setValues(prop.getStringList());

		mobs.clear();
		mobs.add(EntityZombie.class);
		mobs.add(EntitySkeleton.class);
		mobs.add(EntitySpider.class);
		mobs.add(EntityCaveSpider.class);
		mobs.add(EntityEnderman.class);
		mobs.add(EntityCavenicSkeleton.class);
		mobs.add(EntityCavenicZombie.class);
		mobs.add(EntityCavenicSpider.class);

		prop = config.get(category, "towerDungeonMobs", mobs.stream().map(EntityList::getKey).map(ResourceLocation::toString).sorted().toArray(String[]::new));
		prop.setConfigEntryClass(CaveConfigEntries.selectMobs);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		towerDungeonMobs.setValues(prop.getStringList());

		prop = config.get(category, "caveBrightness", 0.075D);
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
		if (biomeManager.config == null)
		{
			biomeManager.config = Config.loadConfig("aquacavern", "biomes");
		}
		else
		{
			biomeManager.getCaveBiomes().clear();
		}

		if (biomeManager.config.getCategoryNames().isEmpty())
		{
			List<CaveBiome> biomes = Lists.newArrayList();

			biomes.add(new CaveBiome(Biomes.OCEAN, 100));
			biomes.add(new CaveBiome(Biomes.PLAINS, 50));
			biomes.add(new CaveBiome(Biomes.DESERT, 10));
			biomes.add(new CaveBiome(Biomes.DESERT_HILLS, 5));
			biomes.add(new CaveBiome(Biomes.FOREST, 50));
			biomes.add(new CaveBiome(Biomes.FOREST_HILLS, 10));
			biomes.add(new CaveBiome(Biomes.TAIGA, 70));
			biomes.add(new CaveBiome(Biomes.TAIGA_HILLS, 10));
			biomes.add(new CaveBiome(Biomes.JUNGLE, 50));
			biomes.add(new CaveBiome(Biomes.JUNGLE_HILLS, 10));
			biomes.add(new CaveBiome(Biomes.SWAMPLAND, 30));
			biomes.add(new CaveBiome(Biomes.EXTREME_HILLS, 20));
			biomes.add(new CaveBiome(Biomes.ICE_PLAINS, 80, new BlockMeta(Blocks.PACKED_ICE.getDefaultState())));
			biomes.add(new CaveBiome(Biomes.ICE_MOUNTAINS, 10, new BlockMeta(Blocks.PACKED_ICE.getDefaultState())));
			biomes.add(new CaveBiome(Biomes.SAVANNA, 50));
			biomes.add(new CaveBiome(Biomes.MESA, 30, new BlockMeta(Blocks.RED_SANDSTONE.getDefaultState())));

			CavernConfig.generateBiomesConfig(biomeManager, biomes);
		}
		else
		{
			CavernConfig.addBiomesFromConfig(biomeManager);
		}

		Config.saveConfig(biomeManager.config);
	}

	public static void syncVeinsConfig()
	{
		if (veinManager.config == null)
		{
			veinManager.config = Config.loadConfig("aquacavern", "veins");
		}
		else
		{
			veinManager.getCaveVeins().clear();
		}

		if (veinManager.config.getCategoryNames().isEmpty())
		{
			List<CaveVein> veins = Lists.newArrayList();

			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.GRANITE.getMetadata()), 30, 25, 1, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.DIORITE.getMetadata()), 32, 25, 1, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.ANDESITE.getMetadata()), 32, 25, 1, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.COAL_ORE, 0), 40, 17, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.IRON_ORE, 0), 40, 10, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.GOLD_ORE, 0), 8, 7, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.REDSTONE_ORE, 0), 12, 7, 1, 40));
			veins.add(new CaveVein(new BlockMeta(Blocks.LAPIS_ORE, 0), 8, 5, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.DIAMOND_ORE, 0), 3, 6, 1, 20));
			veins.add(new CaveVein(new BlockMeta(Blocks.EMERALD_ORE, 0), 8, 5, 50, 127));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.AQUAMARINE_ORE.getMetadata()), 30, 8, 20, 127));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.MAGNITE_ORE.getMetadata()), 35, 10, 1, 127));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.RANDOMITE_ORE.getMetadata()), 28, 6, 1, 127));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.HEXCITE_ORE.getMetadata()), 6, 5, 1, 30));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.FISSURED_STONE.getMetadata()), 70, 3, 1, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.DIRT, 0), 20, 25, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.GRAVEL, 0), 10, 20, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.CLAY, 0), 30, 20, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.SAND, BlockSand.EnumType.SAND.getMetadata()), 15, 20, 1, 127));

			if (Config.highProfiles)
			{
				veins.add(new CaveVein(new BlockMeta(Blocks.COAL_ORE, 0), 35, 20, 128, 255));
				veins.add(new CaveVein(new BlockMeta(Blocks.IRON_ORE, 0), 30, 12, 128, 255));
				veins.add(new CaveVein(new BlockMeta(Blocks.GOLD_ORE, 0), 5, 8, 128, 255));
				veins.add(new CaveVein(new BlockMeta(Blocks.LAPIS_ORE, 0), 4, 7, 128, 255));
				veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.AQUAMARINE_ORE.getMetadata()), 12, 12, 128, 255, Type.COLD, Type.WATER, Type.WET));
				veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.MAGNITE_ORE.getMetadata()), 30, 10, 128, 255));
				veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.RANDOMITE_ORE.getMetadata()), 28, 4, 128, 255));
				veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.HEXCITE_ORE.getMetadata()), 4, 5, 200, 255));
				veins.add(new CaveVein(new BlockMeta(Blocks.DIRT, 0), 20, 25, 128, 255));
				veins.add(new CaveVein(new BlockMeta(Blocks.GRAVEL, 0), 10, 20, 128, 255));
				veins.add(new CaveVein(new BlockMeta(Blocks.CLAY, 0), 30, 20, 128, 255));
				veins.add(new CaveVein(new BlockMeta(Blocks.SAND, BlockSand.EnumType.SAND.getMetadata()), 10, 20, 128, 255, Type.SANDY));
			}

			CavernConfig.generateVeinsConfig(veinManager, veins);
		}
		else
		{
			if (CavernConfig.addVeinsFromConfig(veinManager))
			{
				try
				{
					FileUtils.forceDelete(new File(veinManager.config.toString()));

					veinManager.getCaveVeins().clear();
					veinManager.config = null;

					syncVeinsConfig();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		Config.saveConfig(veinManager.config);
	}
}