package cavern.client.config.dimension;

import java.util.List;

import cavern.client.config.CaveCategoryEntry;
import cavern.config.Config;
import cavern.config.HugeCavernConfig;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HugeCavernConfigEntry extends CaveCategoryEntry
{
	public HugeCavernConfigEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected Configuration getConfig()
	{
		return HugeCavernConfig.config;
	}

	@Override
	protected String getEntryName()
	{
		return "dimension.hugeCavern";
	}

	@Override
	protected List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> ret = super.getConfigElements();

		ret.add(new DummyCategoryElement("cavern:hugeCavernBiomes", Config.LANG_KEY + "biomes", HugeCavernBiomesEntry.class));
		ret.add(new DummyCategoryElement("cavern:hugeCavernVeins", Config.LANG_KEY + "veins", HugeCavernVeinsEntry.class));

		return ret;
	}
}