package cavern.world;

import cavern.api.CavernAPI;
import cavern.api.IPortalCache;
import cavern.block.BlockPortalCavern;
import cavern.block.CaveBlocks;
import cavern.config.GeneralConfig;
import cavern.stats.PortalCache;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterCavern extends Teleporter
{
	protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
	protected static final IBlockState MOSSY_COBBLESTONE = Blocks.MOSSY_COBBLESTONE.getDefaultState();

	private final BlockPortalCavern portal;

	public TeleporterCavern(WorldServer worldServer, BlockPortalCavern portal)
	{
		super(worldServer);
		this.portal = portal;
	}

	public TeleporterCavern(WorldServer worldServer)
	{
		this(worldServer, CaveBlocks.CAVERN_PORTAL);
	}

	public ResourceLocation getKey()
	{
		return portal.getRegistryName();
	}

	@Override
	public void placeInPortal(Entity entity, float rotationYaw)
	{
		DimensionType type = world.provider.getDimensionType();
		boolean flag = false;

		if (GeneralConfig.portalCache)
		{
			IPortalCache cache = PortalCache.get(entity);
			ResourceLocation key = getKey();
			double posX = entity.posX;
			double posY = entity.posY;
			double posZ = entity.posZ;

			if (cache.hasLastPos(key, type))
			{
				BlockPos pos = cache.getLastPos(key, type);

				if (entity instanceof EntityPlayerMP)
				{
					((EntityPlayerMP)entity).connection.setPlayerLocation(pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D, entity.rotationYaw, entity.rotationPitch);
				}
				else
				{
					entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D, entity.rotationYaw, entity.rotationPitch);
				}

				if (placeInExistingPortal(entity, rotationYaw))
				{
					flag = true;
				}
				else
				{
					entity.setPositionAndUpdate(posX, posY, posZ);
				}
			}
		}

		if (!flag && !placeInExistingPortal(entity, rotationYaw))
		{
			makePortal(entity);

			placeInExistingPortal(entity, rotationYaw);
		}

		if (!flag && entity instanceof EntityLivingBase)
		{
			((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 25, 0, false, false));
		}

		if (entity instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)entity;

			if (CavernAPI.dimension.isInCaveDimensions(player) && player.getBedLocation() == null)
			{
				player.setSpawnPoint(player.getPosition(), true);
			}
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, float rotationYaw)
	{
		double d0 = -1.0D;
		int x = MathHelper.floor(entity.posX);
		int z = MathHelper.floor(entity.posZ);
		boolean flag = true;
		BlockPos pos = BlockPos.ORIGIN;
		long coord = ChunkPos.asLong(x, z);

		if (destinationCoordinateCache.containsKey(coord))
		{
			PortalPosition portalpos = destinationCoordinateCache.get(coord);
			d0 = 0.0D;
			pos = portalpos;
			portalpos.lastUpdateTime = world.getTotalWorldTime();
			flag = false;
		}
		else
		{
			BlockPos origin = new BlockPos(entity);

			for (int px = -128; px <= 128; ++px)
			{
				BlockPos current;

				for (int pz = -128; pz <= 128; ++pz)
				{
					for (BlockPos blockpos = origin.add(px, world.getActualHeight() - 1 - origin.getY(), pz); blockpos.getY() >= 0; blockpos = current)
					{
						current = blockpos.down();

						if (isPortalBlock(world.getBlockState(blockpos)))
						{
							while (isPortalBlock(world.getBlockState(current = blockpos.down())))
							{
								blockpos = current;
							}

							double dist = blockpos.distanceSq(origin);

							if (d0 < 0.0D || dist < d0)
							{
								d0 = dist;
								pos = blockpos;
							}
						}
					}
				}
			}
		}

		if (d0 >= 0.0D)
		{
			if (flag)
			{
				destinationCoordinateCache.put(coord, new PortalPosition(pos, world.getTotalWorldTime()));
			}

			IPortalCache cache = PortalCache.get(entity);
			double posX = pos.getX() + 0.5D;
			double posZ = pos.getZ() + 0.5D;
			BlockPattern.PatternHelper pattern = portal.createPatternHelper(world, pos);
			boolean flag1 = pattern.getForwards().rotateY().getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE;
			double d1 = pattern.getForwards().getAxis() == EnumFacing.Axis.X ? (double)pattern.getFrontTopLeft().getZ() : (double)pattern.getFrontTopLeft().getX();
			double posY = pattern.getFrontTopLeft().getY() + 1 - cache.getLastPortalVec().y * pattern.getHeight();

			if (flag1)
			{
				++d1;
			}

			if (pattern.getForwards().getAxis() == EnumFacing.Axis.X)
			{
				posZ = d1 + (1.0D - cache.getLastPortalVec().x) * pattern.getWidth() * pattern.getForwards().rotateY().getAxisDirection().getOffset();
			}
			else
			{
				posX = d1 + (1.0D - cache.getLastPortalVec().x) * pattern.getWidth() * pattern.getForwards().rotateY().getAxisDirection().getOffset();
			}

			float f = 0.0F;
			float f1 = 0.0F;
			float f2 = 0.0F;
			float f3 = 0.0F;

			if (pattern.getForwards().getOpposite() == cache.getTeleportDirection())
			{
				f = 1.0F;
				f1 = 1.0F;
			}
			else if (pattern.getForwards().getOpposite() == cache.getTeleportDirection().getOpposite())
			{
				f = -1.0F;
				f1 = -1.0F;
			}
			else if (pattern.getForwards().getOpposite() == cache.getTeleportDirection().rotateY())
			{
				f2 = 1.0F;
				f3 = -1.0F;
			}
			else
			{
				f2 = -1.0F;
				f3 = 1.0F;
			}

			double d3 = entity.motionX;
			double d4 = entity.motionZ;
			entity.motionX = d3 * f + d4 * f3;
			entity.motionZ = d3 * f2 + d4 * f1;
			entity.rotationYaw = rotationYaw - cache.getTeleportDirection().getOpposite().getHorizontalIndex() * 90 + pattern.getForwards().getHorizontalIndex() * 90;

			if (entity instanceof EntityPlayerMP)
			{
				((EntityPlayerMP)entity).connection.setPlayerLocation(posX, posY, posZ, entity.rotationYaw, entity.rotationPitch);
			}
			else
			{
				entity.setLocationAndAngles(posX, posY, posZ, entity.rotationYaw, entity.rotationPitch);
			}

			return true;
		}

		return false;
	}

	protected boolean isNotAir(BlockPos pos)
	{
		return !world.isAirBlock(pos) || !world.isAirBlock(pos.up());
	}

	protected boolean isPortalBlock(IBlockState state)
	{
		return state.getBlock() == portal;
	}

	@Override
	public boolean makePortal(Entity entity)
	{
		int range = 16;
		double d0 = -1.0D;
		int x = MathHelper.floor(entity.posX);
		int y = MathHelper.floor(entity.posY);
		int z = MathHelper.floor(entity.posZ);
		int x1 = x;
		int y1 = y;
		int z1 = z;
		int i = 0;
		int j = random.nextInt(4);
		MutableBlockPos pos = new MutableBlockPos();

		for (int px = x - range; px <= x + range; ++px)
		{
			double xSize = px + 0.5D - entity.posX;

			for (int pz = z - range; pz <= z + range; ++pz)
			{
				double zSize = pz + 0.5D - entity.posZ;

				outside: for (int py = world.getActualHeight() - 1; py >= 0; --py)
				{
					if (world.isAirBlock(pos.setPos(px, py, pz)))
					{
						while (py > 0 && world.isAirBlock(pos.setPos(px, py - 1, pz)))
						{
							--py;
						}

						for (int k = j; k < j + 4; ++k)
						{
							int i1 = k % 2;
							int j1 = 1 - i1;

							if (k % 4 >= 2)
							{
								i1 = -i1;
								j1 = -j1;
							}

							for (int size1 = 0; size1 < 3; ++size1)
							{
								for (int size2 = 0; size2 < 4; ++size2)
								{
									for (int height = -1; height < 4; ++height)
									{
										int checkX = px + (size2 - 1) * i1 + size1 * j1;
										int checkY = py + height;
										int checkZ = pz + (size2 - 1) * j1 - size1 * i1;

										pos.setPos(checkX, checkY, checkZ);

										if (height < 0 && !world.getBlockState(pos).getMaterial().isSolid() || height >= 0 && !world.isAirBlock(pos))
										{
											continue outside;
										}
									}
								}
							}

							double ySize = py + 0.5D - entity.posY;
							double size = xSize * xSize + ySize * ySize + zSize * zSize;

							if (d0 < 0.0D || size < d0)
							{
								d0 = size;
								x1 = px;
								y1 = py;
								z1 = pz;
								i = k % 4;
							}
						}
					}
				}
			}
		}

		if (d0 < 0.0D)
		{
			for (int px = x - range; px <= x + range; ++px)
			{
				double xSize = px + 0.5D - entity.posX;

				for (int pz = z - range; pz <= z + range; ++pz)
				{
					double zSize = pz + 0.5D - entity.posZ;

					outside: for (int py = world.getActualHeight() - 1; py >= 0; --py)
					{
						if (world.isAirBlock(pos.setPos(px, py, pz)))
						{
							while (py > 0 && world.isAirBlock(pos.setPos(px, py - 1, pz)))
							{
								--py;
							}

							for (int k = j; k < j + 2; ++k)
							{
								int i1 = k % 2;
								int j1 = 1 - i1;

								for (int width = 0; width < 4; ++width)
								{
									for (int height = -1; height < 4; ++height)
									{
										int px1 = px + (width - 1) * i1;
										int py1 = py + height;
										int pz1 = pz + (width - 1) * j1;

										pos.setPos(px1, py1, pz1);

										if (height < 0 && !world.getBlockState(pos).getMaterial().isSolid() || height >= 0 && !world.isAirBlock(pos))
										{
											continue outside;
										}
									}
								}

								double ySize = py + 0.5D - entity.posY;
								double size = xSize * xSize + ySize * ySize + zSize * zSize;

								if (d0 < 0.0D || size < d0)
								{
									d0 = size;
									x1 = px;
									y1 = py;
									z1 = pz;
									i = k % 2;
								}
							}
						}
					}
				}
			}
		}

		int x2 = x1;
		int y2 = y1;
		int z2 = z1;
		int i1 = i % 2;
		int j1 = 1 - i1;

		if (i % 4 >= 2)
		{
			i1 = -i1;
			j1 = -j1;
		}

		if (d0 < 0.0D)
		{
			y1 = MathHelper.clamp(y1, CavernAPI.dimension.isInCaves(entity) ? 10 : 70, world.getActualHeight() - 10);
			y2 = y1;

			for (int size1 = -1; size1 <= 1; ++size1)
			{
				for (int size2 = 1; size2 < 3; ++size2)
				{
					for (int height = -1; height < 3; ++height)
					{
						int blockX = x2 + (size2 - 1) * i1 + size1 * j1;
						int blockY = y2 + height;
						int blockZ = z2 + (size2 - 1) * j1 - size1 * i1;
						boolean flag = height < 0;

						world.setBlockState(pos.setPos(blockX, blockY, blockZ), flag ? MOSSY_COBBLESTONE : AIR);
					}
				}
			}
		}

		IBlockState state = portal.getDefaultState().withProperty(BlockPortal.AXIS, i1 != 0 ? EnumFacing.Axis.X : EnumFacing.Axis.Z);

		for (int width = 0; width < 4; ++width)
		{
			for (int height = -1; height < 4; ++height)
			{
				int blockX = x2 + (width - 1) * i1;
				int blockY = y2 + height;
				int blockZ = z2 + (width - 1) * j1;
				boolean flag = width == 0 || width == 3 || height == -1 || height == 3;

				world.setBlockState(pos.setPos(blockX, blockY, blockZ), flag ? MOSSY_COBBLESTONE : state, 2);
			}
		}

		for (int width = 0; width < 4; ++width)
		{
			for (int height = -1; height < 4; ++height)
			{
				int blockX = x2 + (width - 1) * i1;
				int blockY = y2 + height;
				int blockZ = z2 + (width - 1) * j1;

				world.notifyNeighborsOfStateChange(pos.setPos(blockX, blockY, blockZ), world.getBlockState(pos).getBlock(), false);
			}
		}

		return true;
	}
}