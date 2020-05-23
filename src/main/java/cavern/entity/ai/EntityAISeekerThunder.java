package cavern.entity.ai;

import cavern.core.CaveSounds;
import cavern.entity.boss.EntitySkySeeker;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityAISeekerThunder extends EntityAIBase
{
	private final EntitySkySeeker attacker;
	private EntityLivingBase targetEntity;
	private World world;

	private double attackPosX;
	private double attackPosY;
	private double attackPosZ;
	private double attackPosX2;
	private double attackPosY2;
	private double attackPosZ2;

	public EntityAISeekerThunder(EntitySkySeeker entity)
	{
		this.attacker = entity;
		this.world = entity.world;
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
			attackPosX = targetEntity.posX;
			attackPosY = targetEntity.posY;
			attackPosZ = targetEntity.posZ;

			return attacker.getAttackStatus() == EntitySkySeeker.Status.THUNDER_PRE;
		}
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		return attacker.getAttackStatus() == EntitySkySeeker.Status.THUNDER_PRE && targetEntity != null || attacker.getAttackStatus() == EntitySkySeeker.Status.THUNDER && targetEntity != null;
	}

	@Override
	public void resetTask()
	{
		targetEntity = null;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		attacker.getNavigator().clearPath();

		if (attacker.getAttackStatus() == EntitySkySeeker.Status.THUNDER_PRE)
		{
			if (attacker.getTicksProgress() == 1)
			{
				attacker.playSound(CaveSounds.MAGIC_SPELLING, 2.0F, 1.0F);
			}
			else if (attacker.getTicksProgress() == 40)
			{
				attackPosX2 = targetEntity.posX;
				attackPosY2 = targetEntity.posY;
				attackPosZ2 = targetEntity.posZ;
			}

			world.spawnParticle(EnumParticleTypes.SPELL_INSTANT, attackPosX, attackPosY, attackPosZ, 0.0F, 0.0F, 0.0F);
		}
		else if (attacker.getAttackStatus() == EntitySkySeeker.Status.THUNDER)
		{
			if (attacker.getTicksProgress() == 1)
			{
				attacker.playSound(CaveSounds.MAGIC_SUCCESS_MISC, 2.0F, 1.0F);
			}

			if (attacker.getTicksProgress() == 20)
			{
				EntityLightningBolt thunderbolt = new EntityLightningBolt(world, attackPosX, attackPosY, attackPosZ, false);

				world.addWeatherEffect(thunderbolt);
			}

			if (attacker.getTicksProgress() == 40)
			{
				EntityLightningBolt thunderbolt = new EntityLightningBolt(world, attackPosX2, attackPosY2, attackPosZ2, false);

				world.addWeatherEffect(thunderbolt);
			}

			if (attacker.getHealth() < attacker.getMaxHealth() / 2.25 && attacker.getTicksProgress() == 60)
			{
				EntityLightningBolt thunderbolt = new EntityLightningBolt(world, attackPosX, attackPosY, attackPosZ, false);

				world.addWeatherEffect(thunderbolt);

				EntityLightningBolt thunderbolt2 = new EntityLightningBolt(world, attackPosX2, attackPosY2, attackPosZ2, false);

				world.addWeatherEffect(thunderbolt2);
			}
		}

		attacker.getLookHelper().setLookPositionWithEntity(targetEntity, 30.0F, 30.0F);
	}
}