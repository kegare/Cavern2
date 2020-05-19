package cavern.inventory;

import cavern.item.ItemMagicBook;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotStorage extends Slot
{
	private boolean canTake = true;
	private boolean canPut = true;

	public SlotStorage(IInventory inventory, int index, int xPosition, int yPosition)
	{
		super(inventory, index, xPosition, yPosition);
	}

	public SlotStorage setCanTake(boolean take)
	{
		canTake = take;

		return this;
	}

	public SlotStorage setCanPut(boolean put)
	{
		canPut = put;

		return this;
	}

	public boolean isValidStack(ItemStack stack)
	{
		if (!stack.isEmpty() && stack.getItem() instanceof ItemMagicBook)
		{
			if (stack.getMetadata() == ItemMagicBook.EnumType.STORAGE.getMetadata())
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player)
	{
		return (canTake || isValidStack(getStack())) && super.canTakeStack(player);
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return (canPut || isValidStack(stack)) && super.isItemValid(stack);
	}
}