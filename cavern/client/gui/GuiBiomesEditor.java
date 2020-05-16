package cavern.client.gui;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cavern.client.config.CaveConfigGui;
import cavern.config.CavernConfig;
import cavern.config.Config;
import cavern.config.manager.CaveBiome;
import cavern.config.manager.CaveBiomeManager;
import cavern.util.ArrayListExtended;
import cavern.util.BlockMeta;
import cavern.util.CaveFilters;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBiomesEditor extends GuiScreen
{
	protected final GuiScreen parent;
	protected final CaveBiomeManager manager;

	protected BiomeList biomeList;

	protected GuiButton doneButton;
	protected GuiButton editButton;
	protected GuiButton cancelButton;
	protected GuiButton addButton;
	protected GuiButton removeButton;
	protected GuiButton clearButton;

	protected GuiCheckBox detailInfo;
	protected GuiCheckBox instantFilter;

	protected GuiTextField filterTextField;

	protected boolean editMode;

	protected GuiTextField weightField;
	protected GuiTextField terrainBlockField;
	protected GuiTextField terrainBlockMetaField;
	protected GuiTextField topBlockField;
	protected GuiTextField topBlockMetaField;

	protected HoverChecker detailHoverChecker;
	protected HoverChecker instantHoverChecker;
	protected HoverChecker weightHoverChecker;
	protected HoverChecker terrainBlockHoverChecker;
	protected HoverChecker topBlockHoverChecker;

	protected int maxLabelWidth;

	protected final List<String> editLabelList = Lists.newArrayList();
	protected final List<GuiTextField> editFieldList = Lists.newArrayList();

	public GuiBiomesEditor(GuiScreen parent, CaveBiomeManager manager)
	{
		this.parent = parent;
		this.manager = manager;
	}

	@Override
	public void initGui()
	{
		if (biomeList == null)
		{
			biomeList = new BiomeList();

			refreshBiomes(manager.getCaveBiomes().values());
		}

		biomeList.setDimensions(width, height, 32, height - (editMode ? 95 : 28));

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
		editButton.enabled = biomeList.selected != null;
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
		editLabelList.add(I18n.format(Config.LANG_KEY + "biomes.weight"));
		editLabelList.add(I18n.format(Config.LANG_KEY + "biomes.terrainBlock"));
		editLabelList.add("");
		editLabelList.add(I18n.format(Config.LANG_KEY + "biomes.topBlock"));
		editLabelList.add("");

		for (String key : editLabelList)
		{
			maxLabelWidth = Math.max(maxLabelWidth, fontRenderer.getStringWidth(key));
		}

		if (weightField == null)
		{
			weightField = new GuiTextField(1, fontRenderer, 0, 0, 0, 15);
			weightField.setMaxStringLength(3);
		}

		int i = maxLabelWidth + 8 + width / 2;
		weightField.x = width / 2 - i / 2 + maxLabelWidth + 10;
		weightField.y = biomeList.bottom + 7;
		weightField.width = width / 2 + i / 2 - 45 - weightField.x + 40;

		if (terrainBlockField == null)
		{
			terrainBlockField = new GuiTextField(2, fontRenderer, 0, 0, 0, weightField.height);
			terrainBlockField.setMaxStringLength(100);
		}

		terrainBlockField.x = weightField.x;
		terrainBlockField.y = weightField.y + weightField.height + 5;
		terrainBlockField.width = weightField.width / 4 + weightField.width / 2 - 1;

		if (terrainBlockMetaField == null)
		{
			terrainBlockMetaField = new GuiTextField(3, fontRenderer, 0, 0, 0, terrainBlockField.height);
			terrainBlockMetaField.setMaxStringLength(100);
		}

		terrainBlockMetaField.x = terrainBlockField.x + terrainBlockField.width + 3;
		terrainBlockMetaField.y = terrainBlockField.y;
		terrainBlockMetaField.width = weightField.width / 4 - 1;

		if (topBlockField == null)
		{
			topBlockField = new GuiTextField(4, fontRenderer, 0, 0, 0, terrainBlockField.height);
			topBlockField.setMaxStringLength(100);
		}

		topBlockField.x = terrainBlockField.x;
		topBlockField.y = terrainBlockMetaField.y + terrainBlockMetaField.height + 5;
		topBlockField.width = terrainBlockField.width;

		if (topBlockMetaField == null)
		{
			topBlockMetaField = new GuiTextField(5, fontRenderer, 0, 0, 0, topBlockField.height);
			topBlockMetaField.setMaxStringLength(100);
		}

		topBlockMetaField.x = terrainBlockMetaField.x;
		topBlockMetaField.y = topBlockField.y;
		topBlockMetaField.width = terrainBlockMetaField.width;

		editFieldList.clear();

		if (editMode)
		{
			editFieldList.add(weightField);
			editFieldList.add(terrainBlockField);
			editFieldList.add(terrainBlockMetaField);
			editFieldList.add(topBlockField);
			editFieldList.add(topBlockMetaField);
		}

		weightHoverChecker = new HoverChecker(weightField.y - 1, weightField.y + weightField.height, weightField.x - maxLabelWidth - 12, weightField.x - 10, 800);
		terrainBlockHoverChecker = new HoverChecker(terrainBlockField.y - 1, terrainBlockField.y + terrainBlockField.height, terrainBlockField.x - maxLabelWidth - 12, terrainBlockField.x - 10, 800);
		topBlockHoverChecker = new HoverChecker(topBlockField.y - 1, topBlockField.y + topBlockField.height, topBlockField.x - maxLabelWidth - 12, topBlockField.x - 10, 800);
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
						for (CaveBiome caveBiome : biomeList.selected)
						{
							if (!Strings.isNullOrEmpty(weightField.getText()))
							{
								caveBiome.setWeight(NumberUtils.toInt(weightField.getText(), caveBiome.getWeight()));
							}

							if (!Strings.isNullOrEmpty(terrainBlockField.getText()))
							{
								Block block = Block.getBlockFromName(terrainBlockField.getText());

								if (block != null && block != Blocks.AIR)
								{
									int meta = BlockMeta.getMetaFromString(block, terrainBlockMetaField.getText());

									if (meta < 0)
									{
										meta = 0;
									}

									caveBiome.setTerrainBlock(new BlockMeta(block, meta));
								}
							}

							if (!Strings.isNullOrEmpty(topBlockField.getText()))
							{
								Block block = Block.getBlockFromName(topBlockField.getText());

								if (block != null && block != Blocks.AIR)
								{
									int meta = BlockMeta.getMetaFromString(block, topBlockMetaField.getText());

									if (meta < 0)
									{
										meta = 0;
									}

									caveBiome.setTopBlock(new BlockMeta(block, meta));
								}
							}
						}

						actionPerformed(cancelButton);

						biomeList.scrollToTop();
						biomeList.scrollToSelected();
					}
					else
					{
						manager.getCaveBiomes().clear();

						try
						{
							FileUtils.forceDelete(new File(manager.config.toString()));

							manager.config.load();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}

						Collections.sort(biomeList.biomes);

						CavernConfig.generateBiomesConfig(manager, biomeList.biomes);

						Config.saveConfig(manager.config);

						actionPerformed(cancelButton);

						biomeList.selected.clear();
						biomeList.scrollToTop();
					}

					break;
				case 1:
					if (editMode)
					{
						actionPerformed(cancelButton);
					}
					else if (!biomeList.selected.isEmpty())
					{
						editMode = true;
						initGui();

						biomeList.scrollToTop();
						biomeList.scrollToSelected();

						if (biomeList.selected.size() > 1)
						{
							weightField.setText("");
							terrainBlockField.setText("");
							terrainBlockMetaField.setText("");
							topBlockField.setText("");
							topBlockMetaField.setText("");
						}
						else for (CaveBiome biome : biomeList.selected)
						{
							if (biome != null)
							{
								weightField.setText(Integer.toString(biome.getWeight()));
								terrainBlockField.setText(biome.getTerrainBlock().getBlockName());
								terrainBlockMetaField.setText(biome.getTerrainBlock().getMetaString());
								topBlockField.setText(biome.getTopBlock().getBlockName());
								topBlockMetaField.setText(biome.getTopBlock().getMetaString());
							}
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
					Set<Biome> invisibleBiomes = biomeList.biomes.stream().map(CaveBiome::getBiome).collect(Collectors.toSet());

					mc.displayGuiScreen(new GuiSelectBiome(this, new ISelectorCallback<Biome>()
					{
						@Override
						public boolean isValidEntry(Biome entry)
						{
							return entry != null && !invisibleBiomes.contains(entry);
						}

						@Override
						public void onSelected(List<Biome> selected)
						{
							if (editMode)
							{
								return;
							}

							biomeList.selected.clear();

							for (Biome biome : selected)
							{
								CaveBiome caveBiome = new CaveBiome(biome, 10);

								biomeList.biomes.addIfAbsent(caveBiome);
								biomeList.contents.addIfAbsent(caveBiome);
								biomeList.selected.add(caveBiome);
							}

							biomeList.scrollToTop();
							biomeList.scrollToSelected();
						}
					}));

					break;
				case 4:
					for (CaveBiome biome : biomeList.selected)
					{
						biomeList.biomes.remove(biome);
						biomeList.contents.remove(biome);
					}

					biomeList.selected.clear();
					break;
				case 5:
					biomeList.biomes.forEach(entry -> biomeList.selected.add(entry));

					actionPerformed(removeButton);
					break;
				case 6:
					CaveConfigGui.detailInfo = detailInfo.isChecked();
					break;
				case 7:
					CaveConfigGui.instantFilter = instantFilter.isChecked();
					break;
				default:
					biomeList.actionPerformed(button);
			}
		}
	}

	@Override
	public void updateScreen()
	{
		if (editMode)
		{
			for (GuiTextField textField : editFieldList)
			{
				textField.updateCursorCounter();
			}
		}
		else
		{
			editButton.enabled = !biomeList.selected.isEmpty();
			removeButton.enabled = editButton.enabled;

			filterTextField.updateCursorCounter();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		biomeList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRenderer, I18n.format(Config.LANG_KEY + "biomes"), width / 2, 15, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, ticks);

		if (editMode)
		{
			GuiTextField textField;

			for (int i = 0, size = editFieldList.size(); i < size; ++i)
			{
				textField = editFieldList.get(i);
				textField.drawTextBox();

				drawString(fontRenderer, editLabelList.get(i), textField.x - maxLabelWidth - 10, textField.y + 3, 0xBBBBBB);
			}

			if (weightHoverChecker.checkHover(mouseX, mouseY))
			{
				List<String> hover = Lists.newArrayList();
				String key = Config.LANG_KEY + "biomes.weight";

				hover.add(TextFormatting.GRAY + I18n.format(key));
				hover.addAll(fontRenderer.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

				drawHoveringText(hover, mouseX, mouseY);
			}
			else if (terrainBlockHoverChecker.checkHover(mouseX, mouseY))
			{
				List<String> hover = Lists.newArrayList();
				String key = Config.LANG_KEY + "biomes.terrainBlock";

				hover.add(TextFormatting.GRAY + I18n.format(key));
				hover.addAll(fontRenderer.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

				drawHoveringText(hover, mouseX, mouseY);
			}
			else if (topBlockHoverChecker.checkHover(mouseX, mouseY))
			{
				List<String> hover = Lists.newArrayList();
				String key = Config.LANG_KEY + "biomes.topBlock";

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
		else if (biomeList.isMouseYWithinSlotBounds(mouseY) && isCtrlKeyDown())
		{
			CaveBiome caveBiome = biomeList.contents.get(biomeList.getSlotIndexFromScreenCoords(mouseX, mouseY), null);

			if (caveBiome != null)
			{
				List<String> info = Lists.newArrayList();
				String prefix = TextFormatting.GRAY.toString();

				info.add(prefix + I18n.format(Config.LANG_KEY + "biomes.weight") + ": " + caveBiome.getWeight());
				info.add(prefix + I18n.format(Config.LANG_KEY + "biomes.terrainBlock") + ": " + caveBiome.getTerrainBlock().getName());
				info.add(prefix + I18n.format(Config.LANG_KEY + "biomes.topBlock") + ": " + caveBiome.getTopBlock().getName());

				drawHoveringText(info, mouseX, mouseY);
			}
		}

		if (biomeList.selected.size() > 1 && mouseX <= 100 && mouseY <= 20)
		{
			drawString(fontRenderer, I18n.format(Config.LANG_KEY + "select.entry.selected", biomeList.selected.size()), 5, 5, 0xEFEFEF);
		}
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		biomeList.handleMouseInput();

		if (editMode)
		{
			if (weightField.isFocused())
			{
				int i = Mouse.getDWheel();

				if (i < 0)
				{
					weightField.setText(Integer.toString(Math.max(NumberUtils.toInt(weightField.getText()) - 1, 1)));
				}
				else if (i > 0)
				{
					weightField.setText(Integer.toString(Math.min(NumberUtils.toInt(weightField.getText()) + 1, 100)));
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
			for (GuiTextField textField : editFieldList)
			{
				textField.mouseClicked(x, y, code);
			}

			if (!isShiftKeyDown())
			{
				if (terrainBlockField.isFocused())
				{
					terrainBlockField.setFocused(false);

					mc.displayGuiScreen(new GuiSelectBlock(this, terrainBlockField, terrainBlockMetaField));
				}
				else if (topBlockField.isFocused())
				{
					topBlockField.setFocused(false);

					mc.displayGuiScreen(new GuiSelectBlock(this, topBlockField, topBlockMetaField));
				}
			}
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
				else if (textField.isFocused())
				{
					if (textField != terrainBlockField && textField != terrainBlockMetaField && textField != topBlockField && textField != topBlockMetaField)
					{
						if (!CharUtils.isAsciiControl(c) && !CharUtils.isAsciiNumeric(c))
						{
							continue;
						}
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
					biomeList.setFilter(null);
				}
				else if (instantFilter.isChecked() && changed || code == Keyboard.KEY_RETURN)
				{
					biomeList.setFilter(text);
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
					biomeList.selected.clear();
				}
				else if (code == Keyboard.KEY_TAB)
				{
					if (++biomeList.nameType > 1)
					{
						biomeList.nameType = 0;
					}
				}
				else if (code == Keyboard.KEY_UP)
				{
					biomeList.scrollUp();
				}
				else if (code == Keyboard.KEY_DOWN)
				{
					biomeList.scrollDown();
				}
				else if (code == Keyboard.KEY_HOME)
				{
					biomeList.scrollToTop();
				}
				else if (code == Keyboard.KEY_END)
				{
					biomeList.scrollToEnd();
				}
				else if (code == Keyboard.KEY_SPACE)
				{
					biomeList.scrollToSelected();
				}
				else if (code == Keyboard.KEY_PRIOR)
				{
					biomeList.scrollToPrev();
				}
				else if (code == Keyboard.KEY_NEXT)
				{
					biomeList.scrollToNext();
				}
				else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
				{
					filterTextField.setFocused(true);
				}
				else if (isCtrlKeyDown() && code == Keyboard.KEY_A)
				{
					biomeList.contents.forEach(entry -> biomeList.selected.add(entry));
				}
				else if (code == Keyboard.KEY_DELETE && !biomeList.selected.isEmpty())
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
		biomeList.currentPanoramaPaths = null;
	}

	public void refreshBiomes(Collection<CaveBiome> biomes)
	{
		biomeList.biomes.clear();
		biomeList.contents.clear();

		biomes.stream().sorted().forEach(biome ->
		{
			biomeList.biomes.addIfAbsent(biome);
			biomeList.contents.addIfAbsent(biome);
		});
	}

	protected class BiomeList extends GuiListSlot
	{
		protected final ArrayListExtended<CaveBiome> biomes = new ArrayListExtended<>();
		protected final ArrayListExtended<CaveBiome> contents = new ArrayListExtended<>();
		protected final Set<CaveBiome> selected = Sets.newTreeSet();

		protected final Map<String, List<CaveBiome>> filterCache = Maps.newHashMap();

		protected int nameType;
		protected boolean clickFlag;

		protected BiomeList()
		{
			super(GuiBiomesEditor.this.mc, 0, 0, 0, 0, 22);
		}

		@Override
		public void scrollToSelected()
		{
			if (!selected.isEmpty())
			{
				int amount = 0;

				for (CaveBiome biome : selected)
				{
					amount = contents.indexOf(biome) * getSlotHeight();

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
			CaveBiome caveBiome = contents.get(slot, null);

			if (caveBiome == null)
			{
				return;
			}

			Biome biome = caveBiome.getBiome();
			String text;

			switch (nameType)
			{
				case 1:
					text = biome.getRegistryName().toString();
					break;
				default:
					text = biome.getBiomeName();
					break;
			}

			if (!Strings.isNullOrEmpty(text))
			{
				drawCenteredString(fontRenderer, text, width / 2, par3 + 3, 0xFFFFFF);
			}

			if (detailInfo.isChecked() || Keyboard.isKeyDown(Keyboard.KEY_TAB))
			{
				drawItemStack(itemRender, caveBiome.getTerrainBlock(), width / 2 - 100, par3 + 1, fontRenderer, Integer.toString(Biome.getIdForBiome(biome)));
				drawItemStack(itemRender, caveBiome.getTopBlock(), width / 2 + 90, par3 + 1, fontRenderer, Integer.toString(caveBiome.getWeight()));
			}
		}

		@Override
		protected void elementClicked(int slot, boolean flag, int mouseX, int mouseY)
		{
			if (editMode)
			{
				return;
			}

			CaveBiome biome = contents.get(slot, null);

			if (biome != null && (clickFlag = !clickFlag == true) && !selected.remove(biome))
			{
				if (!isCtrlKeyDown())
				{
					selected.clear();
				}

				selected.add(biome);
			}
		}

		@Override
		protected boolean isSelected(int slot)
		{
			CaveBiome biome = contents.get(slot, null);

			return biome != null && selected.contains(biome);
		}

		protected void setFilter(String filter)
		{
			List<CaveBiome> result;

			if (Strings.isNullOrEmpty(filter))
			{
				result = biomes;
			}
			else if (filter.equals("selected"))
			{
				result = Lists.newArrayList(selected);
			}
			else
			{
				if (!filterCache.containsKey(filter))
				{
					filterCache.put(filter, biomes.parallelStream().filter(e -> filterMatch(e, filter)).collect(Collectors.toList()));
				}

				result = filterCache.get(filter);
			}

			if (!contents.equals(result))
			{
				contents.clear();
				contents.addAll(result);
			}
		}

		protected boolean filterMatch(CaveBiome entry, String filter)
		{
			return CaveFilters.biomeFilter(entry.getBiome(), filter) || entry.getWeight() == NumberUtils.toInt(filter, -1) ||
				CaveFilters.blockFilter(entry.getTerrainBlock(), filter) || CaveFilters.blockFilter(entry.getTopBlock(), filter);
		}
	}
}