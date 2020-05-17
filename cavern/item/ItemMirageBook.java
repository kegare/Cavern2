package cavern.item;

import javax.annotation.Nullable;

import cavern.api.CavernAPI;
import cavern.core.Cavern;
import cavern.data.PlayerData;
import cavern.data.PortalCache;
import cavern.handler.CaveEventHooks;
import cavern.util.CaveUtils;
import cavern.world.CaveDimensions;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;

public class ItemMirageBook extends Item implements ITeleporter
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
		return getUnlocalizedName() + "." + EnumType.byItemStack(stack).getTranslationKey();
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

	public boolean transferTo(@Nullable DimensionType dimNew, EntityPlayer player)
	{
		if (player == null)
		{
			return false;
		}

		ResourceLocation key = CaveUtils.getKey("mirage_worlds");
		PortalCache cache = PortalCache.get(player);
		DimensionType dimOld = player.world.provider.getDimensionType();

		if (dimNew == null)
		{
			dimNew = cache.getLastDim(key, null);
		}

		if (dimNew == null || dimOld == dimNew)
		{
			return false;
		}

		if (CavernAPI.dimension.isMirageWorlds(dimNew))
		{
			cache.setLastDim(key, dimOld);
		}

		cache.setLastPos(key, dimOld, player.getPosition());

		player.timeUntilPortal = player.getPortalCooldown();

		player = (EntityPlayer)player.changeDimension(dimNew.getId(), this);

		if (player == null)
		{
			return false;
		}

		if (CavernAPI.dimension.isInMirageWorlds(player))
		{
			player.sendStatusMessage(new TextComponentTranslation("item.mirageBook.return"), true);
		}

		return true;
	}

	@Override
	public void placeEntity(World world, Entity entity, float rotationYaw)
	{
		if (attemptToLastPos(world, entity))
		{
			return;
		}

		if (CavernAPI.dimension.isInSkyland(entity))
		{
			attemptToSkyland(world, entity);

			return;
		}

		if (attemptRandomly(world, entity))
		{
			return;
		}

		attemptToVoid(world, entity);
	}

	protected boolean attemptToLastPos(World world, Entity entity)
	{
		PortalCache cache = PortalCache.get(entity);
		ResourceLocation key = CaveUtils.getKey("mirage_worlds");
		DimensionType type = world.provider.getDimensionType();

		if (cache.hasLastPos(key, type))
		{
			BlockPos pos = cache.getLastPos(key, type);

			Cavern.proxy.loadChunk(world, pos.getX() >> 4, pos.getZ() >> 4);

			if (world.getBlockState(pos.down()).getMaterial().isSolid() && world.getBlockState(pos).getBlock().canSpawnInBlock() && world.getBlockState(pos.up()).getBlock().canSpawnInBlock())
			{
				entity.setPositionAndUpdate(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

				return true;
			}

			cache.setLastPos(key, type, null);
		}

		return false;
	}

	protected boolean attemptRandomly(World world, Entity entity)
	{
		int posX = MathHelper.floor(entity.posX);
		int posZ = MathHelper.floor(entity.posZ);
		int count = 0;

		Cavern.proxy.loadChunks(world, posX >> 4, posZ >> 4, 1);

		outside: while (++count < 50)
		{
			int x = posX + itemRand.nextInt(64) - 32;
			int z = posZ + itemRand.nextInt(64) - 32;
			int y = CavernAPI.dimension.isInCaves(entity) ? itemRand.nextInt(30) + 20 : itemRand.nextInt(20) + 60;
			BlockPos pos = new BlockPos(x, y, z);

			while (pos.getY() > 1 && world.isAirBlock(pos))
			{
				pos = pos.down();
			}

			while (pos.getY() < world.getActualHeight() - 3 && !world.isAirBlock(pos))
			{
				pos = pos.up();
			}

			if (world.getBlockState(pos.down()).getMaterial().isSolid() && world.getBlockState(pos).getBlock().canSpawnInBlock() && world.getBlockState(pos.up()).getBlock().canSpawnInBlock())
			{
				for (BlockPos around : BlockPos.getAllInBoxMutable(pos.add(-4, 0, -4), pos.add(4, 0, 4)))
				{
					if (world.getBlockState(around).getMaterial().isLiquid())
					{
						continue outside;
					}
				}

				entity.setPositionAndUpdate(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

				return true;
			}
		}

		return false;
	}

	protected boolean attemptToVoid(World world, Entity entity)
	{
		if (!CavernAPI.dimension.isInTheVoid(entity))
		{
			return false;
		}

		BlockPos pos = new BlockPos(entity.posX, 0.0D, entity.posZ);
		BlockPos from = pos.add(-1, 0, -1);
		BlockPos to = pos.add(1, 0, 1);

		Cavern.proxy.loadChunk(world, pos.getX() >> 4, pos.getZ() >> 4);

		BlockPos.getAllInBoxMutable(from, to).forEach(blockPos -> world.setBlockState(blockPos, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 2));

		entity.setPositionAndUpdate(pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D);

		return true;
	}

	protected void attemptToSkyland(World world, Entity entity)
	{
		int posX = MathHelper.floor(entity.posX);
		int posZ = MathHelper.floor(entity.posZ);
		MutableBlockPos pos = new MutableBlockPos();

		Cavern.proxy.loadChunks(world, posX >> 4, posZ >> 4, 3);

		for (int range = 1; range <= 128; ++range)
		{
			for (int px = - range; px <= range; ++px)
			{
				for (int pz = - range; pz <= range; ++pz)
				{
					if (Math.abs(px) < range && Math.abs(pz) < range) continue;

					for (int y = 100; y >= 50; --y)
					{
						pos.setPos(posX + px, y, posZ + pz);

						if (world.isAirBlock(pos))
						{
							continue;
						}

						Material material = world.getBlockState(pos).getMaterial();

						if (material.isSolid() || material == Material.WATER)
						{
							entity.setPositionAndUpdate(pos.getX() + 0.5D, y + 2.5D, pos.getZ() + 0.5D);

							return;
						}
					}
				}
			}
		}

		world.setBlockState(new BlockPos(entity).down(), Blocks.DIRT.getDefaultState());
	}

	public static ItemStack getRandomBook()
	{
		int i = MathHelper.floor(CaveEventHooks.RANDOM.nextDouble() * EnumType.VALUES.length);
		EnumType type = EnumType.VALUES[i];

		if (type.getDimension() != null)
		{
			return type.getItemStack();
		}

		return ItemStack.EMPTY;
	}

	public enum EnumType
	{
		CAVELAND(0, "caveland"),
		CAVENIA(1, "cavenia"),
		FROST_MOUNTAINS(2, "frostMountains"),
		WIDE_DESERT(3, "wideDesert"),
		THE_VOID(4, "theVoid"),
		DARK_FOREST(5, "darkForest"),
		CROWN_CLIFFS(6, "crownCliffs"),
		SKYLAND(7, "skyland");

		public static final EnumType[] VALUES = new EnumType[values().length];

		private final int meta;
		private final String translationKey;

		private EnumType(int meta, String name)
		{
			this.meta = meta;
			this.translationKey = name;
		}

		public int getMetadata()
		{
			return meta;
		}

		public String getTranslationKey()
		{
			return translationKey;
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
				case CROWN_CLIFFS:
					return CaveDimensions.CROWN_CLIFFS;
				case SKYLAND:
					return CaveDimensions.SKYLAND;
			}

			return null;
		}

		public ItemStack getItemStack()
		{
			return new ItemStack(CaveItems.MIRAGE_BOOK, 1, getMetadata());
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