package cavern.block;

import java.util.Random;

import javax.annotation.Nullable;

import cavern.handler.CaveEventHooks;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class FissureHelper
{
	private static final Random RANDOM = CaveEventHooks.RANDOM;

	public static void fireAreaEffect(World world, BlockPos pos, @Nullable EntityLivingBase entity)
	{
		EntityAreaEffectCloud areaEffectCloud = new EntityAreaEffectCloud(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

		areaEffectCloud.setOwner(entity);
		areaEffectCloud.setRadius(2.5F);
		areaEffectCloud.setRadiusOnUse(-0.5F);
		areaEffectCloud.setWaitTime(10);
		areaEffectCloud.setDuration(20 * 30);
		areaEffectCloud.addEffect(new PotionEffect(getRandomPotion(RANDOM.nextDouble() < 0.35D), 20 * 30, RANDOM.nextInt(2)));

		world.spawnEntity(areaEffectCloud);
	}

	public static void fireExplosion(World world, BlockPos pos)
	{
		double posX = pos.getX() + 0.5D;
		double posY = pos.getY() + 0.5D;
		double posZ = pos.getZ() + 0.5D;
		float strength = 1.45F;

		if (RANDOM.nextDouble() < 0.15D)
		{
			strength = 3.0F;
		}

		world.newExplosion(null, posX, posY, posZ, strength, false, true);
	}

	public static void fireIntensiveEffect(EntityLivingBase entity, int fortune)
	{
		for (int i = 0; i < Math.min(fortune, 5); ++i)
		{
			entity.addPotionEffect(new PotionEffect(getRandomPotion(false), 20 * 60, RANDOM.nextInt(2)));
		}
	}

	private static Potion getRandomPotion(boolean badEffect)
	{
		if (badEffect)
		{
			if (RANDOM.nextDouble() < 0.15D)
			{
				return MobEffects.WEAKNESS;
			}

			if (RANDOM.nextDouble() < 0.3D)
			{
				return MobEffects.POISON;
			}

			if (RANDOM.nextDouble() < 0.3D)
			{
				return MobEffects.SLOWNESS;
			}

			return MobEffects.HUNGER;
		}

		if (RANDOM.nextDouble() < 0.15D)
		{
			return MobEffects.NIGHT_VISION;
		}

		if (RANDOM.nextDouble() < 0.3D)
		{
			return MobEffects.REGENERATION;
		}

		if (RANDOM.nextDouble() < 0.3D)
		{
			return MobEffects.ABSORPTION;
		}

		return MobEffects.RESISTANCE;
	}
}