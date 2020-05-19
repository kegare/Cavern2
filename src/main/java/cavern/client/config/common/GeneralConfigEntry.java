package cavern.client.config.common;

import cavern.client.config.CaveCategoryEntry;
import cavern.config.GeneralConfig;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GeneralConfigEntry extends CaveCategoryEntry
{
	public GeneralConfigEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected Configuration getConfig()
	{
		return GeneralConfig.config;
	}

	@Override
	protected String getEntryName()
	{
		return Configuration.CATEGORY_GENERAL;
	}
}