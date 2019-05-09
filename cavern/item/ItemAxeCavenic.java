package cavern.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemAxeCavenic extends ItemAxeCave
{
	public ItemAxeCavenic()
	{
		super(CaveItems.CAVENIC, 8.0F, -3.05F, "axeCavenic");
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
	{
		World world = target.world;
		int count = 1;

		for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, target.getEntityBoundingBox().grow(2.5D)))
		{
			if (!(entity instanceof IMob))
			{
				continue;
			}

			if (entity instanceof IEntityOwnable && ((IEntityOwnable)entity).getOwner() != null)
			{
				continue;
			}

			double dist = target.getDistanceSq(entity);
			Vec3d vec = getSmashVector(attacker, dist <= 2.0D, (itemRand.nextDouble() + 1.0D) * 1.15D, 0.1D);

			entity.attackEntityFrom(DamageSource.causeMobDamage(attacker), 4.0F);
			entity.addVelocity(vec.x, vec.y, vec.z);

			++count;
		}

		if (count > 1)
		{
			world.playSound(null, target.posX, target.posY + 0.85D, target.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK,
				SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F) + 0.25F);
		}

		stack.damageItem(MathHelper.clamp(count / 2, 1, 3), attacker);

		return true;
	}

	public Vec3d getSmashVector(Entity entity, boolean isCritical, double bashPow, double bashUpRatio)
	{
		double pow = bashPow;
		double upRatio = bashUpRatio * pow;

		if (isCritical)
		{
			upRatio *= 1.5D;
		}

		double vecX = -MathHelper.sin(entity.rotationYaw * 3.141593F / 180F) * (float)pow * 0.5F;
		double vecZ = MathHelper.cos(entity.rotationYaw * 3.141593F / 180F) * (float)pow * 0.5F;

		if (!isCritical)
		{
			double var = 1.0F - itemRand.nextFloat() * 0.35F;

			vecX *= var;
			upRatio *= var;
			vecZ *= var;
		}

		return new Vec3d(vecX, upRatio, vecZ);
	}
}