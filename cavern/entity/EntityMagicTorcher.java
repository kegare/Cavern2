package cavern.entity;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class EntityMagicTorcher extends Entity
{
	protected EntityPlayer entityPlayer;
	protected int torcherRange;

	protected ItemStack torchItem = ItemStack.EMPTY;

	protected int lifeTime = 600;
	protected int idleTime;

	protected boolean tracking;

	public EntityMagicTorcher(World world)
	{
		super(world);
	}

	public EntityMagicTorcher(World world, EntityPlayer player, int range)
	{
		super(world);
		this.entityPlayer = player;
		this.torcherRange = range;
		this.setPosition(player.posX, player.posY, player.posZ);
	}

	public EntityPlayer getPlayer()
	{
		return entityPlayer;
	}

	public int getRange()
	{
		return torcherRange;
	}

	public EntityMagicTorcher setLifeTime(int time)
	{
		lifeTime = time;

		return this;
	}

	public int getLifeTime()
	{
		return lifeTime;
	}

	public void setTracking(boolean value)
	{
		tracking = value;
	}

	public boolean isTracking()
	{
		return tracking;
	}

	@Override
	public void onEntityUpdate()
	{
		if (world.isRemote)
		{
			return;
		}

		if (entityPlayer == null || entityPlayer.isDead)
		{
			setDead();

			return;
		}

		if (entityPlayer.inventory.hasItemStack(new ItemStack(Blocks.TORCH)))
		{
			 if (torchItem.isEmpty())
			 {
				 for (ItemStack stack : entityPlayer.inventory.mainInventory)
				 {
					 if (!stack.isEmpty() && stack.getItem() == Item.getItemFromBlock(Blocks.TORCH))
					 {
						 torchItem = stack;

						 break;
					 }
				 }
			 }

			 if (torchItem.isEmpty())
			 {
				 for (ItemStack stack : entityPlayer.inventory.offHandInventory)
				 {
					 if (!stack.isEmpty() && stack.getItem() == Item.getItemFromBlock(Blocks.TORCH))
					 {
						 torchItem = stack;

						 break;
					 }
				 }
			 }
		}
		else
		{
			setDead();

			return;
		}

		if (--lifeTime <= 0 || !tracking && ++idleTime > 50)
		{
			setDead();

			return;
		}
		else if (lifeTime % 10 != 0)
		{
			return;
		}

		if (tracking)
		{
			setPosition(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ);
		}

		BlockPos center = getPosition();

		for (BlockPos pos : BlockPos.getAllInBoxMutable(center.add(torcherRange, 0, torcherRange), center.add(-torcherRange, 0, -torcherRange)))
		{
			BlockPos blockpos = getTorchPos(pos);

			if (blockpos != null)
			{
				setTorch(blockpos);

				break;
			}
		}
	}

	@Nullable
	protected BlockPos getTorchPos(BlockPos checkPos)
	{
		BlockPos pos = checkPos;
		int diff = 0;

		if (world.isAirBlock(pos))
		{
			while (diff < 5 && world.isAirBlock(pos))
			{
				pos = pos.down();

				++diff;
			}

			pos = pos.up();
		}
		else while (diff < 5 && !world.isAirBlock(pos))
		{
			pos = pos.up();

			++diff;
		}

		if (!world.isAirBlock(pos) || world.isAirBlock(pos.down()))
		{
			return null;
		}

		if (!canPlaceTorchOn(pos.down()))
		{
			return null;
		}

		if (world.rayTraceBlocks(new Vec3d(posX, posY + 1.85D, posZ), new Vec3d(pos), false, true, false) != null)
		{
			return null;
		}

		if (getLightLevel(pos) > 7)
		{
			return null;
		}

		return pos;
	}

	protected int getLightLevel(BlockPos pos)
	{
		return world.getLightFor(EnumSkyBlock.BLOCK, pos);
	}

	protected boolean canPlaceTorchOn(BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);

		return state.getBlock().canPlaceTorchOnTop(state, world, pos);
	}

	protected void setTorch(BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (!block.isReplaceable(world, pos))
		{
			pos = pos.up();
		}

		if (entityPlayer.canPlayerEdit(pos, EnumFacing.UP, torchItem) && world.mayPlace(Blocks.TORCH, pos, false, EnumFacing.UP, null))
		{
			if (!canPlaceTorchOn(pos.down()))
			{
				return;
			}

			if (!world.setBlockState(pos, Blocks.TORCH.getDefaultState(), 11))
			{
				return;
			}

			IBlockState newState = world.getBlockState(pos);

			if (newState.getBlock() == Blocks.TORCH)
			{
				newState.getBlock().onBlockPlacedBy(world, pos, newState, entityPlayer, torchItem);
			}

			SoundType soundType = newState.getBlock().getSoundType(newState, world, pos, entityPlayer);

			world.playSound(null, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);

			if (!entityPlayer.capabilities.isCreativeMode)
			{
				torchItem.shrink(1);
			}

			idleTime = 0;
		}
	}

	@Override
	public void setDead()
	{
		super.setDead();

		if (!world.isRemote && tracking)
		{
			playSound(SoundEvents.BLOCK_GLASS_BREAK, 1.0F, 0.375F);

			entityPlayer.sendStatusMessage(new TextComponentTranslation("item.magicBook.torch.dead"), true);
		}
	}

	@Override
	protected void entityInit() {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {}
}