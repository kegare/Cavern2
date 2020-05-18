package cavern.client.config.dimension;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import cavern.client.gui.GuiBiomesEditor;
import cavern.config.HugeCavernConfig;
import cavern.config.manager.CaveBiomeManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HugeCavernBiomesEntry extends CategoryEntry
{
	public HugeCavernBiomesEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
	{
		super(owningScreen, owningEntryList, configElement);
	}

	@Override
	protected GuiScreen buildChildScreen()
	{
		return new GuiBiomesEditor(owningScreen, HugeCavernConfig.biomeManager);
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}

	@Override
	public void setToDefault()
	{
		CaveBiomeManager manager = HugeCavernConfig.biomeManager;

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
		HugeCavernConfig.syncBiomesConfig();

		if (childScreen != null && childScreen instanceof GuiBiomesEditor)
		{
			((GuiBiomesEditor)childScreen).refreshBiomes(manager.getCaveBiomes().values());
		}
	}
}