package cavern.block;

import java.util.Random;
import java.util.Set;

import com.google.common.collect.Sets;

import cavern.config.GeneralConfig;
import cavern.item.ItemCave;
import cavern.util.CaveUtils;
import cavern.util.WeightedItemStack;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.NonNullList;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.oredict.OreDictionary;

public class RandomiteHelper
{
	private static final Random RANDOM = new Random();

	private static final NonNullList<WeightedItemStack> DROP_ITEMS = NonNullList.create();

	public static void refreshItems()
	{
		DROP_ITEMS.clear();

		Set<String> oreNames = Sets.newTreeSet();
		String[] targetNames = {"treeSapling", "sugarcane", "vine", "slimeball", "enderpearl", "bone", "gunpowder", "string", "torch"};
		String[] targetPrefixes = {"gem", "ingot", "nugget", "dust", "crop"};

		for (String name : targetNames)
		{
			if (OreDictionary.doesOreNameExist(name))
			{
				oreNames.add(name);
			}
		}

		for (String name : OreDictionary.getOreNames())
		{
			for (String prefix : targetPrefixes)
			{
				if (name.startsWith(prefix) && Character.isUpperCase(name.charAt(prefix.length())))
				{
					oreNames.add(name);
				}
			}
		}

		for (String name : oreNames)
		{
			for (ItemStack stack : OreDictionary.getOres(name, false))
			{
				if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
				{
					NonNullList<ItemStack> list = NonNullList.create();

					stack.getItem().getSubItems(CreativeTabs.SEARCH, list);

					for (ItemStack subStack : list)
					{
						addItem(subStack, 50);
					}
				}
				else
				{
					addItem(stack, 50);
				}
			}
		}

		for (Item item : Item.REGISTRY)
		{
			if (item == null || item == Items.AIR || item instanceof ItemBlock)
			{
				continue;
			}

			if (item instanceof ItemFood)
			{
				NonNullList<ItemStack> list = NonNullList.create();

				item.getSubItems(CreativeTabs.SEARCH, list);

				for (ItemStack stack : list)
				{
					addItem(stack, 15);
				}
			}
			else if (item instanceof ItemTool || item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemBow)
			{
				addItem(new ItemStack(item), 5);
			}
		}

		addItem(ItemCave.EnumType.MINER_ORB.getItemStack(), 1);
	}

	public static boolean addItem(ItemStack stack, int weight)
	{
		if (stack.isEmpty() || GeneralConfig.randomiteExcludeItems.hasItemStack(stack))
		{
			return false;
		}

		return DROP_ITEMS.add(new WeightedItemStack(stack, Math.max(weight, 1)));
	}

	public static ItemStack getDropItem()
	{
		if (DROP_ITEMS.isEmpty())
		{
			return ItemStack.EMPTY;
		}

		int totalWeight = WeightedRandom.getTotalWeight(DROP_ITEMS);
		WeightedItemStack item = WeightedRandom.getRandomItem(DROP_ITEMS, RANDOM.nextInt(totalWeight));

		return item != null ? item.getItemStack() : ItemStack.EMPTY;
	}

	public static ItemStack getRandomItem()
	{
		for (int i = 0; i < 20; ++i)
		{
			Item item = Item.REGISTRY.getRandomObject(RANDOM);

			if (item == null || item == Items.AIR)
			{
				continue;
			}

			NonNullList<ItemStack> list = NonNullList.create();

			item.getSubItems(CreativeTabs.SEARCH, list);

			ItemStack stack = CaveUtils.getRandomObject(list, ItemStack.EMPTY);

			if (stack.isEmpty() || GeneralConfig.randomiteExcludeItems.hasItemStack(stack))
			{
				continue;
			}

			return stack;
		}

		return ItemStack.EMPTY;
	}
}