package cavern.world;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import cavern.block.BlockPortalCavern;
import cavern.capability.CaveCapabilities;
import cavern.config.GeneralConfig;
import cavern.core.Cavern;
import cavern.data.PortalCache;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;

public class TeleporterCavern implements ITeleporter
{
	private static final IBlockState AIR = Blocks.AIR.getDefaultState();
	private static final IBlockState MOSSY_STONE = Blocks.MOSSY_COBBLESTONE.getDefaultState();

	protected final BlockPortalCavern portalBlock;

	private Vec3d portalVec;
	private EnumFacing teleportDirection;

	public TeleporterCavern(BlockPortalCavern portal)
	{
		this.portalBlock = portal;
	}

	public TeleporterCavern setPortalInfo(Vec3d vec, EnumFacing direction)
	{
		portalVec = vec;
		teleportDirection = direction;

		return this;
	}

	@Override
	public void placeEntity(World world, Entity entity, float yaw)
	{
		int radius = GeneralConfig.findPortalRange;
		boolean placed = false;

		if (GeneralConfig.portalCache)
		{
			PortalCache cache = entity.getCapability(CaveCapabilities.PORTAL_CACHE, null);

			if (cache != null)
			{
				placed = placeInCachedPortal(world, entity, yaw, radius, cache);
			}
		}

		BlockPos pos = entity.getPosition();

		if (!placed)
		{
			CavePortalList portalList = world.getCapability(CaveCapabilities.CAVE_PORTAL_LIST, null);

			if (portalList != null)
			{
				placed = placeInStoredPortal(world, entity, yaw, radius, pos, portalList);
			}
		}

		if (!placed)
		{
			placed = placeInPortal(world, entity, yaw, radius, pos);

			if (!placed)
			{
				placed = placeInPortal(world, entity, yaw, radius, makePortal(world, entity, radius));
			}
		}
	}

	public boolean placeInCachedPortal(World world, Entity entity, float yaw, int radius, PortalCache cache)
	{
		ResourceLocation key = portalBlock.getRegistryName();
		DimensionType dim = world.provider.getDimensionType();
		BlockPos pos = cache.getLastPos(key, dim, null);

		if (pos == null)
		{
			return false;
		}

		return placeInPortal(world, entity, yaw, radius, pos);
	}

	public boolean placeInStoredPortal(World world, Entity entity, float yaw, int radius, BlockPos checkPos, CavePortalList list)
	{
		List<BlockPos> positions = list.getPortalPositions(portalBlock).stream()
			.filter(o -> new BlockPos(o.getX(), 0, o.getZ()).getDistance(checkPos.getX(), 0, checkPos.getZ()) <= radius)
			.sorted((o1, o2) -> Double.compare(o1.distanceSq(checkPos), o2.distanceSq(checkPos))).collect(Collectors.toList());

		for (BlockPos portalPos : positions)
		{
			if (placeInPortal(world, entity, yaw, 8, portalPos))
			{
				return true;
			}

			list.removePortal(portalBlock, portalPos);
		}

		return false;
	}

	public boolean placeInPortal(World world, Entity entity, float yaw, int radius, final BlockPos checkPos)
	{
		BlockPos pos = null;

		if (world.getBlockState(checkPos).getBlock() == portalBlock)
		{
			pos = checkPos;
		}
		else
		{
			int min = 1;
			int max = world.getActualHeight() - 1;
			MutableBlockPos findPos = new MutableBlockPos(checkPos);
			LongSet findChunks = new LongArraySet();

			findChunks.add(ChunkPos.asLong(checkPos.getX() >> 4, checkPos.getZ() >> 4));

			outside: for (int r = 1; r <= radius; ++r)
			{
				for (int i = -r; i <= r; ++i)
				{
					for (int j = -r; j <= r; ++j)
					{
						if (Math.abs(i) < r && Math.abs(j) < r) continue;

						int x = checkPos.getX() + i;
						int z = checkPos.getZ() + j;
						ChunkPos chunkPos = new ChunkPos(findPos.setPos(x, 0, z));

						if (findChunks.add(ChunkPos.asLong(chunkPos.x, chunkPos.z)))
						{
							Cavern.proxy.loadChunks(world, chunkPos.x, chunkPos.z, 1);
						}

						for (int y = checkPos.getY(); y < max; ++y)
						{
							if (world.getBlockState(findPos.setPos(x, y, z)).getBlock() == portalBlock)
							{
								pos = findPos.toImmutable();

								break outside;
							}
						}

						for (int y = checkPos.getY(); y > min; --y)
						{
							if (world.getBlockState(findPos.setPos(x, y, z)).getBlock() == portalBlock)
							{
								pos = findPos.toImmutable();

								break outside;
							}
						}
					}
				}
			}

			if (pos == null)
			{
				pos = world.getSpawnPoint();
			}

			if (world.getBlockState(pos).getBlock() != portalBlock)
			{
				return false;
			}
		}

		CavePortalList portalList = world.getCapability(CaveCapabilities.CAVE_PORTAL_LIST, null);

		if (portalList != null)
		{
			portalList.addPortal(portalBlock, pos);
		}

		if (portalVec == null)
		{
			portalVec = Vec3d.ZERO;
		}

		if (teleportDirection == null)
		{
			teleportDirection = EnumFacing.NORTH;
		}

		BlockPattern.PatternHelper pattern = portalBlock.createPatternHelper(world, pos);
		double posX = pos.getX() + 0.5D;
		double posY = pattern.getFrontTopLeft().getY() + 1 - portalVec.y * pattern.getHeight();
		double posZ = pos.getZ() + 0.5D;
		double d1 = pattern.getForwards().getAxis() == EnumFacing.Axis.X ? (double)pattern.getFrontTopLeft().getZ() : (double)pattern.getFrontTopLeft().getX();
		boolean flag = pattern.getForwards().rotateY().getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE;

		if (flag)
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
		entity.rotationYaw = yaw - teleportDirection.getOpposite().getHorizontalIndex() * 90 + pattern.getForwards().getHorizontalIndex() * 90;

		entity.setPositionAndUpdate(posX, posY, posZ);

		return true;
	}

