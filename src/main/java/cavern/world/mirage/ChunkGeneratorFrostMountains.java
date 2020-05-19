package cavern.world.mirage;

import java.util.List;
import java.util.Random;

import cavern.block.CaveBlocks;
import cavern.world.gen.MapGenFrostCaves;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class ChunkGeneratorFrostMountains implements IChunkGenerator
{
	protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
	protected static final IBlockState STONE = Blocks.STONE.getDefaultState();
	protected static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
	protected static final IBlockState WATER = Blocks.WATER.getDefaultState();
	protected static final IBlockState ICE = Blocks.ICE.getDefaultState();
	protected static final IBlockState PACKED_ICE = Blocks.PACKED_ICE.getDefaultState();
	protected static final IBlockState GRAVEL = Blocks.GRAVEL.getDefaultState();

	private final World world;
	private final Random rand;

	private NoiseGeneratorOctaves minLimitPerlinNoise;
	private NoiseGeneratorOctaves maxLimitPerlinNoise;
	private NoiseGeneratorOctaves mainPerlinNoise;
	private NoiseGeneratorPerlin surfaceNoise;
	private NoiseGeneratorOctaves depthNoise;

	private final double[] heightMap;
	private final float[] biomeWeights;

	private double[] depthBuffer = new double[256];
	private double[] mainNoiseRegion;
	private double[] minLimitRegion;
	private double[] maxLimitRegion;
	private double[] depthRegion;

	private final MapGenBase caveGenerator = new MapGenFrostCaves();

	public ChunkGeneratorFrostMountains(World world)
	{
		this.world = world;
		this.rand = new Random(world.getSeed());
		this.minLimitPerlinNoise = new NoiseGeneratorOctaves(rand, 16);
		this.maxLimitPerlinNoise = new NoiseGeneratorOctaves(rand, 16);
		this.mainPerlinNoise = new NoiseGeneratorOctaves(rand, 8);
		this.surfaceNoise = new NoiseGeneratorPerlin(rand, 4);
		this.depthNoise = new NoiseGeneratorOctaves(rand, 16);
		this.heightMap = new double[825];
		this.biomeWeights = new float[25];

		for (int i = -2; i <= 2; ++i)
		{
			for (int j = -2; j <= 2; ++j)
			{
				float f = 10.0F / MathHelper.sqrt(i * i + j * j + 0.2F);

				biomeWeights[i + 2 + (j + 2) * 5] = f;
			}
		}
	}

	public void setBlocksInChunk(int x, int z, ChunkPrimer primer)
	{
		generateHeightmap(x * 4, 0, z * 4);

		for (int i = 0; i < 4; ++i)
		{
			int j = i * 5;
			int k = (i + 1) * 5;

			for (int l = 0; l < 4; ++l)
			{
				int i1 = (j + l) * 33;
				int j1 = (j + l + 1) * 33;
				int k1 = (k + l) * 33;
				int l1 = (k + l + 1) * 33;

				for (int i2 = 0; i2 < 32; ++i2)
				{
					double d1 = heightMap[i1 + i2];
					double d2 = heightMap[j1 + i2];
					double d3 = heightMap[k1 + i2];
					double d4 = heightMap[l1 + i2];
					double d5 = (heightMap[i1 + i2 + 1] - d1) * 0.125D;
					double d6 = (heightMap[j1 + i2 + 1] - d2) * 0.125D;
					double d7 = (heightMap[k1 + i2 + 1] - d3) * 0.125D;
					double d8 = (heightMap[l1 + i2 + 1] - d4) * 0.125D;

					for (int j2 = 0; j2 < 8; ++j2)
					{
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * 0.25D;
						double d13 = (d4 - d2) * 0.25D;

						for (int k2 = 0; k2 < 4; ++k2)
						{
							double d16 = (d11 - d10) * 0.25D;
							double lvt_45_1_ = d10 - d16;

							for (int l2 = 0; l2 < 4; ++l2)
							{
								if ((lvt_45_1_ += d16) > 0.0D)
								{
									primer.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2, STONE);
								}
								else if (i2 * 8 + j2 < 63)
								{
									primer.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2, WATER);
								}
							}

							d10 += d12;
							d11 += d13;
						}

						d1 += d5;
						d2 += d6;
						d3 += d7;
						d4 += d8;
					}
				}
			}
		}
	}

	public void replaceBiomeBlocks(int x, int z, ChunkPrimer primer)
	{
		depthBuffer = surfaceNoise.getRegion(depthBuffer, x * 16, z * 16, 16, 16, 0.0625D, 0.0625D, 1.0D);

		for (int i = 0; i < 16; ++i)
		{
			for (int j = 0; j < 16; ++j)
			{
				generateTerrain(world, rand, primer, x * 16 + i, z * 16 + j, depthBuffer[j + i * 16]);
			}
		}
	}

	public void generateTerrain(World world, Random rand, ChunkPrimer primer, int x, int z, double noiseVal)
	{
		int i = world.getSeaLevel();
		IBlockState top = ICE;
		IBlockState fillter = CaveBlocks.SLIPPERY_ICE.getDefaultState();
		int j = -1;
		int k = (int)(noiseVal / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
		int l = x & 15;
		int m = z & 15;
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		for (int y = 255; y >= 0; --y)
		{
			if (y <= rand.nextInt(5))
			{
				primer.setBlockState(m, y, l, BEDROCK);
			}
			else
			{
				IBlockState state = primer.getBlockState(m, y, l);

				if (state.getMaterial() == Material.AIR)
				{
					j = -1;
				}
				else if (state.getBlock() == Blocks.STONE)
				{
					if (j == -1)
					{
						if (k <= 0)
						{
							top = AIR;
							fillter = STONE;
						}
						else if (y >= i - 4 && y <= i + 1)
						{
							top = ICE;
							fillter = PACKED_ICE;
						}

						if (y < i && (top == null || top.getMaterial() == Material.AIR))
						{
							if (Biomes.ICE_MOUNTAINS.getTemperature(pos.setPos(x, y, z)) < 0.15F)
							{
								top = ICE;
							}
							else
							{
								top = WATER;
							}
						}

						j = k;

						if (y >= i - 1)
						{
							primer.setBlockState(m, y, l, top);
						}
						else if (y < i - 7 - k)
						{
							top = AIR;
							fillter = STONE;

							primer.setBlockState(m, y, l, GRAVEL);
						}
						else
						{
							primer.setBlockState(m, y, l, fillter);
						}
					}
					else if (j > 0)
					{
						--j;

						primer.setBlockState(m, y, l, fillter);
					}
				}
			}
		}
	}

	private void generateHeightmap(int posX, int posY, int posZ)
	{
		depthRegion = depthNoise.generateNoiseOctaves(depthRegion, posX, posZ, 5, 5, 200.0D, 200.0D, 0.5D);
		mainNoiseRegion = mainPerlinNoise.generateNoiseOctaves(mainNoiseRegion, posX, posY, posZ, 5, 33, 5, 8.555150000000001D, 4.277575000000001D, 8.555150000000001D);
		minLimitRegion = minLimitPerlinNoise.generateNoiseOctaves(minLimitRegion, posX, posY, posZ, 5, 33, 5, 684.412D, 684.412D, 684.412D);
		maxLimitRegion = maxLimitPerlinNoise.generateNoiseOctaves(maxLimitRegion, posX, posY, posZ, 5, 33, 5, 684.412D, 684.412D, 684.412D);
		int i = 0;
		int j = 0;

		for (int k = 0; k < 5; ++k)
		{
			for (int l = 0; l < 5; ++l)
			{
				float f2 = 0.0F;
				float f3 = 0.0F;
				float f4 = 0.0F;

				for (int j1 = -2; j1 <= 2; ++j1)
				{
					for (int k1 = -2; k1 <= 2; ++k1)
					{
						float f5 = 0.6F;
						float f6 = 0.85F;
						float f7 = biomeWeights[j1 + 2 + (k1 + 2) * 5] / (f5 + 2.0F);

						f2 += f6 * f7;
						f3 += f5 * f7;
						f4 += f7;
					}
				}

				f2 = f2 / f4;
				f3 = f3 / f4;
				f2 = f2 * 0.9F + 0.1F;
				f3 = (f3 * 4.0F - 1.0F) / 8.0F;

				double d7 = depthRegion[j] / 8000.0D;

				if (d7 < 0.0D)
				{
					d7 = -d7 * 0.3D;
				}

				d7 = d7 * 3.0D - 2.0D;

				if (d7 < 0.0D)
				{
					d7 = d7 / 2.0D;

					if (d7 < -1.0D)
					{
						d7 = -1.0D;
					}

					d7 = d7 / 1.4D;
					d7 = d7 / 2.0D;
				}
				else
				{
					if (d7 > 1.0D)
					{
						d7 = 1.0D;
					}

					d7 = d7 / 8.0D;
				}

				++j;
				double d8 = f3;
				double d9 = f2;
				d8 = d8 + d7 * 0.2D;
				d8 = d8 * 8.5D / 8.0D;
				double d0 = 8.5D + d8 * 4.0D;

				for (int l1 = 0; l1 < 33; ++l1)
				{
					double d1 = (l1 - d0) * 12.0D * 128.0D / 256.0D / d9;

					if (d1 < 0.0D)
					{
						d1 *= 4.0D;
					}

					double d2 = minLimitRegion[i] / 512.0D;
					double d3 = maxLimitRegion[i] / 512.0D;
					double d4 = (mainNoiseRegion[i] / 10.0D + 1.0D) / 2.0D;
					double d5 = MathHelper.clampedLerp(d2, d3, d4) - d1;

					if (l1 > 29)
					{
						double d6 = (l1 - 29) / 3.0F;

						d5 = d5 * (1.0D - d6) + -10.0D * d6;
					}

					heightMap[i] = d5;
					++i;
				}
			}
		}
	}

	@Override
	public Chunk generateChunk(int x, int z)
	{
		rand.setSeed(x * 341873128712L + z * 132897987541L);

		ChunkPrimer primer = new ChunkPrimer();

		setBlocksInChunk(x, z, primer);
		replaceBiomeBlocks(x, z, primer);

		caveGenerator.generate(world, x, z, primer);

		Chunk chunk = new Chunk(this.world, primer, x, z);
		byte[] biomes = chunk.getBiomeArray();

		for (int i = 0; i < biomes.length; ++i)
		{
			biomes[i] = (byte)Biome.getIdForBiome(Biomes.ICE_MOUNTAINS);
		}

		chunk.generateSkylightMap();

		return chunk;
	}

	@Override
	public void populate(int x, int z)
	{
		BlockFalling.fallInstantly = true;

		int blockX = x * 16;
		int blockY = z * 16;
		BlockPos blockpos = new BlockPos(blockX, 0, blockY);
		rand.setSeed(world.getSeed());
		long xSeed = rand.nextLong() / 2L * 2L + 1L;
		long zSeed = rand.nextLong() / 2L * 2L + 1L;
		rand.setSeed(x * xSeed + z * zSeed ^ world.getSeed());

		ForgeEventFactory.onChunkPopulate(true, this, world, rand, x, z, false);

		if (TerrainGen.populate(this, world, rand, x, z, false, PopulateChunkEvent.Populate.EventType.LAKE))
		{
			int i1 = rand.nextInt(16) + 8;
			int j1 = rand.nextInt(128);
			int k1 = rand.nextInt(16) + 8;

			new WorldGenLakes(Blocks.WATER).generate(world, rand, blockpos.add(i1, j1, k1));
		}

		Biomes.ICE_MOUNTAINS.decorate(world, rand, new BlockPos(blockX, 0, blockY));

		blockpos = blockpos.add(8, 0, 8);

		if (TerrainGen.populate(this, world, rand, x, z, false, PopulateChunkEvent.Populate.EventType.ICE))
		{
			for (int i = 0; i < 16; ++i)
			{
				for (int j = 0; j < 16; ++j)
				{
					BlockPos top = world.getPrecipitationHeight(blockpos.add(i, 0, j));
					BlockPos pos = top.down();

					if (world.canBlockFreezeWater(pos))
					{
						world.setBlockState(pos, ICE, 2);
					}

					if (world.canSnowAt(top, true))
					{
						world.setBlockState(top, Blocks.SNOW_LAYER.getDefaultState(), 2);
					}
				}
			}
		}

		ForgeEventFactory.onChunkPopulate(false, this, world, rand, x, z, false);

		BlockFalling.fallInstantly = false;
	}

	@Override
	public boolean generateStructures(Chunk chunk, int x, int z)
	{
		return false;
	}

	@Override
	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
	{
		Biome biome = world.getBiome(pos);

		return biome.getSpawnableList(creatureType);
	}

	@Override
	public boolean isInsideStructure(World world, String structureName, BlockPos pos)
	{
		return false;
	}

	@Override
	public BlockPos getNearestStructurePos(World world, String structureName, BlockPos pos, boolean findUnexplored)
	{
		return null;
	}

	@Override
	public void recreateStructures(Chunk chunk, int x, int z) {}
}