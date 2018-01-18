package cavern.miningassist;

import com.google.common.collect.Sets;

import cavern.config.property.ConfigBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class AditMiningSnapshot extends MiningSnapshot
{
	public AditMiningSnapshot(World world, BlockPos pos, IBlockState state)
	{
		super(world, pos, state);
	}

	public AditMiningSnapshot(World world, BlockPos pos, IBlockState state, EntityLivingBase entity)
	{
		super(world, pos, state, entity);
	}

	@Override
	public ConfigBlocks getValidTargetBlocks()
	{
		return MiningAssist.ADIT.getValidTargetBlocks();
	}

	@Override
	public void checkForMining()
	{
		miningTargets = Sets.newHashSet();

		if (EnumFacing.getDirectionFromEntityLiving(originPos, miner).getAxis().isVertical())
		{
			return;
		}

		if (originPos.getY() == MathHelper.floor(miner.posY + 0.5D))
		{
			offer(originPos.up());
		}
		else
		{
			offer(originPos.down());
		}
	}
}