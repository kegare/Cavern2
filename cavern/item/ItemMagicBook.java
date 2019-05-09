package cavern.item;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import com.google.common.collect.Sets;

import cavern.core.Cavern;
import cavern.handler.CaveEventHooks;
import cavern.inventory.InventoryMagicStorage;
import cavern.magic.Magic;
import cavern.magic.MagicBook;
import cavern.magic.MagicExplosion;
import cavern.magic.MagicHeal;
import cavern.magic.MagicInfinity;
import cavern.magic.MagicInvisible;
import cavern.magic.MagicOverload;
import cavern.magic.MagicStorage;
import cavern.magic.MagicSummon;
import cavern.magic.MagicTeleport;
import cavern.magic.MagicThunderbolt;
import cavern.magic.MagicTorch;
import cavern.magic.MagicWarp;
import cavern.util.CaveLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMagicBook extends Item
{
	public ItemMagicBook()
	{
		super();
		this.setTranslationKey("magicBook");
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}

	@Override
	public String getTranslationKey(ItemStack stack)
	{
		return getTranslationKey() + "." + EnumType.byItemStack(stack).getTranslationKey();
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		return Cavern.proxy.translateFormat(getTranslationKey() + ".name", super.getItemStackDisplayName(stack));
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

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag)
	{
		switch (EnumType.byItemStack(stack))
		{
			case STORAGE:
				IInventory inventory = InventoryMagicStorage.get(stack).getInventory();

				if (inventory == null)
				{
					break;
				}

				int count = 0;
				Set<String> names = Sets.newTreeSet();

				for (int i = 0; i < inventory.getSizeInventory() && count < 5; ++i)
				{
					ItemStack item = inventory.getStackInSlot(i);

					if (!item.isEmpty() && names.add(item.getDisplayName()))
					{
						++count;
					}
				}

				for (String name : names)
				{
					tooltip.add(TextFormatting.ITALIC + name + TextFormatting.RESET);
				}

				break;
			case WARP:
				Pair<BlockPos, DimensionType> warpPoint = MagicWarp.getWarpPoint(stack);

				if (warpPoint == null)
				{
					break;
				}

				BlockPos pos = warpPoint.getLeft();
				DimensionType type = warpPoint.getRight();

				tooltip.add(String.format("%s: %d, %d, %d", type.getName(), pos.getX(), pos.getY(), pos.getZ()));

				break;
			default:
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (getMana(stack) <= 0)
		{
			return false;
		}

		return mc.player.capabilities.isCreativeMode || !isInCoolTime(mc.player, stack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (MagicBook.get(mc.player).getSpellingMagicBook() == stack)
		{
			return true;
		}

		return getMana(stack) < EnumType.byItemStack(stack).getMana();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		MagicBook book = MagicBook.get(mc.player);

		if (book.getSpellingMagicBook() == stack)
		{
			double progress = book.getSpellingMagic().getSpellingProgress();

			if (book.getSpecialMagic() != null && book.getSpecialMagic() instanceof MagicInfinity)
			{
				progress = MathHelper.clamp(progress * 2.5D, 0.0D, 1.0D);
			}

			return 1.0D - progress;
		}

		return 1.0D - (double)getMana(stack) / (double)EnumType.byItemStack(stack).getMana();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		MagicBook book = MagicBook.get(mc.player);

		if (book.getSpellingMagicBook() == stack)
		{
			return book.getSpellingMagic().isOverload() ? 0xF9C9B9 : 0x00A2D0;
		}

		return super.getRGBDurabilityForDisplay(stack);
	}

	public static int getMana(ItemStack stack)
	{
		NBTTagCompound nbt = stack.getTagCompound();

		return nbt == null ? 0 : MathHelper.clamp(nbt.getInteger("Mana"), 0, EnumType.byItemStack(stack).getMana());
	}

	public static ItemStack setMana(ItemStack stack, int amount)
	{
		NBTTagCompound nbt = stack.getTagCompound();

		if (nbt == null)
		{
			nbt = new NBTTagCompound();
		}

		nbt.setInteger("Mana", MathHelper.clamp(amount, 0, EnumType.byItemStack(stack).getMana()));
		stack.setTagCompound(nbt);

		return stack;
	}

	public static int consumeMana(ItemStack stack, int amount)
	{
		return getMana(setMana(stack, getMana(stack) - amount));
	}

	public static long getLastUseTime(ItemStack stack)
	{
		NBTTagCompound nbt = stack.getTagCompound();

		return nbt == null ? 0L : nbt.getLong("LastUseTime");
	}

	public static ItemStack setLastUseTime(ItemStack stack, long time)
	{
		NBTTagCompound nbt = stack.getTagCompound();

		if (nbt == null)
		{
			nbt = new NBTTagCompound();
		}

		nbt.setLong("LastUseTime", time);
		stack.setTagCompound(nbt);

		return stack;
	}

	public static boolean isInCoolTime(EntityPlayer player, ItemStack stack)
	{
		MagicBook book = MagicBook.get(player);
		EnumType type = EnumType.byItemStack(stack);
		int coolTime = type.getCoolTime();

		if (book.getSpecialMagic() != null && book.getSpecialMagic() instanceof MagicInfinity)
		{
			coolTime = Math.min(coolTime, 5 * 20);
		}

		if (coolTime > 0)
		{
			long lastUseTime = getLastUseTime(stack);

			if (lastUseTime > 0L && player.world.getTotalWorldTime() - lastUseTime <= coolTime)
			{
				return true;
			}
		}

		return false;
	}

	public static ItemStack getRandomBook()
	{
		int i = MathHelper.floor(CaveEventHooks.RANDOM.nextDouble() * EnumType.VALUES.length);
		EnumType type = EnumType.VALUES[i];
		int mana = type.getMana();

		if (mana >= 10)
		{
			mana = MathHelper.getInt(CaveEventHooks.RANDOM, mana / 2, mana);
		}
		else if (mana > 1)
		{
			mana = CaveEventHooks.RANDOM.nextInt(mana) + 1;
		}

		return type.getItemStack(mana);
	}

	public enum EnumType
	{
		STORAGE(0, "storage", 50, MagicStorage.class, 5 * 20),
		HEAL(1, "heal", 10, MagicHeal.class, 20 * 20),
		WARP(2, "warp", 10, MagicWarp.class, 60 * 20),
		TELEPORT(3, "teleport", 50, MagicTeleport.class, 15 * 20),
		TORCH(4, "torch", 30, MagicTorch.class, 60 * 20),
		INVISIBLE(5, "invisible", 10, MagicInvisible.class, 20 * 20),
		SUMMON(6, "summon", 30, MagicSummon.class, 60 * 20),
		EXPLOSION(7, "explosion", 20, MagicExplosion.class, 30 * 20),
		THUNDERBOLT(8, "thunderbolt", 20, MagicThunderbolt.class, 30 * 20),
		INFINITY(9, "infinity", 3, MagicInfinity.class, 180 * 20),
		OVERLOAD(10, "overload", 3, MagicOverload.class, 60 * 20);

		private final int meta;
		private final String translationKey;
		private final int mana;
		private final Class<? extends Magic> magicClass;
		private final int coolTime;

		public static final EnumType[] VALUES = new EnumType[values().length];

		private EnumType(int meta, String name, int mana, Class<? extends Magic> magic, int coolTime)
		{
			this.meta = meta;
			this.translationKey = name;
			this.mana = mana;
			this.magicClass = magic;
			this.coolTime = coolTime;
		}

		public int getMetadata()
		{
			return meta;
		}

		public String getTranslationKey()
		{
			return translationKey;
		}

		public int getMana()
		{
			return mana;
		}

		public Class<? extends Magic> getMagicClass()
		{
			return magicClass;
		}

		@Nullable
		public Magic createMagic(World world, EntityPlayer player, EnumHand hand)
		{
			try
			{
				return getMagicClass().getConstructor(World.class, EntityPlayer.class, EnumHand.class).newInstance(world, player, hand);
			}
			catch (ReflectiveOperationException e)
			{
				CaveLog.log(Level.ERROR, e, "Failed to create the %s magic.", name());

				return null;
			}
		}

		public int getCoolTime()
		{
			return coolTime;
		}

		public ItemStack getItemStack()
		{
			return getItemStack(getMana());
		}

		public ItemStack getItemStack(int mana)
		{
			return setMana(new ItemStack(CaveItems.MAGIC_BOOK, 1, getMetadata()), mana);
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