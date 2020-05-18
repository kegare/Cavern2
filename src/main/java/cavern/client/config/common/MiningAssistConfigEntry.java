package cavern.client.config.common;

import cavern.client.config.CaveCategoryEntry;
import cavern.config.MiningAssistConfig;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MiningAssistConfigEntry extends CaveCategoryEntry
{
	public MiningAssistConfigEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected Configuration getConfig()
	{
		return MiningAssistConfig.config;
	}

	@Override
	protected String getEntryName()
	{
		return "miningassist";
	}
}