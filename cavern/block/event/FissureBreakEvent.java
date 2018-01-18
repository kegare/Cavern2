package cavern.block.event;

import cavern.api.IFissureBreakEvent;
import net.minecraft.util.WeightedRandom;

public class FissureBreakEvent extends WeightedRandom.Item
{
	private final IFissureBreakEvent event;

	public FissureBreakEvent(IFissureBreakEvent event, int weight)
	{
		super(weight);
		this.event = event;
	}

	public IFissureBreakEvent get()
	{
		return event;
	}
}