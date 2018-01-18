package cavern.client.config.dimension;

import java.util.List;

import cavern.client.config.CaveCategoryEntry;
import cavern.config.CaveniaConfig;
import cavern.config.Config;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CaveniaConfigEntry extends CaveCategoryEntry
{
	public CaveniaConfigEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected Configuration getConfig()
	{
		return CaveniaConfig.config;
	}

	@Override
	protected String getEntryName()
	{
		return "dimension.cavenia";
	}

	@Override
	protected List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> ret = super.getConfigElements();

		ret.add(new DummyCategoryElement("cavern:caveniaBiomes", Config.LANG_KEY + "biomes", CaveniaBiomesEntry.class));
		ret.add(new DummyCategoryElement("cavern:caveniaVeins", Config.LANG_KEY + "veins", CaveniaVeinsEntry.class));

		return ret;
	}
}