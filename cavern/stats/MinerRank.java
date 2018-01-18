package cavern.stats;

import javax.annotation.Nullable;

import cavern.item.CaveItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public enum MinerRank
{
	BEGINNER(0, 0, 1.0F, "beginner", new ItemStack(Items.WOODEN_PICKAXE)),
	STONE_MINER(1, 50, 1.0F, "stoneMiner", new ItemStack(Items.STONE_PICKAXE)),
	IRON_MINER(2, 100, 1.0F, "ironMiner", new ItemStack(Items.IRON_PICKAXE)),
	MAGNITE_MINER(3, 300, 1.1F, "magniteMiner", new ItemStack(CaveItems.MAGNITE_PICKAXE)),
	GOLD_MINER(4, 1000, 1.2F, "goldMiner", new ItemStack(Items.GOLDEN_PICKAXE)),
	AQUA_MINER(5, 1500, 1.25F, "aquaMiner", new ItemStack(CaveItems.AQUAMARINE_PICKAXE)),
	HEXCITE_MINER(6, 3000, 1.5F, "hexciteMiner", new ItemStack(CaveItems.HEXCITE_PICKAXE)),
	DIAMOND_MINER(7, 5000, 1.75F, "diamondMiner", new ItemStack(Items.DIAMOND_PICKAXE));

	public static final MinerRank[] VALUES = new MinerRank[values().length];

	private final int rank;
	private final int phase;
	private final float boost;
	private final String name;
	private final ItemStack theItemStack;

	private MinerRank(int rank, int phase, float boost, String name, @Nullable ItemStack stack)
	{
		this.rank = rank;
		this.phase = phase;
		this.boost = boost;
		this.name = name;
		this.theItemStack = stack;
	}

	public int getRank()
	{
		return rank;
	}

	public int getPhase()
	{
		return phase;
	}

	public float getBoost()
	{
		return boost;
	}

	public String getName()
	{
		return name;
	}

	public String getUnlocalizedName()
	{
		return "cavern.minerrank." + name;
	}

	public ItemStack getItemStack()
	{
		return theItemStack == null ? ItemStack.EMPTY : theItemStack;
	}

	public static MinerRank get(int rank)
	{
		if (rank < 0)
		{
			rank = 0;
		}

		int max = VALUES.length - 1;

		if (rank > max)
		{
			rank = max;
		}

		return VALUES[rank];
	}

	static
	{
		for (MinerRank rank : values())
		{
			VALUES[rank.getRank()] = rank;
		}
	}
}