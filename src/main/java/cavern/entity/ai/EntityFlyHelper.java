package cavern.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.MathHelper;

public class EntityFlyHelper extends EntityMoveHelper
{
	public EntityFlyHelper(EntityLiving living)
	{
		super(living);
	}

	@Override
	public void onUpdateMoveHelper()
	{
		if (action == EntityMoveHelper.Action.STRAFE)
		{
			entity.setNoGravity(false);

			float f1;
			float f2 = moveForward;
			float f3 = moveStrafe;
			float f4 = MathHelper.sqrt(f2 * f2 + f3 * f3);

			if (entity.onGround)
			{
				f1 = (float)(speed * entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
			}
			else
			{
				f1 = (float)(speed * entity.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).getAttributeValue());
			}

			if (f4 < 1.0F)
			{
				f4 = 1.0F;
			}

			f4 = f1 / f4;
			f2 = f2 * f4;
			f3 = f3 * f4;
			float f5 = MathHelper.sin(entity.rotationYaw * 0.017453292F);
			float f6 = MathHelper.cos(entity.rotationYaw * 0.017453292F);
			float f7 = f2 * f6 - f3 * f5;
			float f8 = f3 * f6 + f2 * f5;
			PathNavigate pathnavigate = entity.getNavigator();

			if (pathnavigate != null)
			{
				NodeProcessor nodeprocessor = pathnavigate.getNodeProcessor();

				if (nodeprocessor != null && nodeprocessor.getPathNodeType(entity.world, MathHelper.floor(entity.posX + f7), MathHelper.floor(entity.posY), MathHelper.floor(entity.posZ + f8)) != PathNodeType.WALKABLE)
				{
					moveForward = 1.0F;
					moveStrafe = 0.0F;
				}
			}

			if (f4 < 8.800000277905201E-7D)
			{
				entity.setMoveVertical(0.0F);
				entity.setMoveForward(0.0F);

				return;
			}

			entity.setAIMoveSpeed(f1);
			entity.setMoveForward(moveForward);
			entity.setMoveStrafing(moveStrafe);
			action = EntityMoveHelper.Action.WAIT;
		}
		else if (action == EntityMoveHelper.Action.MOVE_TO)
		{
			action = EntityMoveHelper.Action.WAIT;
			entity.setNoGravity(true);

			double d0 = posX - entity.posX;
			double d1 = posY - entity.posY;
			double d2 = posZ - entity.posZ;
			double d3 = d0 * d0 + d1 * d1 + d2 * d2;

			if (d3 < 8.800000277905201E-7D)
			{
				entity.setMoveVertical(0.0F);
				entity.setMoveForward(0.0F);

				return;
			}

			float f = (float)(MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;

			entity.rotationYaw = limitAngle(entity.rotationYaw, f, 10.0F);

			float f1;

			if (entity.onGround)
			{
				f1 = (float)(speed * entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
			}
			else
			{
				f1 = (float)(speed * entity.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).getAttributeValue());
			}

			entity.setAIMoveSpeed(f1);
			double d4 = MathHelper.sqrt(d0 * d0 + d2 * d2);
			float f2 = (float)(-(MathHelper.atan2(d1, d4) * (180D / Math.PI)));
			entity.rotationPitch = limitAngle(entity.rotationPitch, f2, 10.0F);
			entity.setMoveVertical(d1 > 0.0D ? f1 : -f1);
		}
		else
		{
			entity.setNoGravity(false);
			entity.setMoveVertical(0.0F);
			entity.setMoveForward(0.0F);
		}
	}
}