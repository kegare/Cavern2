package cavern.client.gui;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cavern.client.CaveRenderingRegistry;
import cavern.client.config.CaveConfigGui;
import cavern.client.gui.GuiSelectOreDict.OreDictEntry;
import cavern.config.Config;
import cavern.util.ArrayListExtended;
import cavern.util.BlockMeta;
import cavern.util.CaveFilters;
import cavern.util.CaveUtils;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ArrayEntry;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@SideOnly(Side.CLIENT)
public class GuiMiningPointsEditor extends GuiScreen
{
	protected final GuiScreen parent;
	protected final ArrayEntry arrayEntry;

	protected PointList pointList;

	protected GuiButton doneButton;
	protected GuiButton editButton;
	protected GuiButton cancelButton;
	protected GuiButton addButton;
	protected GuiButton removeButton;
	protected GuiButton clearButton;

	protected GuiCheckBox detailInfo;
	protected GuiCheckBox instantFilter;

	protected GuiTextField filterTextField;

	protected HoverChecker detailHoverChecker;
	protected HoverChecker instantHoverChecker;

	protected boolean editMode;

	protected GuiTextField pointField;

	protected HoverChecker pointHoverChecker;

	private int maxLabelWidth;

	private final List<String> editLabelList = Lists.newArrayList();
	private final List<GuiTextField> editFieldList = Lists.newArrayList();

	public GuiMiningPointsEditor(GuiScreen parent, ArrayEntry entry)
	{
		this.parent = parent;
		this.arrayEntry = entry;
	}

