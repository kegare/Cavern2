package cavern.inventory;

import invtweaks.api.container.ChestContainer;
import invtweaks.api.container.ChestContainer.RowSizeCallback;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

@ChestContainer
public class ContainerStorage extends Container
{
	private final IInventory storageInventory;
	private final int numRows;

	public ContainerStorage(IInventory playerInventory, IInventory storageInventory, EntityPlayer player)
	{
		this.storageInventory = storageInventory;
		this.numRows = storageInventory.getSizeInventory() / 9;

		storageInventory.openInventory(player);

		for (int row = 0; row < numRows; ++row)
		{
			for (int column = 0; column < 9; ++column)
			{
				addSlotToContainer(new SlotStorage(storageInventory, column + row * 9, 8 + column * 18, 18 + row * 18).setCanPut(false));
			}
		}

		int i = (numRows - 4) * 18;

		for (int row = 0; row < 3; ++row)
		{
			for (int column = 0; column < 9; ++column)
			{
				addSlotToContainer(new SlotStorage(playerInventory, column + row * 9 + 9, 8 + column * 18, 103 + row * 18 + i).setCanTake(false));
			}
		}

		for (int column = 0; column < 9; ++column)
		{
			addSlotToContainer(new SlotStorage(playerInventory, column, 8 + column * 18, 161 + i).setCanTake(false));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return storageInventory.isUsableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		ItemStack result = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack())
		{
			ItemStack stack = slot.getStack();
			result = stack.copy();

			if (index < numRows * 9)
			{
				if (!mergeItemStack(stack, numRows * 9, inventorySlots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!mergeItemStack(stack, 0, numRows * 9, false))
			{
				return ItemStack.EMPTY;
			}

			if (stack.isEmpty())
			{
				slot.putStack(ItemStack.EMPTY);
			}
			else
			{
				slot.onSlotChanged();
			}
		}

		return result;
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);

		storageInventory.closeInventory(player);
	}

	@RowSizeCallback
	public int getRowSize()
	{
		return numRows + 3;
	}
}