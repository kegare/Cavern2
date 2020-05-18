package cavern.client.config;

import java.util.List;

import cavern.client.gui.GuiSelectBlock;
import cavern.client.gui.GuiSelectOreDict;
import cavern.client.gui.GuiSelectOreDict.OreDictEntry;
import cavern.client.gui.ISelectorCallback;
import cavern.client.gui.SelectSwitchEntry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ArrayEntry;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SelectBlocksAndOreDictsEntry extends ArrayEntry
{
	public SelectBlocksAndOreDictsEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
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

			mc.displayGuiScreen(getSelectBlockGuiScreen());
		}
	}

	public GuiSelectBlock getSelectBlockGuiScreen()
	{
		GuiSelectBlock selectBlock = createSelectBlockGuiScreen();
		GuiSelectOreDict selectOreDict = createSelectOreDictGuiScreen();

		selectBlock.setSwitchEntry(new SelectSwitchEntry(selectOreDict, "oreDict"));
		selectOreDict.setSwitchEntry(new SelectSwitchEntry(selectBlock, "block"));

		return selectBlock;
	}

	protected GuiSelectBlock createSelectBlockGuiScreen()
	{
		return new GuiSelectBlock(owningScreen, this);
	}

	protected GuiSelectOreDict createSelectOreDictGuiScreen()
	{
		return new GuiSelectOreDict(owningScreen, this, new ISelectorCallback<OreDictEntry>()
		{
			@Override
			public boolean isValidEntry(OreDictEntry entry)
			{
				return !entry.getItemStack().isEmpty() && entry.getItemStack().getItem() instanceof ItemBlock;
			}

			@Override
			public void onSelected(List<OreDictEntry> selected) {}
		});
	}
}