	@Override
	public void initGui()
	{
		if (pointList == null)
		{
			pointList = new PointList();
		}

		pointList.setDimensions(width, height, 32, height - (editMode ? 52 : 28));

		if (doneButton == null)
		{
			doneButton = new GuiButtonExt(0, 0, 0, 65, 20, I18n.format("gui.done"));
		}

		doneButton.x = width / 2 + 135;
		doneButton.y = height - doneButton.height - 4;

		if (editButton == null)
		{
			editButton = new GuiButtonExt(1, 0, 0, doneButton.width, doneButton.height, I18n.format("gui.edit"));
			editButton.enabled = false;
		}

		editButton.x = doneButton.x - doneButton.width - 3;
		editButton.y = doneButton.y;
		editButton.enabled = pointList.selected != null;
		editButton.visible = !editMode;

		if (cancelButton == null)
		{
			cancelButton = new GuiButtonExt(2, 0, 0, editButton.width, editButton.height, I18n.format("gui.cancel"));
		}

		cancelButton.x = editButton.x;
		cancelButton.y = editButton.y;
		cancelButton.visible = editMode;

		if (removeButton == null)
		{
			removeButton = new GuiButtonExt(4, 0, 0, doneButton.width, doneButton.height, I18n.format("gui.remove"));
		}

		removeButton.x = editButton.x - editButton.width - 3;
		removeButton.y = doneButton.y;
		removeButton.visible =  !editMode;

		if (addButton == null)
		{
			addButton = new GuiButtonExt(3, 0, 0, doneButton.width, doneButton.height, I18n.format("gui.add"));
		}

		addButton.x = removeButton.x - removeButton.width - 3;
		addButton.y = doneButton.y;
		addButton.visible = !editMode;

		if (clearButton == null)
		{
			clearButton = new GuiButtonExt(5, 0, 0, removeButton.width, removeButton.height, I18n.format("gui.clear"));
		}

		clearButton.x = removeButton.x;
		clearButton.y = removeButton.y;
		clearButton.visible = false;

		if (detailInfo == null)
		{
			detailInfo = new GuiCheckBox(6, 0, 5, I18n.format(Config.LANG_KEY + "detail"), true);
		}

		detailInfo.setIsChecked(CaveConfigGui.detailInfo);
		detailInfo.x = width / 2 + 95;

		if (instantFilter == null)
		{
			instantFilter = new GuiCheckBox(7, 0, detailInfo.y + detailInfo.height + 2, I18n.format(Config.LANG_KEY + "instant"), true);
		}

		instantFilter.setIsChecked(CaveConfigGui.instantFilter);
		instantFilter.x = detailInfo.x;

		buttonList.clear();
		buttonList.add(doneButton);

		if (editMode)
		{
			buttonList.add(cancelButton);
		}
		else
		{
			buttonList.add(editButton);
			buttonList.add(addButton);
			buttonList.add(removeButton);
			buttonList.add(clearButton);
		}

		buttonList.add(detailInfo);
		buttonList.add(instantFilter);

		if (filterTextField == null)
		{
			filterTextField = new GuiTextField(0, fontRenderer, 0, 0, 122, 16);
			filterTextField.setMaxStringLength(500);
		}

		filterTextField.x = width / 2 - 200;
		filterTextField.y = height - filterTextField.height - 6;

		detailHoverChecker = new HoverChecker(detailInfo, 800);
		instantHoverChecker = new HoverChecker(instantFilter, 800);

		editLabelList.clear();
		editLabelList.add(I18n.format(Config.LANG_KEY  + "points.point"));

		for (String key : editLabelList)
		{
			maxLabelWidth = Math.max(maxLabelWidth, fontRenderer.getStringWidth(key));
		}

		if (pointField == null)
		{
			pointField = new GuiTextField(5, fontRenderer, 0, 0, 0, 15);
			pointField.setMaxStringLength(5);
		}

		int i = maxLabelWidth + 8 + width / 2;
		pointField.x = width / 2 - i / 2 + maxLabelWidth + 10;
		pointField.y = pointList.bottom + 7;
		int fieldWidth = width / 2 + i / 2 - 45 - pointField.x + 40;
		pointField.width = fieldWidth / 4 + fieldWidth / 2 - 1;

		editFieldList.clear();

		if (editMode)
		{
			editFieldList.add(pointField);
		}

		pointHoverChecker = new HoverChecker(pointField.y - 1, pointField.y + pointField.height, pointField.x - maxLabelWidth - 12, pointField.x - 10, 800);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			switch (button.id)
			{
				case 0:
					if (editMode)
					{
						if (!Strings.isNullOrEmpty(pointField.getText()))
						{
							for (PointEntry entry : pointList.selected)
							{
								entry.setPoint(NumberUtils.toInt(pointField.getText(), entry.getPoint()));
							}
						}

						actionPerformed(cancelButton);

						pointList.scrollToTop();
						pointList.scrollToSelected();
					}
					else
					{
						arrayEntry.setListFromChildScreen(pointList.points.stream().map(PointEntry::toString).collect(Collectors.toList()).toArray());

						actionPerformed(cancelButton);

						pointList.selected.clear();
						pointList.scrollToTop();
					}

					break;
				case 1:
					if (editMode)
					{
						actionPerformed(cancelButton);
					}
					else if (!pointList.selected.isEmpty())
					{
						editMode = true;
						initGui();

						pointList.scrollToTop();
						pointList.scrollToSelected();

						if (pointList.selected.size() > 1)
						{
							pointField.setText("");
						}
						else for (PointEntry entry : pointList.selected)
						{
							pointField.setText(Integer.toString(entry.getPoint()));
						}
					}

					break;
				case 2:
					if (editMode)
					{
						editMode = false;
						initGui();
					}
					else
					{
						mc.displayGuiScreen(parent);
					}

					break;
				case 3:
					if (isShiftKeyDown())
					{
						mc.displayGuiScreen(getSelectBlockGuiScreen());
					}
					else
					{
						mc.displayGuiScreen(getSelectOreDictGuiScreen());
					}

					break;
				case 4:
					for (PointEntry entry : pointList.selected)
					{
						if (pointList.points.remove(entry))
						{
							pointList.contents.remove(entry);
						}
					}

					pointList.selected.clear();
					break;
				case 5:
					pointList.points.forEach(entry -> pointList.selected.add(entry));

					actionPerformed(removeButton);
					break;
				case 6:
					CaveConfigGui.detailInfo = detailInfo.isChecked();
					break;
				case 7:
					CaveConfigGui.instantFilter = instantFilter.isChecked();
					break;
				default:
					pointList.actionPerformed(button);
			}
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
		Set<BlockMeta> invisibleBlocks = pointList.points.stream().filter(PointEntry::isBlockMeta).map(PointEntry::getBlockMeta).collect(Collectors.toSet());

		return new GuiSelectBlock(this, new ISelectorCallback<BlockMeta>()
		{
			@Override
			public boolean isValidEntry(BlockMeta entry)
			{
				return entry != null && !invisibleBlocks.contains(entry);
			}

			@Override
			public void onSelected(List<BlockMeta> selected)
			{
				if (editMode)
				{
					return;
				}

				pointList.selected.clear();

				for (BlockMeta blockMeta : selected)
				{
					PointEntry entry = new PointEntry(blockMeta, 1);

					if (pointList.points.addIfAbsent(entry))
					{
						pointList.contents.addIfAbsent(entry);

						pointList.selected.add(entry);
					}
				}

				pointList.scrollToTop();
				pointList.scrollToSelected();
			}
		});
	}

