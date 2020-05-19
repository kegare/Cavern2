package cavern.entity.ai;

import cavern.block.BlockAcresia;
import cavern.block.CaveBlocks;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAIEatAcresia extends EntityAIBase
{
	private static final Predicate<IBlockState> IS_ACRESIA = BlockStateMatcher.forBlock(CaveBlocks.ACRESIA).where(BlockAcresia.AGE, Predicates.equalTo(4));
	private final EntityLiving acresiaEaterEntity;
	private final World entityWorld;
	int eatingGrassTimer;

	public EntityAIEatAcresia(EntityLiving acresiaEaterEntityIn)
	{
		this.acresiaEaterEntity = acresiaEaterEntityIn;
		this.entityWorld = acresiaEaterEntityIn.world;
		this.setMutexBits(7);
	}

	public boolean shouldExecute()
	{
		if (this.acresiaEaterEntity.getRNG().nextInt(this.acresiaEaterEntity.isChild() ? 50 : 1000) != 0)
		{
			return false;
		} else
		{
			BlockPos blockpos = new BlockPos(this.acresiaEaterEntity.posX, this.acresiaEaterEntity.posY, this.acresiaEaterEntity.posZ);

			if (IS_ACRESIA.apply(this.entityWorld.getBlockState(blockpos)))
			{
				return true;
			}
			return false;
		}
	}

	public void startExecuting()
	{
		this.eatingGrassTimer = 40;
		this.entityWorld.setEntityState(this.acresiaEaterEntity, (byte) 10);
		this.acresiaEaterEntity.getNavigator().clearPath();
	}

	public void resetTask()
	{
		this.eatingGrassTimer = 0;
	}

	public boolean shouldContinueExecuting()
	{
		return this.eatingGrassTimer > 0;
	}

	public int getEatingGrassTimer()
	{
		return this.eatingGrassTimer;
	}

	public void updateTask()
	{
		this.eatingGrassTimer = Math.max(0, this.eatingGrassTimer - 1);

		if (this.eatingGrassTimer == 4)
		{
			BlockPos blockpos = new BlockPos(this.acresiaEaterEntity.posX, this.acresiaEaterEntity.posY, this.acresiaEaterEntity.posZ);
			IBlockState iblockstate = this.entityWorld.getBlockState(blockpos);

			if (IS_ACRESIA.apply(this.entityWorld.getBlockState(blockpos)))
			{
				if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.entityWorld, this.acresiaEaterEntity))
				{
					this.entityWorld.setBlockState(blockpos, iblockstate.withProperty(BlockAcresia.AGE, 3), 2);
					this.entityWorld.playEvent(2001, blockpos, Block.getStateId(iblockstate));
				}

				this.acresiaEaterEntity.heal(1);
				this.acresiaEaterEntity.eatGrassBonus();
			}
		}
	}
}