package cavern.client.config;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.client.config.common.DisplayConfigEntry;
import cavern.client.config.common.GeneralConfigEntry;
import cavern.client.config.common.MiningAssistConfigEntry;
import cavern.client.config.dimension.DimensionConfigEntry;
import cavern.config.Config;
import cavern.core.Cavern;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CaveConfigGui extends GuiConfig
{
	public static boolean detailInfo = true;
	public static boolean instantFilter = true;

	public CaveConfigGui(GuiScreen parent)
	{
		super(parent, getConfigElements(), Cavern.MODID, false, false, I18n.format(Config.LANG_KEY + "title"));
	}

	private static List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = Lists.newArrayList();

		list.add(new DummyCategoryElement("cavern:generalConfig", Config.LANG_KEY + Configuration.CATEGORY_GENERAL, GeneralConfigEntry.class));
		list.add(new DummyCategoryElement("cavern:displayConfig", Config.LANG_KEY + "display", DisplayConfigEntry.class));
		list.add(new DummyCategoryElement("cavern:miningAssistConfig", Config.LANG_KEY + "miningassist", MiningAssistConfigEntry.class));
		list.add(new DummyCategoryElement("cavern:dimensionConfig", Config.LANG_KEY + "dimension", DimensionConfigEntry.class));

		return list;
	}

	@Override
	public void initGui()
	{
		if (entryList == null || needsRefresh)
		{
			entryList = new CaveConfigGuiEntries(this, mc);
			needsRefresh = false;
		}

		super.initGui();
	}
}