	public GuiSelectOreDict getSelectOreDictGuiScreen()
	{
		GuiSelectOreDict selectOreDict = createSelectOreDictGuiScreen();
		GuiSelectBlock selectBlock = createSelectBlockGuiScreen();

		selectOreDict.setSwitchEntry(new SelectSwitchEntry(selectBlock, "block"));
		selectBlock.setSwitchEntry(new SelectSwitchEntry(selectOreDict, "oreDict"));

		return selectOreDict;
	}

	protected GuiSelectOreDict createSelectOreDictGuiScreen()
	{
		Set<OreDictEntry> invisibleDicts = pointList.points.stream().filter(PointEntry::isOreDict).map(PointEntry::getOreDict).collect(Collectors.toSet());

		return new GuiSelectOreDict(this, new ISelectorCallback<OreDictEntry>()
		{
			@Override
			public boolean isValidEntry(OreDictEntry entry)
			{
				return !entry.getItemStack().isEmpty() && entry.getItemStack().getItem() instanceof ItemBlock && !invisibleDicts.contains(entry);
			}

			@Override
			public void onSelected(List<OreDictEntry> selected)
			{
				if (editMode)
				{
					return;
				}

				pointList.selected.clear();

				for (OreDictEntry oreDict : selected)
				{
					PointEntry entry = new PointEntry(oreDict, 1);

					if (pointList.points.addIfAbsent(entry))
					{
						pointList.contents.addIfAbsent(entry);

						pointList.selected.add(entry);
					}
				}

				pointList.scrollToTop();
				pointList.scrollToSelected();
			}
		});
	}

