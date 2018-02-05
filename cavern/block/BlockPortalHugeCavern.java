package cavern.block;

import cavern.api.CavernAPI;
import cavern.config.HugeCavernConfig;
import cavern.stats.MinerRank;
import cavern.util.CaveUtils;
import cavern.world.CaveDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DimensionType;
import net.minecraftforge.oredict.OreDictionary;

public class BlockPortalHugeCavern extends BlockPortalCavern
{
	public BlockPortalHugeCavern()
	{
		super();
		this.setUnlocalizedName("portal.hugeCavern");
	}

	@Override
	public DimensionType getDimension()
	{
		return CaveDimensions.HUGE_CAVERN;
	}

	@Override
	public boolean isEntityInCave(Entity entity)
	{
		return CavernAPI.dimension.isInHugeCavern(entity);
	}

	@Override
	public boolean isTriggerItem(ItemStack stack)
	{
		if (!HugeCavernConfig.triggerItems.isEmpty())
		{
			return HugeCavernConfig.triggerItems.hasItemStack(stack);
		}

		if (!stack.isEmpty() && stack.getItem() == Items.DIAMOND)
		{
			return true;
		}

		for (ItemStack dictStack : OreDictionary.getOres("gemDiamond", false))
		{
			if (CaveUtils.isItemEqual(stack, dictStack))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public MinerRank getMinerRank()
	{
		return MinerRank.IRON_MINER;
	}
}