package cavern.item;

import cavern.api.item.IIceEquipment;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemPickaxeIce extends ItemPickaxeCave implements IIceEquipment
{
	public ItemPickaxeIce()
	{
		super(CaveItems.ICE, "pickaxeIce");
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

	@Override
	public boolean canHarvestBlock(IBlockState state)
	{
		Material material = state.getMaterial();

		if (material == Material.ICE || material == Material.PACKED_ICE)
		{
			return true;
		}

		return super.canHarvestBlock(state);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state)
	{
		Material material = state.getMaterial();

		if (material == Material.ICE || material == Material.PACKED_ICE)
		{
			return efficiency;
		}

		return super.getDestroySpeed(stack, state);
	}
}