package cavern.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.world.World;

public class EntityCavenicArrow extends EntityTippedArrow
{
	public EntityCavenicArrow(World world)
	{
		super(world);
	}

	public EntityCavenicArrow(World world, double x, double y, double z)
	{
		super(world, x, y, z);
	}

	public EntityCavenicArrow(World world, EntityLivingBase shooter)
	{
		super(world, shooter);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (timeInGround > 10)
		{
			setDead();
		}
	}
}