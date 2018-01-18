package cavern.client.gui;

import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;

import cavern.item.CaveItems;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.RegenerationGuiMessage.EnumType;
import cavern.network.server.RegenerationMessage;
import cavern.world.CaveDimensions;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRegeneration extends GuiScreen
{
	private static boolean backup = true;

	public final List<DimensionType> dimensions = Lists.newArrayList();

	protected GuiButton regenButton;
	protected GuiButton cancelButton;

	protected GuiCheckBox backupCheckBox, cavernCheckBox, hugeCavernCheckBox, aquaCavernCheckBox,
		cavelandCheckBox, caveniaCheckBox, frostMountainsCheckBox, wideDesertCheckBox, theVoidCheckBox, darkForestCheckBox;

	private HoverChecker backupHoverChecker;

	@Override
	public void initGui()
	{
		if (regenButton == null)
		{
			regenButton = new GuiButtonExt(0, 0, 0, I18n.format("cavern.regeneration.gui.regenerate"));
		}

		regenButton.x = width / 2 - 100;
		regenButton.y = height / 4 + regenButton.height + 65;

		if (cancelButton == null)
		{
			cancelButton = new GuiButtonExt(1, 0, 0, I18n.format("gui.cancel"));
		}

		cancelButton.x = regenButton.x;
		cancelButton.y = regenButton.y + regenButton.height + 5;

		if (backupCheckBox == null)
		{
			backupCheckBox = new GuiCheckBox(2, 10, 0, I18n.format("cavern.regeneration.gui.backup"), backup);
		}

		backupCheckBox.y = height - 20;

		if (cavernCheckBox == null)
		{
			cavernCheckBox = new GuiCheckBox(3, 10, 8, I18n.format("dimension.cavern.name"), dimensions.contains(CaveDimensions.CAVERN));
		}

		if (CaveDimensions.CAVERN == null)
		{
			cavernCheckBox.enabled = false;
			cavernCheckBox.setIsChecked(false);
		}

		GuiButton before = cavernCheckBox;

		if (hugeCavernCheckBox == null)
		{
			hugeCavernCheckBox = new GuiCheckBox(4, 10, before.y + before.height + 5, I18n.format("dimension.hugeCavern.name"), dimensions.contains(CaveDimensions.HUGE_CAVERN));
		}

		if (CaveDimensions.HUGE_CAVERN == null)
		{
			hugeCavernCheckBox.enabled = false;
			hugeCavernCheckBox.setIsChecked(false);
		}

		before = hugeCavernCheckBox;

		if (aquaCavernCheckBox == null)
		{
			aquaCavernCheckBox = new GuiCheckBox(5, 10, before.y + before.height + 5, I18n.format("dimension.aquaCavern.name"), dimensions.contains(CaveDimensions.AQUA_CAVERN));
		}

		if (CaveDimensions.AQUA_CAVERN == null)
		{
			aquaCavernCheckBox.enabled = false;
			aquaCavernCheckBox.setIsChecked(false);
		}

		before = aquaCavernCheckBox;

		String mirage = CaveItems.MIRAGE_BOOK.getUnlocalizedName();

		if (cavelandCheckBox == null)
		{
			cavelandCheckBox = new GuiCheckBox(6, 10, before.y + before.height + 5, I18n.format(mirage + ".caveland.name"), dimensions.contains(CaveDimensions.CAVELAND));
		}

		if (CaveDimensions.CAVELAND == null)
		{
			cavelandCheckBox.enabled = false;
			cavelandCheckBox.setIsChecked(false);
		}

		before = cavelandCheckBox;

		if (caveniaCheckBox == null)
		{
			caveniaCheckBox = new GuiCheckBox(7, 10, before.y + before.height + 5, I18n.format(mirage + ".cavenia.name"), dimensions.contains(CaveDimensions.CAVENIA));
		}

		if (CaveDimensions.CAVENIA == null)
		{
			caveniaCheckBox.enabled = false;
			caveniaCheckBox.setIsChecked(false);
		}

		before = caveniaCheckBox;

		if (frostMountainsCheckBox == null)
		{
			frostMountainsCheckBox = new GuiCheckBox(8, 10, before.y + before.height + 5, I18n.format(mirage + ".frostMountains.name"), dimensions.contains(CaveDimensions.FROST_MOUNTAINS));
		}

		if (CaveDimensions.FROST_MOUNTAINS == null)
		{
			frostMountainsCheckBox.enabled = false;
			frostMountainsCheckBox.setIsChecked(false);
		}

		before = frostMountainsCheckBox;

		if (wideDesertCheckBox == null)
		{
			wideDesertCheckBox = new GuiCheckBox(9, 10, before.y + before.height + 5, I18n.format(mirage + ".wideDesert.name"), dimensions.contains(CaveDimensions.WIDE_DESERT));
		}

		if (CaveDimensions.WIDE_DESERT == null)
		{
			wideDesertCheckBox.enabled = false;
			wideDesertCheckBox.setIsChecked(false);
		}

		before = wideDesertCheckBox;

		if (theVoidCheckBox == null)
		{
			theVoidCheckBox = new GuiCheckBox(10, 10, before.y + before.height + 5, I18n.format(mirage + ".theVoid.name"), dimensions.contains(CaveDimensions.THE_VOID));
		}

		if (CaveDimensions.THE_VOID == null)
		{
			theVoidCheckBox.enabled = false;
			theVoidCheckBox.setIsChecked(false);
		}

		before = theVoidCheckBox;

		if (darkForestCheckBox == null)
		{
			darkForestCheckBox = new GuiCheckBox(10, 10, before.y + before.height + 5, I18n.format(mirage + ".darkForest.name"), dimensions.contains(CaveDimensions.DARK_FOREST));
		}

		if (CaveDimensions.DARK_FOREST == null)
		{
			darkForestCheckBox.enabled = false;
			darkForestCheckBox.setIsChecked(false);
		}

		before = darkForestCheckBox;

		buttonList.clear();
		buttonList.add(regenButton);
		buttonList.add(cancelButton);
		buttonList.add(backupCheckBox);
		buttonList.add(cavernCheckBox);
		buttonList.add(hugeCavernCheckBox);
		buttonList.add(aquaCavernCheckBox);
		buttonList.add(cavelandCheckBox);
		buttonList.add(caveniaCheckBox);
		buttonList.add(frostMountainsCheckBox);
		buttonList.add(wideDesertCheckBox);
		buttonList.add(theVoidCheckBox);
		buttonList.add(darkForestCheckBox);

		if (backupHoverChecker == null)
		{
			backupHoverChecker = new HoverChecker(backupCheckBox, 800);
		}
	}

	@Override
	protected void keyTyped(char c, int code)
	{
		if (code == Keyboard.KEY_ESCAPE)
		{
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
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
					RegenerationMessage message = new RegenerationMessage();

					message.backup = backupCheckBox.isChecked();

					if (cavernCheckBox.isChecked())
					{
						message.dimensions.add(CaveDimensions.CAVERN);
					}

					if (hugeCavernCheckBox.isChecked())
					{
						message.dimensions.add(CaveDimensions.HUGE_CAVERN);
					}

					if (aquaCavernCheckBox.isChecked())
					{
						message.dimensions.add(CaveDimensions.AQUA_CAVERN);
					}

					if (cavelandCheckBox.isChecked())
					{
						message.dimensions.add(CaveDimensions.CAVELAND);
					}

					if (caveniaCheckBox.isChecked())
					{
						message.dimensions.add(CaveDimensions.CAVENIA);
					}

					if (frostMountainsCheckBox.isChecked())
					{
						message.dimensions.add(CaveDimensions.FROST_MOUNTAINS);
					}

					if (wideDesertCheckBox.isChecked())
					{
						message.dimensions.add(CaveDimensions.WIDE_DESERT);
					}

					if (theVoidCheckBox.isChecked())
					{
						message.dimensions.add(CaveDimensions.THE_VOID);
					}

					if (darkForestCheckBox.isChecked())
					{
						message.dimensions.add(CaveDimensions.DARK_FOREST);
					}

					if (!message.dimensions.isEmpty())
					{
						CaveNetworkRegistry.sendToServer(message);

						regenButton.enabled = false;
						cancelButton.visible = false;
					}

					break;
				case 1:
					mc.displayGuiScreen(null);
					mc.setIngameFocus();
					break;
				case 2:
					backup = backupCheckBox.isChecked();
					break;
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		drawGradientRect(0, 0, width, height, 0, Integer.MAX_VALUE);

		GlStateManager.pushMatrix();
		GlStateManager.scale(2.0F, 2.0F, 2.0F);
		drawCenteredString(fontRenderer, I18n.format("cavern.regeneration.gui.title"), width / 4, 30, 0xFFFFFF);
		GlStateManager.popMatrix();

		drawCenteredString(fontRenderer, I18n.format("cavern.regeneration.gui.info"), width / 2, 100, 0xEEEEEE);

		super.drawScreen(mouseX, mouseY, ticks);

		if (backupHoverChecker.checkHover(mouseX, mouseY))
		{
			drawHoveringText(fontRenderer.listFormattedStringToWidth(I18n.format("cavern.regeneration.gui.backup.tooltip"), 300), mouseX, mouseY);
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	public void updateProgress(EnumType type)
	{
		regenButton.enabled = false;
		cancelButton.visible = false;

		if (type == null)
		{
			regenButton.visible = false;
			cancelButton.visible = true;
		}
		else switch (type)
		{
			case START:
				regenButton.displayString = I18n.format("cavern.regeneration.gui.progress.start");
				break;
			case BACKUP:
				regenButton.displayString = I18n.format("cavern.regeneration.gui.progress.backup");
				break;
			case REGENERATED:
				regenButton.displayString = I18n.format("cavern.regeneration.gui.progress.regenerated");
				cancelButton.displayString = I18n.format("gui.done");
				cancelButton.visible = true;
				break;
			case FAILED:
				regenButton.displayString = I18n.format("cavern.regeneration.gui.progress.failed");
				cancelButton.visible = true;
				break;
			default:
		}
	}
}