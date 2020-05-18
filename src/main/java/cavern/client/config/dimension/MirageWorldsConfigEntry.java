package cavern.client.config.dimension;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.client.config.CaveCategoryEntry;
import cavern.config.Config;
import cavern.config.MirageWorldsConfig;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MirageWorldsConfigEntry extends CaveCategoryEntry
{
	public MirageWorldsConfigEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected Configuration getConfig()
	{
		return MirageWorldsConfig.config;
	}

	@Override
	protected String getEntryName()
	{
		return "dimension.mirageWorlds";
	}

	@Override
	protected List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = Lists.newArrayList();

		list.add(new DummyCategoryElement("cavern:cavelandConfig", Config.LANG_KEY + "dimension.caveland", CavelandConfigEntry.class));
		list.add(new DummyCategoryElement("cavern:caveniaConfig", Config.LANG_KEY + "dimension.cavenia", CaveniaConfigEntry.class));
		list.addAll(super.getConfigElements());

		return list;
	}
}