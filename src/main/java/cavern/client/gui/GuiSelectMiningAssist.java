package cavern.client.gui;

import java.io.IOException;
import java.util.Comparator;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.ImmutableList;

import cavern.miningassist.MiningAssist;
import cavern.network.CaveNetworkRegistry;
import cavern.network.server.MiningAssistMessage;
import cavern.util.PanoramaPaths;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSelectMiningAssist extends GuiScreen
{
	protected ModeList modeList;

	protected GuiButton doneButton;

	@Override
	public void initGui()
	{
		if (modeList == null)
		{
			modeList = new ModeList();
		}

		modeList.setDimensions(width, height, 32, height - 28);

		if (doneButton == null)
		{
			doneButton = new GuiButtonExt(0, 0, 0, 145, 20, I18n.format("gui.done"));
		}

		doneButton.x = width / 2 - doneButton.width / 2;
		doneButton.y = height - doneButton.height - 4;

		buttonList.clear();
		buttonList.add(doneButton);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled && button.id == 0)
		{
			MiningAssist select = modeList.selected;

			if (select != null && select != MiningAssist.byPlayer(mc.player))
			{
				CaveNetworkRegistry.NETWORK.sendToServer(new MiningAssistMessage(select));
			}

			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		modeList.drawScreen(mouseX, mouseY, partialTicks);

		drawCenteredString(fontRenderer, I18n.format("cavern.miningassist.select"), width / 2, 15, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		modeList.handleMouseInput();
	}

	@Override
	protected void keyTyped(char c, int code) throws IOException
	{
		if (code == Keyboard.KEY_ESCAPE)
		{
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}
		else if (code == Keyboard.KEY_UP)
		{
			int i = modeList.selected.getType() - 1;

			if (i < 0)
			{
				i = modeList.types.size() - 1;
			}

			modeList.selected = MiningAssist.get(i);
		}
		else if (code == Keyboard.KEY_DOWN || code == Keyboard.KEY_TAB)
		{
			modeList.selected = MiningAssist.get(modeList.selected.getType() + 1);
		}
		else if (code == Keyboard.KEY_HOME)
		{
			modeList.scrollToTop();
		}
		else if (code == Keyboard.KEY_END)
		{
			modeList.scrollToEnd();
		}
		else if (code == Keyboard.KEY_SPACE)
		{
			modeList.scrollToSelected();
		}
		else if (code == Keyboard.KEY_PRIOR)
		{
			modeList.scrollToPrev();
		}
		else if (code == Keyboard.KEY_NEXT)
		{
			modeList.scrollToNext();
		}
		else if (code == Keyboard.KEY_RETURN)
		{
			actionPerformed(doneButton);
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	protected class ModeList extends GuiListSlot implements Comparator<MiningAssist>
	{
		protected final ImmutableList<MiningAssist> types = ImmutableList.copyOf(MiningAssist.VALUES);

		protected boolean clickFlag;
		protected MiningAssist selected;

		protected ModeList()
		{
			super(GuiSelectMiningAssist.this.mc, 0, 0, 0, 0, 18);

			selected = MiningAssist.byPlayer(GuiSelectMiningAssist.this.mc.player);
		}

		@Override
		public PanoramaPaths getPanoramaPaths()
		{
			return null;
		}

		@Override
		public void scrollToSelected()
		{
			if (selected != null)
			{
				int amount = types.indexOf(selected) * getSlotHeight();

				if (getAmountScrolled() != amount)
				{
					return;
				}

				scrollToTop();
				scrollBy(amount);
			}
		}

		@Override
		protected int getSize()
		{
			return types.size();
		}

		@Override
		protected void drawBackground()
		{
			drawDefaultBackground();
		}

		@Override
		protected void drawSlot(int slot, int par2, int par3, int par4, int mouseX, int mouseY, float partialTicks)
		{
			drawCenteredString(fontRenderer, I18n.format(types.get(slot).getUnlocalizedName()), width / 2, par3 + 1, 0xFFFFFF);
		}

		@Override
		protected void elementClicked(int slot, boolean flag, int mouseX, int mouseY)
		{
			if (clickFlag = !clickFlag == true)
			{
				selected = types.get(slot);
			}
		}

		@Override
		protected boolean isSelected(int slot)
		{
			return selected == types.get(slot);
		}

		@Override
		public int compare(MiningAssist o1, MiningAssist o2)
		{
			return Integer.compare(o1.getType(), o2.getType());
		}
	}
}