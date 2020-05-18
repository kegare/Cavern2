package cavern.handler;

import cavern.world.CaveDimensions;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class TerrainEventHooks
{
	@SubscribeEvent
	public void onDecorateBiome(DecorateBiomeEvent.Decorate event)
	{
		World world = event.getWorld();

		if (world.provider.getDimensionType() == CaveDimensions.SKYLAND)
		{
			if (event.getType() == DecorateBiomeEvent.Decorate.EventType.FOSSIL)
			{
				event.setResult(Result.DENY);
			}
		}
	}
}