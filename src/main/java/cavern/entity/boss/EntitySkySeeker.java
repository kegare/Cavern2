package cavern.entity.boss;

import cavern.entity.ai.EntityAIAttackMoveRanged;
import cavern.entity.ai.EntityAISeekerChase;
import cavern.entity.ai.EntityAISeekerThunder;
import cavern.entity.ai.EntityAIUpdateStatus;
import cavern.entity.movehelper.EntityCaveFlyHelper;
import cavern.entity.projectile.EntityBeam;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntitySkySeeker extends EntityMob implements IRangedAttackMob
{
	private static final DataParameter<Boolean> SLEEP = EntityDataManager.<Boolean>createKey(EntitySkySeeker.class, DataSerializers.BOOLEAN);
	private static final DataParameter<String> ATTACK_STATUS = EntityDataManager.<String>createKey(EntitySkySeeker.class, DataSerializers.STRING);


	private float heightOffset = 0.5f;
	private int heightOffsetUpdateTime;

	private int ticksProgress;


	public final BossInfoServer bossInfo = (BossInfoServer) (new BossInfoServer(this.getDisplayName(), BossInfo.Color.WHITE, BossInfo.Overlay.PROGRESS));

	public EntitySkySeeker(World worldIn)
	{
		super(worldIn);
		this.setSize(0.75F, 1.9F);
		this.isImmuneToFire = true;
		this.moveHelper = new EntityCaveFlyHelper(this);
		this.experienceValue = 100;
		this.bossInfo.setVisible(false);
	}

	@Override
	public boolean isNonBoss()
	{
		return false;
	}

	public BossInfoServer getBossInfo()
	{
		return bossInfo;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	protected void initEntityAI()
	{
		this.tasks.addTask(0, new AIDoNothing());
		this.tasks.addTask(1, new EntityAIUpdateStatus(this));
		this.tasks.addTask(2, new EntityAISeekerChase(this, 1.25F));
		this.tasks.addTask(3, new EntityAISeekerThunder(this));
		this.tasks.addTask(5, new EntityAIAttackMoveRanged(this, 1.0D, 50, 16.0F)
		{
			@Override
			public boolean shouldExecute()
			{
				return super.shouldExecute() && isBeamStatus();
			}

			@Override
			public boolean shouldContinueExecuting()
			{
				return super.shouldContinueExecuting() && isBeamStatus();
			}
		});
		this.tasks.addTask(8, new EntityAIWanderAvoidWater(this, 1.1D));
		this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
		this.tasks.addTask(10, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, AbstractIllager.class, true));
		this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityIronGolem.class, true));
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(340.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(8.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.262896D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(30D);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
		this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.44D);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(SLEEP, Boolean.FALSE);
		this.dataManager.register(ATTACK_STATUS, Status.NONE.name());
	}

	public void setInWeb()
	{
	}

	public void addTrackingPlayer(EntityPlayerMP player)
	{
		super.addTrackingPlayer(player);
		this.bossInfo.addPlayer(player);
	}

	public void removeTrackingPlayer(EntityPlayerMP player)
	{
		super.removeTrackingPlayer(player);
		this.bossInfo.removePlayer(player);
	}

	@Override
	public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn)
	{
		super.setAttackTarget(entitylivingbaseIn);
		if (entitylivingbaseIn == null)
		{
			this.setAttackStatus(Status.NONE);
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		this.setSleep(compound.getBoolean("Sleep"));

		if (this.hasCustomName())
		{
			this.bossInfo.setName(this.getDisplayName());
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		compound.setBoolean("Sleep", this.isSleep());
	}

	public void setCustomNameTag(String name)
	{
		super.setCustomNameTag(name);
		this.bossInfo.setName(this.getDisplayName());
	}

	@Override
	public float getEyeHeight()
	{
		return this.height * 0.8F;
	}

	@Override
	protected void collideWithEntity(Entity entityIn)
	{
		super.collideWithEntity(entityIn);
		if (this.getAttackStatus() == Status.STOMP)
		{
			entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 10.0F);
			if (entityIn instanceof EntityLivingBase)
			{
				((EntityLivingBase) entityIn).knockBack(this, 1.25F * 0.5F, MathHelper.sin(rotationYaw * 0.017453292F), -MathHelper.cos(rotationYaw * 0.017453292F));
			}
			else
			{
				entityIn.addVelocity(-MathHelper.sin(rotationYaw * 0.017453292F) * 1.25F * 0.5F, 0.1D, MathHelper.cos(rotationYaw * 0.017453292F) * 1.25F * 0.5F);
			}
		}
	}

	@Nullable
	public AxisAlignedBB getCollisionBoundingBox()
	{
		return this.isEntityAlive() ? this.getEntityBoundingBox() : null;
	}

	@Override
	public boolean canBreatheUnderwater()
	{
		return true;
	}

	@Override
	public boolean doesEntityNotTriggerPressurePlate()
	{
		return true;
	}

	@Override
	protected PathNavigate createNavigator(World worldIn)
	{
		PathNavigateFlying pathnavigateflying = new PathNavigateFlying(this, worldIn);
		pathnavigateflying.setCanOpenDoors(false);
		pathnavigateflying.setCanFloat(true);
		pathnavigateflying.setCanEnterDoors(true);
		return pathnavigateflying;
	}

	@Override
	public void onLivingUpdate()
	{
		if (this.getAttackStatus() != Status.STOMP && !this.onGround && this.motionY < 0.0D)
		{
			this.motionY *= 0.6D;
		}

		this.setTicksProgress(this.getTicksProgress() + 1);

		if (this.getAttackStatus() != Status.NONE && this.getTicksProgress() >= this.getMaxTicksForStatus())
		{
			this.updateStatus();
		}


		super.onLivingUpdate();
	}

	private void updateStatus()
	{
		if (this.getAttackStatus() == Status.CHASE)
		{
			this.setAttackStatus(Status.STOMP);
		}
		else if (this.getAttackStatus() == Status.THUNDER_PRE)
		{
			this.setAttackStatus(Status.THUNDER);
		}
		else if (this.getAttackStatus() != Status.NONE)
		{
			this.setAttackStatus(Status.NONE);
		}
	}

	public void setAttackStatus(String statusName)
	{
		this.setTicksProgress(0);
		this.dataManager.set(ATTACK_STATUS, statusName);
	}

	public void setAttackStatus(Status status)
	{
		this.setTicksProgress(0);
		this.dataManager.set(ATTACK_STATUS, status.name());
	}

	public Status getAttackStatus()
	{
		return Status.getTypeByName(dataManager.get(ATTACK_STATUS));
	}


	public int getMaxTicksForStatus()
	{
		return getAttackStatus().duration;
	}

	public boolean isBeamStatus()
	{
		return getAttackStatus() == Status.BEAM;
	}

	public boolean isThunderPreAttack()
	{
		return getAttackStatus() == Status.THUNDER_PRE;
	}

	public boolean isThunderAttack()
	{
		return getAttackStatus() == Status.THUNDER;
	}

	public boolean isMagicPreAttack()
	{
		return getAttackStatus() == Status.THUNDER_PRE;
	}

	public boolean isMagicAttack()
	{
		return getAttackStatus() == Status.THUNDER;
	}

	public int getTicksProgress()
	{
		return ticksProgress;
	}

	public void setTicksProgress(int ticksProgress)
	{
		this.ticksProgress = ticksProgress;
	}

	@Override
	protected void updateAITasks()
	{
		if (!isSleep())
		{
			--this.heightOffsetUpdateTime;

			if (this.heightOffsetUpdateTime <= 0)
			{

				this.heightOffsetUpdateTime = 100;

				this.heightOffset = 0.5f + (float) this.rand.nextGaussian() * 2.0f;

			}

			EntityLivingBase target = getAttackTarget();

			if (target != null && target.isEntityAlive() && target.posY + (double) target.getEyeHeight() > this.posY + (double) getEyeHeight() + (double) this.heightOffset)
			{
				this.motionY = (0.2 - motionY) * 0.2;
				this.isAirBorne = true;
			}

			super.updateAITasks();

			this.bossInfo.setVisible(true);
		}
		else
		{
			this.bossInfo.setVisible(false);
		}
		this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
	}

	public boolean isSleep()
	{
		return ((Boolean) this.dataManager.get(SLEEP)).booleanValue();
	}

	public void setSleep(boolean sleep)
	{
		this.dataManager.set(SLEEP, Boolean.valueOf(sleep));
	}

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor)
	{
		double d1 = target.posX - this.posX;
		double d2 = target.getEntityBoundingBox().minY + (double) (target.height / 2.0F) - (this.posY + (double) (this.height / 2.0F));
		double d3 = target.posZ - this.posZ;

		EntityBeam projectile = new EntityBeam(this.world, this, d1 + this.getRNG().nextGaussian() * 0.01 - 0.005, d2, d3 + this.getRNG().nextGaussian() * 0.01 - 0.005);

		Vec3d vec3d = this.getLook(1.0F);

		playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));

		projectile.setLocationAndAngles(this.posX + vec3d.x * 1.3D, this.posY + this.getEyeHeight(), this.posZ + vec3d.z * 1.3D, this.rotationYaw, this.rotationPitch);

