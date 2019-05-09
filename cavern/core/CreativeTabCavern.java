package cavern.core;

import java.util.List;

import cavern.block.CaveBlocks;
import cavern.item.CaveItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabCavern extends CreativeTabs
{
	public CreativeTabCavern()
	{
		super(Cavern.MODID);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ItemStack createIcon()
	{
		return new ItemStack(CaveBlocks.CAVERN_PORTAL);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void displayAllRelevantItems(NonNullList<ItemStack> list)
	{
		List<Item> caveItems = CaveItems.getItems();

		for (Item item : caveItems)
		{
			for (CreativeTabs tab : item.getCreativeTabs())
			{
				if (tab == this)
				{
					item.getSubItems(this, list);
				}
			}
		}

		for (Item item : Item.REGISTRY)
		{
			if (item == null || caveItems.contains(item))
			{
				continue;
			}

			for (CreativeTabs tab : item.getCreativeTabs())
			{
				if (tab == this)
				{
					item.getSubItems(this, list);
				}
			}
		}
	}
}