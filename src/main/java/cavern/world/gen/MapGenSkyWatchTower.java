package cavern.world.gen;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import cavern.world.mirage.ChunkGeneratorSkyland;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

public class MapGenSkyWatchTower extends MapGenStructure
{
	private final ChunkGeneratorSkyland provider;
	private final int distance;

	public MapGenSkyWatchTower(ChunkGeneratorSkyland provider)
	{
		this.provider = provider;
		this.distance = 34;
	}

	@Override
	public String getStructureName()
	{
		return "SkyWatchTower";
	}

	@Override
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
	{
		int i = chunkX;
		int j = chunkZ;

		if (chunkX < 0)
		{
			chunkX -= distance - 1;
		}

		if (chunkZ < 0)
		{
			chunkZ -= distance - 1;
		}

		int k = chunkX / distance;
		int l = chunkZ / distance;
		Random random = world.setRandomSeed(k, l, 14267312);
		k = k * distance;
		l = l * distance;
		k = k + random.nextInt(distance - 8);
		l = l + random.nextInt(distance - 8);

		if (i == k && j == l)
		{
			return true;
		}

		return false;
	}

	@Override
	public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored)
	{
		world = worldIn;

		return findNearestStructurePosBySpacing(worldIn, this, pos, distance, 8, 14267312, false, 100, findUnexplored);
	}

	@Override
	protected StructureStart getStructureStart(int chunkX, int chunkZ)
	{
		return new MapGenSkyWatchTower.Start(world, provider, rand, chunkX, chunkZ);
	}

	public static class Start extends StructureStart
	{
		private boolean isValid;

		public Start() {}

		public Start(World world, ChunkGeneratorSkyland provider, Random random, int chunkX, int chunkZ)
		{
			super(chunkX, chunkZ);
			this.create(world, provider, random, chunkX, chunkZ);
		}

		private void create(World world, ChunkGeneratorSkyland provider, Random random, int chunkX, int chunkZ)
		{
			Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
			ChunkPrimer primer = new ChunkPrimer();

			provider.setBlocksInChunk(chunkX, chunkZ, primer);

			BlockPos blockpos = new BlockPos(chunkX * 16 + 8, 90, chunkZ * 16 + 8);
			List<SkyWatchTowerPiece.SkyCastleTemplate> list = Lists.<SkyWatchTowerPiece.SkyCastleTemplate>newLinkedList();

			SkyWatchTowerPiece.generateCore(world.getSaveHandler().getStructureTemplateManager(), blockpos, rotation, list, random);

			components.addAll(list);

			updateBoundingBox();

			isValid = true;
		}

		@Override
		public boolean isSizeableStructure()
		{
			return isValid;
		}
	}
}