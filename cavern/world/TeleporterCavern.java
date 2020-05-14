package cavern.world;

import java.util.Random;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ObjectUtils;

import cavern.api.IPortalCache;
import cavern.block.BlockPortalCavern;
import cavern.stats.PortalCache;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.ITeleporter;

public class TeleporterCavern implements ITeleporter
{
	private static final IBlockState AIR = Blocks.AIR.getDefaultState();
	private static final IBlockState MOSSY_STONE = Blocks.MOSSY_COBBLESTONE.getDefaultState();

	protected final WorldServer world;
	protected final Random random;
	protected final BlockPortalCavern portalBlock;

	private IPortalCache teleporterCache;

	public TeleporterCavern(WorldServer world, BlockPortalCavern portal)
	{
		this.world = world;
		this.random = new Random(world.getSeed());
		this.portalBlock = portal;
	}

	protected IPortalCache getCache(ICapabilityProvider provider)
	{
		if (teleporterCache != null)
		{
			return teleporterCache;
		}

		teleporterCache = PortalCache.get(provider);

		return teleporterCache;
	}

	@Override
	public void placeEntity(World worldIn, Entity entity, float yaw)
	{
		int baseY = world.getSeaLevel();
		int rangeY = 30;
		int min = baseY - rangeY;
		int max = baseY + rangeY;
		int worldHeight = world.getActualHeight();
		max = Math.min(min < 0 ? max + Math.abs(min) : max, worldHeight);
		min = Math.max(max > worldHeight ? Math.max(min - (max - worldHeight), 0) : min, 0);
		BlockPos pos = new BlockPos(entity);

		world.getChunkProvider().loadChunk(pos.getX() >> 4, pos.getZ() >> 4);

		if (placeInPortal(entity, yaw, 32, min, max, null))
		{
			return;
		}

		BlockPos portalPos = makePortal(entity, 16, min, max, false);

		if (portalPos != null && placeInPortal(entity, yaw, 0, min, max, portalPos))
		{
			return;
		}

		placeInPortal(entity, yaw, 0, min, max, makePortal(entity, 16, min, max, true));
	}

	public boolean placeInPortal(Entity entity, float rotationYaw, int checkRange, int checkMin, int checkMax, @Nullable BlockPos checkPos)
	{
		double portalDist = -1.0D;
		BlockPos pos = BlockPos.ORIGIN;

		if (checkPos != null)
		{
			portalDist = 0.0D;
			pos = checkPos;
		}
		else
		{
			BlockPos origin = new BlockPos(entity);
			MutableBlockPos current = new MutableBlockPos();

			current.setPos(origin.getX(), checkMax, origin.getZ());

			while (current.getY() > checkMin)
			{
				current.move(EnumFacing.DOWN);

				if (world.getBlockState(current).getBlock() == portalBlock)
				{
					while (world.getBlockState(current.move(EnumFacing.DOWN)).getBlock() == portalBlock)
					{
						;
					}

					current.move(EnumFacing.UP);

					double dist = current.distanceSq(origin);

					if (portalDist < 0.0D || dist < portalDist)
					{
						portalDist = dist;
						pos = new BlockPos(current);
					}
				}
			}

			if (portalDist < 0.0D)
			{
				for (int range = 1; range <= checkRange; ++range)
				{
					for (int i = -range; i <= range; ++i)
					{
						for (int j = -range; j <= range; ++j)
						{
							current.setPos(origin.getX() + i, checkMax, origin.getZ() + j);

							while (current.getY() > checkMin)
							{
								current.move(EnumFacing.DOWN);

								if (world.getBlockState(current).getBlock() == portalBlock)
								{
									while (world.getBlockState(current.move(EnumFacing.DOWN)).getBlock() == portalBlock)
									{
										;
									}

									current.move(EnumFacing.UP);

									double dist = current.distanceSq(origin);

									if (portalDist < 0.0D || dist < portalDist)
									{
										portalDist = dist;
										pos = new BlockPos(current);
									}
								}
							}
						}
					}

					if (portalDist >= 0.0D)
					{
						break;
					}
				}
			}
		}

		if (portalDist >= 0.0D)
		{
			IPortalCache cache = getCache(entity);
			Vec3d portalVec = ObjectUtils.defaultIfNull(cache.getLastPortalVec(), Vec3d.ZERO);
			EnumFacing teleportDirection = ObjectUtils.defaultIfNull(cache.getTeleportDirection(), EnumFacing.NORTH);
			double posX = pos.getX() + 0.5D;
			double posZ = pos.getZ() + 0.5D;
			BlockPattern.PatternHelper pattern = portalBlock.createPatternHelper(world, pos);
			boolean flag1 = pattern.getForwards().rotateY().getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE;
			double d1 = pattern.getForwards().getAxis() == EnumFacing.Axis.X ? (double)pattern.getFrontTopLeft().getZ() : (double)pattern.getFrontTopLeft().getX();
			double posY = pattern.getFrontTopLeft().getY() + 1 - portalVec.y * pattern.getHeight();

			if (flag1)
			{
				++d1;
			}

			if (pattern.getForwards().getAxis() == EnumFacing.Axis.X)
			{
				posZ = d1 + (1.0D - portalVec.x) * pattern.getWidth() * pattern.getForwards().rotateY().getAxisDirection().getOffset();
			}
			else
			{
				posX = d1 + (1.0D - portalVec.x) * pattern.getWidth() * pattern.getForwards().rotateY().getAxisDirection().getOffset();
			}

			float f1 = 0.0F;
			float f2 = 0.0F;
			float f3 = 0.0F;
			float f4 = 0.0F;

			if (pattern.getForwards().getOpposite() == teleportDirection)
			{
				f1 = 1.0F;
				f2 = 1.0F;
			}
			else if (pattern.getForwards().getOpposite() == teleportDirection.getOpposite())
			{
				f1 = -1.0F;
				f2 = -1.0F;
			}
			else if (pattern.getForwards().getOpposite() == teleportDirection.rotateY())
			{
				f3 = 1.0F;
				f4 = -1.0F;
			}
			else
			{
				f3 = -1.0F;
				f4 = 1.0F;
			}

			double mx = entity.motionX;
			double mz = entity.motionZ;

			entity.motionX = mx * f1 + mz * f4;
			entity.motionZ = mx * f3 + mz * f2;
			entity.rotationYaw = rotationYaw - teleportDirection.getOpposite().getHorizontalIndex() * 90 + pattern.getForwards().getHorizontalIndex() * 90;

			entity.setPositionAndUpdate(posX, posY, posZ);

			return true;
		}

		return false;
	}

