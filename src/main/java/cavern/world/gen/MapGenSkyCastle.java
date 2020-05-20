package cavern.world.gen;

import cavern.world.mirage.ChunkGeneratorSkyland;
import com.google.common.collect.Lists;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

import java.util.List;
import java.util.Random;

public class MapGenSkyCastle extends MapGenStructure
{
	/**
	 * None
	 */
	private int distance;
	private final ChunkGeneratorSkyland provider;

	public MapGenSkyCastle(ChunkGeneratorSkyland providerIn)
	{
		this.distance = 36;
		this.provider = providerIn;
	}

	public String getStructureName()
	{
		return "SkyCastle";
	}

	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
	{
		int i = chunkX;
		int j = chunkZ;

		if (chunkX < 0)
		{
			chunkX -= this.distance - 1;
		}

		if (chunkZ < 0)
		{
			chunkZ -= this.distance - 1;
		}

		int k = chunkX / this.distance;
		int l = chunkZ / this.distance;
		Random random = this.world.setRandomSeed(k, l, 14267312);
		k = k * this.distance;
		l = l * this.distance;
		k = k + random.nextInt(this.distance - 8);
		l = l + random.nextInt(this.distance - 8);

		if (i == k && j == l)
		{
			return true;
		}

		return false;
	}

	public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored)
	{
		this.world = worldIn;
		return findNearestStructurePosBySpacing(worldIn, this, pos, this.distance, 8, 10387312, false, 100, findUnexplored);
	}

	protected StructureStart getStructureStart(int chunkX, int chunkZ)
	{
		return new MapGenSkyCastle.Start(this.world, this.provider, this.rand, chunkX, chunkZ);
	}

	public static class Start extends StructureStart
	{
		private boolean isValid;


		public Start()
		{
		}

		public Start(World p_i47235_1_, ChunkGeneratorSkyland p_i47235_2_, Random p_i47235_3_, int p_i47235_4_, int p_i47235_5_)
		{
			super(p_i47235_4_, p_i47235_5_);
			this.create(p_i47235_1_, p_i47235_2_, p_i47235_3_, p_i47235_4_, p_i47235_5_);
		}

		private void create(World p_191092_1_, ChunkGeneratorSkyland p_191092_2_, Random p_191092_3_, int p_191092_4_, int p_191092_5_)
		{
			Rotation rotation = Rotation.values()[p_191092_3_.nextInt(Rotation.values().length)];
			ChunkPrimer chunkprimer = new ChunkPrimer();
			p_191092_2_.setBlocksInChunk(p_191092_4_, p_191092_5_, chunkprimer);
			int i = 5;
			int j = 5;


			BlockPos blockpos = new BlockPos(p_191092_4_ * 16 + 8, 90, p_191092_5_ * 16 + 8);
			List<SkyCastlePiece.SkyCastleTemplate> list = Lists.<SkyCastlePiece.SkyCastleTemplate>newLinkedList();
			SkyCastlePiece.generateCore(p_191092_1_.getSaveHandler().getStructureTemplateManager(), blockpos, rotation, list, p_191092_3_);
			this.components.addAll(list);
			this.updateBoundingBox();
			this.isValid = true;
		}


		/**
		 * currently only defined for Villages, returns true if Village has more than 2 non-road components
		 */
		public boolean isSizeableStructure()
		{
			return this.isValid;
		}
	}
}