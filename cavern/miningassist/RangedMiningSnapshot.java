package cavern.miningassist;

import com.google.common.collect.Sets;

import cavern.config.MiningAssistConfig;
import cavern.config.property.ConfigBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RangedMiningSnapshot extends MiningSnapshot
{
	public RangedMiningSnapshot(World world, BlockPos pos, IBlockState state)
	{
		super(world, pos, state);
	}

	public RangedMiningSnapshot(World world, BlockPos pos, IBlockState state, EntityLivingBase entity)
	{
		super(world, pos, state, entity);
	}

	public int getRowRange()
	{
		return MiningAssistConfig.rangedMining;
	}

	public int getColumnRange()
	{
		return MiningAssistConfig.rangedMining;
	}

	@Override
	public ConfigBlocks getValidTargetBlocks()
	{
		return MiningAssist.RANGED.getValidTargetBlocks();
	}

	@Override
	public void checkForMining()
	{
		miningTargets = Sets.newTreeSet(this);

		switch (EnumFacing.getDirectionFromEntityLiving(originPos, miner).getAxis())
		{
			case X:
				checkX();
				break;
			case Y:
				checkY();
				break;
			case Z:
				checkZ();
				break;
		}
	}

	protected void checkX()
	{
		int row = getRowRange();
		int column = getColumnRange();

		for (int i = -row; i <= row; ++i)
		{
			for (int j = -column; j <= column; ++j)
			{
				offer(originPos.add(0, j, i));
			}
		}
	}

	protected void checkY()
	{
		int row = getRowRange();
		int column = getColumnRange();

		for (int i = -row; i <= row; ++i)
		{
			for (int j = -column; j <= column; ++j)
			{
				offer(originPos.add(i, 0, j));
			}
		}
	}

	protected void checkZ()
	{
		int row = getRowRange();
		int column = getColumnRange();

		for (int i = -row; i <= row; ++i)
		{
			for (int j = -column; j <= column; ++j)
			{
				offer(originPos.add(i, j, 0));
			}
		}
	}
}