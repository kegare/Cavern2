package cavern.client.gui;

import org.apache.logging.log4j.Level;

import cavern.util.BlockMeta;
import cavern.util.CaveLog;
import cavern.util.ItemMeta;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SelectListHelper
{
	protected static final NonNullList<BlockMeta> BLOCKS = NonNullList.create();
	protected static final NonNullList<ItemMeta> ITEMS = NonNullList.create();

	@SuppressWarnings("deprecation")
	public static void setupBlocks()
	{
		NonNullList<ItemStack> subList = NonNullList.create();

		for (Block block : ForgeRegistries.BLOCKS.getValuesCollection())
		{
			try
			{
				if (block instanceof BlockAir || block instanceof ITileEntityProvider)
				{
					continue;
				}

				Item item = Item.getItemFromBlock(block);

				if (item == Items.AIR)
				{
					continue;
				}

				if (block != Block.getBlockFromItem(item))
				{
					continue;
				}

				subList.clear();

				block.getSubBlocks(CreativeTabs.SEARCH, subList);

				for (ItemStack stack : subList)
				{
					if (stack.isEmpty() || stack.getItem() != item)
					{
						continue;
					}

					int meta = stack.getItemDamage();
					IBlockState state = block.getStateFromMeta(meta);

					if (meta != block.getMetaFromState(state))
					{
						continue;
					}

					BlockMeta blockMeta = new BlockMeta(block, meta);

					if (!BLOCKS.contains(blockMeta))
					{
						BLOCKS.add(blockMeta);
					}
				}
			}
			catch (Exception e)
			{
				CaveLog.log(Level.ERROR, "An error occurred while setup. Skip: %s", block.toString());
			}
		}
	}

	public static void setupItems()
	{
		NonNullList<ItemStack> subList = NonNullList.create();

		for (Item item : ForgeRegistries.ITEMS.getValuesCollection())
		{
			try
			{
				if (item == Items.AIR)
				{
					continue;
				}

				subList.clear();

				item.getSubItems(CreativeTabs.SEARCH, subList);

				for (ItemStack stack : subList)
				{
					ItemMeta itemMeta = new ItemMeta(stack);

					if (!ITEMS.contains(itemMeta))
					{
						ITEMS.add(itemMeta);
					}
				}
			}
			catch (Exception e)
			{
				CaveLog.log(Level.ERROR, "An error occurred while setup. Skip: %s", item.toString());
			}
		}
	}
}