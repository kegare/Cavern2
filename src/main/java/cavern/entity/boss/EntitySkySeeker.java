package cavern.entity.boss;

import javax.annotation.Nullable;

import cavern.entity.ai.EntityAIAttackMoveRanged;
import cavern.entity.ai.EntityAISeekerChase;
import cavern.entity.ai.EntityAISeekerStatus;
import cavern.entity.ai.EntityAISeekerThunder;
import cavern.entity.ai.EntityFlyHelper;
import cavern.entity.projectile.EntityBeam;
import cavern.util.CaveUtils;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySkySeeker extends EntityMob implements IRangedAttackMob
{
	private static final ResourceLocation LOOT_SEEKER = LootTableList.register(CaveUtils.getKey("entities/sky_seeker"));

	private static final DataParameter<Boolean> SLEEP = EntityDataManager.<Boolean>createKey(EntitySkySeeker.class, DataSerializers.BOOLEAN);
	private static final DataParameter<String> ATTACK_STATUS = EntityDataManager.<String>createKey(EntitySkySeeker.class, DataSerializers.STRING);

	private float heightOffset = 0.5f;
	private int heightOffsetUpdateTime;

	private int ticksProgress;

	private final BossInfoServer bossInfo = new BossInfoServer(getDisplayName(), BossInfo.Color.WHITE, BossInfo.Overlay.PROGRESS);

	public EntitySkySeeker(World world)
	{
		super(world);
		this.setSize(0.75F, 1.9F);
		this.isImmuneToFire = true;
		this.moveHelper = new EntityFlyHelper(this);
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

	@Override
	protected void initEntityAI()
	{
		tasks.addTask(0, new AIDoNothing());
		tasks.addTask(1, new EntityAISeekerStatus(this));
		tasks.addTask(2, new EntityAISeekerChase(this, 1.25F));
		tasks.addTask(3, new EntityAISeekerThunder(this));
		tasks.addTask(5, new EntityAIAttackMoveRanged<EntitySkySeeker>(this, 1.0D, 50, 16.0F)
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
		tasks.addTask(8, new EntityAIWanderAvoidWater(this, 1.1D));
		tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(9, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
		tasks.addTask(10, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
		targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, AbstractIllager.class, true));
		targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityIronGolem.class, true));
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(340.0D);
		getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(8.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.262896D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(30D);
		getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
		getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.44D);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();

		dataManager.register(SLEEP, Boolean.FALSE);
		dataManager.register(ATTACK_STATUS, Status.NONE.name());
	}

	@Override
	protected ResourceLocation getLootTable()
	{
		return LOOT_SEEKER;
	}

	@Override
	public void setInWeb() {}

	@Override
	public void addTrackingPlayer(EntityPlayerMP player)
	{
		super.addTrackingPlayer(player);

		bossInfo.addPlayer(player);
	}

	@Override
	public void removeTrackingPlayer(EntityPlayerMP player)
	{
		super.removeTrackingPlayer(player);

		bossInfo.removePlayer(player);
	}

	@Override
	public void setAttackTarget(@Nullable EntityLivingBase living)
	{
		super.setAttackTarget(living);

		if (living == null)
		{
			setAttackStatus(Status.NONE);
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);

		setSleep(compound.getBoolean("Sleep"));

		if (hasCustomName())
		{
			bossInfo.setName(getDisplayName());
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);

		compound.setBoolean("Sleep", isSleep());
	}

	@Override
	public void setCustomNameTag(String name)
	{
		super.setCustomNameTag(name);

		bossInfo.setName(getDisplayName());
	}

	@Override
	public float getEyeHeight()
	{
		return height * 0.8F;
	}

	@Override
	protected void collideWithEntity(Entity entity)
	{
		super.collideWithEntity(entity);

		if (getAttackStatus() == Status.STOMP)
		{
			entity.attackEntityFrom(DamageSource.causeMobDamage(this), 10.0F);

			if (entity instanceof EntityLivingBase)
			{
				((EntityLivingBase)entity).knockBack(this, 1.25F * 0.5F, MathHelper.sin(rotationYaw * 0.017453292F), -MathHelper.cos(rotationYaw * 0.017453292F));
			}
			else
			{
				entity.addVelocity(-MathHelper.sin(rotationYaw * 0.017453292F) * 1.25F * 0.5F, 0.1D, MathHelper.cos(rotationYaw * 0.017453292F) * 1.25F * 0.5F);
			}
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox()
	{
		return isEntityAlive() ? getEntityBoundingBox() : null;
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
	protected PathNavigate createNavigator(World world)
	{
		PathNavigateFlying flying = new PathNavigateFlying(this, world);

		flying.setCanOpenDoors(false);
		flying.setCanFloat(true);
		flying.setCanEnterDoors(true);

		return flying;
	}

	@Override
	public void onLivingUpdate()
	{
		if (getAttackStatus() != Status.STOMP && !onGround && motionY < 0.0D)
		{
			motionY *= 0.6D;
		}

		setTicksProgress(getTicksProgress() + 1);

		if (getAttackStatus() != Status.NONE && getTicksProgress() >= getMaxTicksForStatus())
		{
			updateStatus();
		}

		super.onLivingUpdate();
	}

	private void updateStatus()
	{
		if (getAttackStatus() == Status.CHASE)
		{
			setAttackStatus(Status.STOMP);
		}
		else if (getAttackStatus() == Status.THUNDER_PRE)
		{
			setAttackStatus(Status.THUNDER);
		}
		else if (getAttackStatus() != Status.NONE)
		{
			setAttackStatus(Status.NONE);
		}
	}

	public void setAttackStatus(String statusName)
	{
		setTicksProgress(0);

		dataManager.set(ATTACK_STATUS, statusName);
	}

	public void setAttackStatus(Status status)
	{
		setTicksProgress(0);

		dataManager.set(ATTACK_STATUS, status.name());
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

	public void setTicksProgress(int progress)
	{
		ticksProgress = progress;
	}

	@Override
	protected void updateAITasks()
	{
		if (!isSleep())
		{
			--heightOffsetUpdateTime;

			if (heightOffsetUpdateTime <= 0)
			{
				heightOffsetUpdateTime = 100;

				heightOffset = 0.5f + (float)rand.nextGaussian() * 2.0f;
			}

			EntityLivingBase target = getAttackTarget();

			if (target != null && target.isEntityAlive() && target.posY + target.getEyeHeight() > posY + getEyeHeight() + heightOffset)
			{
				motionY = (0.2 - motionY) * 0.2;
				isAirBorne = true;
			}

			super.updateAITasks();

			bossInfo.setVisible(true);
		}
		else
		{
			bossInfo.setVisible(false);
		}

		bossInfo.setPercent(getHealth() / getMaxHealth());
	}

	public boolean isSleep()
	{
		return dataManager.get(SLEEP);
	}

	public void setSleep(boolean sleep)
	{
		dataManager.set(SLEEP, sleep);
	}

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor)
	{
		double d1 = target.posX - posX;
		double d2 = target.getEntityBoundingBox().minY + target.height / 2.0F - (posY + height / 2.0F);
		double d3 = target.posZ - posZ;

		EntityBeam projectile = new EntityBeam(world, this, d1 + getRNG().nextGaussian() * 0.01 - 0.005, d2, d3 + getRNG().nextGaussian() * 0.01 - 0.005);
		Vec3d vec3d = getLook(1.0F);

		playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));

		projectile.setLocationAndAngles(posX + vec3d.x * 1.3D, posY + getEyeHeight(), posZ + vec3d.z * 1.3D, rotationYaw, rotationPitch);
		projectile.posY = posY + height / 2.0F + 0.5D;

		world.spawnEntity(projectile);
	}

	@Override
	public void setSwingingArms(boolean swingingArms) {}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (isEntityInvulnerable(source))
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
			for (int i = 0; i < 10; i++)
			{
				int x = MathHelper.floor(posX);
				int y = MathHelper.floor(posY - 0.20000000298023224D);
				int z = MathHelper.floor(posZ);
				IBlockState state = world.getBlockState(new BlockPos(x, y, z));

				if (state.getMaterial() != Material.AIR)
				{
					world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, posX + (rand.nextFloat() - 0.5D) * width, getEntityBoundingBox().minY + 0.1D, posZ + (rand.nextFloat() - 0.5D) * width, 4.0D * (rand.nextFloat() - 0.5D), 0.5D, (rand.nextFloat() - 0.5D) * 4.0D, Block.getStateId(state));
				}
			}

			setAttackStatus(Status.NONE);
		}
	}

	@Override
	protected void updateFallState(double y, boolean onGround, IBlockState state, BlockPos pos) {}

	public enum Status
	{
		NONE(0),
		THUNDER_PRE(80),
		THUNDER(100),
		BEAM(120),
		CHASE(100),
		STOMP(60);

		private final int duration;

		private Status(int duration)
		{
			this.duration = duration;
		}

		private static Status getTypeByName(String name)
		{
			for (Status type : values())
			{
				if (type.name().equals(name))
				{
					return type;
				}
			}

			return NONE;
		}
	}

	private class AIDoNothing extends EntityAIBase
	{
		private AIDoNothing()
		{
			this.setMutexBits(7);
		}

		@Override
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