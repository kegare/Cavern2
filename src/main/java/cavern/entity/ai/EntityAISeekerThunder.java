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

	World world;

	private double attackPosX;
	private double attackPosY;
	private double attackPosZ;
	private double attackPosX2;
	private double attackPosY2;
	private double attackPosZ2;

	public EntityAISeekerThunder(EntitySkySeeker entitySkySeeker)
	{
		this.attacker = entitySkySeeker;
		this.world = entitySkySeeker.world;
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
			this.attackPosX = this.targetEntity.posX;
			this.attackPosY = this.targetEntity.posY;
			this.attackPosZ = this.targetEntity.posZ;

			return this.attacker.getAttackStatus() == EntitySkySeeker.Status.THUNDER_PRE;
		}
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		return this.attacker.getAttackStatus() == EntitySkySeeker.Status.THUNDER_PRE && this.targetEntity != null || this.attacker.getAttackStatus() == EntitySkySeeker.Status.THUNDER && this.targetEntity != null;
	}

	@Override
	public void resetTask()
	{
		this.targetEntity = null;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		double d0 = this.attacker.getDistanceSq(targetEntity.posX, targetEntity.getEntityBoundingBox().minY, targetEntity.posZ);


		this.attacker.getNavigator().clearPath();

		if (this.attacker.getAttackStatus() == EntitySkySeeker.Status.THUNDER_PRE)
		{
			if (this.attacker.getTicksProgress() == 1)
			{
				this.attacker.playSound(CaveSounds.MAGIC_SPELLING, 2.0F, 1.0F);
			}
			else if (this.attacker.getTicksProgress() == 40)
			{
				this.attackPosX2 = this.targetEntity.posX;
				this.attackPosY2 = this.targetEntity.posY;
				this.attackPosZ2 = this.targetEntity.posZ;
			}

			this.world.spawnParticle(EnumParticleTypes.SPELL_INSTANT, this.attackPosX, this.attackPosY, this.attackPosZ, 0.0F, 0.0F, 0.0F);
		}
		else if (this.attacker.getAttackStatus() == EntitySkySeeker.Status.THUNDER)
		{
			if (this.attacker.getTicksProgress() == 1)
			{
				this.attacker.playSound(CaveSounds.MAGIC_SUCCESS_MISC, 2.0F, 1.0F);
			}


			if (this.attacker.getTicksProgress() == 20)
			{
				EntityLightningBolt thunderbolt = new EntityLightningBolt(this.world, this.attackPosX, this.attackPosY, this.attackPosZ, false);

				this.world.addWeatherEffect(thunderbolt);
			}
			if (this.attacker.getTicksProgress() == 40)
			{
				EntityLightningBolt thunderbolt = new EntityLightningBolt(this.world, this.attackPosX2, this.attackPosY2, this.attackPosZ2, false);

				this.world.addWeatherEffect(thunderbolt);
			}

			if (this.attacker.getHealth() < this.attacker.getMaxHealth() / 2.25 && this.attacker.getTicksProgress() == 60)
			{
				EntityLightningBolt thunderbolt = new EntityLightningBolt(this.world, this.attackPosX, this.attackPosY, this.attackPosZ, false);

				this.world.addWeatherEffect(thunderbolt);

				EntityLightningBolt thunderbolt2 = new EntityLightningBolt(this.world, this.attackPosX2, this.attackPosY2, this.attackPosZ2, false);

				this.world.addWeatherEffect(thunderbolt2);
			}

		}


		this.attacker.getLookHelper().setLookPositionWithEntity(this.targetEntity, 30.0F, 30.0F);
	}
}