//        float d0 = (this.rand.nextFloat() * 16.0F) - 8.0F;

		projectile.posY = this.posY + (double) (this.height / 2.0F) + 0.5D;
		this.world.spawnEntity(projectile);

	}

	@Override
	public void setSwingingArms(boolean swingingArms)
	{

	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (this.isEntityInvulnerable(source))
		{
			return false;
		}
		else if (source.isFireDamage() || source == DamageSource.LIGHTNING_BOLT)
		{
			return false;
		}
		else
		{
			if (isSleep())
			{
				if (source.getImmediateSource() instanceof EntityLivingBase)
				{
					setSleep(false);

					return false;
				}
			}
			if (source.getImmediateSource() instanceof EntityArrow)
			{
				return false;
			}
			else if (source.isProjectile())
			{
				return super.attackEntityFrom(source, amount * 0.65F);
			}
			else
			{
				return super.attackEntityFrom(source, amount * 0.85F);
			}
		}
	}


	@Override
	public void fall(float distance, float damageMultiplier)
	{
		if (getAttackStatus() == Status.STOMP)
		{
			for (int f = 0; f < 10; f++)
			{
				int i = MathHelper.floor(this.posX);
				int j = MathHelper.floor(this.posY - 0.20000000298023224D);
				int k = MathHelper.floor(this.posZ);
				IBlockState iblockstate = this.world.getBlockState(new BlockPos(i, j, k));

				if (iblockstate.getMaterial() != Material.AIR)
				{
					this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, 4.0D * ((double) this.rand.nextFloat() - 0.5D), 0.5D, ((double) this.rand.nextFloat() - 0.5D) * 4.0D, Block.getStateId(iblockstate));
				}
			}

			setAttackStatus(Status.NONE);
		}
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos)
	{
	}

	public enum Status
	{
		NONE(0),
		THUNDER_PRE(80),
		THUNDER(100),
		BEAM(120),
		CHASE(100),
		STOMP(60);

		final int duration;

		Status(int duration)
		{
			this.duration = duration;
		}

		private static Status getTypeByName(String nameIn)
		{
			for (Status type : values())
			{
				if (type.name().equals(nameIn))
				{
					return type;
				}
			}
			return NONE;
		}
	}

	class AIDoNothing extends EntityAIBase
	{
		public AIDoNothing()
		{
			this.setMutexBits(7);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute()
		{
			return EntitySkySeeker.this.isSleep();
		}

		@Override
		public void updateTask()
		{
			super.updateTask();
			EntitySkySeeker.this.getNavigator().clearPath();
		}
	}
}