	@Nullable
	public BlockPos makePortal(World world, Entity entity, int radius)
	{
		int originX = MathHelper.floor(entity.posX);
		int originY = MathHelper.floor(entity.posY);
		int originZ = MathHelper.floor(entity.posZ);
		int min = 10;
		int max = world.getActualHeight() - 10;
		int x = originX;
		int y = originY;
		int z = originZ;
		int i = 0;
		int j = world.rand.nextInt(4);
		MutableBlockPos pos = new MutableBlockPos();
		double portalDist = -1.0D;

		for (int r = 1; r <= radius; ++r)
		{
			for (int rx = -r; rx <= r; ++rx)
			{
				for (int rz = -r; rz <= r; ++rz)
				{
					if (Math.abs(rx) < r && Math.abs(rz) < r) continue;

					int px = originX + rx;
					int pz = originZ + rz;
					double xSize = px + 0.5D - entity.posX;
					double zSize = pz + 0.5D - entity.posZ;

					int py = min;

					while (py < max && !world.isAirBlock(pos.setPos(px, py, pz)))
					{
						++py;
					}

					if (py >= max)
					{
						continue;
					}

					outside: for (int k = j; k < j + 4; ++k)
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
							x = px;
							y = py;
							z = pz;
							i = k % 4;
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
			for (int r = 1; r <= radius; ++r)
			{
				for (int rx = -r; rx <= r; ++rx)
				{
					for (int rz = -r; rz <= r; ++rz)
					{
						if (Math.abs(rx) < r && Math.abs(rz) < r) continue;

						int px = originX + rx;
						int pz = originZ + rz;
						double xSize = px + 0.5D - entity.posX;
						double zSize = pz + 0.5D - entity.posZ;

						int py = min;

						while (py < max && !world.isAirBlock(pos.setPos(px, py, pz)))
						{
							++py;
						}

						if (py >= max)
						{
							continue;
						}

						outside: for (int k = j; k < j + 2; ++k)
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
								x = px;
								y = py;
								z = pz;
								i = k % 2;
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

		int x2 = x;
		int y1 = y;
		int z2 = z;
		int i1 = i % 2;
		int j1 = 1 - i1;

		if (i % 4 >= 2)
		{
			i1 = -i1;
			j1 = -j1;
		}

		if (portalDist < 0.0D)
		{
			y = MathHelper.clamp(y, min, max);
			y1 = y;

			for (int size1 = -1; size1 <= 1; ++size1)
			{
				for (int size2 = 1; size2 < 3; ++size2)
				{
					for (int height = -1; height < 3; ++height)
					{
						int blockX = x2 + (size2 - 1) * i1 + size1 * j1;
						int blockY = y1 + height;
						int blockZ = z2 + (size2 - 1) * j1 - size1 * i1;
						boolean isFloor = height < 0;

						world.setBlockState(pos.setPos(blockX, blockY, blockZ), isFloor ? MOSSY_STONE : AIR);
					}
				}
			}
		}

		IBlockState portalState = portalBlock.getDefaultState().withProperty(BlockPortal.AXIS, i1 != 0 ? EnumFacing.Axis.X : EnumFacing.Axis.Z);
		BlockPos portalPos = null;

		for (int width = 0; width < 4; ++width)
		{
			for (int height = -1; height < 4; ++height)
			{
				int blockX = x2 + (width - 1) * i1;
				int blockY = y1 + height;
				int blockZ = z2 + (width - 1) * j1;
				boolean isFrame = width == 0 || width == 3 || height == -1 || height == 3;

				world.setBlockState(pos.setPos(blockX, blockY, blockZ), isFrame ? MOSSY_STONE : portalState, 2);

				if (width == 1 && height == 0)
				{
					portalPos = new BlockPos(blockX + 0.5D, blockY + 0.5D, blockZ + 0.5D);
				}
			}
		}

		return portalPos;
	}
}