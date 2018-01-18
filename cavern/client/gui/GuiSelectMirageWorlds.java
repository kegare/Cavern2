package cavern.client.gui;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cavern.block.CaveBlocks;
import cavern.item.ItemMirageBook;
import cavern.item.ItemMirageBook.EnumType;
import cavern.network.CaveNetworkRegistry;
import cavern.network.server.MirageTeleportMessage;
import cavern.util.PanoramaPaths;
import cavern.world.CaveDimensions;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSelectMirageWorlds extends GuiScreen
{
	protected WorldList worldList;

	protected GuiButton doneButton;

	public final Set<DimensionType> dimensions = Sets.newHashSet();

	@Override
	public void initGui()
	{
		if (worldList == null)
		{
			worldList = new WorldList();
		}

		worldList.setDimensions(width, height, 32, height - 28);

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
			if (worldList.selected != null)
			{
				CaveNetworkRegistry.sendToServer(new MirageTeleportMessage(worldList.selected.getLeft()));
			}

			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}
	}

	@Override
	public void updateScreen()
	{
		if (worldList.selected == null)
		{
			doneButton.enabled = true;
		}
		else if (!dimensions.isEmpty())
		{
			doneButton.enabled = dimensions.contains(worldList.selected.getLeft());
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		worldList.drawScreen(mouseX, mouseY, partialTicks);

		drawCenteredString(fontRenderer, I18n.format(CaveBlocks.MIRAGE_PORTAL.getUnlocalizedName() + ".select"), width / 2, 15, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		worldList.handleMouseInput();
	}

	@Override
	protected void keyTyped(char c, int code) throws IOException
	{
		if (code == Keyboard.KEY_ESCAPE)
		{
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}
		else if (code == Keyboard.KEY_BACK)
		{
			worldList.selected = null;
		}
		else if (code == Keyboard.KEY_UP)
		{
			worldList.scrollUp();
		}
		else if (code == Keyboard.KEY_DOWN)
		{
			worldList.scrollDown();
		}
		else if (code == Keyboard.KEY_HOME)
		{
			worldList.scrollToTop();
		}
		else if (code == Keyboard.KEY_END)
		{
			worldList.scrollToEnd();
		}
		else if (code == Keyboard.KEY_SPACE)
		{
			worldList.scrollToSelected();
		}
		else if (code == Keyboard.KEY_PRIOR)
		{
			worldList.scrollToPrev();
		}
		else if (code == Keyboard.KEY_NEXT)
		{
			worldList.scrollToNext();
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

	protected class WorldList extends GuiListSlot implements Comparator<Pair<DimensionType, ItemStack>>
	{
		protected final List<Pair<DimensionType, ItemStack>> types = Lists.newArrayList();

		protected boolean clickFlag;
		protected Pair<DimensionType, ItemStack> selected;

		protected WorldList()
		{
			super(GuiSelectMirageWorlds.this.mc, 0, 0, 0, 0, 18);

			DimensionType select = null;

			for (ItemStack stack : mc.player.getHeldEquipment())
			{
				if (!stack.isEmpty() && stack.getItem() instanceof ItemMirageBook)
				{
					select = EnumType.byItemStack(stack).getDimension();

					if (select != null)
					{
						break;
					}
				}
			}

			loadEntries(mc.player.inventory.mainInventory, select);
			loadEntries(mc.player.inventory.offHandInventory, select);

			types.sort(this);
		}

		public void loadEntries(NonNullList<ItemStack> list, @Nullable DimensionType select)
		{
			Set<DimensionType> dimensions = Sets.newHashSet();

			for (ItemStack stack : list)
			{
				if (!stack.isEmpty() && stack.getItem() instanceof ItemMirageBook)
				{
					DimensionType type = EnumType.byItemStack(stack).getDimension();

					if (type != null && dimensions.add(type))
					{
						Pair<DimensionType, ItemStack> entry = Pair.of(type, stack);

						types.add(entry);

						if (select != null && type == select)
						{
							selected = entry;
						}
					}
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
			Pair<DimensionType, ItemStack> entry = types.get(slot);
			DimensionType type = entry.getLeft();
			ItemStack stack = entry.getRight();

			drawCenteredString(fontRenderer, CaveDimensions.getLocalizedName(type), width / 2, par3 + 1, 0xFFFFFF);

			drawItemStack(itemRender, stack, width / 2 - 100, par3 - 1);
		}

		@Override
		protected void elementClicked(int slot, boolean flag, int mouseX, int mouseY)
		{
			Pair<DimensionType, ItemStack> entry = types.get(slot);

			if (clickFlag = !clickFlag == true)
			{
				selected = selected == entry ? null : entry;
			}
		}

		@Override
		protected boolean isSelected(int slot)
		{
			Pair<DimensionType, ItemStack> entry = types.get(slot);

			return selected == entry;
		}

		@Override
		public int compare(Pair<DimensionType, ItemStack> o1, Pair<DimensionType, ItemStack> o2)
		{
			return Integer.compareUnsigned(o2.getLeft().getId(), o1.getLeft().getId());
		}
	}
}