package cavern.inventory;

import org.apache.commons.lang3.ObjectUtils;

import cavern.capability.CaveCapabilities;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class InventoryMagicStorage
{
	private IInventory inventory;

	public IInventory getInventory()
	{
		return inventory;
	}

	public void setInventory(IInventory inv)
	{
		inventory = inv;
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		if (inventory == null)
		{
			return;
		}

		nbt.setInteger("Size", inventory.getSizeInventory() / 9);

		NBTTagList list = new NBTTagList();

		for (int i = 0, size = inventory.getSizeInventory(); i < size; ++i)
		{
			ItemStack stack = inventory.getStackInSlot(i);

			if (!stack.isEmpty())
			{
				NBTTagCompound compound = new NBTTagCompound();
				compound.setByte("Slot", (byte)i);
				stack.writeToNBT(compound);
				list.appendTag(compound);
			}
		}

		nbt.setTag("Items", list);
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		if (inventory == null)
		{
			int size;

			if (nbt.hasKey("Size", NBT.TAG_ANY_NUMERIC))
			{
				size = 9 * nbt.getInteger("Size");
			}
			else return;

			inventory = new InventoryBasic("Items", false, size);
		}

		NBTTagList list = nbt.getTagList("Items", NBT.TAG_COMPOUND);

		for (int i = 0, size = list.tagCount(); i < size; ++i)
		{
			NBTTagCompound compound = list.getCompoundTagAt(i);
			int j = compound.getByte("Slot") & 255;

			if (j >= 0 && j < inventory.getSizeInventory())
			{
				inventory.setInventorySlotContents(j, new ItemStack(compound));
			}
		}
	}

	public static InventoryMagicStorage get(ItemStack stack)
	{
		return ObjectUtils.defaultIfNull(CaveCapabilities.getCapability(stack, CaveCapabilities.MAGIC_STORAGE), new InventoryMagicStorage());
	}
}