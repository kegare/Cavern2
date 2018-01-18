package cavern.miningassist;

import javax.annotation.Nullable;

import cavern.api.IMinerStats;
import cavern.config.MiningAssistConfig;
import cavern.config.property.ConfigBlocks;
import cavern.stats.MinerStats;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public enum MiningAssist implements IStringSerializable
{
	DISABLED(0, "disabled"),
	QUICK(1, "quick"),
	RANGED(2, "ranged"),
	ADIT(3, "adit");

	public static final MiningAssist[] VALUES = new MiningAssist[values().length];

	private final int type;
	private final String name;

	private MiningAssist(int type, String name)
	{
		this.type = type;
		this.name = name;
	}

	public int getType()
	{
		return type;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public String getUnlocalizedName()
	{
		return "cavern.miningassist." + name;
	}

	@Nullable
	public ConfigBlocks getValidTargetBlocks()
	{
		switch (this)
		{
			case QUICK:
				return MiningAssistConfig.quickTargetBlocks;
			case RANGED:
				return MiningAssistConfig.rangedTargetBlocks;
			case ADIT:
				return MiningAssistConfig.aditTargetBlocks;
			default:
		}

		return null;
	}

	public boolean isEffectiveTarget(ItemStack stack, IBlockState state)
	{
		ConfigBlocks targets = getValidTargetBlocks();

		if (targets == null || targets.isEmpty())
		{
			switch (this)
			{
				case QUICK:
					return state.getBlock() instanceof BlockOre || state.getBlock() instanceof BlockRedstoneOre || MinerStats.getPointAmount(state) > 0;
				default:
					return stack.canHarvestBlock(state);
			}
		}

		return targets.hasBlockState(state);
	}

	public static MiningAssist byPlayer(EntityPlayer player)
	{
		return byMiner(MinerStats.get(player, true));
	}

	public static MiningAssist byMiner(@Nullable IMinerStats stats)
	{
		return get(stats == null ? 0 : stats.getMiningAssist());
	}

	public static MiningAssist get(int type)
	{
		if (type < 0 || type >= VALUES.length)
		{
			type = 0;
		}

		return VALUES[type];
	}

	static
	{
		for (MiningAssist assist : values())
		{
			VALUES[assist.getType()] = assist;
		}
	}
}