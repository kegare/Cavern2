package cavern.item;

import cavern.api.item.IIceEquipment;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentFireAspect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemSwordIce extends ItemSwordCave implements IIceEquipment
{
	public ItemSwordIce()
	{
		super(CaveItems.ICE, "swordIce");
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
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
	{
		return super.canApplyAtEnchantingTable(stack, enchantment) && !(enchantment instanceof EnchantmentFireAspect);
	}
}