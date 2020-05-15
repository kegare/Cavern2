package cavern.block;

import cavern.config.AquaCavernConfig;
import cavern.data.MinerRank;
import cavern.item.CaveItems;
import cavern.item.ItemCave;
import cavern.util.CaveUtils;
import cavern.world.CaveDimensions;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DimensionType;
import net.minecraftforge.oredict.OreDictionary;

public class BlockPortalAquaCavern extends BlockPortalCavern
{
	public BlockPortalAquaCavern()
	{
		super();
		this.setUnlocalizedName("portal.aquaCavern");
	}

	@Override
	public DimensionType getDimension()
	{
		return CaveDimensions.AQUA_CAVERN;
	}

	@Override
	public boolean isTriggerItem(ItemStack stack)
	{
		if (!AquaCavernConfig.triggerItems.isEmpty())
		{
			return AquaCavernConfig.triggerItems.hasItemStack(stack);
		}

		if (!stack.isEmpty() && stack.getItem() == CaveItems.CAVE_ITEM && stack.getMetadata() == ItemCave.EnumType.AQUAMARINE.getMetadata())
		{
			return true;
		}

		for (ItemStack dictStack : OreDictionary.getOres("gemAquamarine", false))
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
		return CaveDimensions.CAVERN == null && CaveDimensions.HUGE_CAVERN == null ? MinerRank.BEGINNER : MinerRank.AQUA_MINER;
	}
}