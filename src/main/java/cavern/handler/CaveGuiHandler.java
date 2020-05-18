package cavern.handler;

import cavern.client.gui.GuiStorage;
import cavern.inventory.ContainerStorage;
import cavern.inventory.InventoryMagicStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CaveGuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int par1, int par2, int par3)
	{
		switch (ID)
		{
			case 0:
				return new ContainerStorage(player.inventory, getMagicStorageInventory(player.getHeldItem(EnumHand.values()[par1])), player);
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int par1, int par2, int par3)
	{
		switch (ID)
		{
			case 0:
				return new GuiStorage(player.inventory, getMagicStorageInventory(player.getHeldItem(EnumHand.values()[par1])), player);
		}

		return null;
	}

	private IInventory getMagicStorageInventory(ItemStack stack)
	{
		InventoryMagicStorage storage = InventoryMagicStorage.get(stack);
		IInventory inventory = storage.getInventory();

		if (inventory == null)
		{
			inventory = new InventoryBasic("Items", false, 9 * 3);
		}
		else return inventory;

		storage.setInventory(inventory);

		return inventory;
	}
}