package cavern.item;

import cavern.core.Cavern;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemCave extends Item
{
	public ItemCave()
	{
		super();
		this.setUnlocalizedName("itemCave");
		this.setHasSubtypes(true);
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return "item." + EnumType.byItemStack(stack).getUnlocalizedName();
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
		if (!isInCreativeTab(tab))
		{
			return;
		}

		for (EnumType type : EnumType.VALUES)
		{
			subItems.add(type.getItemStack());
		}
	}

	public enum EnumType
	{
		AQUAMARINE(0, "aquamarine"),
		MAGNITE_INGOT(1, "ingotMagnite"),
		HEXCITE(2, "hexcite"),
		ICE_STICK(3, "stickIce"),
		MINER_ORB(4, "orbMiner"),
		CAVENIC_ORB(5, "orbCavenic");

		public static final EnumType[] VALUES = new EnumType[values().length];

		private final int meta;
		private final String unlocalizedName;

		private EnumType(int meta, String name)
		{
			this.meta = meta;
			this.unlocalizedName = name;
		}

		public int getMetadata()
		{
			return meta;
		}

		public String getUnlocalizedName()
		{
			return unlocalizedName;
		}

		public ItemStack getItemStack()
		{
			return getItemStack(1);
		}

		public ItemStack getItemStack(int amount)
		{
			return new ItemStack(CaveItems.CAVE_ITEM, amount, getMetadata());
		}

		public static EnumType byMetadata(int meta)
		{
			if (meta < 0 || meta >= VALUES.length)
			{
				meta = 0;
			}

			return VALUES[meta];
		}

		public static EnumType byItemStack(ItemStack stack)
		{
			return byMetadata(stack.isEmpty() ? 0 : stack.getMetadata());
		}

		static
		{
			for (EnumType type : values())
			{
				VALUES[type.getMetadata()] = type;
			}
		}
	}
}