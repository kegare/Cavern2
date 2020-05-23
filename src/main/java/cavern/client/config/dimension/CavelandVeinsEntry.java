package cavern.client.config.dimension;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import cavern.client.gui.GuiVeinsEditor;
import cavern.config.CavelandConfig;
import cavern.config.manager.CaveVeinManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CavelandVeinsEntry extends CategoryEntry
{
	public CavelandVeinsEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
	{
		super(owningScreen, owningEntryList, configElement);
	}

	@Override
	protected GuiScreen buildChildScreen()
	{
		return new GuiVeinsEditor(owningScreen, CavelandConfig.VEINS);
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}

	@Override
	public void setToDefault()
	{
		CaveVeinManager manager = CavelandConfig.VEINS;

		try
		{
			FileUtils.forceDelete(new File(manager.config.toString()));
		}
		catch (IOException e)
		{
			e.printStackTrace();

			return;
		}

		manager.getCaveVeins().clear();

		manager.config = null;
		CavelandConfig.syncVeinsConfig();

		if (childScreen != null && childScreen instanceof GuiVeinsEditor)
		{
			((GuiVeinsEditor)childScreen).refreshVeins(manager.getCaveVeins());
		}
	}

	@Override
	public boolean enabled()
	{
		return super.enabled() && !CavelandConfig.autoVeins;
	}
}