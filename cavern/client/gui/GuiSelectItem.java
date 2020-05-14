package cavern.client.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cavern.client.config.CaveConfigGui;
import cavern.config.Config;
import cavern.util.ArrayListExtended;
import cavern.util.CaveFilters;
import cavern.util.ItemMeta;
import cavern.util.PanoramaPaths;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ArrayEntry;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSelectItem extends GuiScreen
{
	private static final ArrayListExtended<ItemMeta> ITEMS = new ArrayListExtended<>();

	static
	{
		NonNullList<ItemStack> list = NonNullList.create();

		for (Item item : Item.REGISTRY)
		{
			if (item == null || item == Items.AIR)
			{
				continue;
			}

			list.clear();

			item.getSubItems(ObjectUtils.defaultIfNull(item.getCreativeTab(), CreativeTabs.SEARCH), list);

			if (list.isEmpty())
			{
				ITEMS.addIfAbsent(new ItemMeta(item, 0));
			}
			else for (ItemStack stack : list)
			{
				ITEMS.addIfAbsent(new ItemMeta(stack));
			}
		}
	}

	protected final GuiScreen parent;

	protected ISelectorCallback<ItemMeta> selectorCallback;

	protected GuiTextField nameField, metaField;

	protected ArrayEntry arrayEntry;

	protected ItemList itemList;

	protected GuiButton doneButton;

	protected GuiCheckBox detailInfo;
	protected GuiCheckBox instantFilter;

	protected GuiTextField filterTextField;

	protected HoverChecker selectedHoverChecker;
	protected HoverChecker detailHoverChecker;
	protected HoverChecker instantHoverChecker;

	public GuiSelectItem(GuiScreen parent)
	{
		this.parent = parent;
	}

	public GuiSelectItem(GuiScreen parent, @Nullable ISelectorCallback<ItemMeta> callback)
	{
		this(parent);
		this.selectorCallback = callback;
	}

	public GuiSelectItem(GuiScreen parent, @Nullable GuiTextField nameField, @Nullable GuiTextField metaField)
	{
		this(parent);
		this.nameField = nameField;
		this.metaField = metaField;
	}

	public GuiSelectItem(GuiScreen parent, @Nullable GuiTextField nameField, @Nullable GuiTextField metaField, @Nullable ISelectorCallback<ItemMeta> callback)
	{
		this(parent, nameField, metaField);
		this.selectorCallback = callback;
	}

	public GuiSelectItem(GuiScreen parent, @Nullable ArrayEntry arrayEntry)
	{
		this(parent);
		this.arrayEntry = arrayEntry;
	}

	public GuiSelectItem(GuiScreen parent, @Nullable ArrayEntry arrayEntry, @Nullable ISelectorCallback<ItemMeta> callback)
	{
		this(parent, arrayEntry);
		this.selectorCallback = callback;
	}

	@Override
	public void initGui()
	{
		if (itemList == null)
		{
			itemList = new ItemList();
		}

		itemList.setDimensions(width, height, 32, height - 28);

		if (doneButton == null)
		{
			doneButton = new GuiButtonExt(0, 0, 0, 145, 20, I18n.format("gui.done"));
		}

		doneButton.x = width / 2 + 10;
		doneButton.y = height - doneButton.height - 4;

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
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			switch (button.id)
			{
				case 0:
					if (selectorCallback != null)
					{
						selectorCallback.onSelected(ImmutableList.copyOf(itemList.selected));
					}

					if (arrayEntry != null)
					{
						if (itemList.selected.isEmpty())
						{
							arrayEntry.setListFromChildScreen(new Object[0]);
						}
						else
						{
							arrayEntry.setListFromChildScreen(itemList.selected.stream().map(ItemMeta::getName).collect(Collectors.toList()).toArray());
						}
					}

					if (itemList.selected.isEmpty())
					{
						if (nameField != null)
						{
							nameField.setText("");
						}

						if (metaField != null)
						{
							metaField.setText("");
						}
					}
					else for (ItemMeta itemMeta : itemList.selected)
					{
						if (nameField != null)
						{
							nameField.setText(itemMeta.getItemName());
						}

						if (metaField != null)
						{
							metaField.setText(Integer.toString(itemMeta.getMeta()));
						}

						break;
					}

					if (nameField != null)
					{
						nameField.setFocused(true);
						nameField.setCursorPositionEnd();
					}
					else if (metaField != null)
					{
						metaField.setFocused(true);
						metaField.setCursorPositionEnd();
					}

					mc.displayGuiScreen(parent);

					itemList.selected.clear();
					itemList.scrollToTop();
					break;
				case 1:
					CaveConfigGui.detailInfo = detailInfo.isChecked();
					break;
				case 2:
					CaveConfigGui.instantFilter = instantFilter.isChecked();
					break;
				default:
					itemList.actionPerformed(button);
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
		itemList.drawScreen(mouseX, mouseY, ticks);

		boolean single = nameField != null || metaField != null;
		String name = null;

		if (single)
		{
			name = I18n.format(Config.LANG_KEY + "select.item");
		}
		else
		{
			name = I18n.format(Config.LANG_KEY + "select.item.multiple");
		}

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

		if (!single && !itemList.selected.isEmpty())
		{
			if (mouseX <= 100 && mouseY <= 20)
			{
				drawString(fontRenderer, I18n.format(Config.LANG_KEY + "select.item.selected", itemList.selected.size()), 5, 5, 0xEFEFEF);
			}

			if (selectedHoverChecker.checkHover(mouseX, mouseY))
			{
				List<String> texts = Lists.newArrayList();

				for (ItemMeta itemMeta : itemList.selected)
				{
					name = itemList.getItemMetaTypeName(itemMeta);

					if (!Strings.isNullOrEmpty(name))
					{
						texts.add(name);
					}
				}

				drawHoveringText(texts, mouseX, mouseY);
			}
		}
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		itemList.handleMouseInput();
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
				itemList.setFilter(null);
			}
			else if (instantFilter.isChecked() && changed || code == Keyboard.KEY_RETURN)
			{
				itemList.setFilter(text);
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
				itemList.selected.clear();
			}
			else if (code == Keyboard.KEY_TAB)
			{
				if (++itemList.nameType > 2)
				{
					itemList.nameType = 0;
				}
			}
			else if (code == Keyboard.KEY_UP)
			{
				itemList.scrollUp();
			}
			else if (code == Keyboard.KEY_DOWN)
			{
				itemList.scrollDown();
			}
			else if (code == Keyboard.KEY_HOME)
			{
				itemList.scrollToTop();
			}
			else if (code == Keyboard.KEY_END)
			{
				itemList.scrollToEnd();
			}
			else if (code == Keyboard.KEY_SPACE)
			{
				itemList.scrollToSelected();
			}
			else if (code == Keyboard.KEY_PRIOR)
			{
				itemList.scrollToPrev();
			}
			else if (code == Keyboard.KEY_NEXT)
			{
				itemList.scrollToNext();
			}
			else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
			{
				filterTextField.setFocused(true);
			}
			else if (isCtrlKeyDown() && code == Keyboard.KEY_A)
			{
				itemList.contents.forEach(entry -> itemList.selected.add(entry));
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	protected class ItemList extends GuiListSlot
	{
		protected final ArrayListExtended<ItemMeta> entries = new ArrayListExtended<>();
		protected final ArrayListExtended<ItemMeta> contents = new ArrayListExtended<>();
		protected final Set<ItemMeta> selected = Sets.newTreeSet();
		protected final Map<String, List<ItemMeta>> filterCache = Maps.newHashMap();

		protected int nameType;
		protected boolean clickFlag;

		protected ItemList()
		{
			super(GuiSelectItem.this.mc, 0, 0, 0, 0, 18);

			Set<ItemMeta> select = Sets.newHashSet();

			if (nameField != null)
			{
				String name = nameField.getText();
				int meta = -1;

				if (metaField != null)
				{
					meta = NumberUtils.toInt(metaField.getText(), -1);
				}

				if (!Strings.isNullOrEmpty(name))
				{
					select.add(new ItemMeta(name, meta));
				}
			}

			if (arrayEntry != null)
			{
				Arrays.stream(arrayEntry.getCurrentValues()).map(Object::toString).filter(value -> !Strings.isNullOrEmpty(value)).forEach(value ->
				{
					if (!value.contains(":"))
					{
						value = "minecraft:" + value;
					}

					ItemMeta itemMeta;

					if (value.indexOf(':') != value.lastIndexOf(':'))
					{
						int i = value.lastIndexOf(':');

						itemMeta = new ItemMeta(value.substring(0, i), NumberUtils.toInt(value.substring(i + 1)));
					}
					else
					{
						itemMeta = new ItemMeta(value, 0);
					}

					if (!itemMeta.isEmpty())
					{
						select.add(itemMeta);
					}
				});
			}

			for (ItemMeta itemMeta : ITEMS)
			{
				if (selectorCallback == null || selectorCallback.isValidEntry(itemMeta))
				{
					entries.addIfAbsent(itemMeta);
					contents.addIfAbsent(itemMeta);

					if (select.contains(itemMeta))
					{
						selected.add(itemMeta);
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

				for (ItemMeta itemMeta : selected)
				{
					amount = contents.indexOf(itemMeta) * getSlotHeight();

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

		@Nullable
		public String getItemMetaTypeName(ItemMeta itemMeta, ItemStack stack)
		{
			if (itemMeta == null)
			{
				return null;
			}

			if (stack.isEmpty())
			{
				stack = itemMeta.getItemStack();
			}

			String name = null;

			switch (nameType)
			{
				case 1:
					name = itemMeta.getName();
					break;
				case 2:
					name = stack.getUnlocalizedName();
					name = name.substring(name.indexOf(".") + 1);
					break;
				default:
					name = stack.getDisplayName();
					break;
			}

			return name;
		}

		public String getItemMetaTypeName(ItemMeta itemMeta)
		{
			return getItemMetaTypeName(itemMeta, ItemStack.EMPTY);
		}

		@Override
		protected void drawBackground()
		{
			drawDefaultBackground();
		}

		@Override
		protected void drawSlot(int slot, int par2, int par3, int par4, int mouseX, int mouseY, float partialTicks)
		{
			ItemMeta itemMeta = contents.get(slot, null);

			if (itemMeta == null)
			{
				return;
			}

			ItemStack stack = itemMeta.getItemStack();
			String name = getItemMetaTypeName(itemMeta, stack);

			if (!Strings.isNullOrEmpty(name))
			{
				drawCenteredString(fontRenderer, name, width / 2, par3 + 1, 0xFFFFFF);
			}

			if (detailInfo.isChecked())
			{
				drawItemStack(itemRender, stack, width / 2 - 100, par3 - 1);
			}
		}

		@Override
		protected void elementClicked(int slot, boolean flag, int mouseX, int mouseY)
		{
			ItemMeta itemMeta = contents.get(slot, null);

			if (itemMeta != null && (clickFlag = !clickFlag == true) && !selected.remove(itemMeta))
			{
				if (nameField != null || metaField != null)
				{
					selected.clear();
				}

				selected.add(itemMeta);
			}
		}

		@Override
		protected boolean isSelected(int slot)
		{
			ItemMeta itemMeta = contents.get(slot, null);

			return itemMeta != null && selected.contains(itemMeta);
		}

		protected void setFilter(String filter)
		{
			List<ItemMeta> result;

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

		protected boolean filterMatch(ItemMeta itemMeta, String filter)
		{
			return CaveFilters.itemFilter(itemMeta, filter);
		}
	}
}