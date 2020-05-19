package cavern.client.config.dimension;

import java.util.List;

import cavern.client.config.CaveCategoryEntry;
import cavern.config.CavernConfig;
import cavern.config.Config;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CavernConfigEntry extends CaveCategoryEntry
{
	public CavernConfigEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected Configuration getConfig()
	{
		return CavernConfig.config;
	}

	@Override
	protected String getEntryName()
	{
		return "dimension.cavern";
	}

	@Override
	protected List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> ret = super.getConfigElements();

		ret.add(new DummyCategoryElement("cavern:cavernBiomes", Config.LANG_KEY + "biomes", CavernBiomesEntry.class));
		ret.add(new DummyCategoryElement("cavern:cavernVeins", Config.LANG_KEY + "veins", CavernVeinsEntry.class));

		return ret;
	}
}