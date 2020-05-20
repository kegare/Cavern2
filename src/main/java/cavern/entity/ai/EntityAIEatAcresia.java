package cavern.entity.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import cavern.block.BlockAcresia;
import cavern.block.CaveBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityAIEatAcresia extends EntityAIBase
{
	private static final Predicate<IBlockState> IS_ACRESIA = BlockStateMatcher.forBlock(CaveBlocks.ACRESIA).where(BlockAcresia.AGE, Predicates.equalTo(4));

	private final EntityLiving eater;
	private final World world;
	private int eatingGrassTimer;

	public EntityAIEatAcresia(EntityLiving entity)
	{
		this.eater = entity;
		this.world = entity.world;
		this.setMutexBits(7);
	}

	@Override
	public boolean shouldExecute()
	{
		if (eater.getRNG().nextInt(eater.isChild() ? 50 : 1000) != 0)
		{
			return false;
		}
		else
		{
			BlockPos blockpos = new BlockPos(eater.posX, eater.posY, eater.posZ);

			if (IS_ACRESIA.apply(world.getBlockState(blockpos)))
			{
				return true;
			}

			return false;
		}
	}

	@Override
	public void startExecuting()
	{
		eatingGrassTimer = 40;
		world.setEntityState(eater, (byte)10);
		eater.getNavigator().clearPath();
	}

	@Override
	public void resetTask()
	{
		this.eatingGrassTimer = 0;
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		return eatingGrassTimer > 0;
	}

	public int getEatingGrassTimer()
	{
		return eatingGrassTimer;
	}

	@Override
	public void updateTask()
	{
		eatingGrassTimer = Math.max(0, eatingGrassTimer - 1);

		if (eatingGrassTimer == 4)
		{
			BlockPos blockpos = new BlockPos(eater.posX, eater.posY, eater.posZ);
			IBlockState state = world.getBlockState(blockpos);

			if (IS_ACRESIA.apply(world.getBlockState(blockpos)))
			{
				if (ForgeEventFactory.getMobGriefingEvent(world, eater))
				{
					world.setBlockState(blockpos, state.withProperty(BlockAcresia.AGE, 3), 2);
					world.playEvent(2001, blockpos, Block.getStateId(state));
				}

				eater.heal(1);
				eater.eatGrassBonus();
			}
		}
	}
}