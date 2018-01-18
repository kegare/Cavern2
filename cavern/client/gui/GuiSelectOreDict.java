package cavern.client.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cavern.client.config.CaveConfigGui;
import cavern.config.Config;
import cavern.util.ArrayListExtended;
import cavern.util.CaveUtils;
import cavern.util.PanoramaPaths;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ArrayEntry;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@SideOnly(Side.CLIENT)
public class GuiSelectOreDict extends GuiScreen
{
	protected final GuiScreen parent;

	protected ISelectorCallback<OreDictEntry> selectorCallback;
	protected SelectSwitchEntry switchEntry;

	protected ArrayEntry arrayEntry;

	protected OreDictList oreDictList;

	protected GuiButton doneButton;
	protected GuiButton switchButton;

	protected GuiCheckBox detailInfo;
	protected GuiCheckBox instantFilter;

	protected GuiTextField filterTextField;

	protected HoverChecker selectedHoverChecker;
	protected HoverChecker detailHoverChecker;
	protected HoverChecker instantHoverChecker;

	public GuiSelectOreDict(GuiScreen parent)
	{
		this.parent = parent;
	}

	public GuiSelectOreDict(GuiScreen parent, @Nullable ArrayEntry arrayEntry)
	{
		this(parent);
		this.arrayEntry = arrayEntry;
	}

	public GuiSelectOreDict(GuiScreen parent, @Nullable ISelectorCallback<OreDictEntry> callback)
	{
		this(parent);
		this.selectorCallback = callback;
	}

	public GuiSelectOreDict(GuiScreen parent, @Nullable ArrayEntry arrayEntry, @Nullable ISelectorCallback<OreDictEntry> callback)
	{
		this(parent, arrayEntry);
		this.selectorCallback = callback;
	}

	public void setSwitchEntry(@Nullable SelectSwitchEntry entry)
	{
		switchEntry = entry;
	}

	@Override
	public void initGui()
	{
		if (oreDictList == null)
		{
			oreDictList = new OreDictList();
		}

		oreDictList.setDimensions(width, height, 32, height - 28);

		boolean hasSwitch = switchEntry != null;
		int buttonWidth = hasSwitch ? 70 : 145;

		if (doneButton == null)
		{
			doneButton = new GuiButtonExt(0, 0, 0, buttonWidth, 20, I18n.format("gui.done"));
		}

		doneButton.x = width / 2 + 10;
		doneButton.y = height - doneButton.height - 4;

		if (switchButton == null)
		{
			switchButton = new GuiButtonExt(3, 0, 0, buttonWidth, 20, hasSwitch ? switchEntry.getTranslatedName() : "");
			switchButton.visible = hasSwitch;
		}

		switchButton.x = doneButton.x;
		switchButton.y = doneButton.y;

		if (hasSwitch)
		{
			doneButton.x += buttonWidth + 3;
		}

		if (detailInfo == null)
		{
			detailInfo = new GuiCheckBox(1, 0, 5, I18n.format(Config.LANG_KEY + "detail"), true);
		}

		detailInfo.setIsChecked(CaveConfigGui.detailInfo);
		detailInfo.x = width / 2 + 95;

		if (instantFilter == null)
		{
			instantFilter = new GuiCheckBox(2, 0, detailInfo.y + detailInfo.height + 2, I18n.format(Config.LANG_KEY + "instant"), true);
		}

		instantFilter.setIsChecked(CaveConfigGui.instantFilter);
		instantFilter.x = detailInfo.x;

		buttonList.clear();
		buttonList.add(doneButton);
		buttonList.add(switchButton);
		buttonList.add(detailInfo);
		buttonList.add(instantFilter);

		if (filterTextField == null)
		{
			filterTextField = new GuiTextField(0, fontRenderer, 0, 0, 150, 16);
			filterTextField.setMaxStringLength(100);
		}

		filterTextField.x = width / 2 - filterTextField.width - 5;
		filterTextField.y = height - filterTextField.height - 6;

		selectedHoverChecker = new HoverChecker(0, 20, 0, 100, 800);
		detailHoverChecker = new HoverChecker(detailInfo, 800);
		instantHoverChecker = new HoverChecker(instantFilter, 800);

		if (switchEntry != null && mc.currentScreen == this)
		{
			switchEntry.getGuiScreen().setWorldAndResolution(mc, width, height);
		}
	}

