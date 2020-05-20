package cavern.entity.monster;

import cavern.client.particle.ParticleCrazyMob;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCrazyZombie extends EntityCavenicZombie
{
	private final BossInfoServer bossInfo = new BossInfoServer(getDisplayName(), BossInfo.Color.BLUE, BossInfo.Overlay.PROGRESS);

	public EntityCrazyZombie(World world)
	{
		super(world);
		this.experienceValue = 50;
	}

	@Override
	protected void applyMobAttributes()
	{
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(2000.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.5D);
		getEntityAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0D);
	}

	@Override
	public int getMaxSpawnedInChunk()
	{
		return 1;
	}

	@Override
	public boolean isNonBoss()
	{
		return false;
	}

	@Override
	protected boolean canBeRidden(Entity entity)
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (world.isRemote)
		{
			for (int i = 0; i < 3; ++i)
			{
				int var1 = rand.nextInt(2) * 2 - 1;
				int var2 = rand.nextInt(2) * 2 - 1;
				double ptX = posX + 0.25D * var1;
				double ptY = posY + 0.65D + rand.nextFloat();
				double ptZ = posZ + 0.25D * var2;
				double motionX = rand.nextFloat() * 1.0F * var1;
				double motionY = (rand.nextFloat() - 0.25D) * 0.125D;
				double motionZ = rand.nextFloat() * 1.0F * var2;
				ParticleCrazyMob particle = new ParticleCrazyMob(world, ptX, ptY, ptZ, motionX, motionY, motionZ);

				FMLClientHandler.instance().getClient().effectRenderer.addEffect(particle);
			}
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		boolean ret = super.attackEntityAsMob(entity);

		int power = rand.nextInt(5) == 0 ? rand.nextInt(3) + 3 : 0;

		if (power > 0)
		{
			if (entity instanceof EntityLivingBase)
			{
				((EntityLivingBase)entity).knockBack(this, power * 0.5F, MathHelper.sin(rotationYaw * 0.017453292F), -MathHelper.cos(rotationYaw * 0.017453292F));
			}
			else
			{
				entity.addVelocity(-MathHelper.sin(rotationYaw * 0.017453292F) * power * 0.5F, 0.1D, MathHelper.cos(rotationYaw * 0.017453292F) * power * 0.5F);
			}
		}

		return ret;
	}

	@Override
	protected void updateAITasks()
	{
		super.updateAITasks();

		if (!world.isRemote)
		{
			boolean canSee = false;
			double distance = -1.0F;

			for (EntityPlayerMP player : bossInfo.getPlayers())
			{
				distance = getDistance(player);

				if (canEntityBeSeen(player) && distance < 20.0D)
				{
					canSee = true;

					break;
				}
			}

			bossInfo.setDarkenSky(!canSee || distance < 30.0D);
			bossInfo.setVisible(canSee);
		}

		bossInfo.setPercent(getHealth() / getMaxHealth());
	}

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
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);

		if (hasCustomName())
		{
			bossInfo.setName(getDisplayName());
		}
	}

	@Override
	public void setCustomNameTag(String name)
	{
		super.setCustomNameTag(name);

		bossInfo.setName(getDisplayName());
	}
}