package cavern.api.event;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class CriticalMiningEvent extends BlockEvent
{
	private final EntityPlayer player;
	private final int fortuneLevel;
	private final List<ItemStack> originalDrops;
	private final List<ItemStack> drops;

	public CriticalMiningEvent(World world, BlockPos pos, IBlockState state, EntityPlayer player, int fortune, List<ItemStack> originalDrops, List<ItemStack> drops)
	{
		super(world, pos, state);
		this.player = player;
		this.fortuneLevel = fortune;
		this.originalDrops = originalDrops;
		this.drops = drops;
	}

	public EntityPlayer getEntityPlayer()
	{
		return player;
	}

	public int getFortuneLevel()
	{
		return fortuneLevel;
	}

	public List<ItemStack> getOriginalDrops()
	{
		return originalDrops;
	}

	public List<ItemStack> getBonusDrops()
	{
		return drops;
	}
}