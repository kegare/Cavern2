package cavern.entity;

import javax.annotation.Nullable;

import cavern.api.CavernAPI;
import cavern.api.ICavenicMob;
import cavern.item.ItemCave;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityCavenicWitch extends EntityWitch implements ICavenicMob
{
	public EntityCavenicWitch(World world)
	{
		super(world);
		this.experienceValue = 12;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		applyMobAttributes();
	}

	protected void applyMobAttributes()
	{
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
	}

	@Override
	protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source)
	{
		super.dropLoot(wasRecentlyHit, lootingModifier, source);

		if (rand.nextInt(5) == 0)
		{
			entityDropItem(ItemCave.EnumType.CAVENIC_ORB.getItemStack(), 0.5F);
		}
	}

	public boolean isFriends(@Nullable Entity entity)
	{
		return entity != null && entity instanceof EntityCavenicWitch;
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source)
	{
		if (super.isEntityInvulnerable(source))
		{
			return true;
		}

		if (source.getTrueSource() == this || source.getImmediateSource() == this)
		{
			return true;
		}

		if (isFriends(source.getTrueSource()) || isFriends(source.getImmediateSource()))
		{
			return true;
		}

		return false;
	}

	@Override
	public void setAttackTarget(EntityLivingBase entity)
	{
		if (isFriends(entity))
		{
			return;
		}

		super.setAttackTarget(entity);
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor)
	{
		int count = getAttackPotionCount();

		for (int i = 0; i < count; ++i)
		{
			if (!isDrinkingPotion())
			{
				double d0 = target.posY + target.getEyeHeight() - 1.100000023841858D;
				double d1 = target.posX + target.motionX - posX;
				double d2 = d0 - posY;
				double d3 = target.posZ + target.motionZ - posZ;
				float f = MathHelper.sqrt(d1 * d1 + d3 * d3);
				PotionType potion = PotionTypes.HARMING;

				if (rand.nextFloat() < (target.getHealth() >= 8.0F ? 0.5F : 0.3F))
				{
					if (target.isEntityUndead())
					{
						potion = PotionTypes.HEALING;
					}
					else
					{
						potion = PotionTypes.POISON;
					}
				}
				else if (rand.nextFloat() < 0.25F)
				{
					potion = PotionTypes.WEAKNESS;
				}
				else if (rand.nextFloat() < 0.2F)
				{
					potion = PotionTypes.SLOWNESS;
				}

				EntityPotion entity = new EntityPotion(world, this, PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), potion));

				entity.rotationPitch -= -20.0F;
				entity.shoot(d1, d2 + f * 0.2F, d3, 0.75F, 8.0F);

				world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_WITCH_THROW, getSoundCategory(), 1.0F, 0.8F + rand.nextFloat() * 0.4F);
				world.spawnEntity(entity);
			}
		}
	}

	public int getAttackPotionCount()
	{
		return world.getWorldInfo().getDifficulty().getDifficultyId();
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
	public boolean getCanSpawnHere()
	{
		return CavernAPI.dimension.isInCaves(this) && super.getCanSpawnHere();
	}

	@Override
	public int getMaxSpawnedInChunk()
	{
		return CavernAPI.dimension.isInCavenia(this) ? 4 : 1;
	}
}