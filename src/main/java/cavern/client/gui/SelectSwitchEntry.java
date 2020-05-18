package cavern.client.gui;

import cavern.config.Config;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SelectSwitchEntry
{
	private final GuiScreen guiScreen;
	private final String name;

	public SelectSwitchEntry(GuiScreen guiScreen, String name)
	{
		this.guiScreen = guiScreen;
		this.name = name;
	}

	public GuiScreen getGuiScreen()
	{
		return guiScreen;
	}

	public String getName()
	{
		return name;
	}

	public String getTranslatedName()
	{
		return I18n.format(Config.LANG_KEY + "select.switch." + name);
	}
}