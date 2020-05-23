package cavern.entity.ai;

import cavern.entity.boss.EntitySkySeeker;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISeekerChase extends EntityAIBase
{
	private final EntitySkySeeker attacker;
	private EntityLivingBase targetEntity;
	private final double speed;

	public EntityAISeekerChase(EntitySkySeeker entitySkySeeker, double speedIn)
	{
		this.attacker = entitySkySeeker;
		this.speed = speedIn;
	}

	@Override
	public boolean shouldExecute()
	{
		this.targetEntity = this.attacker.getAttackTarget();

		if (this.targetEntity == null)
		{
			return false;
		}
		else
		{
			return this.attacker.getAttackStatus() == EntitySkySeeker.Status.CHASE;
		}
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		return this.attacker.getAttackStatus() == EntitySkySeeker.Status.CHASE && this.targetEntity != null || this.attacker.getAttackStatus() == EntitySkySeeker.Status.STOMP && this.targetEntity != null;
	}

	@Override
	public void resetTask()
	{
		this.attacker.getNavigator().clearPath();
		this.targetEntity = null;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		if (this.attacker.getAttackStatus() == EntitySkySeeker.Status.CHASE)
		{
			this.attacker.getNavigator().tryMoveToXYZ(this.targetEntity.posX, this.targetEntity.posY + 6, this.targetEntity.posZ, this.speed);
		}
		else if (this.attacker.getAttackStatus() == EntitySkySeeker.Status.STOMP)
		{
			this.attacker.motionY = -0.45F;
			this.attacker.getNavigator().clearPath();
		}


		this.attacker.getLookHelper().setLookPositionWithEntity(this.targetEntity, 30.0F, 30.0F);
	}
}
