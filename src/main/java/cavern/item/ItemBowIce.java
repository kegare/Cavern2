package cavern.item;

import javax.annotation.Nullable;

import cavern.api.item.IIceEquipment;
import cavern.core.Cavern;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentArrowFire;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBowIce extends ItemBow implements IIceEquipment
{
	public ItemBowIce()
	{
		super();
		this.setUnlocalizedName("bowIce");
		this.setCreativeTab(Cavern.TAB_CAVERN);
		this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			@Override
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
			{
				if (entity == null)
				{
					return 0.0F;
				}
				else
				{
					ItemStack itemstack = entity.getActiveItemStack();
					float f = 0.001F  * getCharge(itemstack);

					return !itemstack.isEmpty() && itemstack.getItem() == ItemBowIce.this ? (stack.getMaxItemUseDuration() - entity.getItemInUseCount()) / Math.max(10.0F - f, 6.7F) : 0.0F;
				}
			}
		});
	}

	@Override
	public int getMaxDamage(ItemStack stack)
	{
		int max = super.getMaxDamage(stack);

		return max + max / 8 * getCharge(stack);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		int duration = super.getMaxItemUseDuration(stack);
		int min = duration / 2;
		int max = duration / 3;

		return MathHelper.clamp(min - duration / 8 * getCharge(stack), min, max);
	}

	@Override
	public int getItemEnchantability()
	{
		return 0;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
	{
		return super.canApplyAtEnchantingTable(stack, enchantment) && !(enchantment instanceof EnchantmentArrowFire);
	}
}