	protected void setResult()
	{
		if (selectorCallback != null)
		{
			selectorCallback.onSelected(ImmutableList.copyOf(oreDictList.selected));
		}

		if (arrayEntry != null)
		{
			if (switchEntry == null || mc.currentScreen == this)
			{
				if (oreDictList.selected.isEmpty())
				{
					arrayEntry.setListFromChildScreen(new String[0]);
				}
				else
				{
					arrayEntry.setListFromChildScreen(oreDictList.selected.stream().map(OreDictEntry::getName).collect(Collectors.toList()).toArray());
				}
			}
			else if (!oreDictList.selected.isEmpty())
			{
				Object[] values = arrayEntry.getCurrentValues();
				Object[] newValues = oreDictList.selected.stream().map(OreDictEntry::getName).collect(Collectors.toList()).toArray();

				if (values == null || values.length <= 0)
				{
					arrayEntry.setListFromChildScreen(newValues);
				}
				else
				{
					arrayEntry.setListFromChildScreen(ArrayUtils.addAll(values, newValues));
				}
			}
		}
	}

	@Override
	public void confirmClicked(boolean result, int id)
	{
		super.confirmClicked(result, id);

		if (id == 3)
		{
			setResult();
		}
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			switch (button.id)
			{
				case 0:
					setResult();

					if (switchEntry != null)
					{
						switchEntry.getGuiScreen().confirmClicked(true, 3);
					}

					mc.displayGuiScreen(parent);

					oreDictList.selected.clear();
					oreDictList.scrollToTop();
					break;
				case 1:
					CaveConfigGui.detailInfo = detailInfo.isChecked();
					break;
				case 2:
					CaveConfigGui.instantFilter = instantFilter.isChecked();
					break;
				case 3:
					if (switchEntry != null)
					{
						mc.displayGuiScreen(switchEntry.getGuiScreen());
					}

					break;
				default:
					oreDictList.actionPerformed(button);
			}
		}
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		filterTextField.updateCursorCounter();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		oreDictList.drawScreen(mouseX, mouseY, ticks);

		String name = I18n.format(Config.LANG_KEY + "select.oreDict");

		if (!Strings.isNullOrEmpty(name))
		{
			drawCenteredString(fontRenderer, name, width / 2, 15, 0xFFFFFF);
		}

		super.drawScreen(mouseX, mouseY, ticks);

		filterTextField.drawTextBox();

		if (detailHoverChecker.checkHover(mouseX, mouseY))
		{
			drawHoveringText(fontRenderer.listFormattedStringToWidth(I18n.format(Config.LANG_KEY + "detail.hover"), 300), mouseX, mouseY);
		}
		else if (instantHoverChecker.checkHover(mouseX, mouseY))
		{
			drawHoveringText(fontRenderer.listFormattedStringToWidth(I18n.format(Config.LANG_KEY + "instant.hover"), 300), mouseX, mouseY);
		}

