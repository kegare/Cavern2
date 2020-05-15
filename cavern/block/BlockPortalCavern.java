package cavern.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.cache.LoadingCache;

import cavern.api.CavernAPI;
import cavern.client.gui.GuiMiningRecords;
import cavern.client.gui.GuiRegeneration;
import cavern.config.CavernConfig;
import cavern.config.GeneralConfig;
import cavern.core.Cavern;
import cavern.data.Miner;
import cavern.data.MinerRank;
import cavern.data.PortalCache;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.RegenerationGuiMessage;
import cavern.plugin.MCEPlugin;
import cavern.util.CaveUtils;
import cavern.world.CaveDimensions;
import cavern.world.TeleporterCavern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockPattern.PatternHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class BlockPortalCavern extends BlockPortal
{
	public BlockPortalCavern()
	{
		super();
		this.setUnlocalizedName("portal.cavern");
		this.setSoundType(SoundType.GLASS);
		this.setTickRandomly(false);
		this.setBlockUnbreakable();
		this.disableStats();
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {}

	@Override
	public boolean trySpawnPortal(World world, BlockPos pos)
	{
		if (CavernAPI.dimension.isMirageWorlds(world.provider.getDimensionType()))
		{
			return false;
		}

		Size size = new Size(world, pos, EnumFacing.Axis.X);

		if (size.isValid() && size.portalBlockCount == 0)
		{
			size.placePortalBlocks();

			return true;
		}

		size = new Size(world, pos, EnumFacing.Axis.Z);

		if (size.isValid() && size.portalBlockCount == 0)
		{
			size.placePortalBlocks();

			return true;
		}

		return false;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos pos2)
	{
		EnumFacing.Axis axis = state.getValue(AXIS);
		Size size;

		if (axis == EnumFacing.Axis.X)
		{
			size = new Size(world, pos, EnumFacing.Axis.X);

			if (!size.isValid() || size.portalBlockCount < size.width * size.height)
			{
				world.setBlockToAir(pos);
			}
		}
		else if (axis == EnumFacing.Axis.Z)
		{
			size = new Size(world, pos, EnumFacing.Axis.Z);

			if (!size.isValid() || size.portalBlockCount < size.width * size.height)
			{
				world.setBlockToAir(pos);
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!GeneralConfig.portalMenu)
		{
			return true;
		}

		if (Loader.isModLoaded(MCEPlugin.MODID) && openShop(world, pos, state, player, hand, side))
		{
			return true;
		}

		if (world.provider.getDimensionType() == getDimension())
		{
			if (world.isRemote)
			{
				openMiningRecords(world, pos, state, player, hand, side);
			}

			return true;
		}

		if (Cavern.proxy.isSinglePlayer() && world.isRemote)
		{
			openRegeneration(world, pos, state, player, hand, side);
		}
		else if (player instanceof EntityPlayerMP)
		{
			EntityPlayerMP playerMP = (EntityPlayerMP)player;

			if (playerMP.mcServer.getPlayerList().canSendCommands(playerMP.getGameProfile()))
			{
				CaveNetworkRegistry.sendTo(new RegenerationGuiMessage(RegenerationGuiMessage.EnumType.OPEN), playerMP);
			}
		}

		return true;
	}

	@SideOnly(Side.CLIENT)
	public void openMiningRecords(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side)
	{
		FMLClientHandler.instance().showGuiScreen(new GuiMiningRecords());
	}

	@SideOnly(Side.CLIENT)
	public void openRegeneration(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side)
	{
		GuiRegeneration regeneration = new GuiRegeneration();

		regeneration.dimensions.add(getDimension());

		FMLClientHandler.instance().showGuiScreen(regeneration);
	}

	public boolean openShop(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side)
	{
		ItemStack held = player.getHeldItem(hand);

		if (CaveUtils.isItemPickaxe(held) && MCEPlugin.openShop(MCEPlugin.getPortalShop(), world, player, pos))
		{
			return true;
		}

		return false;
	}

	@Nullable
	public DimensionType getDimension()
	{
		return CaveDimensions.CAVERN;
	}

	public boolean isTriggerItem(ItemStack stack)
	{
		if (!CavernConfig.triggerItems.isEmpty())
		{
			return CavernConfig.triggerItems.hasItemStack(stack);
		}

		if (!stack.isEmpty() && stack.getItem() == Items.EMERALD)
		{
			return true;
		}

		for (ItemStack dictStack : OreDictionary.getOres("gemEmerald", false))
		{
			if (CaveUtils.isItemEqual(stack, dictStack))
			{
				return true;
			}
		}

		return false;
	}

	public MinerRank getMinerRank()
	{
		return MinerRank.BEGINNER;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
	{
		if (world.isRemote || getDimension() == null)
		{
			return;
		}

		if (entity.isDead || entity.isSneaking() || entity.isRiding() || entity.isBeingRidden() || !entity.isNonBoss() || entity instanceof IProjectile)
		{
			return;
		}

		int cd = Math.max(entity.getPortalCooldown(), 50);

		if (entity.timeUntilPortal > 0)
		{
			entity.timeUntilPortal = cd;

			return;
		}

		ResourceLocation key = getRegistryName();
		PortalCache cache = PortalCache.get(entity);
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		DimensionType dimOld = world.provider.getDimensionType();
		DimensionType dimNew = dimOld == getDimension() ? cache.getLastDim(key) : getDimension();
		WorldServer worldNew = server.getWorld(dimNew.getId());
		ITeleporter teleporter = new TeleporterCavern(worldNew, this);
		BlockPos prevPos = entity.getPosition();

		entity.timeUntilPortal = cd;

		if (entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)entity;

			if (!player.capabilities.isCreativeMode && Miner.get(player).getRank() < getMinerRank().getRank())
			{
				player.sendStatusMessage(new TextComponentTranslation("cavern.message.portal.rank", new TextComponentTranslation(getMinerRank().getUnlocalizedName())), true);

				return;
			}
		}

		cache.setLastDim(key, dimOld);
		cache.setLastPos(key, dimOld, prevPos);

		PatternHelper pattern = createPatternHelper(world, pos);
		double d0 = pattern.getForwards().getAxis() == EnumFacing.Axis.X ? (double)pattern.getFrontTopLeft().getZ() : (double)pattern.getFrontTopLeft().getX();
		double d1 = pattern.getForwards().getAxis() == EnumFacing.Axis.X ? entity.posZ : entity.posX;
		d1 = Math.abs(MathHelper.pct(d1 - (pattern.getForwards().rotateY().getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? 1 : 0), d0, d0 - pattern.getWidth()));
		double d2 = MathHelper.pct(entity.posY - 1.0D, pattern.getFrontTopLeft().getY(), pattern.getFrontTopLeft().getY() - pattern.getHeight());

		cache.setLastPortalVec(new Vec3d(d1, d2, 0.0D));
		cache.setTeleportDirection(pattern.getForwards());

		entity.changeDimension(dimNew.getId(), teleporter);
	}

	@Override
	public PatternHelper createPatternHelper(World world, BlockPos pos)
	{
		EnumFacing.Axis axis = EnumFacing.Axis.Z;
		Size size = new Size(world, pos, EnumFacing.Axis.X);
		LoadingCache<BlockPos, BlockWorldState> cache = BlockPattern.createLoadingCache(world, true);

		if (!size.isValid())
		{
			axis = EnumFacing.Axis.X;
			size = new Size(world, pos, EnumFacing.Axis.Z);
		}

		if (!size.isValid())
		{
			return new PatternHelper(pos, EnumFacing.NORTH, EnumFacing.UP, cache, 1, 1, 1);
		}
		else
		{
			int[] values = new int[EnumFacing.AxisDirection.values().length];
			EnumFacing facing = size.rightDir.rotateYCCW();
			BlockPos blockpos = size.bottomLeft.up(size.getHeight() - 1);

			for (EnumFacing.AxisDirection direction : EnumFacing.AxisDirection.values())
			{
				PatternHelper pattern = new PatternHelper(facing.getAxisDirection() == direction ? blockpos : blockpos.offset(size.rightDir, size.getWidth() - 1), EnumFacing.getFacingFromAxis(direction, axis), EnumFacing.UP, cache, size.getWidth(), size.getHeight(), 1);

				for (int i = 0; i < size.getWidth(); ++i)
				{
					for (int j = 0; j < size.getHeight(); ++j)
					{
						BlockWorldState state = pattern.translateOffset(i, j, 1);

						if (state.getBlockState() != null && state.getBlockState().getMaterial() != Material.AIR)
						{
							++values[direction.ordinal()];
						}
					}
				}
			}

			EnumFacing.AxisDirection ax = EnumFacing.AxisDirection.POSITIVE;

			for (EnumFacing.AxisDirection direction : EnumFacing.AxisDirection.values())
			{
				if (values[direction.ordinal()] < values[ax.ordinal()])
				{
					ax = direction;
				}
			}

			return new PatternHelper(facing.getAxisDirection() == ax ? blockpos : blockpos.offset(size.rightDir, size.getWidth() - 1), EnumFacing.getFacingFromAxis(ax, axis), EnumFacing.UP, cache, size.getWidth(), size.getHeight(), 1);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {}

	@Override
	public ItemStack getItem(World world, BlockPos pos, IBlockState state)
	{
		return new ItemStack(this);
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list)
	{
		if (getDimension() != null)
		{
			super.getSubBlocks(tab, list);
		}
	}

	public class Size
	{
		private final World world;
		private final EnumFacing.Axis axis;
		private final EnumFacing rightDir;
		private final EnumFacing leftDir;

		private int portalBlockCount;
		private BlockPos bottomLeft;
		private int height;
		private int width;
		private IBlockState portalFrame;

		public Size(World world, BlockPos pos, EnumFacing.Axis axis)
		{
			this.world = world;
			this.axis = axis;

			if (axis == EnumFacing.Axis.X)
			{
				this.leftDir = EnumFacing.EAST;
				this.rightDir = EnumFacing.WEST;
			}
			else
			{
				this.leftDir = EnumFacing.NORTH;
				this.rightDir = EnumFacing.SOUTH;
			}

			for (BlockPos blockpos = pos; pos.getY() > blockpos.getY() - 21 && pos.getY() > 0 && isEmptyBlock(world.getBlockState(pos.down())); pos = pos.down())
			{
				;
			}

			int i = getDistanceUntilEdge(pos, leftDir) - 1;

			if (i >= 0)
			{
				this.bottomLeft = pos.offset(leftDir, i);
				this.width = getDistanceUntilEdge(bottomLeft, rightDir);

				if (width < 2 || width > 21)
				{
					this.bottomLeft = null;
					this.width = 0;
				}
			}

			if (bottomLeft != null)
			{
				this.height = calculatePortalHeight();
			}
		}

		protected int getDistanceUntilEdge(BlockPos pos, EnumFacing face)
		{
			int i;

			for (i = 0; i < 22; ++i)
			{
				BlockPos pos1 = pos.offset(face, i);

				if (!isEmptyBlock(world.getBlockState(pos1)) || !isFrameBlock(world.getBlockState(pos1.down())))
				{
					break;
				}
			}

			return isFrameBlock(world.getBlockState(pos.offset(face, i))) ? i : 0;
		}

		public int getHeight()
		{
			return height;
		}

		public int getWidth()
		{
			return width;
		}

		protected int calculatePortalHeight()
		{
			int i;

			outside: for (height = 0; height < 21; ++height)
			{
				for (i = 0; i < width; ++i)
				{
					BlockPos pos = bottomLeft.offset(rightDir, i).up(height);
					IBlockState state = world.getBlockState(pos);

					if (!isEmptyBlock(state))
					{
						break outside;
					}

					if (state.getBlock() == BlockPortalCavern.this)
					{
						++portalBlockCount;
					}

					if (i == 0)
					{
						if (!isFrameBlock(world.getBlockState(pos.offset(leftDir))))
						{
							break outside;
						}
					}
					else if (i == width - 1)
					{
						if (!isFrameBlock(world.getBlockState(pos.offset(rightDir))))
						{
							break outside;
						}
					}
				}
			}

			for (i = 0; i < width; ++i)
			{
				if (!isFrameBlock(world.getBlockState(bottomLeft.offset(rightDir, i).up(height))))
				{
					height = 0;

					break;
				}
			}

			if (height <= 21 && height >= 3)
			{
				return height;
			}
			else
			{
				bottomLeft = null;
				width = 0;
				height = 0;

				return 0;
			}
		}

		protected boolean isEmptyBlock(IBlockState state)
		{
			return state.getMaterial() == Material.AIR || state.getBlock() == BlockPortalCavern.this;
		}

		protected boolean isFrameBlock(IBlockState state)
		{
			if (portalFrame == null)
			{
				if (state.getBlock() == Blocks.MOSSY_COBBLESTONE)
				{
					portalFrame = Blocks.MOSSY_COBBLESTONE.getDefaultState();
				}
				else if (state.getBlock() == Blocks.STONEBRICK && state.getBlock().getMetaFromState(state) == BlockStoneBrick.MOSSY_META)
				{
					portalFrame = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY);
				}
			}

			return CaveUtils.areBlockStatesEqual(portalFrame, state);
		}

		public boolean isValid()
		{
			return bottomLeft != null && width >= 2 && width <= 21 && height >= 3 && height <= 21;
		}

		public void placePortalBlocks()
		{
			for (int i = 0; i < width; ++i)
			{
				BlockPos pos = bottomLeft.offset(rightDir, i);

				for (int j = 0; j < height; ++j)
				{
					world.setBlockState(pos.up(j), getDefaultState().withProperty(AXIS, axis), 2);
				}
			}
		}
	}
}