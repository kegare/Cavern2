package cavern.world.gen;

import java.util.Random;

import cavern.block.CaveBlocks;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenTaiga2;

public class WorldGenSpruceTreePerverted extends WorldGenTaiga2
{
	public WorldGenSpruceTreePerverted(boolean flag)
	{
		super(flag);
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos)
	{
		int treeHeight = rand.nextInt(4) + 5;
		int i = 1 + rand.nextInt(2);
		int j = treeHeight - i;
		int k = 2 + rand.nextInt(2);
		boolean flag = true;

		if (pos.getY() >= 1 && pos.getY() + treeHeight + 1 <= world.getHeight())
		{
			for (int y = pos.getY(); y <= pos.getY() + 1 + treeHeight && flag; ++y)
			{
				int l = 1;

				if (y - pos.getY() < i)
				{
					l = 0;
				}
				else
				{
					l = k;
				}

				MutableBlockPos blockpos = new MutableBlockPos();

				for (int x = pos.getX() - l; x <= pos.getX() + l && flag; ++x)
				{
					for (int z = pos.getZ() - l; z <= pos.getZ() + l && flag; ++z)
					{
						if (y >= 0 && y < world.getHeight())
						{
							IBlockState state = world.getBlockState(blockpos.setPos(x, y, z));

							if (!state.getBlock().isAir(state, world, blockpos.setPos(x, y, z)) && !state.getBlock().isLeaves(state, world, blockpos.setPos(x, y, z)))
							{
								flag = false;
							}
						}
						else
						{
							flag = false;
						}
					}
				}
			}

			if (!flag)
			{
				return false;
			}
			else
			{
				IBlockState wood = CaveBlocks.PERVERTED_LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
				IBlockState leaves = CaveBlocks.PERVERTED_LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
				BlockPos down = pos.down();
				IBlockState state = world.getBlockState(down);

				if (state.getBlock().canSustainPlant(state, world, down, EnumFacing.UP, (BlockSapling)Blocks.SAPLING) && pos.getY() < world.getHeight() - treeHeight - 1)
				{
					state.getBlock().onPlantGrow(state, world, down, pos);

					int l = rand.nextInt(2);
					int m = 1;
					int n = 0;

					for (int py = 0; py <= j; ++py)
					{
						int y = pos.getY() + treeHeight - py;

						for (int x = pos.getX() - l; x <= pos.getX() + l; ++x)
						{
							int px = x - pos.getX();

							for (int z = pos.getZ() - l; z <= pos.getZ() + l; ++z)
							{
								int pz = z - pos.getZ();

								if (Math.abs(px) != l || Math.abs(pz) != l || l <= 0)
								{
									BlockPos blockpos = new BlockPos(x, y, z);
									state = world.getBlockState(blockpos);

									if (state.getBlock().canBeReplacedByLeaves(state, world, blockpos))
									{
										setBlockAndNotifyAdequately(world, blockpos, leaves);
									}
								}
							}
						}

						if (l >= m)
						{
							l = n;
							n = 1;
							++m;

							if (m > k)
							{
								m = k;
							}
						}
						else
						{
							++l;
						}
					}

					int py = rand.nextInt(3);

					for (int y = 0; y < treeHeight - py; ++y)
					{
						BlockPos up = pos.up(y);
						state = world.getBlockState(up);

						if (state.getBlock().isAir(state, world, up) || state.getBlock().isLeaves(state, world, up))
						{
							setBlockAndNotifyAdequately(world, pos.up(y), wood);
						}
					}

					return true;
				}

				return false;
			}
		}

		return false;
	}
}