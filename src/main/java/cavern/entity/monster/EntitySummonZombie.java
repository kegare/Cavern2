package cavern.entity.monster;

import cavern.api.entity.IEntitySummonable;
import cavern.core.Cavern;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class EntitySummonZombie extends EntityZombie implements IEntitySummonable
{
	private int lifeTime;
	private EntityPlayer summoner;

	public EntitySummonZombie(World world)
	{
		this(world, null);
	}

	public EntitySummonZombie(World world, int lifeTime)
	{
		this(world, lifeTime, null);
	}

	public EntitySummonZombie(World world, EntityPlayer player)
	{
		this(world, 6000, player);
	}

	public EntitySummonZombie(World world, int lifeTime, EntityPlayer player)
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
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(2, new EntityAIZombieAttack(this, 1.0D, false));
		tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
		tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityMob.class, 8.0F));
		tasks.addTask(8, new EntityAILookIdle(this));
		applyEntityAI();
	}

	@Override
	protected void applyEntityAI()
	{
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
	public void setBreakDoorsAItask(boolean enabled) {}

	@Override
	protected int getExperiencePoints(EntityPlayer player)
	{
		return 0;
	}

	@Override
	protected boolean shouldBurnInDay()
	{
		return false;
	}

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
	protected ItemStack getSkullDrop()
	{
		return ItemStack.EMPTY;
	}

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