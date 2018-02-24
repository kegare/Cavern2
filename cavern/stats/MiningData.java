package cavern.stats;

import org.apache.commons.lang3.ObjectUtils;

import cavern.api.IMiningData;
import cavern.capability.CaveCapabilities;
import cavern.config.GeneralConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;

public class MiningData implements IMiningData
{
	private final EntityPlayer player;

	private IBlockState lastMiningBlock;
	private int lastMiningPoint;
	private int miningCombo;
	private long lastMiningTime;

	public MiningData(EntityPlayer player)
	{
		this.player = player;
	}

	@Override
	public IBlockState getLastMiningBlock()
	{
		return lastMiningBlock;
	}

	@Override
	public int getLastMiningPoint()
	{
		return lastMiningPoint;
	}

	@Override
	public int getMiningCombo()
	{
		return miningCombo;
	}

	@Override
	public long getLastMiningTime()
	{
		return lastMiningTime;
	}

	@Override
	public void notifyMining(IBlockState state, int point)
	{
		lastMiningBlock = state;
		lastMiningPoint = point;
		lastMiningTime = player.world.getTotalWorldTime();

		if (GeneralConfig.miningCombo)
		{
			++miningCombo;
		}
	}

	@Override
	public void onUpdate()
	{
		if (miningCombo == 0)
		{
			return;
		}

		if (player.world.getTotalWorldTime()  - lastMiningTime > 15 * 20)
		{
			miningCombo = 0;
		}
	}

	public static IMiningData get(EntityPlayer player)
	{
		return ObjectUtils.defaultIfNull(CaveCapabilities.getCapability(player, CaveCapabilities.MINING_DATA), new MiningData(null));
	}
}