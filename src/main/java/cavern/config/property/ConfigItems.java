package cavern.config.property;

import java.util.Arrays;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.Sets;

import cavern.util.ItemMeta;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ConfigItems
{
	private String[] values;

	private final Set<ItemMeta> items = Sets.newHashSet();

	public String[] getValues()
	{
		if (values == null)
		{
			values = new String[0];
		}

		return values;
	}

	public void setValues(String[] items)
	{
		values = items;
	}

	public Set<ItemMeta> getItems()
	{
		return items;
	}

	public boolean isEmpty()
	{
		return items.isEmpty();
	}

	public String[] createValues(NonNullList<ItemStack> items)
	{
		Set<String> set = Sets.newTreeSet();

		for (ItemStack stack : items)
		{
			String registryName = stack.getItem().getRegistryName().toString();

			if (stack.getHasSubtypes())
			{
				set.add(registryName + ":" + stack.getMetadata());
			}
			else
			{
				set.add(registryName);
			}
		}

		return set.toArray(new String[set.size()]);
	}

	public boolean hasItemStack(ItemStack stack)
	{
		if (stack.isEmpty())
		{
			return false;
		}

		for (ItemMeta itemMeta : items)
		{
			if (itemMeta.getItem() == stack.getItem())
			{
				if (itemMeta.getHasSubtypes())
				{
					if (itemMeta.getMeta() == stack.getMetadata())
					{
						return true;
					}
				}
				else return true;
			}
		}

		return false;
	}

	public void refreshItems()
	{
		items.clear();

		Arrays.stream(getValues()).map(String::trim).filter(StringUtils::isNotEmpty).forEach(value ->
		{
			if (!value.contains(":"))
			{
				value = "minecraft:" + value;
			}

			ItemMeta itemMeta;

			if (value.indexOf(':') != value.lastIndexOf(':'))
			{
				int i = value.lastIndexOf(':');

				itemMeta = new ItemMeta(value.substring(0, i), NumberUtils.toInt(value.substring(i + 1)));
			}
			else
			{
				itemMeta = new ItemMeta(value, 0);
			}

			if (!itemMeta.isEmpty())
			{
				items.add(itemMeta);
			}
		});
	}
}