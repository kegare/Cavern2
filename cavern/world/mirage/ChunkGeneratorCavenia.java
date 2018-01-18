package cavern.world.mirage;

import java.util.List;
import java.util.Random;

import cavern.config.CaveniaConfig;
import cavern.config.manager.CaveBiome;
import cavern.world.gen.MapGenCaveniaCaves;
import cavern.world.gen.VeinGenerator;
import net.minecraft.block.BlockFalling;
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

public class ChunkGeneratorCavenia implements IChunkGenerator
{
	protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
	protected static final IBlockState STONE = Blocks.STONE.getDefaultState();
	protected static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();

	private final World world;
	private final Random rand;

	private Biome[] biomesForGeneration;

	private MapGenBase caveGenerator = new MapGenCaveniaCaves();

	private VeinGenerator veinGenerator = new VeinGenerator(CaveniaConfig.veinManager.getCaveVeins());

	private WorldGenerator lakeWaterGen = new WorldGenLakes(Blocks.WATER);
	private WorldGenerator lakeLavaGen = new WorldGenLakes(Blocks.LAVA);
	private WorldGenerator liquidWaterGen = new WorldGenLiquids(Blocks.FLOWING_WATER);
	private WorldGenerator liquidLavaGen = new WorldGenLiquids(Blocks.FLOWING_LAVA);

	public ChunkGeneratorCavenia(World world)
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
					primer.setBlockState(x, y, z, STONE);
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
				CaveBiome caveBiome = CaveniaConfig.biomeManager.getCaveBiome(biome);
				IBlockState top = caveBiome == null ? STONE : caveBiome.getTopBlock().getBlockState();
				IBlockState filter = caveBiome == null ? top : caveBiome.getTerrainBlock().getBlockState();

				primer.setBlockState(x, 0, z, BEDROCK);
				primer.setBlockState(x, blockHeight, z, BEDROCK);

				for (int y = 1; y <= blockHeight - 1; ++y)
				{
					if (primer.getBlockState(x, y, z).getMaterial().isSolid() && primer.getBlockState(x, y + 1, z).getBlock() == Blocks.AIR)
					{
						primer.setBlockState(x, y, z, top);
					}
					else if (primer.getBlockState(x, y, z).getBlock() == Blocks.STONE)
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

		if (CaveniaConfig.generateCaves)
		{
			caveGenerator.generate(world, chunkX, chunkZ, primer);
		}

		replaceBiomeBlocks(chunkX, chunkZ, primer);

		veinGenerator.generate(world, rand, biomesForGeneration, primer);

		Chunk chunk = new Chunk(world, primer, chunkX, chunkZ);
		byte[] biomeArray = chunk.getBiomeArray();

		for (int i = 0; i < biomeArray.length; ++i)
		{
			biomeArray[i] = (byte)Biome.getIdForBiome(biomesForGeneration[i]);
		}

		chunk.resetRelightChecks();

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

		if (BiomeDictionary.hasType(biome, Type.NETHER))
		{
			if (CaveniaConfig.generateLakes && rand.nextInt(4) == 0 && TerrainGen.populate(this, world, rand, chunkX, chunkZ, false, EventType.LAVA))
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight - 32) + 16;
				z = rand.nextInt(16) + 8;

				lakeLavaGen.generate(world, rand, blockPos.add(x, y, z));
			}
		}
		else if (!BiomeDictionary.hasType(biome, Type.END))
		{
			if (CaveniaConfig.generateLakes)
			{
				if (!BiomeDictionary.hasType(biome, Type.SANDY) && rand.nextInt(4) == 0 && TerrainGen.populate(this, world, rand, chunkX, chunkZ, false, EventType.LAKE))
				{
					x = rand.nextInt(16) + 8;
					y = rand.nextInt(worldHeight - 16);
					z = rand.nextInt(16) + 8;

					lakeWaterGen.generate(world, rand, blockPos.add(x, y, z));
				}

				if (rand.nextInt(10) == 0 && TerrainGen.populate(this, world, rand, chunkX, chunkZ, false, EventType.LAVA))
				{
					x = rand.nextInt(16) + 8;
					y = rand.nextInt(worldHeight / 2 - 16) + 32;
					z = rand.nextInt(16) + 8;

					lakeLavaGen.generate(world, rand, blockPos.add(x, y, z));
				}
			}
		}

		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(world, rand, blockPos));

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(world, rand, blockPos));

		if (TerrainGen.decorate(world, rand, blockPos, Decorate.EventType.SHROOM))
		{
			int i = 0;

			if (BiomeDictionary.hasType(biome, Type.MUSHROOM))
			{
				i += 2;
			}
			else if (BiomeDictionary.hasType(biome, Type.NETHER))
			{
				i += 1;
			}

			if (rand.nextInt(2) <= i)
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight - 16) + 10;
				z = rand.nextInt(16) + 8;

				decorator.mushroomBrownGen.generate(world, rand, blockPos.add(x, y, z));
			}

			if (rand.nextInt(7) <= i)
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight - 16) + 10;
				z = rand.nextInt(16) + 8;

				decorator.mushroomRedGen.generate(world, rand, blockPos.add(x, y, z));
			}
		}

		if (decorator.generateFalls)
		{
			if (BiomeDictionary.hasType(biome, Type.NETHER))
			{
				if (TerrainGen.decorate(world, rand, blockPos, Decorate.EventType.LAKE_LAVA))
				{
					for (int i = 0; i < 70; ++i)
					{
						x = rand.nextInt(16) + 8;
						y = rand.nextInt(worldHeight - 22) + 20;
						z = rand.nextInt(16) + 8;

						liquidLavaGen.generate(world, rand, blockPos.add(x, y, z));
					}
				}
			}
			else if (BiomeDictionary.hasType(biome, Type.WATER))
			{
				if (TerrainGen.decorate(world, rand, blockPos, Decorate.EventType.LAKE_WATER))
				{
					for (int i = 0; i < 50; ++i)
					{
						x = rand.nextInt(16) + 8;
						y = rand.nextInt(rand.nextInt(worldHeight - 16) + 10);
						z = rand.nextInt(16) + 8;

						liquidWaterGen.generate(world, rand, blockPos.add(x, y, z));
					}
				}
			}
			else if (!BiomeDictionary.hasType(biome, Type.END))
			{
				if (TerrainGen.decorate(world, rand, blockPos, Decorate.EventType.LAKE_WATER))
				{
					for (int i = 0; i < 50; ++i)
					{
						x = rand.nextInt(16) + 8;
						y = rand.nextInt(rand.nextInt(worldHeight - 16) + 10);
						z = rand.nextInt(16) + 8;

						liquidWaterGen.generate(world, rand, blockPos.add(x, y, z));
					}
				}

				if (TerrainGen.decorate(world, rand, blockPos, Decorate.EventType.LAKE_LAVA))
				{
					for (int i = 0; i < 50; ++i)
					{
						x = rand.nextInt(16) + 8;
						y = rand.nextInt(worldHeight / 2 - 32) + 20;
						z = rand.nextInt(16) + 8;

						liquidLavaGen.generate(world, rand, blockPos.add(x, y, z));
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