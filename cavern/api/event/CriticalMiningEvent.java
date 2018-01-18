package cavern.api.event;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class CriticalMiningEvent extends HarvestDropsEvent
{
	private final List<ItemStack> drops;

	public CriticalMiningEvent(HarvestDropsEvent event, List<ItemStack> drops)
	{
		super(event.getWorld(), event.getPos(), event.getState(), event.getFortuneLevel(), event.getDropChance(), event.getDrops(), event.getHarvester(), event.isSilkTouching());
		this.drops = drops;
	}

	public List<ItemStack> getBonusDrops()
	{
		return drops;
	}
}