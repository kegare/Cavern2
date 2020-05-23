package cavern.entity.monster;

import cavern.entity.ai.EntityAIAttackMoveRanged;
import cavern.entity.ai.EntityFlyHelper;
import cavern.entity.projectile.EntityBeam;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityCrystalTurret extends EntityMob implements IRangedAttackMob
{
	private float heightOffset = 0.5f;
	private int heightOffsetUpdateTime;

	public EntityCrystalTurret(World world)
	{
		super(world);
		this.setSize(0.65F, 0.65F);
		this.isImmuneToFire = true;
		this.moveHelper = new EntityFlyHelper(this);
	}

	@Override
	protected void initEntityAI()
	{
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(3, new EntityAIAttackMoveRanged<>(this, 1.0D, 35, 16.0F));
		tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.1D));
		tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(6, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
		targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityIronGolem.class, true));
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
		getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(3.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.282896D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20D);
		getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
		getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.42D);
	}

	@Override
	public float getEyeHeight()
	{
		return height * 0.55F;
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
		if (!onGround && motionY < 0.0D)
		{
			motionY *= 0.6D;
		}

		super.onLivingUpdate();
	}

	@Override
	protected void updateAITasks()
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
		Vec3d vec3d = this.getLook(1.0F);

		playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));

		projectile.setLocationAndAngles(posX + vec3d.x * 1.3D, posY + getEyeHeight(), posZ + vec3d.z * 1.3D, rotationYaw, rotationPitch);
		projectile.posY = posY + height / 2.0F + 0.5D;

		world.spawnEntity(projectile);
	}

	@Override
	public boolean isOnSameTeam(Entity entity)
	{
		if (super.isOnSameTeam(entity))
		{
			return true;
		}
		else if (entity instanceof EntityCrystalTurret)
		{
			return getTeam() == null && entity.getTeam() == null;
		}
		else
		{
			return false;
		}
	}

	@Override
	public void fall(float distance, float damageMultiplier) {}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {}

	@Override
	public void setSwingingArms(boolean swingingArms) {}
}