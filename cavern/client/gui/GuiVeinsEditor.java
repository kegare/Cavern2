package cavern.client.gui;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cavern.client.config.CaveConfigGui;
import cavern.config.CavernConfig;
import cavern.config.Config;
import cavern.config.manager.CaveVein;
import cavern.config.manager.CaveVeinManager;
import cavern.util.ArrayListExtended;
import cavern.util.BlockMeta;
import cavern.util.CaveFilters;
import cavern.util.CaveUtils;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiVeinsEditor extends GuiScreen
{
	protected final GuiScreen parent;
	protected final CaveVeinManager manager;

	protected VeinList veinList;

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

	protected GuiTextField blockField;
	protected GuiTextField blockMetaField;
	protected GuiTextField targetField;
	protected GuiTextField targetMetaField;
	protected GuiTextField weightField;
	protected GuiTextField chanceField;
	protected GuiTextField sizeField;
	protected GuiTextField minHeightField;
	protected GuiTextField maxHeightField;
	protected GuiTextField biomesField;

	protected HoverChecker detailHoverChecker;
	protected HoverChecker instantHoverChecker;
	protected HoverChecker blockHoverChecker;
	protected HoverChecker targetHoverChecker;
	protected HoverChecker weightHoverChecker;
	protected HoverChecker chanceHoverChecker;
	protected HoverChecker sizeHoverChecker;
	protected HoverChecker heightHoverChecker;
	protected HoverChecker biomesHoverChecker;

	protected int maxLabelWidth;

	protected final List<String> editLabelList = Lists.newArrayList();
	protected final List<GuiTextField> editFieldList = Lists.newArrayList();

	public GuiVeinsEditor(GuiScreen parent, CaveVeinManager manager)
	{
		this.parent = parent;
		this.manager = manager;
	}

	@Override
	public void initGui()
	{
		if (veinList == null)
		{
			veinList = new VeinList();

			refreshVeins(manager.getCaveVeins());
		}

		veinList.setDimensions(width, height, 32, height - (editMode ? 170 : 28));

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
		editButton.enabled = veinList.selected != null;
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
		editLabelList.add(I18n.format(Config.LANG_KEY  + "veins.block"));
		editLabelList.add("");
		editLabelList.add(I18n.format(Config.LANG_KEY  + "veins.targetBlock"));
		editLabelList.add("");
		editLabelList.add(I18n.format(Config.LANG_KEY  + "veins.weight"));
		editLabelList.add(I18n.format(Config.LANG_KEY  + "veins.chance"));
		editLabelList.add(I18n.format(Config.LANG_KEY  + "veins.size"));
		editLabelList.add(I18n.format(Config.LANG_KEY  + "veins.height"));
		editLabelList.add("");
		editLabelList.add(I18n.format(Config.LANG_KEY  + "veins.biomes"));

		for (String key : editLabelList)
		{
			maxLabelWidth = Math.max(maxLabelWidth, fontRenderer.getStringWidth(key));
		}

		if (blockField == null)
		{
			blockField = new GuiTextField(1, fontRenderer, 0, 0, 0, 15);
			blockField.setMaxStringLength(100);
		}

		int i = maxLabelWidth + 8 + width / 2;
		blockField.x = width / 2 - i / 2 + maxLabelWidth + 10;
		blockField.y = veinList.bottom + 5;
		int fieldWidth = width / 2 + i / 2 - 45 - blockField.x + 40;
		blockField.width = fieldWidth / 4 + fieldWidth / 2 - 1;

		if (blockMetaField == null)
		{
			blockMetaField = new GuiTextField(2, fontRenderer, 0, 0, 0, blockField.height);
			blockMetaField.setMaxStringLength(100);
		}

		blockMetaField.x = blockField.x + blockField.width + 3;
		blockMetaField.y = blockField.y;
		blockMetaField.width = fieldWidth / 4 - 1;

		if (targetField == null)
		{
			targetField = new GuiTextField(3, fontRenderer, 0, 0, 0, blockField.height);
			targetField.setMaxStringLength(100);
		}

		targetField.x = blockField.x;
		targetField.y = blockField.y + blockField.height + 5;
		targetField.width = blockField.width;

		if (targetMetaField == null)
		{
			targetMetaField = new GuiTextField(4, fontRenderer, 0, 0, 0, blockField.height);
			targetMetaField.setMaxStringLength(100);
		}

		targetMetaField.x = targetField.x + targetField.width + 3;
		targetMetaField.y = targetField.y;
		targetMetaField.width = blockMetaField.width;

		if (weightField == null)
		{
			weightField = new GuiTextField(5, fontRenderer, 0, 0, 0, blockField.height);
			weightField.setMaxStringLength(3);
		}

		weightField.x = targetField.x;
		weightField.y = targetField.y + targetField.height + 5;
		weightField.width = fieldWidth;

		if (chanceField == null)
		{
			chanceField = new GuiTextField(6, fontRenderer, 0, 0, 0, blockField.height);
			chanceField.setMaxStringLength(3);
		}

		chanceField.x = weightField.x;
		chanceField.y = weightField.y + weightField.height + 5;
		chanceField.width = weightField.width;

		if (sizeField == null)
		{
			sizeField = new GuiTextField(7, fontRenderer, 0, 0, 0, blockField.height);
			sizeField.setMaxStringLength(3);
		}

		sizeField.x = chanceField.x;
		sizeField.y = chanceField.y + chanceField.height + 5;
		sizeField.width = chanceField.width;

		if (minHeightField == null)
		{
			minHeightField = new GuiTextField(8, fontRenderer, 0, 0, 0, blockField.height);
			minHeightField.setMaxStringLength(3);
		}

		minHeightField.x = sizeField.x;
		minHeightField.y = sizeField.y + sizeField.height + 5;
		minHeightField.width = fieldWidth / 2 - 1;

		if (maxHeightField == null)
		{
			maxHeightField = new GuiTextField(9, fontRenderer, 0, 0, 0, blockField.height);
			maxHeightField.setMaxStringLength(3);
		}

		maxHeightField.x = minHeightField.x + minHeightField.width + 3;
		maxHeightField.y = minHeightField.y;
		maxHeightField.width = minHeightField.width;

		if (biomesField == null)
		{
			biomesField = new GuiTextField(10, fontRenderer, 0, 0, 0, blockField.height);
			biomesField.setMaxStringLength(800);
		}

		biomesField.x = minHeightField.x;
		biomesField.y = minHeightField.y + minHeightField.height + 5;
		biomesField.width = sizeField.width;

		editFieldList.clear();

		if (editMode)
		{
			editFieldList.add(blockField);
			editFieldList.add(blockMetaField);
			editFieldList.add(targetField);
			editFieldList.add(targetMetaField);
			editFieldList.add(weightField);
			editFieldList.add(chanceField);
			editFieldList.add(sizeField);
			editFieldList.add(minHeightField);
			editFieldList.add(maxHeightField);
			editFieldList.add(biomesField);
		}

		blockHoverChecker = new HoverChecker(blockField.y - 1, blockField.y + blockField.height, blockField.x - maxLabelWidth - 12, blockField.x - 10, 800);
		targetHoverChecker = new HoverChecker(targetField.y - 1, targetField.y + targetField.height, targetField.x - maxLabelWidth - 12, targetField.x - 10, 800);
		weightHoverChecker = new HoverChecker(weightField.y - 1, weightField.y + weightField.height, weightField.x - maxLabelWidth - 12, weightField.x - 10, 800);
		chanceHoverChecker = new HoverChecker(chanceField.y - 1, chanceField.y + chanceField.height, chanceField.x - maxLabelWidth - 12, chanceField.x - 10, 800);
		sizeHoverChecker = new HoverChecker(sizeField.y - 1, sizeField.y + sizeField.height, sizeField.x - maxLabelWidth - 12, sizeField.x - 10, 800);
		heightHoverChecker = new HoverChecker(minHeightField.y - 1, minHeightField.y + minHeightField.height, minHeightField.x - maxLabelWidth - 12, minHeightField.x - 10, 800);
		biomesHoverChecker = new HoverChecker(biomesField.y - 1, biomesField.y + biomesField.height, biomesField.x - maxLabelWidth - 12, biomesField.x - 10, 800);
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
						for (CaveVein vein : veinList.selected)
						{
							if (!Strings.isNullOrEmpty(blockField.getText()))
							{
								Block block = Block.getBlockFromName(blockField.getText());

								if (block != null && block != Blocks.AIR)
								{
									int meta = BlockMeta.getMetaFromString(block, blockMetaField.getText());

									if (meta < 0)
									{
										meta = 0;
									}

									vein.setBlockMeta(new BlockMeta(block, meta));
								}
							}

							if (!Strings.isNullOrEmpty(targetField.getText()))
							{
								Block block = Block.getBlockFromName(targetField.getText());

								if (block != null && block != Blocks.AIR)
								{
									int meta = BlockMeta.getMetaFromString(block, targetMetaField.getText());

									if (meta < 0)
									{
										meta = 0;
									}

									vein.setTarget(new BlockMeta(block, meta));
								}
							}

							if (!Strings.isNullOrEmpty(weightField.getText()))
							{
								vein.setWeight(NumberUtils.toInt(weightField.getText(), vein.getWeight()));
							}

							if (!Strings.isNullOrEmpty(chanceField.getText()))
							{
								vein.setChance(NumberUtils.toDouble(chanceField.getText(), vein.getChance()));
							}

							if (!Strings.isNullOrEmpty(sizeField.getText()))
							{
								vein.setSize(NumberUtils.toInt(sizeField.getText(), vein.getSize()));
							}

							if (!Strings.isNullOrEmpty(minHeightField.getText()))
							{
								vein.setMinHeight(NumberUtils.toInt(minHeightField.getText(), vein.getMinHeight()));
							}

							if (!Strings.isNullOrEmpty(maxHeightField.getText()))
							{
								vein.setMaxHeight(NumberUtils.toInt(maxHeightField.getText(), vein.getMaxHeight()));
							}

							if (!Strings.isNullOrEmpty(biomesField.getText()))
							{
								String text = biomesField.getText();
								Set<String> biomes = Sets.newTreeSet();

								if (text.contains(","))
								{
									for (String str : Splitter.on(',').trimResults().omitEmptyStrings().split(text))
									{
										if (Strings.isNullOrEmpty(str))
										{
											biomes.add(str);
										}
									}
								}
								else
								{
									biomes.add(text);
								}

								if (!biomes.isEmpty())
								{
									vein.setBiomes(biomes.toArray(new String[biomes.size()]));
								}
							}
						}

						actionPerformed(cancelButton);

						veinList.scrollToTop();
						veinList.scrollToSelected();
					}
					else
					{
						manager.getCaveVeins().clear();

						try
						{
							FileUtils.forceDelete(new File(manager.config.toString()));

							manager.config.load();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}

						CavernConfig.generateVeinsConfig(manager, veinList.veins);

						Config.saveConfig(manager.config);

						actionPerformed(cancelButton);

						veinList.selected.clear();
						veinList.scrollToTop();
					}

					break;
				case 1:
					if (editMode)
					{
						actionPerformed(cancelButton);
					}
					else if (!veinList.selected.isEmpty())
					{
						editMode = true;
						initGui();

						veinList.scrollToTop();
						veinList.scrollToSelected();

						if (veinList.selected.size() > 1)
						{
							blockField.setText("");
							blockMetaField.setText("");
							targetField.setText("");
							targetMetaField.setText("");
							weightField.setText("");
							chanceField.setText("");
							sizeField.setText("");
							minHeightField.setText("");
							maxHeightField.setText("");
							biomesField.setText("");
						}
						else for (CaveVein vein : veinList.selected)
						{
							if (vein != null)
							{
								blockField.setText(vein.getBlockMeta().getBlockName());
								blockMetaField.setText(vein.getBlockMeta().getMetaString());
								targetField.setText(vein.getTarget().getBlockName());
								targetMetaField.setText(vein.getTarget().getMetaString());
								weightField.setText(Integer.toString(vein.getWeight()));
								chanceField.setText(Double.toString(vein.getChance()));
								sizeField.setText(Integer.toString(vein.getSize()));
								minHeightField.setText(Integer.toString(vein.getMinHeight()));
								maxHeightField.setText(Integer.toString(vein.getMaxHeight()));

								String[] biomes = vein.getBiomes();

								if (biomes == null || biomes.length <= 0)
								{
									biomesField.setText("");
								}
								else
								{
									biomesField.setText(Joiner.on(", ").join(biomes));
								}
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
					mc.displayGuiScreen(new GuiSelectBlock(this, selected ->
					{
						if (editMode)
						{
							return;
						}

						veinList.selected.clear();

						for (BlockMeta blockMeta : selected)
						{
							CaveVein vein = new CaveVein(blockMeta, 1, 1, 1, 255);

							veinList.veins.addIfAbsent(vein);
							veinList.contents.addIfAbsent(vein);
							veinList.selected.add(vein);
						}

						veinList.scrollToTop();
						veinList.scrollToSelected();
					}));

					break;
				case 4:
					for (CaveVein vein : veinList.selected)
					{
						veinList.veins.remove(vein);
						veinList.contents.remove(vein);
					}

					veinList.selected.clear();
					break;
				case 5:
					veinList.veins.forEach(entry -> veinList.selected.add(entry));

					actionPerformed(removeButton);
					break;
				case 6:
					CaveConfigGui.detailInfo = detailInfo.isChecked();
					break;
				case 7:
					CaveConfigGui.instantFilter = instantFilter.isChecked();
					break;
				default:
					veinList.actionPerformed(button);
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
			editButton.enabled = !veinList.selected.isEmpty();
			removeButton.enabled = editButton.enabled;

			filterTextField.updateCursorCounter();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		veinList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRenderer, I18n.format(Config.LANG_KEY + "veins"), width / 2, 15, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, ticks);

		if (editMode)
		{
			GuiTextField textField;

			for (int i = 0; i < editFieldList.size(); ++i)
			{
				textField = editFieldList.get(i);
				textField.drawTextBox();

				drawString(fontRenderer, editLabelList.get(i), textField.x - maxLabelWidth - 10, textField.y + 3, 0xBBBBBB);
			}

			if (blockHoverChecker.checkHover(mouseX, mouseY))
			{
				List<String> hover = Lists.newArrayList();
				String key = Config.LANG_KEY + "veins.block";

				hover.add(TextFormatting.GRAY + I18n.format(key));
				hover.addAll(fontRenderer.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

				drawHoveringText(hover, mouseX, mouseY);
			}
			else if (targetHoverChecker.checkHover(mouseX, mouseY))
			{
				List<String> hover = Lists.newArrayList();
				String key = Config.LANG_KEY + "veins.targetBlock";

				hover.add(TextFormatting.GRAY + I18n.format(key));
				hover.addAll(fontRenderer.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

				drawHoveringText(hover, mouseX, mouseY);
			}
			else if (weightHoverChecker.checkHover(mouseX, mouseY))
			{
				List<String> hover = Lists.newArrayList();
				String key = Config.LANG_KEY + "veins.weight";

				hover.add(TextFormatting.GRAY + I18n.format(key));
				hover.addAll(fontRenderer.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

				drawHoveringText(hover, mouseX, mouseY);
			}
			else if (chanceHoverChecker.checkHover(mouseX, mouseY))
			{
				List<String> hover = Lists.newArrayList();
				String key = Config.LANG_KEY + "veins.chance";

				hover.add(TextFormatting.GRAY + I18n.format(key));
				hover.addAll(fontRenderer.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

				drawHoveringText(hover, mouseX, mouseY);
			}
			else if (sizeHoverChecker.checkHover(mouseX, mouseY))
			{
				List<String> hover = Lists.newArrayList();
				String key = Config.LANG_KEY + "veins.size";

				hover.add(TextFormatting.GRAY + I18n.format(key));
				hover.addAll(fontRenderer.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

				drawHoveringText(hover, mouseX, mouseY);
			}
			else if (heightHoverChecker.checkHover(mouseX, mouseY))
			{
				List<String> hover = Lists.newArrayList();
				String key = Config.LANG_KEY + "veins.height";

				hover.add(TextFormatting.GRAY + I18n.format(key));
				hover.addAll(fontRenderer.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

				drawHoveringText(hover, mouseX, mouseY);
			}
			else if (biomesHoverChecker.checkHover(mouseX, mouseY))
			{
				List<String> hover = Lists.newArrayList();
				String key = Config.LANG_KEY + "veins.biomes";

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
		else if (veinList.isMouseYWithinSlotBounds(mouseY) && isCtrlKeyDown())
		{
			CaveVein vein = veinList.contents.get(veinList.getSlotIndexFromScreenCoords(mouseX, mouseY), null);

			if (vein != null)
			{
				List<String> info = Lists.newArrayList();
				String prefix = TextFormatting.GRAY.toString();

				info.add(prefix + I18n.format(Config.LANG_KEY + "veins.block") + ": " + vein.getBlockMeta().getName());
				info.add(prefix + I18n.format(Config.LANG_KEY + "veins.targetBlock") + ": " + vein.getTarget().getName());
				info.add(prefix + I18n.format(Config.LANG_KEY + "veins.weight") + ": " + vein.getWeight());
				info.add(prefix + I18n.format(Config.LANG_KEY + "veins.chance") + ": " + vein.getChance());
				info.add(prefix + I18n.format(Config.LANG_KEY + "veins.size") + ": " + vein.getSize());
				info.add(prefix + I18n.format(Config.LANG_KEY + "veins.height") + ": " + vein.getMinHeight() + ", " + vein.getMaxHeight());

				String[] biomes = vein.getBiomes();

				if (biomes != null && biomes.length > 0)
				{
					List<String> list = fontRenderer.listFormattedStringToWidth(I18n.format(Config.LANG_KEY + "veins.biomes") + ": " + Joiner.on(", ").join(biomes), 300);

					for (String text : list)
					{
						info.add(prefix + text);
					}
				}

				drawHoveringText(info, mouseX, mouseY);
			}
		}

		if (veinList.selected.size() > 1 && mouseX <= 100 && mouseY <= 20)
		{
			drawString(fontRenderer, I18n.format(Config.LANG_KEY + "select.entry.selected", veinList.selected.size()), 5, 5, 0xEFEFEF);
		}
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		veinList.handleMouseInput();

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
			else if (chanceField.isFocused())
			{
				int i = Mouse.getDWheel();

				if (i < 0)
				{
					chanceField.setText(Double.toString(Math.max(NumberUtils.toDouble(chanceField.getText()) - 0.1D, 0.0D)));
				}
				else if (i > 0)
				{
					chanceField.setText(Double.toString(Math.min(NumberUtils.toDouble(chanceField.getText()) + 0.1D, 1.0D)));
				}
			}
			else if (sizeField.isFocused())
			{
				int i = Mouse.getDWheel();

				if (i < 0)
				{
					sizeField.setText(Integer.toString(Math.max(NumberUtils.toInt(sizeField.getText()) - 1, 1)));
				}
				else if (i > 0)
				{
					sizeField.setText(Integer.toString(Math.min(NumberUtils.toInt(sizeField.getText()) + 1, 500)));
				}
			}
			else if (minHeightField.isFocused())
			{
				int i = Mouse.getDWheel();

				if (i < 0)
				{
					minHeightField.setText(Integer.toString(Math.max(NumberUtils.toInt(minHeightField.getText()) - 1, 0)));
				}
				else if (i > 0)
				{
					minHeightField.setText(Integer.toString(Math.min(NumberUtils.toInt(minHeightField.getText()) + 1, NumberUtils.toInt(maxHeightField.getText()) - 1)));
				}
			}
			else if (maxHeightField.isFocused())
			{
				int i = Mouse.getDWheel();

				if (i < 0)
				{
					maxHeightField.setText(Integer.toString(Math.max(NumberUtils.toInt(maxHeightField.getText()) - 1, NumberUtils.toInt(minHeightField.getText()) + 1)));
				}
				else if (i > 0)
				{
					maxHeightField.setText(Integer.toString(Math.min(NumberUtils.toInt(maxHeightField.getText()) + 1, 255)));
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
				if (blockField.isFocused())
				{
					blockField.setFocused(false);

					mc.displayGuiScreen(new GuiSelectBlock(this, blockField, blockMetaField));
				}
				else if (targetField.isFocused())
				{
					targetField.setFocused(false);

					mc.displayGuiScreen(new GuiSelectBlock(this, targetField, targetMetaField));
				}
				else if (biomesField.isFocused())
				{
					biomesField.setFocused(false);

					mc.displayGuiScreen(new GuiSelectBiome(this, biomesField));
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
					if (textField != blockField && textField != blockMetaField && textField != targetField && textField != targetMetaField && textField != biomesField)
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
					veinList.setFilter(null);
				}
				else if (instantFilter.isChecked() && changed || code == Keyboard.KEY_RETURN)
				{
					veinList.setFilter(text);
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
					veinList.selected.clear();
				}
				else if (code == Keyboard.KEY_TAB)
				{
					if (++veinList.nameType > 2)
					{
						veinList.nameType = 0;
					}
				}
				else if (code == Keyboard.KEY_UP)
				{
					if (isCtrlKeyDown())
					{
						Collections.sort(veinList.selected, veinList);

						for (CaveVein vein : veinList.selected)
						{
							veinList.contents.swapTo(veinList.contents.indexOf(vein), -1);
							veinList.veins.swapTo(veinList.veins.indexOf(vein), -1);
						}

						veinList.scrollToTop();
						veinList.scrollToSelected();
					}
					else
					{
						veinList.scrollUp();
					}
				}
				else if (code == Keyboard.KEY_DOWN)
				{
					if (isCtrlKeyDown())
					{
						Collections.sort(veinList.selected, veinList);
						Collections.reverse(veinList.selected);

						for (CaveVein vein : veinList.selected)
						{
							veinList.contents.swapTo(veinList.contents.indexOf(vein), 1);
							veinList.veins.swapTo(veinList.veins.indexOf(vein), 1);
						}

						veinList.scrollToTop();
						veinList.scrollToSelected();
					}
					else
					{
						veinList.scrollDown();
					}
				}
				else if (code == Keyboard.KEY_HOME)
				{
					veinList.scrollToTop();
				}
				else if (code == Keyboard.KEY_END)
				{
					veinList.scrollToEnd();
				}
				else if (code == Keyboard.KEY_SPACE)
				{
					veinList.scrollToSelected();
				}
				else if (code == Keyboard.KEY_PRIOR)
				{
					veinList.scrollToPrev();
				}
				else if (code == Keyboard.KEY_NEXT)
				{
					veinList.scrollToNext();
				}
				else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
				{
					filterTextField.setFocused(true);
				}
				else if (isCtrlKeyDown() && code == Keyboard.KEY_A)
				{
					veinList.contents.forEach(entry -> veinList.selected.add(entry));
				}
				else if (code == Keyboard.KEY_DELETE && !veinList.selected.isEmpty())
				{
					actionPerformed(removeButton);
				}
				else if (code == Keyboard.KEY_C && isCtrlKeyDown())
				{
					Collections.sort(veinList.selected, veinList);

					veinList.copied.clear();

					for (CaveVein entry : veinList.selected)
					{
						veinList.copied.add(new CaveVein(entry));
					}
				}
				else if (code == Keyboard.KEY_X && isCtrlKeyDown())
				{
					keyTyped(Character.MIN_VALUE, Keyboard.KEY_C);

					actionPerformed(removeButton);
				}
				else if (code == Keyboard.KEY_V && isCtrlKeyDown() && !veinList.copied.isEmpty())
				{
					int index1 = -1;
					int index2 = -1;
					int i = 0;

					for (CaveVein vein : veinList.copied)
					{
						CaveVein entry = new CaveVein(vein);

						if (veinList.veins.add(entry) && veinList.contents.add(entry) && !veinList.selected.isEmpty())
						{
							if (index1 < 0)
							{
								index1 = veinList.contents.indexOf(veinList.selected.get(veinList.selected.size() - 1)) + 1;
							}

							Collections.swap(veinList.contents, index1 + i, veinList.contents.indexOf(entry));

							if (index2 < 0)
							{
								index2 = veinList.veins.indexOf(veinList.selected.get(veinList.selected.size() - 1)) + 1;
							}

							Collections.swap(veinList.veins, index2 + i, veinList.veins.indexOf(entry));

							++i;
						}
					}

					veinList.scrollToTop();
					veinList.scrollToSelected();
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
		veinList.currentPanoramaPaths = null;
	}

	public void refreshVeins(Collection<CaveVein> veins)
	{
		veinList.veins.clear();
		veinList.contents.clear();

		for (CaveVein vein : veins)
		{
			veinList.veins.addIfAbsent(vein);
			veinList.contents.addIfAbsent(vein);
		}
	}

	protected class VeinList extends GuiListSlot implements Comparator<CaveVein>
	{
		protected final ArrayListExtended<CaveVein> veins = new ArrayListExtended<>();
		protected final ArrayListExtended<CaveVein> contents = new ArrayListExtended<>();
		protected final List<CaveVein> selected = Lists.newArrayList();
		protected final List<CaveVein> copied = Lists.newArrayList();

		protected final Map<String, List<CaveVein>> filterCache = Maps.newHashMap();

		protected int nameType;
		protected boolean clickFlag;

		public VeinList()
		{
			super(GuiVeinsEditor.this.mc, 0, 0, 0, 0, 22);
		}

		@Override
		public void scrollToSelected()
		{
			if (!selected.isEmpty())
			{
				int amount = 0;

				for (CaveVein vein : selected)
				{
					amount = contents.indexOf(vein) * getSlotHeight();

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
			CaveVein vein = contents.get(slot, null);

			if (vein == null)
			{
				return;
			}

			BlockMeta blockMeta = vein.getBlockMeta();
			Block block = blockMeta.getBlock();
			int meta = blockMeta.getMeta();
			ItemStack stack = new ItemStack(block, 1, meta);
			boolean hasItem = stack.getItem() != Items.AIR;

			String text = null;

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

			if (!Strings.isNullOrEmpty(text))
			{
				drawCenteredString(fontRenderer, text, width / 2, par3 + 3, 0xFFFFFF);
			}

			if (detailInfo.isChecked())
			{
				drawItemStack(itemRender, blockMeta, width / 2 - 100, par3 + 1, fontRenderer, Integer.toString(vein.getSize()));
				drawItemStack(itemRender, vein.getTarget(), width / 2 + 90, par3 + 1, fontRenderer, Integer.toString(vein.getWeight()));
			}
		}

		@Override
		protected void elementClicked(int slot, boolean flag, int mouseX, int mouseY)
		{
			if (editMode)
			{
				return;
			}

			CaveVein vein = contents.get(slot, null);

			if (vein != null && (clickFlag = !clickFlag == true) && !selected.remove(vein))
			{
				if (!isCtrlKeyDown())
				{
					selected.clear();
				}

				selected.add(vein);
			}
		}

		@Override
		protected boolean isSelected(int slot)
		{
			CaveVein vein = contents.get(slot, null);

			return vein != null && selected.contains(vein);
		}

		@Override
		public int compare(CaveVein o1, CaveVein o2)
		{
			int i = CaveUtils.compareWithNull(o1, o2);

			if (i == 0 && o1 != null && o2 != null)
			{
				i = Integer.compare(veins.indexOf(o1), veins.indexOf(o2));
			}

			return i;
		}

		protected void setFilter(String filter)
		{
			List<CaveVein> result;

			if (Strings.isNullOrEmpty(filter))
			{
				result = veins;
			}
			else if (filter.equals("selected"))
			{
				result = selected;
			}
			else
			{
				if (!filterCache.containsKey(filter))
				{
					filterCache.put(filter, veins.parallelStream().filter(e -> filterMatch(e, filter)).collect(Collectors.toList()));
				}

				result = filterCache.get(filter);
			}

			if (!contents.equals(result))
			{
				contents.clear();
				contents.addAll(result);
			}
		}

		protected boolean filterMatch(CaveVein vein, String filter)
		{
			if (CaveFilters.blockFilter(vein.getBlockMeta(), filter) || CaveFilters.blockFilter(vein.getTarget(), filter))
			{
				return true;
			}

			for (Biome biome : vein.getBiomeList())
			{
				if (CaveFilters.biomeFilter(biome, filter))
				{
					return true;
				}
			}

			return false;
		}
	}
}