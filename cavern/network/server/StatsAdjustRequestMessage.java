package cavern.network.server;

import cavern.handler.CaveEventHooks;
import cavern.network.CaveNetworkRegistry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class StatsAdjustRequestMessage implements IPlayerMessage<StatsAdjustRequestMessage, IMessage>
{
	private static long requestTime;

	@Override
	public IMessage process(EntityPlayerMP player)
	{
		CaveEventHooks.adjustPlayerStats(player);

		return null;
	}

	public static void request()
	{
		long time = System.currentTimeMillis();

		if (requestTime <= 0 || time - requestTime > 5000L)
		{
			requestTime = time;

			CaveNetworkRegistry.sendToServer(new StatsAdjustRequestMessage());
		}
	}
}