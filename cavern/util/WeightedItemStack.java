package cavern.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

public class WeightedItemStack extends WeightedRandom.Item
{
	private final ItemStack itemStack;

	public WeightedItemStack(ItemStack stack, int weight)
	{
		super(weight);
		this.itemStack = stack;
	}

	public ItemStack getItemStack()
	{
		return itemStack.copy();
	}
}