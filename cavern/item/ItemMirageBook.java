package cavern.item;

import javax.annotation.Nullable;

import cavern.api.CavernAPI;
import cavern.api.IPortalCache;
import cavern.core.Cavern;
import cavern.stats.PlayerData;
import cavern.stats.PortalCache;
import cavern.util.CaveUtils;
import cavern.world.CaveDimensions;
import cavern.world.WorldCachedData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemMirageBook extends Item
{
	public ItemMirageBook()
	{
		super();
		this.setUnlocalizedName("mirageBook");
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return "item.mirageBook." + EnumType.byItemStack(stack).getUnlocalizedName();
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		return Cavern.proxy.translateFormat(getUnlocalizedName() + ".name", super.getItemStackDisplayName(stack));
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
			if (type.getDimension() != null)
			{
				subItems.add(type.getItemStack());
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		DimensionType type = EnumType.byItemStack(stack).getDimension();

		if (type == null)
		{
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}

		if (world.provider.getDimensionType() == type)
		{
			if (!world.isRemote)
			{
				PlayerData.get(player).setLastTeleportTime(type, world.getTotalWorldTime());

				transferTo(null, player);
			}

			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}

		if (world.isRemote)
		{
			if (CavernAPI.dimension.isInMirageWorlds(player))
			{
				player.sendStatusMessage(new TextComponentTranslation(getUnlocalizedName() + ".fail"), true);
			}
			else
			{
				player.sendStatusMessage(new TextComponentTranslation(getUnlocalizedName() + ".portal"), true);
			}
		}

		return new ActionResult<>(EnumActionResult.PASS, stack);
	}

	public boolean transferTo(@Nullable DimensionType dimNew, EntityPlayer entityPlayer)
	{
		if (entityPlayer == null || !(entityPlayer instanceof EntityPlayerMP))
		{
			return false;
		}

		EntityPlayerMP player = (EntityPlayerMP)entityPlayer;
		ResourceLocation key = CaveUtils.getKey("mirage_worlds");
		IPortalCache cache = PortalCache.get(player);
		MinecraftServer server = player.mcServer;
		DimensionType dimOld = player.world.provider.getDimensionType();

		if (dimNew == null)
		{
			dimNew = cache.getLastDim(key, null);
		}

		if (dimNew == null || dimOld == dimNew)
		{
			return false;
		}

		WorldServer worldNew = server.getWorld(dimNew.getId());

		if (CavernAPI.dimension.isMirageWorlds(dimNew))
		{
			cache.setLastDim(key, dimOld);
		}

		cache.setLastPos(key, dimOld, player.getPosition());

		player.timeUntilPortal = player.getPortalCooldown();

		double x = player.posX;
		double y = player.posY + player.getEyeHeight();
		double z = player.posZ;

		CaveUtils.transferPlayerToDimension(player, dimNew, WorldCachedData.get(worldNew).getMirageTeleporter());

		x = player.posX;
		y = player.posY + player.getEyeHeight();
		z = player.posZ;

		BlockPos pos = player.getPosition().down();
		IBlockState state = worldNew.getBlockState(pos);

		worldNew.playSound(null, x, y, z, state.getBlock().getSoundType(state, worldNew, pos, player).getFallSound(), SoundCategory.BLOCKS, 0.75F, 1.0F);

		if (player.getBedLocation(dimNew.getId()) == null)
		{
			player.setSpawnChunk(player.getPosition(), true, dimNew.getId());
		}

		return true;
	}

	public enum EnumType
	{
		CAVELAND(0, "caveland"),
		CAVENIA(1, "cavenia"),
		FROST_MOUNTAINS(2, "frostMountains"),
		WIDE_DESERT(3, "wideDesert"),
		THE_VOID(4, "theVoid"),
		DARK_FOREST(5, "darkForest");

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

		@Nullable
		public DimensionType getDimension()
		{
			switch (this)
			{
				case CAVELAND:
					return CaveDimensions.CAVELAND;
				case CAVENIA:
					return CaveDimensions.CAVENIA;
				case FROST_MOUNTAINS:
					return CaveDimensions.FROST_MOUNTAINS;
				case WIDE_DESERT:
					return CaveDimensions.WIDE_DESERT;
				case THE_VOID:
					return CaveDimensions.THE_VOID;
				case DARK_FOREST:
					return CaveDimensions.DARK_FOREST;
			}

			return null;
		}

		public ItemStack getItemStack()
		{
			return getItemStack(1);
		}

		public ItemStack getItemStack(int amount)
		{
			return new ItemStack(CaveItems.MIRAGE_BOOK, amount, getMetadata());
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