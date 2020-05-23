package cavern.entity.ai;

import cavern.entity.boss.EntitySkySeeker;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISeekerChase extends EntityAIBase
{
	private final EntitySkySeeker attacker;
	private EntityLivingBase targetEntity;
	private final double speed;

	public EntityAISeekerChase(EntitySkySeeker entity, double speed)
	{
		this.attacker = entity;
		this.speed = speed;
	}

	@Override
	public boolean shouldExecute()
	{
		targetEntity = attacker.getAttackTarget();

		if (targetEntity == null)
		{
			return false;
		}
		else
		{
			return attacker.getAttackStatus() == EntitySkySeeker.Status.CHASE;
		}
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		return attacker.getAttackStatus() == EntitySkySeeker.Status.CHASE && targetEntity != null || attacker.getAttackStatus() == EntitySkySeeker.Status.STOMP && targetEntity != null;
	}

	@Override
	public void resetTask()
	{
		attacker.getNavigator().clearPath();

		targetEntity = null;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		if (attacker.getAttackStatus() == EntitySkySeeker.Status.CHASE)
		{
			attacker.getNavigator().tryMoveToXYZ(targetEntity.posX, targetEntity.posY + 6, targetEntity.posZ, speed);
		}
		else if (attacker.getAttackStatus() == EntitySkySeeker.Status.STOMP)
		{
			attacker.motionY = -0.45F;
			attacker.getNavigator().clearPath();
		}

		attacker.getLookHelper().setLookPositionWithEntity(targetEntity, 30.0F, 30.0F);
	}
}