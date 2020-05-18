package cavern.client.config.dimension;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.client.config.CaveCategoryEntry;
import cavern.config.Config;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DimensionConfigEntry extends CaveCategoryEntry
{
	public DimensionConfigEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected Configuration getConfig()
	{
		return null;
	}

	@Override
	protected String getEntryName()
	{
		return "dimension";
	}

	@Override
	protected List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = Lists.newArrayList();

		list.add(new DummyCategoryElement("cavern:cavernConfig", Config.LANG_KEY + "dimension.cavern", CavernConfigEntry.class));
		list.add(new DummyCategoryElement("cavern:hugeCavernConfig", Config.LANG_KEY + "dimension.hugeCavern", HugeCavernConfigEntry.class));
		list.add(new DummyCategoryElement("cavern:aquaCavernConfig", Config.LANG_KEY + "dimension.aquaCavern", AquaCavernConfigEntry.class));
		list.add(new DummyCategoryElement("cavern:mirageWorldsConfig", Config.LANG_KEY + "dimension.mirageWorlds", MirageWorldsConfigEntry.class));

		return list;
	}

	@Override
	protected GuiScreen buildChildScreen()
	{
		return new GuiConfig(owningScreen, getConfigElements(), owningScreen.modID, getEntryName(),
			configElement.requiresWorldRestart() || owningScreen.allRequireWorldRestart, configElement.requiresMcRestart() || owningScreen.allRequireMcRestart,
			I18n.format(Config.LANG_KEY + getEntryName()));
	}
}