	@Override
	public void updateScreen()
	{
		if (editMode)
		{
			for (GuiTextField textField : editFieldList)
			{
				if (textField.getVisible())
				{
					textField.updateCursorCounter();
				}
			}
		}
		else
		{
			editButton.enabled = !pointList.selected.isEmpty();
			removeButton.enabled = editButton.enabled;

			filterTextField.updateCursorCounter();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		pointList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRenderer, I18n.format(Config.LANG_KEY + "points"), width / 2, 15, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, ticks);

		if (editMode)
		{
			GuiTextField textField;

			for (int i = 0; i < editFieldList.size(); ++i)
			{
				textField = editFieldList.get(i);

				if (textField.getVisible())
				{
					textField.drawTextBox();

					drawString(fontRenderer, editLabelList.get(i), textField.x - maxLabelWidth - 10, textField.y + 3, 0xBBBBBB);
				}
			}

			if (pointHoverChecker.checkHover(mouseX, mouseY))
			{
				List<String> hover = Lists.newArrayList();
				String key = Config.LANG_KEY + "points.point";

				hover.add(TextFormatting.GRAY + I18n.format(key));
				hover.addAll(fontRenderer.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

				drawHoveringText(hover, mouseX, mouseY);
			}
		}
		else
		{
			filterTextField.drawTextBox();
		}

		if (detailHoverChecker.checkHover(mouseX, mouseY))
		{
			drawHoveringText(fontRenderer.listFormattedStringToWidth(I18n.format(Config.LANG_KEY + "detail.hover"), 300), mouseX, mouseY);
		}
		else if (instantHoverChecker.checkHover(mouseX, mouseY))
		{
			drawHoveringText(fontRenderer.listFormattedStringToWidth(I18n.format(Config.LANG_KEY + "instant.hover"), 300), mouseX, mouseY);
		}
		else if (pointList.isMouseYWithinSlotBounds(mouseY) && isCtrlKeyDown())
		{
			PointEntry entry = pointList.contents.get(pointList.getSlotIndexFromScreenCoords(mouseX, mouseY), null);

			if (entry != null)
			{
				List<String> info = Lists.newArrayList();
				String prefix = TextFormatting.GRAY.toString();

				if (entry.isOreDict())
				{
					info.add(prefix + I18n.format(Config.LANG_KEY + "points.oreDict") + ": " + entry.getOreDict().getName());
				}
				else
				{
					info.add(prefix + I18n.format(Config.LANG_KEY + "points.block") + ": " + entry.getBlockMeta().getBlockName() + ":" + entry.getBlockMeta().getMetaString());
				}

				info.add(prefix + I18n.format(Config.LANG_KEY + "points.point") + ": " + entry.getPoint());

				drawHoveringText(info, mouseX, mouseY);
			}
		}

		if (pointList.selected.size() > 1 && mouseX <= 100 && mouseY <= 20)
		{
			drawString(fontRenderer, I18n.format(Config.LANG_KEY + "select.entry.selected", pointList.selected.size()), 5, 5, 0xEFEFEF);
		}
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		pointList.handleMouseInput();

		if (editMode)
		{
			if (pointField.isFocused())
			{
				int i = Mouse.getDWheel();

				if (i < 0)
				{
					pointField.setText(Integer.toString(Math.max(NumberUtils.toInt(pointField.getText()) - 1, 1)));
				}
				else if (i > 0)
				{
					pointField.setText(Integer.toString(NumberUtils.toInt(pointField.getText()) + 1));
				}
			}
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int code) throws IOException
	{
		super.mouseClicked(x, y, code);

		if (code == 1)
		{
			actionPerformed(editButton);
		}
		else if (editMode)
		{
			pointField.mouseClicked(x, y, code);
		}
		else
		{
			filterTextField.mouseClicked(x, y, code);
		}
	}

	@Override
	public void handleKeyboardInput() throws IOException
	{
		super.handleKeyboardInput();

		if (Keyboard.getEventKey() == Keyboard.KEY_LSHIFT || Keyboard.getEventKey() == Keyboard.KEY_RSHIFT)
		{
			clearButton.visible = !editMode && Keyboard.getEventKeyState();
		}
	}

	@Override
	protected void keyTyped(char c, int code) throws IOException
	{
		if (editMode)
		{
			for (GuiTextField textField : editFieldList)
			{
				if (code == Keyboard.KEY_ESCAPE)
				{
					textField.setFocused(false);
				}
				else if (textField.getVisible() && textField.isFocused())
				{
					if (!CharUtils.isAsciiControl(c) && !CharUtils.isAsciiNumeric(c))
					{
						continue;
					}

					textField.textboxKeyTyped(c, code);
				}
			}
		}
		else
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
					pointList.setFilter(null);
				}
				else if (instantFilter.isChecked() && changed || code == Keyboard.KEY_RETURN)
				{
					pointList.setFilter(text);
				}
			}
			else
			{
				if (code == Keyboard.KEY_ESCAPE)
				{
					actionPerformed(doneButton);
				}
				else if (code == Keyboard.KEY_BACK)
				{
					pointList.selected.clear();
				}
				else if (code == Keyboard.KEY_TAB)
				{
					if (++pointList.nameType > 2)
					{
						pointList.nameType = 0;
					}
				}
				else if (code == Keyboard.KEY_HOME)
				{
					pointList.scrollToTop();
				}
				else if (code == Keyboard.KEY_END)
				{
					pointList.scrollToEnd();
				}
				else if (code == Keyboard.KEY_SPACE)
				{
					pointList.scrollToSelected();
				}
				else if (code == Keyboard.KEY_PRIOR)
				{
					pointList.scrollToPrev();
				}
				else if (code == Keyboard.KEY_NEXT)
				{
					pointList.scrollToNext();
				}
				else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
				{
					filterTextField.setFocused(true);
				}
				else if (isCtrlKeyDown() && code == Keyboard.KEY_A)
				{
					pointList.contents.forEach(entry -> pointList.selected.add(entry));
				}
				else if (code == Keyboard.KEY_DELETE && !pointList.selected.isEmpty())
				{
					actionPerformed(removeButton);
				}
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void onGuiClosed()
	{
		pointList.currentPanoramaPaths = null;
	}

	protected class PointList extends GuiListSlot implements Comparator<PointEntry>
	{
		protected final ArrayListExtended<PointEntry> points = new ArrayListExtended<>();
		protected final ArrayListExtended<PointEntry> contents = new ArrayListExtended<>();
		protected final Set<PointEntry> selected = Sets.newTreeSet(this);
		protected final Map<String, List<PointEntry>> filterCache = Maps.newHashMap();

		protected int nameType;
		protected boolean clickFlag;

		protected PointList()
		{
			super(GuiMiningPointsEditor.this.mc, 0, 0, 0, 0, 22);

			for (Object obj : arrayEntry.getCurrentValues())
			{
				String value = String.valueOf(obj);

				if (!Strings.isNullOrEmpty(value) && value.contains(","))
				{
					value = value.trim();

					int i = value.indexOf(',');
					String str = value.substring(0, i).trim();
					int point = NumberUtils.toInt(value.substring(i + 1));

					if (OreDictionary.doesOreNameExist(str))
					{
						PointEntry entry = new PointEntry(new OreDictEntry(str), point);

						points.add(entry);
						contents.add(entry);
					}
					else
					{
						if (!str.contains(":"))
						{
							str = "minecraft:" + str;
						}

						BlockMeta blockMeta;

						if (str.indexOf(':') != str.lastIndexOf(':'))
						{
							i = str.lastIndexOf(':');

							blockMeta = new BlockMeta(str.substring(0, i), str.substring(i + 1));
						}
						else
						{
							blockMeta = new BlockMeta(str, 0);
						}

						if (blockMeta.isNotAir())
						{
							PointEntry entry = new PointEntry(blockMeta, point);

							points.add(entry);
							contents.add(entry);
						}
					}
				}
			}
		}

		@Override
		public void scrollToSelected()
		{
			if (!selected.isEmpty())
			{
				int amount = 0;

				for (PointEntry entry : selected)
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
		protected void drawSlot(int index, int par2, int par3, int par4, int mouseX, int mouseY, float partialTicks)
		{
			PointEntry entry = contents.get(index, null);

			if (entry == null)
			{
				return;
			}

			String text = null;
			ItemStack stack = null;

			if (entry.isOreDict())
			{
				stack = entry.getOreDict().getItemStack();

				if (nameType == 0 && !stack.isEmpty())
				{
					text = stack.getDisplayName();
				}
				else
				{
					text = entry.getOreDict().getName();
				}
			}
			else
			{
				BlockMeta blockMeta = entry.getBlockMeta();
				Block block = blockMeta.getBlock();
				int meta = blockMeta.getMeta();
				stack = new ItemStack(CaveRenderingRegistry.getRenderBlock(block), 1, meta);
				boolean hasItem = stack.getItem() != Items.AIR;

				if (nameType == 1)
				{
					text = blockMeta.getName();
				}
				else if (hasItem)
				{
					switch (nameType)
					{
						case 2:
							text = stack.getUnlocalizedName();
							text = text.substring(text.indexOf(".") + 1);
							break;
						default:
							text = stack.getDisplayName();
							break;
					}
				}
				else switch (nameType)
				{
					case 2:
						text = block.getUnlocalizedName();
						text = text.substring(text.indexOf(".") + 1);
						break;
					default:
						text = block.getLocalizedName();
						break;
				}
			}

			if (!Strings.isNullOrEmpty(text))
			{
				drawCenteredString(fontRenderer, text, width / 2, par3 + 3, 0xFFFFFF);
			}

			if (detailInfo.isChecked())
			{
				drawItemStack(itemRender, stack, width / 2 - 100, par3 + 1, fontRenderer, Integer.toString(entry.getPoint()));
			}
		}

		@Override
		protected void elementClicked(int index, boolean flag, int mouseX, int mouseY)
		{
			if (editMode)
			{
				return;
			}

			PointEntry entry = contents.get(index, null);

			if (entry != null && (clickFlag = !clickFlag == true) && !selected.remove(entry))
			{
				if (!isCtrlKeyDown())
				{
					selected.clear();
				}

				selected.add(entry);
			}
		}

		@Override
		protected boolean isSelected(int index)
		{
			PointEntry entry = contents.get(index, null);

			return entry != null && selected.contains(entry);
		}

		protected void setFilter(String filter)
		{
			List<PointEntry> result;

			if (Strings.isNullOrEmpty(filter))
			{
				result = points;
			}
			else if (filter.equals("selected"))
			{
				result = Lists.newArrayList(selected);
			}
			else
			{
				if (!filterCache.containsKey(filter))
				{
					filterCache.put(filter, points.parallelStream().filter(e -> filterMatch(e, filter)).collect(Collectors.toList()));
				}

				result = filterCache.get(filter);
			}

			if (!contents.equals(result))
			{
				contents.clear();
				contents.addAll(result);
			}
		}

		protected boolean filterMatch(PointEntry entry, String filter)
		{
			if (entry.isOreDict())
			{
				return StringUtils.containsIgnoreCase(entry.getOreDict().getName(), filter);
			}

			return CaveFilters.blockFilter(entry.getBlockMeta(), filter);
		}

		@Override
		public int compare(PointEntry o1, PointEntry o2)
		{
			int i = CaveUtils.compareWithNull(o1, o2);

			if (i == 0 && o1 != null && o2 != null)
			{
				i = Integer.compare(points.indexOf(o1), points.indexOf(o2));
			}

			return i;
		}
	}

	public static class PointEntry implements Comparable<PointEntry>
	{
		private BlockMeta blockMeta;
		private OreDictEntry oreDict;

		private int point;

		public PointEntry(BlockMeta block, int point)
		{
			this.blockMeta = block;
			this.point = point;
		}

		public PointEntry(OreDictEntry oreDict, int point)
		{
			this.oreDict = oreDict;
			this.point = point;
		}

		public BlockMeta getBlockMeta()
		{
			return blockMeta;
		}

		public void setBlockMeta(BlockMeta block)
		{
			blockMeta = block;
		}

		public OreDictEntry getOreDict()
		{
			return oreDict;
		}

		public void setOreDict(OreDictEntry entry)
		{
			oreDict = entry;
		}

		public boolean isBlockMeta()
		{
			return blockMeta != null;
		}

		public boolean isOreDict()
		{
			return oreDict != null;
		}

		public String getName()
		{
			return isOreDict() ? oreDict.toString() : blockMeta.getName(true);
		}

		public int getPoint()
		{
			return point;
		}

		public void setPoint(int value)
		{
			point = value;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			else if (!(obj instanceof PointEntry))
			{
				return false;
			}

			PointEntry entry = (PointEntry)obj;

			if (isOreDict() && entry.isOreDict())
			{
				return oreDict.equals(entry.oreDict);
			}
			else if (isBlockMeta() && entry.isBlockMeta())
			{
				return Objects.equal(blockMeta, entry.blockMeta);
			}

			return false;
		}

		@Override
		public int hashCode()
		{
			return isOreDict() ? oreDict.hashCode() : blockMeta.hashCode();
		}

		@Override
		public String toString()
		{
			return getName() + "," + point;
		}

		@Override
		public int compareTo(PointEntry entry)
		{
			int i = CaveUtils.compareWithNull(this, entry);

			if (i == 0 && entry != null)
			{
				i = Boolean.compare(isOreDict(), entry.isOreDict());

				if (i == 0)
				{
					if (isOreDict() && entry.isOreDict())
					{
						i = oreDict.compareTo(entry.oreDict);
					}
					else
					{
						i = blockMeta.compareTo(entry.blockMeta);
					}
				}
			}

			return i;
		}
	}
}