package cavern.handler.api;

import cavern.api.IMinerStats;
import cavern.api.IPlayerData;
import cavern.api.IPortalCache;
import cavern.api.IStats;
import cavern.stats.MinerStats;
import cavern.stats.PlayerData;
import cavern.stats.PortalCache;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class StatsHandler implements IStats
{
	@Override
	public IPortalCache getPortalCache(Entity entity)
	{
		return PortalCache.get(entity);
	}

	@Override
	public IPlayerData getPlayerData(EntityPlayer player)
	{
		return PlayerData.get(player);
	}

	@Override
	public IMinerStats getMinerStats(EntityPlayer player)
	{
		return MinerStats.get(player);
	}
}