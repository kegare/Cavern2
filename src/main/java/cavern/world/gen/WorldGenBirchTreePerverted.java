package cavern.world.gen;

import java.util.Random;

import cavern.block.CaveBlocks;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBirchTree;

public class WorldGenBirchTreePerverted extends WorldGenBirchTree
{
	private boolean useExtraRandomHeight;

	public WorldGenBirchTreePerverted(boolean notify, boolean useExtraRandomHeightIn)
	{
		super(notify, useExtraRandomHeightIn);
		this.useExtraRandomHeight = useExtraRandomHeightIn;
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos)
	{
		int treeHeight = rand.nextInt(3) + 4;

		if (useExtraRandomHeight)
		{
			treeHeight += rand.nextInt(6);
		}

		boolean flag = true;

		if (pos.getY() >= 1 && pos.getY() + treeHeight + 1 <= 256)
		{
			for (int y = pos.getY(); y <= pos.getY() + 1 + treeHeight; ++y)
			{
				int range = 1;

				if (y == pos.getY())
				{
					range = 0;
				}

				if (y >= pos.getY() + 1 + treeHeight - 2)
				{
					range = 2;
				}

				MutableBlockPos blockpos = new MutableBlockPos();

				for (int x = pos.getX() - range; x <= pos.getX() + range && flag; ++x)
				{
					for (int z = pos.getZ() - range; z <= pos.getZ() + range && flag; ++z)
					{
						if (y >= 0 && y < world.getHeight())
						{
							if (!isReplaceable(world, blockpos.setPos(x, y, z)))
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
				IBlockState wood = CaveBlocks.PERVERTED_LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.BIRCH);
				IBlockState leaves = CaveBlocks.PERVERTED_LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.BIRCH).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
				BlockPos down = pos.down();
				IBlockState state = world.getBlockState(down);
				boolean isSoil = state.getBlock().canSustainPlant(state, world, down, EnumFacing.UP, CaveBlocks.PERVERTED_SAPLING);

				if (isSoil && pos.getY() < world.getHeight() - treeHeight - 1)
				{
					state.getBlock().onPlantGrow(state, world, down, pos);

					for (int y = pos.getY() - 3 + treeHeight; y <= pos.getY() + treeHeight; ++y)
					{
						int i = y - (pos.getY() + treeHeight);
						int range = 1 - i / 2;

						for (int x = pos.getX() - range; x <= pos.getX() + range; ++x)
						{
							int j = x - pos.getX();

							for (int z = pos.getZ() - range; z <= pos.getZ() + range; ++z)
							{
								int k = z - pos.getZ();

								if (Math.abs(j) != range || Math.abs(k) != range || rand.nextInt(2) != 0 && i != 0)
								{
									BlockPos blockpos = new BlockPos(x, y, z);
									IBlockState state2 = world.getBlockState(blockpos);

									if (state2.getBlock().isAir(state2, world, blockpos) || state2.getBlock().isAir(state2, world, blockpos))
									{
										setBlockAndNotifyAdequately(world, blockpos, leaves);
									}
								}
							}
						}
					}

					for (int height = 0; height < treeHeight; ++height)
					{
						BlockPos up = pos.up(height);
						IBlockState state2 = world.getBlockState(up);

						if (state2.getBlock().isAir(state2, world, up) || state2.getBlock().isLeaves(state2, world, up))
						{
							setBlockAndNotifyAdequately(world, pos.up(height), wood);
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