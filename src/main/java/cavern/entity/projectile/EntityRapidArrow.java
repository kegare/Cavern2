package cavern.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.world.World;

public class EntityRapidArrow extends EntityTippedArrow
{
	public EntityRapidArrow(World world)
	{
		super(world);
	}

	public EntityRapidArrow(World world, double x, double y, double z)
	{
		super(world, x, y, z);
	}

	public EntityRapidArrow(World world, EntityLivingBase shooter)
	{
		super(world, shooter);
	}

	@Override
	protected void arrowHit(EntityLivingBase living)
	{
		super.arrowHit(living);

		if (living.getTotalArmorValue() < 20)
		{
			living.hurtResistantTime = 0;
		}
	}
}