		if (!oreDictList.selected.isEmpty())
		{
			if (mouseX <= 100 && mouseY <= 20)
			{
				drawString(fontRenderer, I18n.format(Config.LANG_KEY + "select.oreDict.selected", oreDictList.selected.size()), 5, 5, 0xEFEFEF);
			}

			if (selectedHoverChecker.checkHover(mouseX, mouseY))
			{
				drawHoveringText(oreDictList.selected.stream().map(OreDictEntry::getName).collect(Collectors.toList()), mouseX, mouseY);
			}
		}
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		oreDictList.handleMouseInput();
	}

	@Override
	protected void mouseClicked(int x, int y, int code) throws IOException
	{
		super.mouseClicked(x, y, code);

		filterTextField.mouseClicked(x, y, code);
	}

	@Override
	protected void keyTyped(char c, int code) throws IOException
	{
		if (filterTextField.isFocused())
		{
			if (code == Keyboard.KEY_ESCAPE)
			{
				filterTextField.setFocused(false);
			}

			String prev = filterTextField.getText();

			filterTextField.textboxKeyTyped(c, code);

			String text = filterTextField.getText();
			boolean changed = text != prev;

			if (Strings.isNullOrEmpty(text) && changed)
			{
				oreDictList.setFilter(null);
			}
			else if (instantFilter.isChecked() && changed || code == Keyboard.KEY_RETURN)
			{
				oreDictList.setFilter(text);
			}
		}
		else
		{
			if (code == Keyboard.KEY_ESCAPE)
			{
				mc.displayGuiScreen(parent);
			}
			else if (code == Keyboard.KEY_BACK)
			{
				oreDictList.selected.clear();
			}
			else if (code == Keyboard.KEY_TAB)
			{
				if (++oreDictList.nameType > 1)
				{
					oreDictList.nameType = 0;
				}
			}
			else if (code == Keyboard.KEY_UP)
			{
				oreDictList.scrollUp();
			}
			else if (code == Keyboard.KEY_DOWN)
			{
				oreDictList.scrollDown();
			}
			else if (code == Keyboard.KEY_LEFT || code == Keyboard.KEY_RIGHT)
			{
				switchButton.playPressSound(mc.getSoundHandler());

				actionPerformed(switchButton);
			}
			else if (code == Keyboard.KEY_HOME)
			{
				oreDictList.scrollToTop();
			}
			else if (code == Keyboard.KEY_END)
			{
				oreDictList.scrollToEnd();
			}
			else if (code == Keyboard.KEY_SPACE)
			{
				oreDictList.scrollToSelected();
			}
			else if (code == Keyboard.KEY_PRIOR)
			{
				oreDictList.scrollToPrev();
			}
			else if (code == Keyboard.KEY_NEXT)
			{
				oreDictList.scrollToNext();
			}
			else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
			{
				filterTextField.setFocused(true);
			}
			else if (isCtrlKeyDown() && code == Keyboard.KEY_A)
			{
				oreDictList.contents.forEach(entry -> oreDictList.selected.add(entry));
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	protected class OreDictList extends GuiListSlot
	{
		protected final ArrayListExtended<OreDictEntry> entries = new ArrayListExtended<>();
		protected final ArrayListExtended<OreDictEntry> contents = new ArrayListExtended<>();
		protected final Set<OreDictEntry> selected = Sets.newTreeSet();
		protected final Map<String, List<OreDictEntry>> filterCache = Maps.newHashMap();

		protected int nameType;
		protected boolean clickFlag;

		protected OreDictList()
		{
			super(GuiSelectOreDict.this.mc, 0, 0, 0, 0, 18);

			Set<String> select = Sets.newHashSet();

			if (arrayEntry != null)
			{
				Arrays.stream(arrayEntry.getCurrentValues()).map(Object::toString).filter(value -> !Strings.isNullOrEmpty(value)).forEach(value ->
				{
					value = value.trim();

					if (OreDictionary.doesOreNameExist(value))
					{
						select.add(value);
					}
				});
			}

			for (String name : OreDictionary.getOreNames())
			{
				if (Strings.isNullOrEmpty(name))
				{
					continue;
				}

				OreDictEntry entry = new OreDictEntry(name);

				if (selectorCallback == null || selectorCallback.isValidEntry(entry))
				{
					entries.addIfAbsent(entry);
					contents.addIfAbsent(entry);

					if (select.contains(name))
					{
						selected.add(entry);
					}
				}
			}

			if (!selected.isEmpty())
			{
				scrollToTop();
				scrollToSelected();
			}
		}

		@Override
		public PanoramaPaths getPanoramaPaths()
		{
			return null;
		}

		@Override
		public void scrollToSelected()
		{
			if (!selected.isEmpty())
			{
				int amount = 0;

				for (OreDictEntry entry : selected)
				{
					amount = contents.indexOf(entry) * getSlotHeight();

					if (getAmountScrolled() != amount)
					{
						break;
					}
				}

				scrollToTop();
				scrollBy(amount);
			}
		}

		@Override
		protected int getSize()
		{
			return contents.size();
		}

		@Override
		protected void drawBackground()
		{
			drawDefaultBackground();
		}

		@Override
		protected void drawSlot(int slot, int par2, int par3, int par4, int mouseX, int mouseY, float partialTicks)
		{
			OreDictEntry entry = contents.get(slot, null);

			if (entry == null)
			{
				return;
			}

			String name = entry.getName();

			if (nameType == 1)
			{
				String displayName = entry.getItemStack().getDisplayName();

				if (!Strings.isNullOrEmpty(displayName))
				{
					name = displayName;
				}
			}

			if (!Strings.isNullOrEmpty(name))
			{
				drawCenteredString(fontRenderer, name, width / 2, par3 + 1, 0xFFFFFF);
			}

			if (detailInfo.isChecked())
			{
				drawItemStack(itemRender, entry.getItemStack(), width / 2 - 100, par3 - 1);
			}
		}

		@Override
		protected void elementClicked(int slot, boolean flag, int mouseX, int mouseY)
		{
			OreDictEntry entry = contents.get(slot, null);

			if (entry != null && (clickFlag = !clickFlag == true) && !selected.remove(entry))
			{
				selected.add(entry);
			}
		}

		@Override
		protected boolean isSelected(int slot)
		{
			OreDictEntry entry = contents.get(slot, null);

			return entry != null && selected.contains(entry);
		}

		protected void setFilter(String filter)
		{
			List<OreDictEntry> result;

			if (Strings.isNullOrEmpty(filter))
			{
				result = entries;
			}
			else if (filter.equals("selected"))
			{
				result = Lists.newArrayList(selected);
			}
			else
			{
				if (!filterCache.containsKey(filter))
				{
					filterCache.put(filter, entries.parallelStream().filter(e -> filterMatch(e, filter)).collect(Collectors.toList()));
				}

				result = filterCache.get(filter);
			}

			if (!contents.equals(result))
			{
				contents.clear();
				contents.addAll(result);
			}
		}

		protected boolean filterMatch(OreDictEntry entry, String filter)
		{
			if (entry == null)
			{
				return false;
			}

			if (StringUtils.containsIgnoreCase(entry.getName(), filter))
			{
				return true;
			}

			return StringUtils.containsIgnoreCase(entry.getItemStack().getDisplayName(), filter);
		}
	}

	public static class OreDictEntry implements Comparable<OreDictEntry>
	{
		private String oreDictName;
		private ItemStack cachedItemStack;

		public OreDictEntry(String name)
		{
			this.oreDictName = name;
		}

		public String getName()
		{
			return oreDictName;
		}

		public void setName(String name)
		{
			oreDictName = name;

			refreshItemStack();
		}

		public ItemStack getItemStack()
		{
			if (cachedItemStack == null)
			{
				refreshItemStack();
			}

			return cachedItemStack;
		}

		public void refreshItemStack()
		{
			for (ItemStack stack : OreDictionary.getOres(oreDictName, false))
			{
				if (!stack.isEmpty())
				{
					if (stack.getMetadata() == OreDictionary.WILDCARD_VALUE)
					{
						cachedItemStack = new ItemStack(stack.getItem());

						if (stack.hasTagCompound())
						{
							cachedItemStack.setTagCompound(stack.getTagCompound());
						}
					}
					else
					{
						cachedItemStack = stack.copy();
					}

					break;
				}
			}

			if (cachedItemStack == null)
			{
				cachedItemStack = ItemStack.EMPTY;
			}
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			else if (!(obj instanceof OreDictEntry))
			{
				return false;
			}

			OreDictEntry entry = (OreDictEntry)obj;

			return oreDictName.equals(entry.oreDictName);
		}

		@Override
		public int hashCode()
		{
			return oreDictName.hashCode();
		}

		@Override
		public String toString()
		{
			return oreDictName;
		}

		@Override
		public int compareTo(OreDictEntry entry)
		{
			int i = CaveUtils.compareWithNull(this, entry);

			if (i == 0 && entry != null)
			{
				i = oreDictName.compareTo(entry.oreDictName);
			}

			return i;
		}
	}
}