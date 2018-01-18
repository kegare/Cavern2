package cavern.client.config;

import com.google.common.base.Strings;

import cavern.config.Config;
import cavern.util.Version;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CaveConfigGuiEntries extends GuiConfigEntries
{
	public CaveConfigGuiEntries(GuiConfig parent, Minecraft mc)
	{
		super(parent, mc);
		this.setHasListHeader(true, 8);
	}

	@Override
	public void drawScreenPost(int mouseX, int mouseY, float partialTicks)
	{
		ComparableVersion version = Version.getTarget();

		if (version != null)
		{
			owningScreen.drawCenteredString(mc.fontRenderer, I18n.format(Config.LANG_KEY + "version", version.toString()), width / 2, bottom - 50, 0xDDDDDD);
		}

		String desc;

		if (Version.DEV_DEBUG)
		{
			desc = I18n.format(Config.LANG_KEY + "version.dev");
		}
		else if (Version.isBeta())
		{
			desc = I18n.format(Config.LANG_KEY + "version.beta");
		}
		else if (Version.isAlpha())
		{
			desc = I18n.format(Config.LANG_KEY + "version.alpha");
		}
		else if (Version.isOutdated())
		{
			desc = I18n.format(Config.LANG_KEY + "version.old");
		}
		else
		{
			desc = I18n.format(Config.LANG_KEY + "version.latest");
		}

		if (!Strings.isNullOrEmpty(desc))
		{
			owningScreen.drawCenteredString(mc.fontRenderer, desc, width / 2, bottom - 30, 0xBBBBBB);
		}

		super.drawScreenPost(mouseX, mouseY, partialTicks);
	}
}