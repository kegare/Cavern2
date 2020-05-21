package cavern.world.mirage;

import java.util.List;
import java.util.Random;

import cavern.world.gen.WorldGenSandHouse;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class ChunkGeneratorWideDesert implements IChunkGenerator
{
	protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
	protected static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
	protected static final IBlockState LAVA = Blocks.LAVA.getDefaultState();
	protected static final IBlockState SAND = Blocks.SAND.getDefaultState();
	protected static final IBlockState SANDSTONE = Blocks.SANDSTONE.getDefaultState();

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

	private final MapGenBase caveGenerator = new MapGenCaves();
	private final MapGenVillage villageGenerator = new MapGenVillage();

	private final WorldGenerator lakeLavaGen = new WorldGenLakes(Blocks.LAVA);
	private final WorldGenerator sandHouseGen = new WorldGenSandHouse();

	public ChunkGeneratorWideDesert(World world)
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
									primer.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2, SANDSTONE);
								}
								else if (i2 * 8 + j2 < 36)
								{
									primer.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2, LAVA);
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
		int i = 36;
		IBlockState top = SAND;
		IBlockState fillter = SAND;
		int j = -1;
		int k = (int)(noiseVal / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
		int l = x & 15;
		int m = z & 15;

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
				else if (state.getBlock() == Blocks.SANDSTONE)
				{
					if (j == -1)
					{
						if (k <= 0)
						{
							top = AIR;
							fillter = SAND;
						}
						else if (y >= i - 4 && y <= i + 1)
						{
							top = SAND;
							fillter = SAND;
						}

						if (y < i && (top == null || top.getMaterial() == Material.AIR))
						{
							top = LAVA;
						}

						j = k;

						if (y >= i - 1)
						{
							primer.setBlockState(m, y, l, top);
						}
						else if (y < i - 7 - k)
						{
							top = AIR;
							fillter = SANDSTONE;

							primer.setBlockState(m, y, l, SAND);
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
						float f5 = 0.1F;
						float f6 = 0.2F;
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
		villageGenerator.generate(world, x, z, primer);

		Chunk chunk = new Chunk(this.world, primer, x, z);
		byte[] biomes = chunk.getBiomeArray();

		for (int i = 0; i < biomes.length; ++i)
		{
			biomes[i] = (byte)Biome.getIdForBiome(Biomes.DESERT);
		}

		chunk.generateSkylightMap();

		return chunk;
	}

	@Override
	public void populate(int x, int z)
	{
		BlockFalling.fallInstantly = true;

		int blockX = x << 4;
		int blockZ = z << 4;
		BlockPos blockPos = new BlockPos(blockX, 0, blockZ);
		rand.setSeed(world.getSeed());
		long xSeed = rand.nextLong() / 2L * 2L + 1L;
		long zSeed = rand.nextLong() / 2L * 2L + 1L;
		rand.setSeed(x * xSeed + z * zSeed ^ world.getSeed());
		boolean flag = false;
		ChunkPos chunkpos = new ChunkPos(x, z);

		ForgeEventFactory.onChunkPopulate(true, this, world, rand, x, z, flag);

		flag = villageGenerator.generateStructure(world, rand, chunkpos);

		if (rand.nextInt(50) == 0 && TerrainGen.populate(this, world, rand, x, z, flag, PopulateChunkEvent.Populate.EventType.LAVA))
		{
			int genX = rand.nextInt(16) + 8;
			int genY = rand.nextInt(128);
			int genZ = rand.nextInt(16) + 8;

			lakeLavaGen.generate(world, rand, blockPos.add(genX, genY, genZ));
		}

		if (rand.nextInt(30) == 0)
		{
			int genX = rand.nextInt(16) + 8;
			int genZ = rand.nextInt(16) + 8;

			sandHouseGen.generate(world, rand, world.getTopSolidOrLiquidBlock(blockPos.add(genX, 0, genZ)));
		}

		Biomes.DESERT.decorate(world, rand, blockPos);

		ForgeEventFactory.onChunkPopulate(false, this, world, rand, x, z, flag);

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
		if ("Village".equals(structureName) && villageGenerator != null)
		{
			return villageGenerator.isInsideStructure(pos);
		}

		return false;
	}

	@Override
	public BlockPos getNearestStructurePos(World world, String structureName, BlockPos pos, boolean findUnexplored)
	{
		if ("Village".equals(structureName) && villageGenerator != null)
		{
			return villageGenerator.getNearestStructurePos(world, pos, findUnexplored);
		}

		return null;
	}

	@Override
	public void recreateStructures(Chunk chunk, int x, int z)
	{
		villageGenerator.generate(world, x, z, null);
	}
}