package cavern.client.gui;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cavern.client.config.CaveConfigGui;
import cavern.config.Config;
import cavern.util.CaveFilters;
import cavern.util.CaveUtils;
import cavern.util.PanoramaPaths;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSelectBiome extends GuiScreen
{
	protected final GuiScreen parent;

	protected ISelectorCallback<Biome> selectorCallback;

	protected GuiTextField biomeField;

	protected BiomeList biomeList;

	protected GuiButton doneButton;

	protected GuiCheckBox detailInfo;
	protected GuiCheckBox instantFilter;

	protected GuiTextField filterTextField;

	protected HoverChecker selectedHoverChecker;
	protected HoverChecker detailHoverChecker;
	protected HoverChecker instantHoverChecker;

	public GuiSelectBiome(GuiScreen parent)
	{
		this.parent = parent;
	}

	public GuiSelectBiome(GuiScreen parent, @Nullable ISelectorCallback<Biome> callback)
	{
		this(parent);
		this.selectorCallback = callback;
	}

	public GuiSelectBiome(GuiScreen parent, @Nullable GuiTextField biomeField)
	{
		this(parent);
		this.biomeField = biomeField;
	}

	@Override
	public void initGui()
	{
		if (biomeList == null)
		{
			biomeList = new BiomeList();
		}

		biomeList.setDimensions(width, height, 32, height - 28);

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
						selectorCallback.onSelected(ImmutableList.copyOf(biomeList.selected));
					}

					if (biomeList.selected.isEmpty())
					{
						if (biomeField != null)
						{
							biomeField.setText("");
						}
					}
					else
					{
						Set<String> biomes = Sets.newTreeSet();

						for (Biome biome : biomeList.selected)
						{
							biomes.add(biome.getRegistryName().toString());
						}

						if (!biomes.isEmpty())
						{
							if (biomeField != null)
							{
								biomeField.setText(Joiner.on(", ").join(biomes));
							}
						}
					}

					if (biomeField != null)
					{
						biomeField.setFocused(true);
						biomeField.setCursorPositionEnd();
					}

					mc.displayGuiScreen(parent);

					biomeList.selected.clear();
					biomeList.scrollToTop();
					break;
				case 1:
					CaveConfigGui.detailInfo = detailInfo.isChecked();
					break;
				case 2:
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
		super.updateScreen();

		filterTextField.updateCursorCounter();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		biomeList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRenderer, I18n.format(Config.LANG_KEY + "select.biome"), width / 2, 15, 0xFFFFFF);

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
		else if (biomeList.isMouseYWithinSlotBounds(mouseY) && isCtrlKeyDown())
		{
			Biome biome = biomeList.contents.get(biomeList.getSlotIndexFromScreenCoords(mouseX, mouseY));
			List<String> info = Lists.newArrayList();

			info.add(biome.getBiomeName() + TextFormatting.DARK_GRAY + "   " + biome.getRegistryName().toString());

			IBlockState state = biome.topBlock;
			Block block = state.getBlock();
			int meta = block.getMetaFromState(state);
			ItemStack stack = new ItemStack(block, 1, meta);
			boolean hasItem = stack.getItem() != Items.AIR;

			String text;

			if (hasItem)
			{
				text = stack.getDisplayName();
			}
			else
			{
				text = block.getRegistryName() + ":" + meta;
			}

			info.add(TextFormatting.GRAY + I18n.format(Config.LANG_KEY + "select.biome.info.topBlock") + ": " + text);

			state = biome.fillerBlock;
			block = state.getBlock();
			meta = block.getMetaFromState(state);
			stack = new ItemStack(block, 1, meta);
			hasItem = stack.getItem() != Items.AIR;

			if (hasItem)
			{
				text = stack.getDisplayName();
			}
			else
			{
				text = block.getRegistryName() + ":" + meta;;
			}

			info.add(TextFormatting.GRAY + I18n.format(Config.LANG_KEY + "select.biome.info.fillerBlock") + ": " + text);

			info.add(TextFormatting.GRAY + I18n.format(Config.LANG_KEY + "select.biome.info.temperature") + ": " + biome.getDefaultTemperature());
			info.add(TextFormatting.GRAY + I18n.format(Config.LANG_KEY + "select.biome.info.rainfall") + ": " + biome.getRainfall());

			if (BiomeDictionary.hasAnyType(biome))
			{
				Set<String> types = Sets.newTreeSet();

				for (Type type : BiomeDictionary.getTypes(biome))
				{
					types.add(type.getName());
				}

				info.add(TextFormatting.GRAY + I18n.format(Config.LANG_KEY + "select.biome.info.type") + ": " + Joiner.on(", ").skipNulls().join(types));
			}

			drawHoveringText(info, mouseX, mouseY);
		}

		if (!biomeList.selected.isEmpty())
		{
			if (mouseX <= 100 && mouseY <= 20)
			{
				drawString(fontRenderer, I18n.format(Config.LANG_KEY + "select.biome.selected", biomeList.selected.size()), 5, 5, 0xEFEFEF);
			}

			if (selectedHoverChecker.checkHover(mouseX, mouseY))
			{
				drawHoveringText(biomeList.selected.stream().map(Biome::getBiomeName).collect(Collectors.toList()), mouseX, mouseY);
			}
		}
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		biomeList.handleMouseInput();
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
				mc.displayGuiScreen(parent);
			}
			else if (code == Keyboard.KEY_BACK)
			{
				biomeList.selected.clear();
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
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	protected class BiomeList extends GuiListSlot implements Comparator<Biome>
	{
		protected final NonNullList<Biome> biomes = NonNullList.create();
		protected final NonNullList<Biome> contents = NonNullList.create();
		protected final Set<Biome> selected = Sets.newTreeSet(this);
		protected final Map<String, List<Biome>> filterCache = Maps.newHashMap();

		protected boolean clickFlag;

		protected BiomeList()
		{
			super(GuiSelectBiome.this.mc, 0, 0, 0, 0, 18);

			for (Biome biome : ForgeRegistries.BIOMES.getValuesCollection())
			{
				if (selectorCallback == null || selectorCallback.isValidEntry(biome))
				{
					biomes.add(biome);
					contents.add(biome);
				}
			}

			setSelectedBiomes();

			if (!selected.isEmpty())
			{
				scrollToTop();
				scrollToSelected();
			}
		}

		protected void setSelectedBiomes()
		{
			if (biomeField == null)
			{
				return;
			}

			String text = biomeField.getText();

			if (Strings.isNullOrEmpty(text))
			{
				return;
			}

			if (text.contains(","))
			{
				for (String str : Splitter.on(',').trimResults().omitEmptyStrings().split(text))
				{
					Biome biome = Config.getBiomeFromString(str);

					if (biome != null)
					{
						selected.add(biome);
					}
				}
			}
			else
			{
				Biome biome = Config.getBiomeFromString(text);

				if (biome != null)
				{
					selected.add(biome);
				}
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

				for (Biome biome : selected)
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
			Biome biome = contents.get(slot);
			boolean isTabDown = Keyboard.isKeyDown(Keyboard.KEY_TAB);

			if (isTabDown)
			{
				drawCenteredString(fontRenderer, biome.getRegistryName().toString(), width / 2, par3 + 1, 0xE0E0E0);
			}
			else
			{
				drawCenteredString(fontRenderer, biome.getBiomeName(), width / 2, par3 + 1, 0xFFFFFF);
			}

			if (detailInfo.isChecked() && isTabDown)
			{
				drawString(fontRenderer, Integer.toString(Biome.getIdForBiome(biome)), width / 2 - 100, par3 + 1, 0xFFFFFF);

				drawItemStack(itemRender, biome.topBlock, width / 2 + 70, par3 - 1);
				drawItemStack(itemRender, biome.fillerBlock, width / 2 + 90, par3 - 1);
			}
		}

		@Override
		protected void elementClicked(int slot, boolean flag, int mouseX, int mouseY)
		{
			Biome biome = contents.get(slot);

			if ((clickFlag = !clickFlag == true) && !selected.add(biome))
			{
				selected.remove(biome);
			}
		}

		@Override
		protected boolean isSelected(int slot)
		{
			return selected.contains(contents.get(slot));
		}

		@Override
		public int compare(Biome o1, Biome o2)
		{
			int i = CaveUtils.compareWithNull(o1, o2);

			if (i == 0 && o1 != null && o2 != null)
			{
				i = Integer.compare(biomes.indexOf(o1), biomes.indexOf(o2));
			}

			return i;
		}

		protected void setFilter(String filter)
		{
			List<Biome> result;

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

		protected boolean filterMatch(Biome biome, String filter)
		{
			return CaveFilters.biomeFilter(biome, filter);
		}
	}
}