package cavern.item;

import cavern.api.item.IIceEquipment;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemShovelIce extends ItemShovelCave implements IIceEquipment
{
	public ItemShovelIce()
	{
		super(CaveItems.ICE, "shovelIce");
	}

	@Override
	public int getMaxDamage(ItemStack stack)
	{
		int max = super.getMaxDamage(stack);

		return max + max / 8 * getCharge(stack);
	}

	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass, EntityPlayer player, IBlockState state)
	{
		int level = super.getHarvestLevel(stack, toolClass, player, state);

		if (getCharge(stack) >= 150)
		{
			++level;
		}

		return level;
	}
}