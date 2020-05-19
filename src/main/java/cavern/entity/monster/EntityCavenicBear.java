package cavern.entity.monster;

import cavern.api.entity.ICavenicMob;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class EntityCavenicBear extends EntityPolarBear implements ICavenicMob, IMob
{
	public EntityCavenicBear(World world)
	{
		super(world);
		this.experienceValue = 13;
	}

	@Override
	protected void initEntityAI()
	{
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new AIMeleeAttack());
		tasks.addTask(1, new AIPanic());
		tasks.addTask(4, new EntityAIFollowParent(this, 1.25D));
		tasks.addTask(5, new EntityAIWander(this, 1.0D));
		tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(7, new EntityAILookIdle(this));
		targetTasks.addTask(1, new AIHurtByTarget());
		targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 20, true, true, null));
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0D);
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable)
	{
		return null;
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
		return livingdata;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (!world.isRemote && world.getDifficulty() == EnumDifficulty.PEACEFUL)
		{
			setDead();
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage)
	{
		if (source == DamageSource.FALL)
		{
			damage *= 0.35F;
		}

		return !source.isFireDamage() && super.attackEntityFrom(source, damage);
	}

	@Override
	public float getBlockPathWeight(BlockPos pos)
	{
		return 0.5F - world.getLightBrightness(pos);
	}

	protected boolean isValidLightLevel()
	{
		BlockPos pos = new BlockPos(posX, getEntityBoundingBox().minY, posZ);

		if (world.getLightFor(EnumSkyBlock.SKY, pos) > rand.nextInt(32))
		{
			return false;
		}
		else
		{
			int i = world.getLightFromNeighbors(pos);

			if (world.isThundering())
			{
				int j = world.getSkylightSubtracted();
				world.setSkylightSubtracted(10);
				i = world.getLightFromNeighbors(pos);
				world.setSkylightSubtracted(j);
			}

			return i <= rand.nextInt(8);
		}
	}

	@Override
	protected boolean canDropLoot()
	{
		return true;
	}

	@Override
	public boolean getCanSpawnHere()
	{
		if (!world.getBiome(getPosition()).isSnowyBiome())
		{
			return false;
		}

		IBlockState state = world.getBlockState(new BlockPos(this).down());

		if (!state.canEntitySpawn(this))
		{
			return false;
		}

		if (getBlockPathWeight(new BlockPos(posX, getEntityBoundingBox().minY, posZ)) < 0.0F)
		{
			return false;
		}

		return world.getDifficulty() != EnumDifficulty.PEACEFUL && isValidLightLevel();
	}

	@Override
	public int getMaxSpawnedInChunk()
	{
		return 1;
	}

	@Override
	protected int getExperiencePoints(EntityPlayer player)
	{
		return experienceValue;
	}

	class AIHurtByTarget extends EntityAIHurtByTarget
	{
		public AIHurtByTarget()
		{
			super(EntityCavenicBear.this, false, new Class[0]);
		}

		@Override
		public void startExecuting()
		{
			super.startExecuting();

			alertOthers();
		}

		@Override
		protected void setEntityAttackTarget(EntityCreature creature, EntityLivingBase entity)
		{
			if (creature instanceof EntityPolarBear && !creature.isChild())
			{
				super.setEntityAttackTarget(creature, entity);
			}
		}
	}

	class AIMeleeAttack extends EntityAIAttackMelee
	{
		public AIMeleeAttack()
		{
			super(EntityCavenicBear.this, 1.25D, true);
		}

		@Override
		protected void checkAndPerformAttack(EntityLivingBase entity, double distance)
		{
			double reachSq = getAttackReachSqr(entity);

			if (distance <= reachSq && attackTick <= 0)
			{
				attackTick = 20;
				attacker.attackEntityAsMob(entity);

				EntityCavenicBear.this.setStanding(false);
			}
			else if (distance <= reachSq * 2.0D)
			{
				if (attackTick <= 0)
				{
					EntityCavenicBear.this.setStanding(false);

					attackTick = 20;
				}

				if (attackTick <= 10)
				{
					EntityCavenicBear.this.setStanding(true);
					EntityCavenicBear.this.playWarningSound();
				}
			}
			else
			{
				attackTick = 20;

				EntityCavenicBear.this.setStanding(false);
			}
		}

		@Override
		public void resetTask()
		{
			EntityCavenicBear.this.setStanding(false);

			super.resetTask();
		}

		@Override
		protected double getAttackReachSqr(EntityLivingBase attackTarget)
		{
			return 4.0F + attackTarget.width;
		}
	}

	class AIPanic extends EntityAIPanic
	{
		public AIPanic()
		{
			super(EntityCavenicBear.this, 2.0D);
		}

		@Override
		public boolean shouldExecute()
		{
			return EntityCavenicBear.this.isBurning() && super.shouldExecute();
		}
	}
}