package cavern.util;

import java.util.List;

import cavern.network.CaveNetworkRegistry;
import cavern.network.client.ToastMessage;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class PlayerHelper
{
	public static boolean grantAdvancement(EntityPlayer entityPlayer, String key)
	{
		return grantCriterion(entityPlayer, key, key);
	}

	public static boolean grantCriterion(EntityPlayer entityPlayer, String key, String criterion)
	{
		if (entityPlayer == null || !(entityPlayer instanceof EntityPlayerMP))
		{
			return false;
		}

		EntityPlayerMP player = (EntityPlayerMP)entityPlayer;
		Advancement advancement = player.mcServer.getAdvancementManager().getAdvancement(CaveUtils.getKey(key));

		return advancement != null && player.getAdvancements().grantCriterion(advancement, criterion);
	}

	public static boolean grantToast(EntityPlayer player, String key)
	{
		if (grantCriterion(player, "toasts/" + key, key))
		{
			CaveNetworkRegistry.sendTo(() -> new ToastMessage(key), player);

			return true;
		}

		return false;
	}

	public static SleepResult trySleep(EntityPlayer player, BlockPos pos)
	{
		World world = player.world;
		EnumFacing facing = world.getBlockState(pos).getValue(BlockHorizontal.FACING);

		if (!world.isRemote)
		{
			if (player.isPlayerSleeping() || !player.isEntityAlive())
			{
				return SleepResult.OTHER_PROBLEM;
			}

			if (!bedInRange(player, pos, facing))
			{
				return SleepResult.TOO_FAR_AWAY;
			}

			List<EntityMob> list = world.getEntitiesWithinAABB(EntityMob.class,
				new AxisAlignedBB(pos.getX() - 8.0D, pos.getY() - 5.0D, pos.getZ() - 8.0D, pos.getX() + 8.0D, pos.getY() + 5.0D, pos.getZ() + 8.0D));

			if (!list.isEmpty())
			{
				return SleepResult.NOT_SAFE;
			}
		}

		if (player.isRiding())
		{
			player.dismountRidingEntity();
		}

		setSize(player, 0.2F, 0.2F);

		IBlockState state = null;

		if (world.isBlockLoaded(pos))
		{
			state = world.getBlockState(pos);
		}

		if (state != null && state.getBlock().isBed(state, world, pos, player))
		{
			float offsetX = 0.5F + facing.getFrontOffsetX() * 0.4F;
			float offsetZ = 0.5F + facing.getFrontOffsetZ() * 0.4F;

			setRenderOffsetForSleep(player, facing);

			player.setPosition(pos.getX() + offsetX, pos.getY() + 0.6875F, pos.getZ() + offsetZ);
		}
		else
		{
			player.setPosition(pos.getX() + 0.5F, pos.getY() + 0.6875F, pos.getZ() + 0.5F);
		}

		CaveUtils.setPrivateValue(EntityPlayer.class, player, true, "sleeping", "field_71083_bS");
		CaveUtils.setPrivateValue(EntityPlayer.class, player, 0, "sleepTimer", "field_71076_b");

		player.bedLocation = pos;
		player.motionX = 0.0D;
		player.motionY = 0.0D;
		player.motionZ = 0.0D;

		if (!world.isRemote)
		{
			world.updateAllPlayersSleepingFlag();
		}

		return SleepResult.OK;
	}

	public static boolean bedInRange(Entity entity, BlockPos pos, EnumFacing facing)
	{
		if (Math.abs(entity.posX - pos.getX()) <= 3.0D && Math.abs(entity.posY - pos.getY()) <= 2.0D && Math.abs(entity.posZ - pos.getZ()) <= 3.0D)
		{
			return true;
		}
		else
		{
			BlockPos blockpos = pos.offset(facing.getOpposite());

			return Math.abs(entity.posX - blockpos.getX()) <= 3.0D && Math.abs(entity.posY - blockpos.getY()) <= 2.0D && Math.abs(entity.posZ - blockpos.getZ()) <= 3.0D;
		}
	}

	public static void setSize(Entity entity, float width, float height)
	{
		if (width != entity.width || height != entity.height)
		{
			float f = entity.width;

			entity.width = width;
			entity.height = height;

			if (entity.width < f)
			{
				double half = width / 2.0D;

				entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - half, entity.posY, entity.posZ - half, entity.posX + half, entity.posY + entity.height, entity.posZ + half));

				return;
			}

			AxisAlignedBB box = entity.getEntityBoundingBox();

			entity.setEntityBoundingBox(new AxisAlignedBB(box.minX, box.minY, box.minZ, box.minX + entity.width, box.minY + entity.height, box.minZ + entity.width));

			if (entity.width > f && !entity.world.isRemote)
			{
				boolean firstUpdate = CaveUtils.getPrivateValue(Entity.class, entity, "firstUpdate", "field_70148_d");

				if (!firstUpdate)
				{
					entity.move(MoverType.SELF, f - entity.width, 0.0D, f - entity.width);
				}
			}
		}
	}

	public static void setRenderOffsetForSleep(EntityPlayer player, EnumFacing facing)
	{
		player.renderOffsetX = -1.8F * facing.getFrontOffsetX();
		player.renderOffsetZ = -1.8F * facing.getFrontOffsetZ();
	}
}