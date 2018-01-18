package cavern.block.event;

import java.util.Random;

import cavern.api.IFissureBreakEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FissureEventPotion implements IFissureBreakEvent
{
	@Override
	public boolean onBreakBlock(World world, BlockPos pos, IBlockState state, float chance, int fortune, EntityPlayer player, Random random)
	{
		Potion potion;

		if (random.nextDouble() < 0.4D)
		{
			if (random.nextDouble() < 0.1D)
			{
				potion = MobEffects.INSTANT_DAMAGE;
			}
			else if (random.nextDouble() < 0.3D)
			{
				potion = MobEffects.POISON;
			}
			else if (random.nextDouble() < 0.3D)
			{
				potion = MobEffects.SLOWNESS;
			}
			else
			{
				potion = MobEffects.HUNGER;
			}
		}
		else
		{
			if (random.nextDouble() < 0.1D)
			{
				potion = MobEffects.INSTANT_HEALTH;
			}
			else if (random.nextDouble() < 0.3D)
			{
				potion = MobEffects.REGENERATION;
			}
			else if (random.nextDouble() < 0.3D)
			{
				potion = MobEffects.ABSORPTION;
			}
			else
			{
				potion = MobEffects.RESISTANCE;
			}
		}

		if (potion != null)
		{
			double d0 = random.nextFloat() * 0.5F + 0.25D;
			double d1 = random.nextFloat() * 0.5F + 0.25D;
			double d2 = random.nextFloat() * 0.5F + 0.25D;
			EntityAreaEffectCloud areaEffectCloud = new EntityAreaEffectCloud(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2);

			areaEffectCloud.setOwner(player);
			areaEffectCloud.setRadius(2.0F);
			areaEffectCloud.setRadiusOnUse(-0.5F);
			areaEffectCloud.setWaitTime(10);
			areaEffectCloud.setDuration(150);
			areaEffectCloud.addEffect(new PotionEffect(potion, 200, random.nextInt(2)));

			return world.spawnEntity(areaEffectCloud);
		}

		return false;
	}
}