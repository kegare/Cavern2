package cavern.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityMob;

public class EntityAIAttackMoveRanged<T extends EntityMob & IRangedAttackMob> extends EntityAIBase
{
	private final T entity;
	private final double moveSpeedAmp;
	private int attackCooldown;
	private final float maxAttackDistance;
	private int attackTime = -1;
	private int seeTime;
	private boolean strafingClockwise;
	private boolean strafingBackwards;
	private int strafingTime = -1;

	public EntityAIAttackMoveRanged(T entity, double move, int cooldown, float distance)
	{
		this.entity = entity;
		this.moveSpeedAmp = move;
		this.attackCooldown = cooldown;
		this.maxAttackDistance = distance * distance;
		this.setMutexBits(3);
	}

	public void setAttackCooldown(int time)
	{
		attackCooldown = time;
	}

	@Override
	public boolean shouldExecute()
	{
		return this.entity.getAttackTarget() != null;
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		return (shouldExecute() || !entity.getNavigator().noPath());
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();

		((IRangedAttackMob)entity).setSwingingArms(true);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		entity.getNavigator().clearPath();
		((IRangedAttackMob)entity).setSwingingArms(false);
		seeTime = 0;
		setAttackTime(-1);
	}

	@Override
	public void updateTask()
	{
		EntityLivingBase target = entity.getAttackTarget();

		if (target != null)
		{
			double dist = entity.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
			boolean canSee = entity.getEntitySenses().canSee(target);
			boolean seeing = seeTime > 0;

			if (canSee != seeing)
			{
				seeTime = 0;
			}

			if (canSee)
			{
				++seeTime;
			}
			else
			{
				--seeTime;
			}

			if (dist <= maxAttackDistance)
			{
				entity.getNavigator().clearPath();

				++strafingTime;
			}
			else
			{
				entity.getNavigator().tryMoveToEntityLiving(target, moveSpeedAmp);
				strafingTime = -1;
			}

			if (strafingTime >= 20)
			{
				if (entity.getRNG().nextFloat() < 0.3D)
				{
					strafingClockwise = !strafingClockwise;
				}

				if (entity.getRNG().nextFloat() < 0.3D)
				{
					strafingBackwards = !strafingBackwards;
				}

				strafingTime = 0;
			}

			if (strafingTime > -1)
			{
				if (dist > maxAttackDistance * 0.75F)
				{
					strafingBackwards = false;
				}
				else if (dist < maxAttackDistance * 0.25F)
				{
					strafingBackwards = true;
				}

				entity.getMoveHelper().strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
				entity.faceEntity(target, 30.0F, 30.0F);
			}

			entity.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);

			if (canSee && seeTime > 60)
			{
				((IRangedAttackMob)entity).attackEntityWithRangedAttack(target, 0.6F);

				setAttackTime(attackCooldown);

				seeTime = 0;
			}

		}
	}

	public int getAttackTime()
	{
		return attackTime;
	}

	public void setAttackTime(int time)
	{
		attackTime = time;
	}
}