package cavern.client.gui;

import java.io.IOException;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Strings;

import cavern.client.CaveKeyBindings;
import cavern.client.config.CaveConfigGui;
import cavern.config.Config;
import cavern.stats.MinerRank;
import cavern.stats.MinerStats;
import cavern.util.ArrayListExtended;
import cavern.util.BlockMeta;
import cavern.util.PanoramaPaths;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMiningRecords extends GuiScreen
{
	protected RecordList recordList;

	protected GuiButton doneButton;

	protected GuiCheckBox detailInfo;

	protected HoverChecker detailHoverChecker;
	protected HoverChecker selectedHoverChecker;

	@Override
	public void initGui()
	{
		if (recordList == null)
		{
			recordList = new RecordList();
		}

		recordList.setDimensions(width, height, 32, height - 28);

		if (doneButton == null)
		{
			doneButton = new GuiButtonExt(0, 0, 0, 145, 20, I18n.format("gui.done"));
		}

		doneButton.x = width / 2 + 10;
		doneButton.y = height - doneButton.height - 4;

		if (detailInfo == null)
		{
			detailInfo = new GuiCheckBox(1, 0, 12, I18n.format(Config.LANG_KEY + "detail"), true);
		}

		detailInfo.setIsChecked(CaveConfigGui.detailInfo);
		detailInfo.x = width / 2 + 95;

		buttonList.clear();
		buttonList.add(doneButton);
		buttonList.add(detailInfo);

		detailHoverChecker = new HoverChecker(detailInfo, 800);
		selectedHoverChecker = new HoverChecker(0, 20, 0, 100, 800);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			switch (button.id)
			{
				case 0:
					mc.displayGuiScreen(null);
					break;
				case 1:
					CaveConfigGui.detailInfo = detailInfo.isChecked();
					break;
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		recordList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRenderer, I18n.format("cavern.miningrecords.gui.title"), width / 2, 15, 0xFFFFFF);
		drawString(fontRenderer, String.format("%s: %s (%d)", I18n.format("cavern.miningrecords.gui.score"), recordList.getScoreRank(), recordList.score), width / 2 - 155, height - 18, 0xEEEEEE);

		super.drawScreen(mouseX, mouseY, ticks);

		if (detailHoverChecker.checkHover(mouseX, mouseY))
		{
			drawHoveringText(fontRenderer.listFormattedStringToWidth(I18n.format(Config.LANG_KEY + "detail.hover"), 300), mouseX, mouseY);
		}
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		recordList.handleMouseInput();
	}

	@Override
	protected void keyTyped(char c, int code) throws IOException
	{
		if (code == Keyboard.KEY_ESCAPE || code == CaveKeyBindings.KEY_MINING_RECORDS.getKeyCode())
		{
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}
		else if (code == Keyboard.KEY_TAB)
		{
			if (++recordList.nameType > 2)
			{
				recordList.nameType = 0;
			}
		}
		else if (code == Keyboard.KEY_UP)
		{
			recordList.scrollUp();
		}
		else if (code == Keyboard.KEY_DOWN)
		{
			recordList.scrollDown();
		}
		else if (code == Keyboard.KEY_HOME)
		{
			recordList.scrollToTop();
		}
		else if (code == Keyboard.KEY_END)
		{
			recordList.scrollToEnd();
		}
		else if (code == Keyboard.KEY_PRIOR)
		{
			recordList.scrollToPrev();
		}
		else if (code == Keyboard.KEY_NEXT)
		{
			recordList.scrollToNext();
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	protected class RecordList extends GuiListSlot
	{
		protected final ArrayListExtended<MiningRecord> entries = new ArrayListExtended<>();

		protected int nameType;
		protected int totalCount;
		protected int score;

		protected RecordList()
		{
			super(GuiMiningRecords.this.mc, 0, 0, 0, 0, 18);

			for (Entry<BlockMeta, Integer> entry : MinerStats.get(GuiMiningRecords.this.mc.player).getMiningRecords().entrySet())
			{
				BlockMeta blockMeta = entry.getKey();
				int count = entry.getValue().intValue();

				entries.addIfAbsent(new MiningRecord(blockMeta, count));

				totalCount += count;

				int amount = MinerStats.getPointAmount(blockMeta.getBlock(), blockMeta.getMeta());

				if (count > 1)
				{
					amount += (count - 1) * Math.max(amount / 2, 1);
				}

				score += amount;
			}

			entries.sort(null);
		}

		@Override
		public PanoramaPaths getPanoramaPaths()
		{
			return null;
		}

		@Override
		public void scrollToSelected() {}

		@Override
		protected int getSize()
		{
			return entries.size();
		}

		@Nullable
		public String getBlockName(@Nullable BlockMeta blockMeta, ItemStack stack)
		{
			if (blockMeta == null)
			{
				return null;
			}

			if (stack.isEmpty())
			{
				stack = new ItemStack(blockMeta.getBlock(), 1, blockMeta.getMeta());
			}

			String name = null;

			if (nameType == 1)
			{
				name = blockMeta.getName();
			}
			else if (stack.getItem() != Items.AIR)
			{
				switch (nameType)
				{
					case 2:
						name = stack.getUnlocalizedName();
						name = name.substring(name.indexOf(".") + 1);
						break;
					default:
						name = stack.getDisplayName();
						break;
				}
			}
			else switch (nameType)
			{
				case 2:
					name = blockMeta.getBlock().getUnlocalizedName();
					name = name.substring(name.indexOf(".") + 1);
					break;
				default:
					name = blockMeta.getBlock().getLocalizedName();
					break;
			}

			return name;
		}

		@Nullable
		public String getBlockName(@Nullable BlockMeta blockMeta)
		{
			return getBlockName(blockMeta, ItemStack.EMPTY);
		}

		@Override
		protected void drawBackground()
		{
			drawDefaultBackground();
		}

		@Override
		protected void drawSlot(int slot, int par2, int par3, int par4, int mouseX, int mouseY, float partialTicks)
		{
			MiningRecord record = entries.get(slot, null);

			if (record == null)
			{
				return;
			}

			String name = getBlockName(record.blockMeta);

			if (!Strings.isNullOrEmpty(name))
			{
				drawCenteredString(fontRenderer, name, width / 2, par3 + 1, 0xFFFFFF);
			}

			drawItemStack(itemRender, record.blockMeta, width / 2 - 100, par3 - 1, fontRenderer, Integer.toString(record.count));

			if (detailInfo.isChecked())
			{
				MinerRank rank;

				switch (slot)
				{
					case 0:
						rank = MinerRank.DIAMOND_MINER;
						break;
					case 1:
						rank = MinerRank.GOLD_MINER;
						break;
					case 2:
						rank = MinerRank.IRON_MINER;
						break;
					default:
						if (record.count > 1)
						{
							rank = MinerRank.STONE_MINER;
						}
						else
						{
							rank = MinerRank.BEGINNER;
						}
				}

				double d = (double)record.count / (double)totalCount * 100.0D;

				drawItemStack(itemRender, rank.getItemStack(), width / 2 + 90, par3 - 1, fontRenderer, String.format("%.2f", d) + "%");
			}
		}

		@Override
		protected void elementClicked(int slot, boolean flag, int mouseX, int mouseY) {}

		@Override
		protected boolean isSelected(int slot)
		{
			return false;
		}

		protected String getScoreRank()
		{
			if (score >= 100000)
			{
				return "SS";
			}

			if (score >= 50000)
			{
				return "S";
			}

			if (score >= 10000)
			{
				return "A";
			}

			if (score >= 5000)
			{
				return "B";
			}

			if (score >= 3000)
			{
				return "C";
			}

			if (score >= 1000)
			{
				return "D";
			}

			return "E";
		}
	}

	protected class MiningRecord implements Comparable<MiningRecord>
	{
		protected BlockMeta blockMeta;
		protected int count;

		protected MiningRecord(BlockMeta blockMeta, int count)
		{
			this.blockMeta = blockMeta;
			this.count = count;
		}

		@Override
		public boolean equals(Object obj)
		{
			return blockMeta.equals(obj);
		}

		@Override
		public int hashCode()
		{
			return blockMeta.hashCode();
		}

		@Override
		public int compareTo(MiningRecord record)
		{
			int i = Integer.compare(record.count, count);

			if (i == 0)
			{
				i = blockMeta.compareTo(record.blockMeta);
			}

			return i;
		}
	}
}