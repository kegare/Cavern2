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
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.oredict.OreDictionary;

public class RandomiteHelper
{
	private static final Random RANDOM = new Random();

	public static void refreshItems()
	{
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

		Category.COMMON.getItems().clear();

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
						addItem(Category.COMMON, subStack, 50);
					}
				}
				else
				{
					addItem(Category.COMMON, stack, 50);
				}
			}
		}

		Category.FOOD.getItems().clear();

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
					addItem(Category.FOOD, stack, 10);
				}
			}
			else if (item instanceof ItemArrow)
			{
				addItem(Category.COMMON, new ItemStack(item), 5);
			}
		}

		addItem(Category.COMMON, ItemCave.EnumType.MINER_ORB.getItemStack(), 1);
	}

	public static boolean addItem(Category category, ItemStack stack, int weight)
	{
		if (stack.isEmpty() || GeneralConfig.randomiteExcludeItems.hasItemStack(stack))
		{
			return false;
		}

		return category.getItems().add(new WeightedItemStack(stack, Math.max(weight, 1)));
	}

	public static ItemStack getDropItem(Category category)
	{
		NonNullList<WeightedItemStack> items = category.getItems();

		if (items.isEmpty())
		{
			return ItemStack.EMPTY;
		}

		int totalWeight = WeightedRandom.getTotalWeight(items);
		WeightedItemStack item = WeightedRandom.getRandomItem(items, RANDOM.nextInt(totalWeight));

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

	public enum Category
	{
		COMMON,
		FOOD;

		private final NonNullList<WeightedItemStack> items;

		private Category()
		{
			this.items = NonNullList.create();
		}

		public NonNullList<WeightedItemStack> getItems()
		{
			return items;
		}
	}
}