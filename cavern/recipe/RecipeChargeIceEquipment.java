package cavern.recipe;

import cavern.api.IIceEquipment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeChargeIceEquipment extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
	private ItemStack resultItem = ItemStack.EMPTY;

	@Override
	public boolean matches(InventoryCrafting crafting, World world)
	{
		resultItem = ItemStack.EMPTY;

		if (!(crafting.getStackInRowAndColumn(1, 1).getItem() instanceof IIceEquipment))
		{
			return false;
		}

		int ice = 0;

		for (int row = 0; row < 3; ++row)
		{
			for (int column = 0; column < 3; ++column)
			{
				if (row == 1 && column == 1)
				{
					continue;
				}

				ItemStack stack = crafting.getStackInRowAndColumn(row, column);

				if (!stack.isEmpty())
				{
					Block block = Block.getBlockFromItem(stack.getItem());

					if (block == null)
					{
						continue;
					}

					if (block instanceof BlockIce || block instanceof BlockPackedIce)
					{
						if (row != 1 && column == 1 || row == 1 && column != 1)
						{
							++ice;
						}
					}
					else if (row != 1 && column != 1)
					{
						return false;
					}
				}
			}
		}

		if (ice >= 4)
		{
			resultItem = getResult(crafting);

			return true;
		}

		return false;
	}

	protected ItemStack getResult(InventoryCrafting crafting)
	{
		ItemStack result = crafting.getStackInRowAndColumn(1, 1).copy();
		int ice = 0;
		int packed = 0;

		for (int row = 0; row < 3; ++row)
		{
			for (int column = 0; column < 3; ++column)
			{
				if (row == 1 && column == 1)
				{
					continue;
				}

				ItemStack stack = crafting.getStackInRowAndColumn(row, column);

				if (!stack.isEmpty())
				{
					Block block = Block.getBlockFromItem(stack.getItem());

					if (block == null)
					{
						continue;
					}

					if (block instanceof BlockPackedIce)
					{
						++packed;
					}
					else if (block instanceof BlockIce)
					{
						++ice;
					}
				}
			}
		}

		if (result.isItemStackDamageable() && result.isItemDamaged())
		{
			result.setItemDamage(0);
		}
		else
		{
			result = ((IIceEquipment)result.getItem()).addCharge(result, ice + packed * 9);

			result.getTagCompound().setBoolean("AfterIceCharge", true);
		}

		return result;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafting)
	{
		return resultItem.copy();
	}

	@Override
	public boolean canFit(int width, int height)
	{
		return width * height > 1;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return resultItem;
	}

	@Override
	public boolean isDynamic()
	{
		return true;
	}
}