package cavern.client.config.dimension;

import java.util.List;

import cavern.client.config.CaveCategoryEntry;
import cavern.config.AquaCavernConfig;
import cavern.config.Config;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AquaCavernConfigEntry extends CaveCategoryEntry
{
	public AquaCavernConfigEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected Configuration getConfig()
	{
		return AquaCavernConfig.config;
	}

	@Override
	protected String getEntryName()
	{
		return "dimension.aquaCavern";
	}

	@Override
	protected List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> ret = super.getConfigElements();

		ret.add(new DummyCategoryElement("cavern:aquaCavernBiomes", Config.LANG_KEY + "biomes", AquaCavernBiomesEntry.class));
		ret.add(new DummyCategoryElement("cavern:aquaCavernVeins", Config.LANG_KEY + "veins", AquaCavernVeinsEntry.class));

		return ret;
	}
}