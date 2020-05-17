package cavern.util;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.base.Objects;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemMeta implements Comparable<ItemMeta>
{
	private final Item item;
	private final int meta;

	public ItemMeta(Item item, int meta)
	{
		this.item = item;
		this.meta = meta;
	}

	public ItemMeta(ItemStack stack)
	{
		this(stack.getItem(), stack.getMetadata());
	}

	public ItemMeta(String name, int meta)
	{
		this(ObjectUtils.defaultIfNull(Item.getByNameOrId(name), Items.AIR), meta);
	}

	@Nonnull
	public Item getItem()
	{
		return item;
	}

	public int getMeta()
	{
		return meta;
	}

	public boolean getHasSubtypes()
	{
		return item.getHasSubtypes();
	}

	public ItemStack getItemStack()
	{
		return getItemStack(1);
	}

	public ItemStack getItemStack(int amount)
	{
		return isEmpty() ? ItemStack.EMPTY : new ItemStack(item, amount, meta);
	}

	public boolean isEmpty()
	{
		return item == null || item == Items.AIR;
	}

	public String getItemName()
	{
		return item.getRegistryName().toString();
	}

	public String getName()
	{
		String name = getItemName();

		if (meta < 0 || meta == OreDictionary.WILDCARD_VALUE || !item.getHasSubtypes())
		{
			return name;
		}

		return name + ":" + meta;
	}

	@Override
	public String toString()
	{
		String name = getItemName();

		if (!item.getHasSubtypes())
		{
			return name;
		}

		if (meta < 0 || meta == OreDictionary.WILDCARD_VALUE)
		{
			return name + ",meta=all";
		}

		return name + ",meta=" + meta;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (obj == null || !(obj instanceof ItemMeta))
		{
			return false;
		}

		ItemMeta itemMeta = (ItemMeta)obj;

		if (item != itemMeta.item)
		{
			return false;
		}
		else if (!item.getHasSubtypes() && !itemMeta.item.getHasSubtypes())
		{
			return true;
		}
		else if (meta < 0 || meta == OreDictionary.WILDCARD_VALUE || itemMeta.meta < 0 || itemMeta.meta == OreDictionary.WILDCARD_VALUE)
		{
			return true;
		}

		return meta == itemMeta.meta;
	}

	@Override
	public int hashCode()
	{
		if (!item.getHasSubtypes() || meta < 0 || meta == OreDictionary.WILDCARD_VALUE)
		{
			return item.hashCode();
		}

		return Objects.hashCode(item, meta);
	}

	@Override
	public int compareTo(ItemMeta itemMeta)
	{
		int i = CaveUtils.compareWithNull(this, itemMeta);

		if (i == 0 && itemMeta != null)
		{
			i = getName().compareTo(itemMeta.getName());
		}

		return i;
	}
}