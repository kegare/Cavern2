package cavern.entity.ai;

import cavern.entity.boss.EntitySkySeeker;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISeekerStatus extends EntityAIBase
{
	private final EntitySkySeeker attacker;
	private EntityLivingBase targetEntity;
	private boolean pinchMode;

	public EntityAISeekerStatus(EntitySkySeeker entity)
	{
		this.attacker = entity;
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
			if (attacker.getHealth() < attacker.getMaxHealth() / 2F)
			{
				pinchMode = true;
			}

			return targetEntity.isEntityAlive() && attacker.getAttackStatus() == EntitySkySeeker.Status.NONE;
		}
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();

		if (targetEntity != null)
		{
			float i = pinchMode ? 1.5F : 1;

			if (attacker.world.canBlockSeeSky(targetEntity.getPosition()) && attacker.getDistanceSq(targetEntity) < 86F && attacker.getHealth() < attacker.getMaxHealth() / 1.5F && (attacker.getRNG().nextFloat() < 0.375F * i || attacker.canEntityBeSeen(targetEntity)))
			{
				attacker.setAttackStatus(EntitySkySeeker.Status.THUNDER_PRE);
			}
			else if (attacker.getDistanceSq(targetEntity) < 16F && attacker.getRNG().nextFloat() < 0.35F)
			{
				attacker.setAttackStatus(EntitySkySeeker.Status.CHASE);
			}
			else
			{
				attacker.setAttackStatus(EntitySkySeeker.Status.BEAM);
			}
		}
	}
}