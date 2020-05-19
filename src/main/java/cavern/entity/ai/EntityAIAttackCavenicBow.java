package cavern.entity.ai;

import cavern.item.ItemBowCavenic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class EntityAIAttackCavenicBow<T extends EntityMob & IRangedAttackMob> extends EntityAIAttackRangedBow<T>
{
	private final T attacker;
	private final double moveSpeedAmp;
	private final float maxAttackDistance;
	private final int attackSpeed;

	private int seeTime;
	private int attackTime;
	private int attackCooldown;
	private int attackRapid;
	private boolean strafingClockwise;
	private boolean strafingBackwards;
	private int strafingTime = -1;

	public EntityAIAttackCavenicBow(T attacker, double speedAmplifier, float maxDistance, int attackSpeed)
	{
		super(attacker, speedAmplifier, 0, maxDistance);
		this.attacker = attacker;
		this.moveSpeedAmp = speedAmplifier;
		this.maxAttackDistance = maxDistance * maxDistance;
		this.attackSpeed = attackSpeed;
		this.setMutexBits(3);
	}

	@Override
	public void setAttackCooldown(int time)
	{
	}

	@Override
	public boolean shouldExecute()
	{
		return attacker.getAttackTarget() != null && isBowInMainhand();
	}

	@Override
	protected boolean isBowInMainhand()
	{
		ItemStack held = attacker.getHeldItemMainhand();

		return !held.isEmpty() && held.getItem() instanceof ItemBow;
	}

	protected int getAttackSpeed()
	{
		ItemStack held = attacker.getHeldItemMainhand();

		if (!held.isEmpty() && held.getItem() instanceof ItemBowCavenic)
		{
			return Math.max(attackSpeed / 2, 1);
		}

		return attackSpeed;
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		return (shouldExecute() || !attacker.getNavigator().noPath()) && isBowInMainhand();
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();

		attacker.setSwingingArms(true);
		attackCooldown = 20;
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		attacker.setSwingingArms(false);
		seeTime = 0;
		attackTime = 0;
		attacker.resetActiveHand();
	}

	@Override
	public void updateTask()
	{
		EntityLivingBase target = attacker.getAttackTarget();

		if (target != null)
		{
			double dist = attacker.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
			boolean canSee = attacker.getEntitySenses().canSee(target);
			boolean seeing = seeTime > 0;

			if (canSee != seeing)
			{
				seeTime = 0;
			}

			if (canSee)
			{
				++seeTime;
			} else
			{
				--seeTime;
			}

			if (dist <= maxAttackDistance && seeTime >= 15)
			{
				attacker.getNavigator().clearPath();
				++strafingTime;
			} else
			{
				attacker.getNavigator().tryMoveToEntityLiving(target, moveSpeedAmp);
				strafingTime = -1;
			}

			if (strafingTime >= 5)
			{
				if (attacker.getRNG().nextFloat() < 0.3D)
				{
					strafingClockwise = !strafingClockwise;
				}

				if (attacker.getRNG().nextFloat() < 0.3D)
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
				} else if (dist < maxAttackDistance * 0.25F)
				{
					strafingBackwards = true;
				}

				attacker.getMoveHelper().strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
				attacker.faceEntity(target, 30.0F, 30.0F);
			} else
			{
				attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
			}

			if (attacker.isHandActive())
			{
				if (!canSee && seeTime < -20 || attackTime > 200)
				{
					attacker.resetActiveHand();

					attackTime = 0;
					attackCooldown = 50;
				} else if (canSee && --attackCooldown <= 0)
				{
					if (++attackRapid >= getAttackSpeed())
					{
						attacker.attackEntityWithRangedAttack(target, ItemBow.getArrowVelocity(5));

						attackRapid = 0;
					}

					++attackTime;
				}
			} else if (seeTime >= -20)
			{
				attacker.setActiveHand(EnumHand.MAIN_HAND);

				attackTime = 0;
			}
		}
	}
}