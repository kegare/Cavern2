package cavern.client.config.dimension;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import cavern.client.gui.GuiBiomesEditor;
import cavern.config.CavernConfig;
import cavern.config.manager.CaveBiomeManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CavernBiomesEntry extends CategoryEntry
{
	public CavernBiomesEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
	{
		super(owningScreen, owningEntryList, configElement);
	}

	@Override
	protected GuiScreen buildChildScreen()
	{
		return new GuiBiomesEditor(owningScreen, CavernConfig.biomeManager);
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}

	@Override
	public void setToDefault()
	{
		CaveBiomeManager manager = CavernConfig.biomeManager;

		try
		{
			FileUtils.forceDelete(new File(manager.config.toString()));
		}
		catch (IOException e)
		{
			e.printStackTrace();

			return;
		}

		manager.getCaveBiomes().clear();

		manager.config = null;
		CavernConfig.syncBiomesConfig();

		if (childScreen != null && childScreen instanceof GuiBiomesEditor)
		{
			((GuiBiomesEditor)childScreen).refreshBiomes(manager.getCaveBiomes().values());
		}
	}
}