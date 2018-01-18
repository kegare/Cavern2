package cavern.config.manager;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.common.config.Configuration;

public class CaveVeinManager
{
	private final List<CaveVein> CAVE_VEINS = Lists.newArrayList();

	public Configuration config;

	public boolean addCaveVein(CaveVein vein)
	{
		return getCaveVeins().add(vein);
	}

	public List<CaveVein> getCaveVeins()
	{
		return CAVE_VEINS;
	}
}