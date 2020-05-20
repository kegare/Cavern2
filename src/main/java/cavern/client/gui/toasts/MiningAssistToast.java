package cavern.client.gui.toasts;

import org.lwjgl.input.Keyboard;

import cavern.block.BlockCave;
import cavern.client.CaveKeyBindings;
import cavern.config.MiningAssistConfig;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MiningAssistToast extends CaveToast
{
	private boolean pressMessage;

	@Override
	public Visibility draw(GuiToast toastGui, long delta)
	{
		Visibility result = super.draw(toastGui, delta);

		if (result == Visibility.SHOW && !pressMessage)
		{
			toastGui.getMinecraft().ingameGUI.setOverlayMessage(I18n.format("cavern.miningassist.toggle.press", Keyboard.getKeyName(CaveKeyBindings.KEY_MINING_ASSIST.getKeyCode())), false);

			pressMessage = true;
		}

		return result;
	}

	@Override
	protected int getTextureIndex()
	{
		return 32;
	}

	@Override
	protected ITextComponent getTitle()
	{
		return new TextComponentTranslation("cavern.toast.ability.title");
	}

	@Override
	protected int getTitleColor()
	{
		return 0x2C094F;
	}

	@Override
	protected ITextComponent getDescription()
	{
		ITextComponent component = new TextComponentTranslation("cavern.miningassist.name");
		component.getStyle().setColor(TextFormatting.BLACK);

		return component;
	}

	@Override
	protected ItemStack getIconItemStack(long delta)
	{
		if (delta < 1000L)
		{
			return MiningAssistConfig.minerRank.getRank().getItemStack();
		}

		if (delta < 2000L)
		{
			return new ItemStack(Blocks.COAL_ORE);
		}

		if (delta < 3000L)
		{
			return new ItemStack(Blocks.IRON_ORE);
		}

		if (delta < 4000L)
		{
			return BlockCave.EnumType.AQUAMARINE_ORE.getItemStack();
		}

		if (delta < 5000L)
		{
			return BlockCave.EnumType.MAGNITE_ORE.getItemStack();
		}

		return BlockCave.EnumType.HEXCITE_ORE.getItemStack();
	}
}