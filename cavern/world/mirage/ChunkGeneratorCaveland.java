package cavern.world.mirage;

import java.util.List;
import java.util.Random;

import cavern.config.CavelandConfig;
import cavern.world.gen.MapGenCavelandCaves;
import cavern.world.gen.MapGenCavelandRavine;
import cavern.world.gen.VeinGenerator;
import cavern.world.gen.WorldGenAcresia;
import cavern.world.gen.WorldGenBirchTreePerverted;
import cavern.world.gen.WorldGenSpruceTreePerverted;
import cavern.world.gen.WorldGenTreesPerverted;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

public class ChunkGeneratorCaveland implements IChunkGenerator
{
	protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
	protected static final IBlockState DIRT = Blocks.DIRT.getDefaultState();
	protected static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
	protected static final IBlockState SANDSTONE = Blocks.SANDSTONE.getDefaultState();

	private final World world;
	private final Random rand;

	private Biome[] biomesForGeneration;

	private final MapGenBase caveGenerator = new MapGenCavelandCaves();
	private final MapGenBase ravineGenerator = new MapGenCavelandRavine();

	private final VeinGenerator veinGenerator = new VeinGenerator(CavelandConfig.veinManager.getCaveVeins());

	private final WorldGenerator lakeWaterGen = new WorldGenLakes(Blocks.WATER);
	private final WorldGenerator lakeLavaGen = new WorldGenLakes(Blocks.LAVA);
	private final WorldGenerator liquidWaterGen = new WorldGenLiquids(Blocks.FLOWING_WATER);
	private final WorldGenerator liquidLavaGen = new WorldGenLiquids(Blocks.FLOWING_LAVA);
	private final WorldGenerator deadBushGen = new WorldGenDeadBush();
	private final WorldGenerator acresiaGen = new WorldGenAcresia();

	public ChunkGeneratorCaveland(World world)
	{
		this.world = world;
		this.rand = new Random(world.getSeed());
	}

