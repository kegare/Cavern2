package cavern.client.config.common;

import cavern.client.gui.GuiMiningPointsEditor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ArrayEntry;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MiningPointsEntry extends ArrayEntry
{
	public MiningPointsEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
	{
		super(owningScreen, owningEntryList, configElement);
	}

	@Override
	public void valueButtonPressed(int index)
	{
		if (GuiScreen.isShiftKeyDown())
		{
			super.valueButtonPressed(index);
		}
		else if (btnValue.enabled)
		{
			btnValue.playPressSound(mc.getSoundHandler());

			mc.displayGuiScreen(new GuiMiningPointsEditor(owningScreen, this));
		}
	}
}