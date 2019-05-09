package cavern.world.gen;

import java.util.Random;

import cavern.block.CaveBlocks;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenTrees;

public class WorldGenTreesPerverted extends WorldGenTrees
{
	private final int minTreeHeight;
	private final boolean vinesGrow;
	private final IBlockState stateWood;
	private final IBlockState stateLeaves;

	public WorldGenTreesPerverted(boolean flag, int min, IBlockState wood, IBlockState leaves, boolean grow)
	{
		super(flag);
		this.minTreeHeight = min;
		this.stateWood = wood;
		this.stateLeaves = leaves;
		this.vinesGrow = grow;
	}

	public WorldGenTreesPerverted(boolean flag, int min, BlockPlanks.EnumType type, boolean grow)
	{
		this(flag, min,
			CaveBlocks.PERVERTED_LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, type),
			CaveBlocks.PERVERTED_LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, type).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false)), grow);
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos)
	{
		int treeHeight = rand.nextInt(3) + minTreeHeight;
		boolean flag = true;

		if (pos.getY() >= 1 && pos.getY() + treeHeight + 1 <= world.getHeight())
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
							if (!isReplaceable(world,blockpos.setPos(x, y, z)))
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
				IBlockState state = world.getBlockState(pos.down());

				if (state.getBlock().canSustainPlant(state, world, pos.down(), EnumFacing.UP, CaveBlocks.PERVERTED_SAPLING) && pos.getY() < world.getHeight() - treeHeight - 1)
				{
					setDirtAt(world, pos.down());

					int i = 3;
					int j = 0;

					for (int y = pos.getY() - i + treeHeight; y <= pos.getY() + treeHeight; ++y)
					{
						int k = y - (pos.getY() + treeHeight);
						int range = j + 1 - k / 2;

						for (int x = pos.getX() - range; x <= pos.getX() + range; ++x)
						{
							int px = x - pos.getX();

							for (int z = pos.getZ() - range; z <= pos.getZ() + range; ++z)
							{
								int pz = z - pos.getZ();

								if (Math.abs(px) != range || Math.abs(pz) != range || rand.nextInt(2) != 0 && k != 0)
								{
									BlockPos blockpos = new BlockPos(x, y, z);
									state = world.getBlockState(blockpos);

									if (state.getBlock().isAir(state, world, blockpos) || state.getBlock().isLeaves(state, world, blockpos) || state.getMaterial() == Material.VINE)
									{
										setBlockAndNotifyAdequately(world, blockpos, stateLeaves);
									}
								}
							}
						}
					}

					for (int y = 0; y < treeHeight; ++y)
					{
						BlockPos up = pos.up(y);
						state = world.getBlockState(up);

						if (state.getBlock().isAir(state, world, up) || state.getBlock().isLeaves(state, world, up) || state.getMaterial() == Material.VINE)
						{
							setBlockAndNotifyAdequately(world, pos.up(y), stateWood);

							if (vinesGrow && y > 0)
							{
								if (rand.nextInt(3) > 0 && world.isAirBlock(pos.add(-1, y, 0)))
								{
									setVine(world, pos.add(-1, y, 0), BlockVine.EAST);
								}

								if (rand.nextInt(3) > 0 && world.isAirBlock(pos.add(1, y, 0)))
								{
									setVine(world, pos.add(1, y, 0), BlockVine.WEST);
								}

								if (rand.nextInt(3) > 0 && world.isAirBlock(pos.add(0, y, -1)))
								{
									setVine(world, pos.add(0, y, -1), BlockVine.SOUTH);
								}

								if (rand.nextInt(3) > 0 && world.isAirBlock(pos.add(0, y, 1)))
								{
									setVine(world, pos.add(0, y, 1), BlockVine.NORTH);
								}
							}
						}
					}

					if (vinesGrow)
					{
						for (int y = pos.getY() - 3 + treeHeight; y <= pos.getY() + treeHeight; ++y)
						{
							int k = y - (pos.getY() + treeHeight);
							int range = 2 - k / 2;
							MutableBlockPos blockpos = new MutableBlockPos();

							for (int x = pos.getX() - range; x <= pos.getX() + range; ++x)
							{
								for (int z = pos.getZ() - range; z <= pos.getZ() + range; ++z)
								{
									blockpos.setPos(x, y, z);

									state = world.getBlockState(blockpos);

									if (state.getBlock().isLeaves(state, world, blockpos))
									{
										BlockPos west = blockpos.west();
										BlockPos east = blockpos.east();
										BlockPos north = blockpos.north();
										BlockPos south = blockpos.south();

										if (rand.nextInt(4) == 0 && world.isAirBlock(west))
										{
											setVines(world, west, BlockVine.EAST);
										}

										if (rand.nextInt(4) == 0 && world.isAirBlock(east))
										{
											setVines(world, east, BlockVine.WEST);
										}

										if (rand.nextInt(4) == 0 && world.isAirBlock(north))
										{
											setVines(world, north, BlockVine.SOUTH);
										}

										if (rand.nextInt(4) == 0 && world.isAirBlock(south))
										{
											setVines(world, south, BlockVine.NORTH);
										}
									}
								}
							}
						}

						if (rand.nextInt(5) == 0 && treeHeight > 5)
						{
							for (int k = 0; k < 2; ++k)
							{
								for (EnumFacing face : EnumFacing.Plane.HORIZONTAL)
								{
									if (rand.nextInt(4 - k) == 0)
									{
										EnumFacing side = face.getOpposite();

										setCocoa(world, rand.nextInt(3), pos.add(side.getXOffset(), treeHeight - 5 + k, side.getZOffset()), face);
									}
								}
							}
						}
					}

					return true;
				}

				return false;
			}
		}

		return false;
	}

	private void setCocoa(World world, int age, BlockPos pos, EnumFacing side)
	{
		setBlockAndNotifyAdequately(world, pos, Blocks.COCOA.getDefaultState().withProperty(BlockCocoa.AGE, Integer.valueOf(age)).withProperty(BlockHorizontal.FACING, side));
	}

	private void setVine(World world, BlockPos pos, PropertyBool prop)
	{
		setBlockAndNotifyAdequately(world, pos, Blocks.VINE.getDefaultState().withProperty(prop, Boolean.valueOf(true)));
	}

	private void setVines(World world, BlockPos pos, PropertyBool prop)
	{
		setVine(world, pos, prop);

		int i = 4;

		for (pos = pos.down(); world.isAirBlock(pos) && i > 0; --i)
		{
			setVine(world, pos, prop);

			pos = pos.down();
		}
	}
}