package cavern.entity;

import net.minecraft.block.SoundType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityTorchArrow extends EntityTippedArrow
{
	private ItemStack torchItem = ItemStack.EMPTY;

	public EntityTorchArrow(World world)
	{
		super(world);
	}

	public EntityTorchArrow(World world, double x, double y, double z)
	{
		super(world, x, y, z);
	}

	public EntityTorchArrow(World world, EntityLivingBase shooter)
	{
		super(world, shooter);
	}

	public EntityTorchArrow setTorchItem(ItemStack stack)
	{
		torchItem = stack;

		return this;
	}

	@Override
	protected void onHit(RayTraceResult rayTrace)
	{
		super.onHit(rayTrace);

		if (rayTrace.entityHit != null)
		{
			return;
		}

		if (!world.isRemote && shootingEntity != null && shootingEntity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)shootingEntity;
			ItemStack prevHeldItem = player.getHeldItemMainhand();

			player.setHeldItem(EnumHand.MAIN_HAND, torchItem.isEmpty() ? new ItemStack(Blocks.TORCH) : torchItem);

			BlockPos pos = rayTrace.getBlockPos();
			EnumActionResult result = player.getHeldItemMainhand().getItem().onItemUse(player, world, pos, EnumHand.MAIN_HAND, rayTrace.sideHit,
				(float)rayTrace.hitVec.x, (float)rayTrace.hitVec.y, (float)rayTrace.hitVec.z);

			player.setHeldItem(EnumHand.MAIN_HAND, prevHeldItem);

			if (result == EnumActionResult.SUCCESS)
			{
				SoundType soundType = SoundType.WOOD;

				world.playSound(null, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);

				setDead();
			}
		}
	}
}