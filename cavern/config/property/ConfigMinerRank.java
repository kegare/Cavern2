package cavern.config.property;

import cavern.stats.MinerRank;

public class ConfigMinerRank
{
	private int value;

	public int getValue()
	{
		return value;
	}

	public void setValue(int rank)
	{
		value = rank;
	}

	public MinerRank getRank()
	{
		return MinerRank.get(getValue());
	}
}