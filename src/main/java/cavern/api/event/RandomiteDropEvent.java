package cavern.api.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RandomiteDropEvent extends Event
{
	private final IBlockAccess world;
	private final BlockPos pos;
	private final IBlockState state;
	private final ItemStack originalDropItem;

	private ItemStack dropItem;

	public RandomiteDropEvent(IBlockAccess world, BlockPos pos, IBlockState state, ItemStack drop)
	{
		this.world = world;
		this.pos = pos;
		this.state = state;
		this.originalDropItem = drop;
		this.dropItem = drop;
	}

	public IBlockAccess getWorld()
	{
		return world;
	}

	public BlockPos getPos()
	{
		return pos;
	}

	public IBlockState getState()
	{
		return state;
	}

	public ItemStack getOriginalDropItem()
	{
		return originalDropItem;
	}

	public ItemStack getDropItem()
	{
		return dropItem;
	}

	public void setDropItem(ItemStack drop)
	{
		dropItem = drop;
	}
}