	public void setBlocksInChunk(ChunkPrimer primer)
	{
		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				for (int y = 255; y >= 0; --y)
				{
					primer.setBlockState(x, y, z, DIRT);
				}
			}
		}
	}

	public void replaceBiomeBlocks(int chunkX, int chunkZ, ChunkPrimer primer)
	{
		if (!ForgeEventFactory.onReplaceBiomeBlocks(this, chunkX, chunkZ, primer, world))
		{
			return;
		}

		int worldHeight = world.provider.getActualHeight();
		int blockHeight = worldHeight - 1;

		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				Biome biome = biomesForGeneration[x * 16 + z];
				IBlockState top = biome.topBlock;
				IBlockState filter = biome.fillerBlock;

				if (biome.isSnowyBiome())
				{
					top = Blocks.PACKED_ICE.getDefaultState();
					filter = Blocks.PACKED_ICE.getDefaultState();
				}

				if (filter.getBlock() == Blocks.SAND)
				{
					filter = SANDSTONE;
				}

				primer.setBlockState(x, 0, z, BEDROCK);
				primer.setBlockState(x, blockHeight, z, BEDROCK);
				primer.setBlockState(x, 1, z, primer.getBlockState(x, 2, z));

				for (int y = 1; y <= blockHeight - 1; ++y)
				{
					if (primer.getBlockState(x, y, z).getBlock() == Blocks.GRASS ||
						primer.getBlockState(x, y, z).getMaterial().isSolid() && primer.getBlockState(x, y + 1, z).getBlock() == Blocks.AIR)
					{
						primer.setBlockState(x, y, z, top);
					}
					else if (primer.getBlockState(x, y, z).getBlock() == Blocks.DIRT)
					{
						primer.setBlockState(x, y, z, filter);
					}
				}

				if (blockHeight < 255)
				{
					for (int y = blockHeight + 1; y < 256; ++y)
					{
						primer.setBlockState(x, y, z, AIR);
					}
				}
			}
		}
	}

	@Override
	public Chunk generateChunk(int chunkX, int chunkZ)
	{
		rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);

		biomesForGeneration = world.getBiomeProvider().getBiomes(biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);

		ChunkPrimer primer = new ChunkPrimer();

		setBlocksInChunk(primer);

		caveGenerator.generate(world, chunkX, chunkZ, primer);

		if (CavelandConfig.generateRiver)
		{
			ravineGenerator.generate(world, chunkX, chunkZ, primer);
		}

		replaceBiomeBlocks(chunkX, chunkZ, primer);

		veinGenerator.generate(world, rand, biomesForGeneration, primer);

		Chunk chunk = new Chunk(world, primer, chunkX, chunkZ);
		byte[] biomeArray = chunk.getBiomeArray();

		for (int i = 0; i < biomeArray.length; ++i)
		{
			biomeArray[i] = (byte)Biome.getIdForBiome(biomesForGeneration[i]);
		}

		return chunk;
	}

	@Override
	public void populate(int chunkX, int chunkZ)
	{
		BlockFalling.fallInstantly = true;

		int worldX = chunkX * 16;
		int worldZ = chunkZ * 16;
		BlockPos blockPos = new BlockPos(worldX, 0, worldZ);
		Biome biome = world.getBiome(blockPos.add(16, 0, 16));
		BiomeDecorator decorator = biome.decorator;
		int worldHeight = world.provider.getActualHeight();

		ForgeEventFactory.onChunkPopulate(true, this, world, rand, chunkX, chunkZ, false);

		int x, y, z;

		if (CavelandConfig.generateLakes)
		{
			if (TerrainGen.populate(this, world, rand, chunkX, chunkZ, false, EventType.LAKE))
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight - 16);
				z = rand.nextInt(16) + 8;

				lakeWaterGen.generate(world, rand, blockPos.add(x, y, z));
			}

			if (rand.nextInt(30) == 0 && TerrainGen.populate(this, world, rand, chunkX, chunkZ, false, EventType.LAVA))
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight / 2);
				z = rand.nextInt(16) + 8;

				lakeLavaGen.generate(world, rand, blockPos.add(x, y, z));
			}
		}

		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(world, rand, blockPos));

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(world, rand, blockPos));

		for (int i = 0; i < 10; ++i)
		{
			x = rand.nextInt(16) + 8;
			y = rand.nextInt(worldHeight - 10);
			z = rand.nextInt(16) + 8;

			acresiaGen.generate(world, rand, blockPos.add(x, y, z));
		}

		for (int i = 0; i < 15; ++i)
		{
			x = rand.nextInt(16) + 8;
			y = rand.nextInt(worldHeight / 2 - 10) + worldHeight / 2;
			z = rand.nextInt(16) + 8;

			acresiaGen.generate(world, rand, blockPos.add(x, y, z));
		}

		if (TerrainGen.decorate(world, rand, blockPos, Decorate.EventType.SHROOM))
		{
			for (int i = 0; i < 5; ++i)
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight - 10);
				z = rand.nextInt(16) + 8;

				decorator.mushroomBrownGen.generate(world, rand, blockPos.add(x, y, z));
			}

			for (int i = 0; i < 5; ++i)
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight - 10);
				z = rand.nextInt(16) + 8;

				decorator.mushroomRedGen.generate(world, rand, blockPos.add(x, y, z));
			}
		}

		if (BiomeDictionary.hasType(biome, Type.SANDY))
		{
			if (TerrainGen.decorate(world, rand, blockPos, Decorate.EventType.CACTUS))
			{
				for (int i = 0; i < 80; ++i)
				{
					x = rand.nextInt(16) + 8;
					y = rand.nextInt(worldHeight - 5);
					z = rand.nextInt(16) + 8;

					decorator.cactusGen.generate(world, rand, blockPos.add(x, y, z));
				}
			}

			if (TerrainGen.decorate(world, rand, blockPos, Decorate.EventType.DEAD_BUSH))
			{
				for (int i = 0; i < 10; ++i)
				{
					x = rand.nextInt(16) + 8;
					y = rand.nextInt(worldHeight - 5);
					z = rand.nextInt(16) + 8;

					deadBushGen.generate(world, rand, blockPos.add(x, y, z));
				}
			}
		}
		else
		{
			if (TerrainGen.decorate(world, rand, blockPos, Decorate.EventType.FLOWERS))
			{
				for (int i = 0; i < 8; ++i)
				{
					x = rand.nextInt(16) + 8;
					y = rand.nextInt(worldHeight - 5);
					z = rand.nextInt(16) + 8;

					decorator.flowerGen.generate(world, rand, blockPos.add(x, y, z));
				}
			}

			for (int i = 0; i < 18; ++i)
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight - 5);
				z = rand.nextInt(16) + 8;

				biome.getRandomWorldGenForGrass(rand).generate(world, rand, blockPos.add(x, y, z));
			}

			if (TerrainGen.decorate(world, rand, blockPos, Decorate.EventType.TREE))
			{
				WorldGenAbstractTree treeGen = null;

				if (BiomeDictionary.hasType(biome, Type.JUNGLE))
				{
					treeGen = new WorldGenTreesPerverted(false, 4 + rand.nextInt(7), BlockPlanks.EnumType.JUNGLE, true);
				}
				else if (BiomeDictionary.hasType(biome, Type.FOREST) || !BiomeDictionary.hasType(biome, Type.PLAINS) || rand.nextInt(10) == 0)
				{
					if (BiomeDictionary.hasType(biome, Type.COLD))
					{
						treeGen = new WorldGenSpruceTreePerverted(false);
					}
					else if (rand.nextInt(3) == 0)
					{
						treeGen = new WorldGenBirchTreePerverted(false, false);
					}
					else
					{
						treeGen = new WorldGenTreesPerverted(false, 3, BlockPlanks.EnumType.OAK, true);
					}
				}

				if (treeGen != null)
				{
					for (int i = 0; i < 80; ++i)
					{
						x = rand.nextInt(16) + 8;
						y = rand.nextInt(worldHeight);
						z = rand.nextInt(16) + 8;

						BlockPos pos = blockPos.add(x, y, z);

						if (treeGen.generate(world, rand, pos))
						{
							treeGen.generateSaplings(world, rand, pos);
						}
					}

					for (int i = 0; i < 60; ++i)
					{
						x = rand.nextInt(16) + 8;
						y = 8 + rand.nextInt(5);
						z = rand.nextInt(16) + 8;

						BlockPos pos = blockPos.add(x, y, z);

						if (treeGen.generate(world, rand, pos))
						{
							treeGen.generateSaplings(world, rand, pos);
						}
					}
				}
			}

			if (decorator.generateFalls)
			{
				if (BiomeDictionary.hasType(biome, Type.WATER))
				{
					if (TerrainGen.decorate(world, rand, blockPos, Decorate.EventType.LAKE_WATER))
					{
						for (int i = 0; i < 150; ++i)
						{
							x = rand.nextInt(16) + 8;
							y = rand.nextInt(rand.nextInt(worldHeight - 16) + 10);
							z = rand.nextInt(16) + 8;

							liquidWaterGen.generate(world, rand, blockPos.add(x, y, z));
						}
					}
				}
				else
				{
					if (TerrainGen.decorate(world, rand, blockPos, Decorate.EventType.LAKE_WATER))
					{
						for (int i = 0; i < 100; ++i)
						{
							x = rand.nextInt(16) + 8;
							y = rand.nextInt(rand.nextInt(worldHeight - 16) + 10);
							z = rand.nextInt(16) + 8;

							liquidWaterGen.generate(world, rand, blockPos.add(x, y, z));
						}
					}

					if (TerrainGen.decorate(world, rand, blockPos, Decorate.EventType.LAKE_LAVA))
					{
						for (int i = 0; i < 20; ++i)
						{
							x = rand.nextInt(16) + 8;
							y = rand.nextInt(worldHeight / 2);
							z = rand.nextInt(16) + 8;

							liquidLavaGen.generate(world, rand, blockPos.add(x, y, z));
						}
					}
				}
			}
		}

		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(world, rand, blockPos));

		ForgeEventFactory.onChunkPopulate(false, this, world, rand, chunkX, chunkZ, false);

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