package cavern.api.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public interface IIceEquipment
{
	static ItemStack getChargedItem(Item item, int amount)
	{
		ItemStack stack = new ItemStack(item);

		if (item instanceof IIceEquipment)
		{
			return ((IIceEquipment)item).setCharge(stack, amount);
		}

		return stack;
	}

	default int getCharge(ItemStack stack)
	{
		NBTTagCompound nbt = stack.getTagCompound();

		if (nbt == null || !nbt.hasKey("IceCharge", NBT.TAG_ANY_NUMERIC))
		{
			return 0;
		}

		return nbt.getInteger("IceCharge");
	}

	default ItemStack setCharge(ItemStack stack, int amount)
	{
		NBTTagCompound nbt = stack.getTagCompound();

		if (nbt == null)
		{
			nbt = new NBTTagCompound();
		}

		nbt.setInteger("IceCharge", amount);

		stack.setTagCompound(nbt);

		return stack;
	}

	default ItemStack addCharge(ItemStack stack, int amount)
	{
		return setCharge(stack, Math.max(getCharge(stack) + amount, 0));
	}

	default boolean isHiddenTooltip()
	{
		return false;
	}
}