package cavern.handler.api;

import cavern.api.IMinerStats;
import cavern.api.IMiningData;
import cavern.api.IPlayerData;
import cavern.api.IPortalCache;
import cavern.api.IEntityData;
import cavern.stats.MinerStats;
import cavern.stats.MiningData;
import cavern.stats.PlayerData;
import cavern.stats.PortalCache;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class EntityDataHandler implements IEntityData
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

	@Override
	public IMiningData getMiningData(EntityPlayer player)
	{
		return MiningData.get(player);
	}
}