	@Nullable
	public BlockPos makePortal(Entity entity, int findRange, int findMin, int findMax, boolean force)
	{
		double portalDist = -1.0D;
		int x = MathHelper.floor(entity.posX);
		int y = MathHelper.floor(entity.posY);
		int z = MathHelper.floor(entity.posZ);
		int x1 = x;
		int y1 = y;
		int z1 = z;
		int i = 0;
		int j = random.nextInt(4);
		MutableBlockPos pos = new MutableBlockPos();

		for (int range = 1; range <= findRange; ++range)
		{
			for (int ix = -range; ix <= range; ++ix)
			{
				for (int iz = -range; iz <= range; ++iz)
				{
					int px = x + ix;
					int pz = z + iz;
					double xSize = px + 0.5D - entity.posX;
					double zSize = pz + 0.5D - entity.posZ;

					outside: for (int py = findMax - 1; py > findMin; --py)
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

								if (portalDist < 0.0D || size < portalDist)
								{
									portalDist = size;
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

			if (portalDist >= 0.0D)
			{
				break;
			}
		}

		if (portalDist < 0.0D)
		{
			for (int range = 1; range <= findRange; ++range)
			{
				for (int ix = -range; ix <= range; ++ix)
				{
					for (int iz = -range; iz <= range; ++iz)
					{
						int px = x + ix;
						int pz = z + iz;
						double xSize = px + 0.5D - entity.posX;
						double zSize = pz + 0.5D - entity.posZ;

						outside: for (int py = findMax - 1; py > findMin; --py)
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

									if (portalDist < 0.0D || size < portalDist)
									{
										portalDist = size;
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

				if (portalDist >= 0.0D)
				{
					break;
				}
			}
		}

		if (!force && portalDist < 0.0D)
		{
			return null;
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

		if (portalDist < 0.0D)
		{
			y1 = MathHelper.clamp(y1, findMin, findMax - 10);
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

						world.setBlockState(pos.setPos(blockX, blockY, blockZ), flag ? MOSSY_STONE : AIR);
					}
				}
			}
		}

		IBlockState portalState = portalBlock.getDefaultState().withProperty(BlockPortalCavern.AXIS, i1 != 0 ? EnumFacing.Axis.X : EnumFacing.Axis.Z);
		BlockPos portalPos = null;

		for (int width = 0; width < 4; ++width)
		{
			for (int height = -1; height < 4; ++height)
			{
				int blockX = x2 + (width - 1) * i1;
				int blockY = y2 + height;
				int blockZ = z2 + (width - 1) * j1;
				boolean flag = width == 0 || width == 3 || height == -1 || height == 3;

				world.setBlockState(pos.setPos(blockX, blockY, blockZ), flag ? MOSSY_STONE : portalState, flag ? 2 : 18);

				if (width == 1 && height == 0)
				{
					portalPos = new BlockPos(blockX + 0.5D, blockY + 0.5D, blockZ + 0.5D);
				}
			}
		}

		return portalPos;
	}
}