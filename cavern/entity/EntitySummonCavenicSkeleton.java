package cavern.entity;

import cavern.api.entity.IEntitySummonable;
import cavern.core.Cavern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class EntitySummonCavenicSkeleton extends EntityCavenicSkeleton implements IEntitySummonable
{
	private int lifeTime;
	private EntityPlayer summoner;

	public EntitySummonCavenicSkeleton(World world)
	{
		this(world, null);
	}

	public EntitySummonCavenicSkeleton(World world, int lifeTime)
	{
		this(world, lifeTime, null);
	}

	public EntitySummonCavenicSkeleton(World world, EntityPlayer player)
	{
		this(world, 2400, player);
	}

	public EntitySummonCavenicSkeleton(World world, int lifeTime, EntityPlayer player)
	{
		super(world);
		this.experienceValue = 0;
		this.isImmuneToFire = true;
		this.lifeTime = lifeTime;
		this.summoner = player;
	}

	@Override
	protected void initEntityAI()
	{
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
		tasks.addTask(6, new EntityAIWatchClosest(this, EntityMob.class, 8.0F));
		tasks.addTask(6, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false, CAN_SUMMON_MOB_TARGET));
	}

	@Override
	public String getName()
	{
		String name = super.getName();

		if (hasCustomName())
		{
			return name;
		}

		return Cavern.proxy.translateFormat("entity.summon.name", name);
	}

	@Override
	public boolean isFriends(Entity entity)
	{
		return entity != null && entity instanceof EntityCavenicSkeleton && entity instanceof IEntitySummonable;
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source)
	{
		if (super.isEntityInvulnerable(source))
		{
			return true;
		}

		if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer)
		{
			return true;
		}

		return false;
	}

	@Override
	public void setFire(int seconds) {}

	@Override
	protected boolean canDropLoot()
	{
		return false;
	}

	@Override
	protected ResourceLocation getLootTable()
	{
		return null;
	}

	@Override
	protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {}

	@Override
	protected boolean canDespawn()
	{
		return false;
	}

	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();

		if (!world.isRemote)
		{
			if (--lifeTime <= 0)
			{
				setDead();
			}
			else if (summoner != null && !summoner.isEntityAlive())
			{
				setDead();
			}
		}
	}

	@Override
	public void setDead()
	{
		super.setDead();

		if (!world.isRemote)
		{
			playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
		}
	}

	@Override
	public int getLifeTime()
	{
		return lifeTime;
	}

	@Override
	public EntityPlayer getSummoner()
	{
		return summoner;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);

		compound.setInteger("LifeTime", lifeTime);

		if (summoner != null)
		{
			compound.setTag("Summoner", NBTUtil.createUUIDTag(EntityPlayer.getUUID(summoner.getGameProfile())));
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);

		lifeTime = compound.getInteger("LifeTime");

		if (compound.hasKey("Summoner", NBT.TAG_COMPOUND))
		{
			summoner = world.getPlayerEntityByUUID(NBTUtil.getUUIDFromTag(compound.getCompoundTag("Summoner")));
		}
	}
}