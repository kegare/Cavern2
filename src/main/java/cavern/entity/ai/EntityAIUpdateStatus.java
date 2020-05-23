package cavern.entity.ai;

import cavern.entity.boss.EntitySkySeeker;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIUpdateStatus extends EntityAIBase
{
	private final EntitySkySeeker attacker;
	private EntityLivingBase targetEntity;
	private boolean pinchMode;

	public EntityAIUpdateStatus(EntitySkySeeker entitySkySeeker)
	{
		this.attacker = entitySkySeeker;
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
			if (this.attacker.getHealth() < this.attacker.getMaxHealth() / 2F)
			{
				this.pinchMode = true;
			}

			return this.targetEntity.isEntityAlive() && this.attacker.getAttackStatus() == EntitySkySeeker.Status.NONE;
		}
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();


		if (this.targetEntity != null)
		{
			float i = pinchMode ? 1.5F : 1;

			if (this.attacker.world.canBlockSeeSky(this.targetEntity.getPosition()) && this.attacker.getDistanceSq(this.targetEntity) < 86F && this.attacker.getHealth() < this.attacker.getMaxHealth() / 1.5F && (this.attacker.getRNG().nextFloat() < 0.375F * i || this.attacker.canEntityBeSeen(this.targetEntity)))
			{
				this.attacker.setAttackStatus(EntitySkySeeker.Status.THUNDER_PRE);
			}
			else if (this.attacker.getDistanceSq(this.targetEntity) < 16F && this.attacker.getRNG().nextFloat() < 0.35F)
			{
				this.attacker.setAttackStatus(EntitySkySeeker.Status.CHASE);
			}
			else
			{
				this.attacker.setAttackStatus(EntitySkySeeker.Status.BEAM);
			}
		}
	}

	@Override
	public void resetTask()
	{
		super.resetTask();
	}
}
