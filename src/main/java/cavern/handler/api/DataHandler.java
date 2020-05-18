package cavern.handler.api;

import cavern.api.DataWrapper;
import cavern.api.data.IMiner;
import cavern.api.data.IMiningData;
import cavern.data.Miner;
import cavern.data.MiningData;
import net.minecraft.entity.player.EntityPlayer;

public class DataHandler implements DataWrapper
{
	@Override
	public IMiner getMiner(EntityPlayer player)
	{
		return Miner.get(player);
	}

	@Override
	public IMiningData getMiningData(EntityPlayer player)
	{
		return MiningData.get(player);
	}
}