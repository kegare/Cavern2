package cavern.config;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.client.config.CaveConfigEntries;
import cavern.config.property.ConfigBlocks;
import cavern.config.property.ConfigItems;
import cavern.config.property.ConfigMinerRank;
import cavern.core.Cavern;
import cavern.data.MinerRank;
import cavern.util.CaveUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class MiningAssistConfig
{
	public static Configuration config;

	public static ConfigItems effectiveItems = new ConfigItems();
	public static ConfigMinerRank minerRank = new ConfigMinerRank();
	public static boolean actualMining;
	public static boolean collectDrops;
	public static boolean collectExps;
	public static ConfigBlocks quickTargetBlocks = new ConfigBlocks();
	public static ConfigBlocks rangedTargetBlocks = new ConfigBlocks();
	public static ConfigBlocks aditTargetBlocks = new ConfigBlocks();
	public static boolean priorityQuickMining;
	public static int quickMiningLimit;
	public static int rangedMining;
	public static boolean modifiedHardness;
	public static boolean miningAssistNotify;

	public static void syncConfig()
	{
		String category = "miningassist";
		Property prop;
		String comment;
		List<String> propOrder = Lists.newArrayList();

		if (config == null)
		{
			config = Config.loadConfig("mining-assist");
		}

		prop = config.get(category, "effectiveItems", new String[0]);
		prop.setConfigEntryClass(CaveConfigEntries.selectItems);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		effectiveItems.setValues(prop.getStringList());

		prop = config.get(category, "minerRank", MinerRank.IRON_MINER.getRank());
		prop.setMinValue(0).setMaxValue(MinerRank.values().length - 1).setConfigEntryClass(CaveConfigEntries.cycleInteger);
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

		prop.setComment(comment);
		propOrder.add(prop.getName());
		minerRank.setValue(prop.getInt(minerRank.getValue()));

		prop = config.get(category, "actualMining", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		actualMining = prop.getBoolean(actualMining);

		prop = config.get(category, "collectDrops", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		collectDrops = prop.getBoolean(collectDrops);

		prop = config.get(category, "collectExps", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		collectExps = prop.getBoolean(collectExps);

		prop = config.get(category, "quickTargetBlocks", new String[0]);
		prop.setConfigEntryClass(CaveConfigEntries.selectBlocksAndOreDicts);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		quickTargetBlocks.setValues(prop.getStringList());

		prop = config.get(category, "rangedTargetBlocks", new String[0]);
		prop.setConfigEntryClass(CaveConfigEntries.selectBlocksAndOreDicts);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		rangedTargetBlocks.setValues(prop.getStringList());

		prop = config.get(category, "aditTargetBlocks", new String[0]);
		prop.setConfigEntryClass(CaveConfigEntries.selectBlocksAndOreDicts);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		aditTargetBlocks.setValues(prop.getStringList());

		prop = config.get(category, "priorityQuickMining", false);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		priorityQuickMining = prop.getBoolean(priorityQuickMining);

		prop = config.get(category, "quickMiningLimit", 30);
		prop.setMinValue(1).setMaxValue(100);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		quickMiningLimit = prop.getInt(quickMiningLimit);

		prop = config.get(category, "rangedMining", 1);
		prop.setMinValue(1).setMaxValue(10);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		rangedMining = prop.getInt(rangedMining);

		if (GeneralConfig.SIDE.isClient())
		{
			prop = config.get(category, "modifiedHardness", true);
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			comment += " [default: " + prop.getDefault() + "]";
			prop.setComment(comment);
			propOrder.add(prop.getName());
			modifiedHardness = prop.getBoolean(modifiedHardness);

			prop = config.get(category, "miningAssistNotify", true);
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			comment += " [default: " + prop.getDefault() + "]";
			prop.setComment(comment);
			propOrder.add(prop.getName());
			miningAssistNotify = prop.getBoolean(miningAssistNotify);
		}

		config.setCategoryPropertyOrder(category, propOrder);

		Config.saveConfig(config);
	}

	public static boolean isEffectiveItem(ItemStack stack)
	{
		if (stack.isEmpty())
		{
			return false;
		}

		if (effectiveItems.isEmpty())
		{
			return CaveUtils.isPickaxe(stack);
		}

		if (effectiveItems.hasItemStack(stack))
		{
			return true;
		}

		return false;
	}
}