package cavern.world;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class CaveEntitySpawner
{
    private static final int MOB_COUNT_DIV = (int)Math.pow(17.0D, 2.0D);

	private final Set<ChunkPos> eligibleChunksForSpawning = Sets.newHashSet();
	private final IWorldEntitySpawner entitySpawner;

	public CaveEntitySpawner()
	{
		this(null);
	}

	public CaveEntitySpawner(@Nullable IWorldEntitySpawner spawner)
	{
		this.entitySpawner = spawner;
	}

	public int findChunksForSpawning(WorldServer world, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate)
	{
		if (!spawnHostileMobs && !spawnPeacefulMobs)
		{
			return 0;
		}
		else
		{
			eligibleChunksForSpawning.clear();

			int playerCount = 0;
			double playerHeight = 0.0D;

			for (EntityPlayer player : world.playerEntities)
			{
				if (!player.isSpectator())
				{
					int j = MathHelper.floor(player.posX / 16.0D);
					int k = MathHelper.floor(player.posZ / 16.0D);
					int range = 8;

					for (int i1 = -range; i1 <= range; ++i1)
					{
						for (int j1 = -range; j1 <= range; ++j1)
						{
							boolean flag = i1 == -range || i1 == range || j1 == -range || j1 == range;
							ChunkPos pos = new ChunkPos(i1 + j, j1 + k);

							if (!eligibleChunksForSpawning.contains(pos))
							{
								++playerCount;

								playerHeight += player.posY;

								if (!flag && world.getWorldBorder().contains(pos))
								{
									PlayerChunkMapEntry entry = world.getPlayerChunkMap().getEntry(pos.x, pos.z);

									if (entry != null && entry.isSentToPlayers())
									{
										eligibleChunksForSpawning.add(pos);
									}
								}
							}
						}
					}
				}
			}

			int playerY = playerHeight > 0.0D ? MathHelper.ceil(playerHeight / playerCount) : 50;
			int totalCount = 0;
			BlockPos spawnPos = world.getSpawnPoint();

			for (EnumCreatureType type : EnumCreatureType.values())
			{
				int maxNumber = getMaxNumberOfCreature(world, spawnHostileMobs, spawnPeacefulMobs, spawnOnSetTickRate, type);
				double range = getSpawnRange(world, spawnHostileMobs, spawnPeacefulMobs, spawnOnSetTickRate, type);

				if (maxNumber > 0 && canSpawnCreature(world, spawnHostileMobs, spawnPeacefulMobs, spawnOnSetTickRate, type))
				{
					int max = maxNumber * playerCount / MOB_COUNT_DIV;

					if (world.countEntities(type, true) <= max)
					{
						List<ChunkPos> shuffled = Lists.newArrayList(eligibleChunksForSpawning);
						Collections.shuffle(shuffled);

						MutableBlockPos pos = new MutableBlockPos();

						outside: for (ChunkPos chunkpos : shuffled)
						{
							BlockPos blockpos = getRandomPosition(world, chunkpos.x, playerY, chunkpos.z);
							int originX = blockpos.getX();
							int originY = blockpos.getY();
							int originZ = blockpos.getZ();
							IBlockState state = world.getBlockState(blockpos);

							if (!state.isNormalCube())
							{
								int mobCount = 0;

								for (int l = 0; l < 3; ++l)
								{
									int x = originX;
									int y = originY;
									int z = originZ;
									int n = 6;
									Biome.SpawnListEntry entry = null;
									IEntityLivingData data = null;
									int f = MathHelper.ceil(Math.random() * 4.0D);

									for (int m = 0; m < f; ++m)
									{
										x += world.rand.nextInt(n) - world.rand.nextInt(n);
										y += world.rand.nextInt(1) - world.rand.nextInt(1);
										z += world.rand.nextInt(n) - world.rand.nextInt(n);
										pos.setPos(x, y, z);

										float posX = x + 0.5F;
										float posZ = z + 0.5F;

										if (!world.isAnyPlayerWithinRangeAt(posX, y, posZ, range) && spawnPos.distanceSq(posX, y, posZ) >= range * range)
										{
											if (entry == null)
											{
												entry = getSpawnListEntryForTypeAt(world, type, pos);

												if (entry == null)
												{
													break;
												}
											}

											if (canCreatureTypeSpawnHere(world, type, entry, pos) &&
												WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementForEntity(entry.entityClass), world, pos))
											{
												EntityLiving entity = createSpawnCreature(world, type, spawnPos, entry);

												if (entity == null)
												{
													continue outside;
												}

												entity.setLocationAndAngles(posX, y, posZ, world.rand.nextFloat() * 360.0F, 0.0F);

												Result canSpawn = ForgeEventFactory.canEntitySpawn(entity, world, posX, y, posZ, null);

												if (canSpawn == Result.ALLOW || canSpawn == Result.DEFAULT && entity.getCanSpawnHere() && entity.isNotColliding())
												{
													if (!ForgeEventFactory.doSpecialSpawn(entity, world, posX, y, posZ, null))
													{
														data = entity.onInitialSpawn(world.getDifficultyForLocation(entity.getPosition()), data);
													}

													if (entity.isNotColliding())
													{
														++mobCount;

														world.spawnEntity(entity);
													}
													else
													{
														entity.setDead();
													}

													if (mobCount >= ForgeEventFactory.getMaxSpawnPackSize(entity))
													{
														continue outside;
													}
												}

												totalCount += mobCount;
											}
										}
									}
								}
							}
						}
					}
				}
			}

			return totalCount;
		}
	}

	protected boolean canSpawnCreature(WorldServer world, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, EnumCreatureType type)
	{
		if (entitySpawner != null)
		{
			Boolean ret = entitySpawner.canSpawnCreature(world, spawnHostileMobs, spawnPeacefulMobs, spawnOnSetTickRate, type);

			if (ret != null)
			{
				return ret.booleanValue();
			}
		}

		return (!type.getPeacefulCreature() || spawnPeacefulMobs) && (type.getPeacefulCreature() || spawnHostileMobs) && (!type.getAnimal() || spawnOnSetTickRate);
	}

	protected int getMaxNumberOfCreature(WorldServer world, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, EnumCreatureType type)
	{
		if (entitySpawner != null)
		{
			Integer ret = entitySpawner.getMaxNumberOfCreature(world, spawnHostileMobs, spawnPeacefulMobs, spawnOnSetTickRate, type);

			if (ret != null)
			{
				return ret.intValue();
			}
		}

		return type.getMaxNumberOfCreature();
	}

	protected double getSpawnRange(WorldServer world, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, EnumCreatureType type)
	{
		if (entitySpawner != null)
		{
			Double ret = entitySpawner.getSpawnRange(world, spawnHostileMobs, spawnPeacefulMobs, spawnOnSetTickRate, type);

			if (ret != null)
			{
				return ret.doubleValue();
			}
		}

		return 24.0D;
	}

	protected BlockPos getRandomPosition(World world, int chunkX, int y, int chunkZ)
	{
		int posX = (chunkX << 4) + world.rand.nextInt(16);
		int posZ = (chunkZ << 4) + world.rand.nextInt(16);
		int posY = MathHelper.getInt(world.rand, Math.max(y - 50, 1), Math.min(y + 50, world.getActualHeight()));

		return new BlockPos(posX, posY, posZ);
	}

	@Nullable
	protected Biome.SpawnListEntry getSpawnListEntryForTypeAt(WorldServer world, EnumCreatureType creatureType, BlockPos pos)
	{
		List<Biome.SpawnListEntry> list = world.getBiome(pos).getSpawnableList(creatureType);

		list = ForgeEventFactory.getPotentialSpawns(world, creatureType, pos, list);

		return list != null && !list.isEmpty() ? WeightedRandom.getRandomItem(world.rand, list) : null;
	}

	protected boolean canCreatureTypeSpawnHere(WorldServer world, EnumCreatureType creatureType, Biome.SpawnListEntry spawnListEntry, BlockPos pos)
	{
		List<Biome.SpawnListEntry> list = world.getBiome(pos).getSpawnableList(creatureType);

		list = ForgeEventFactory.getPotentialSpawns(world, creatureType, pos, list);

		return list != null && !list.isEmpty() ? list.contains(spawnListEntry) : false;
	}

	@Nullable
	protected EntityLiving createSpawnCreature(WorldServer world, EnumCreatureType type, BlockPos pos, Biome.SpawnListEntry entry)
	{
		if (entitySpawner != null)
		{
			EntityLiving entity = entitySpawner.createSpawnCreature(world, type, pos, entry);

			if (entity != null)
			{
				return entity;
			}
		}

		return (EntityLiving)EntityList.newEntity(entry.entityClass, world);
	}

	public interface IWorldEntitySpawner
	{
		@Nullable
		default Boolean canSpawnCreature(WorldServer world, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, EnumCreatureType type)
		{
			return null;
		}

		@Nullable
		Integer getMaxNumberOfCreature(WorldServer world, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, EnumCreatureType type);

		@Nullable
		default Double getSpawnRange(WorldServer world, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, EnumCreatureType type)
		{
			return null;
		}

		@Nullable
		default EntityLiving createSpawnCreature(WorldServer world, EnumCreatureType type, BlockPos pos, Biome.SpawnListEntry entry)
		{
			return null;
		}
	}
}