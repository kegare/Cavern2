package cavern.miningassist;

import com.google.common.collect.Sets;

import cavern.config.MiningAssistConfig;
import cavern.util.CaveUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class QuickMiningSnapshot extends MiningSnapshot
{
	private static final int[][] CHECK_OFFSETS = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}, {-1, 0, 0}, {0, -1, 0}, {0, 0, -1}};

	private BlockPos checkPos;

	public QuickMiningSnapshot(World world, BlockPos pos, IBlockState state)
	{
		super(world, pos, state);
	}

	public QuickMiningSnapshot(World world, BlockPos pos, IBlockState state, EntityLivingBase entity)
	{
		super(world, pos, state, entity);
	}

	@Override
	public void checkForMining()
	{
		checkPos = originPos;
		miningTargets = Sets.newTreeSet(this);

		checkChain();

		super.checkForMining();
	}

	protected void checkChain()
	{
		boolean flag;

		do
		{
			flag = false;

			BlockPos pos = checkPos;

			for (int[] offset : CHECK_OFFSETS)
			{
				if (offer(pos.add(offset[0], offset[1], offset[2])))
				{
					checkChain();

					if (!flag)
					{
						flag = true;
					}
				}
			}
		}
		while (flag);
	}

	@Override
	public boolean offer(BlockPos pos)
	{
		if (MathHelper.floor(Math.sqrt(originPos.distanceSq(pos))) >= MiningAssistConfig.quickMiningLimit)
		{
			return false;
		}

		if (super.offer(pos))
		{
			checkPos = pos;

			return true;
		}

		return false;
	}

	@Override
	public boolean validTarget(BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);

		if (state.getBlock().isAir(state, world, pos) || state.getBlockHardness(world, pos) < 0.0F)
		{
			return false;
		}

		return isRedstoneOre(state) && isRedstoneOre(originState) || CaveUtils.isBlockEqual(state, originState);
	}

	private boolean isRedstoneOre(IBlockState state)
	{
		return state.getBlock() == Blocks.REDSTONE_ORE || state.getBlock() == Blocks.LIT_REDSTONE_ORE;
	}
}