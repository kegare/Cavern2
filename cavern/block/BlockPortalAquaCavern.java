package cavern.block;

import cavern.api.CavernAPI;
import cavern.item.CaveItems;
import cavern.item.ItemCave;
import cavern.stats.MinerRank;
import cavern.util.CaveUtils;
import cavern.world.CaveDimensions;
import net.minecraft.entity.Entity;
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
	public boolean isEntityInCave(Entity entity)
	{
		return CavernAPI.dimension.isInAquaCavern(entity);
	}

	@Override
	public boolean isTriggerItem(ItemStack stack)
	{
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
		return MinerRank.AQUA_MINER;
	}
}