package cavern.entity;

import cavern.api.CavernAPI;
import cavern.client.particle.ParticleCrazyMob;
import cavern.entity.ai.EntityAIAttackCavenicBow;
import cavern.item.CaveItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCrazySkeleton extends EntityCavenicSkeleton
{
	private final BossInfoServer bossInfo = new BossInfoServer(getDisplayName(), BossInfo.Color.WHITE, BossInfo.Overlay.PROGRESS);

	public EntityCrazySkeleton(World world)
	{
		super(world);
		this.experienceValue = 50;
		this.setDropChance(EntityEquipmentSlot.MAINHAND, 1.0F);
	}

	@Override
	protected void initCustomAI()
	{
		aiArrowAttack = new EntityAIAttackCavenicBow<>(this, 0.99D, 6.0F, 1);
		aiAttackOnCollide = new EntityAIAttackMelee(this, 1.35D, false)
		{
			@Override
			public void resetTask()
			{
				super.resetTask();

				EntityCrazySkeleton.this.setSwingingArms(false);
			}

			@Override
			public void startExecuting()
			{
				super.startExecuting();

				EntityCrazySkeleton.this.setSwingingArms(true);
			}
		};
	}

	@Override
	protected void applyMobAttributes()
	{
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(2000.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty)
	{
		super.setEquipmentBasedOnDifficulty(difficulty);

		ItemStack stack = new ItemStack(CaveItems.CAVENIC_BOW);

		stack.addEnchantment(Enchantments.INFINITY, 1);

		setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
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

	@Override
	public void onStruckByLightning(EntityLightningBolt lightningBolt) {}

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