package cavern.client.config;

import cavern.client.config.common.MiningPointsEntry;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CaveConfigEntries
{
	public static Class<? extends IConfigEntry> cycleInteger;

	public static Class<? extends IConfigEntry> selectBlocks;
	public static Class<? extends IConfigEntry> selectBlocksAndItems;
	public static Class<? extends IConfigEntry> selectBlocksAndOreDicts;
	public static Class<? extends IConfigEntry> selectItems;
	public static Class<? extends IConfigEntry> selectMobs;

	public static Class<? extends IConfigEntry> miningPoints;

	@SideOnly(Side.CLIENT)
	public static void initEntries()
	{
		cycleInteger = CycleIntegerEntry.class;

		selectBlocks = SelectBlocksEntry.class;
		selectBlocksAndItems = SelectBlocksAndItemsEntry.class;
		selectBlocksAndOreDicts = SelectBlocksAndOreDictsEntry.class;
		selectItems = SelectItemsEntry.class;
		selectMobs = SelectMobsEntry.class;

		miningPoints = MiningPointsEntry.class;
	}
}