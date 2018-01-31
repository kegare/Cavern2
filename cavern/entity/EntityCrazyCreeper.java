package cavern.entity;

import cavern.api.CavernAPI;
import cavern.client.particle.ParticleCrazyMob;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCrazyCreeper extends EntityCavenicCreeper
{
	private final BossInfoServer bossInfo = new BossInfoServer(getDisplayName(), BossInfo.Color.GREEN, BossInfo.Overlay.PROGRESS);

	public EntityCrazyCreeper(World world)
	{
		super(world);
		this.experienceValue = 50;
	}

	@Override
	protected void applyCustomValues()
	{
		fuseTime = 150;
		explosionRadius = 30;

		super.applyCustomValues();
	}

	@Override
	protected void applyMobAttributes()
	{
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1500.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23D);
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

	@Override
	public boolean getCanSpawnHere()
	{
		return CavernAPI.dimension.isInCavenia(this) && super.getCanSpawnHere();
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

				if (canEntityBeSeen(player) || distance <= 32.0D)
				{
					canSee = true;

					break;
				}
			}

			bossInfo.setDarkenSky(!canSee || distance <= 50.0D);
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