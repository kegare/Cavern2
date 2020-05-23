package cavern.entity.monster;

import cavern.entity.ai.EntityAIAttackMoveRanged;
import cavern.entity.movehelper.EntityCaveFlyHelper;
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

	public EntityCrystalTurret(World worldIn)
	{
		super(worldIn);
		this.setSize(0.65F, 0.65F);
		this.isImmuneToFire = true;
		this.moveHelper = new EntityCaveFlyHelper(this);
	}

	@Override
	protected void initEntityAI()
	{
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(3, new EntityAIAttackMoveRanged<>(this, 1.0D, 35, 16.0F));
		this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.1D));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(6, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityIronGolem.class, true));
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(3.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.282896D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20D);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
		this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.42D);
	}

	@Override
	public float getEyeHeight()
	{
		return this.height * 0.55F;
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
		if (!this.onGround && this.motionY < 0.0D)
		{
			this.motionY *= 0.6D;
		}

		super.onLivingUpdate();
	}

	@Override
	protected void updateAITasks()
	{
		// stay over the target by moment

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
	public boolean isOnSameTeam(Entity entityIn)
	{
		if (super.isOnSameTeam(entityIn))
		{
			return true;
		}
		else if (entityIn instanceof EntityCrystalTurret)
		{
			return this.getTeam() == null && entityIn.getTeam() == null;
		}
		else
		{
			return false;
		}
	}

	@Override
	public void fall(float distance, float damageMultiplier)
	{
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos)
	{
	}

	@Override
	public void setSwingingArms(boolean swingingArms)
	{

	}
}