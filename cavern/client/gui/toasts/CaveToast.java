package cavern.client.gui.toasts;

import java.util.List;

import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class CaveToast implements IToast
{
	@Override
	public Visibility draw(GuiToast toastGui, long delta)
	{
		toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		toastGui.drawTexturedModalRect(0, 0, 0, getTextureIndex(), 160, 32);

		List<String> list = toastGui.getMinecraft().fontRenderer.listFormattedStringToWidth(getDescription().getFormattedText(), 125);
		int i = getTitleColor();

		if (list.size() == 1)
		{
			toastGui.getMinecraft().fontRenderer.drawString(getTitle().getUnformattedText(), 30, 7, i | -16777216);
			toastGui.getMinecraft().fontRenderer.drawString(getDescription().getFormattedText(), 30, 18, -1);
		}
		else
		{
			if (delta < 1500L)
			{
				int j = MathHelper.floor(MathHelper.clamp((1500L - delta) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;

				toastGui.getMinecraft().fontRenderer.drawString(getTitle().getUnformattedText(), 30, 11, i | j);
			}
			else
			{
				int j = MathHelper.floor(MathHelper.clamp((delta - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
				int y = 16 - list.size() * toastGui.getMinecraft().fontRenderer.FONT_HEIGHT / 2;

				for (String s : list)
				{
					toastGui.getMinecraft().fontRenderer.drawString(s, 30, y, 16777215 | j);

					y += toastGui.getMinecraft().fontRenderer.FONT_HEIGHT;
				}
			}
		}

		RenderHelper.enableGUIStandardItemLighting();
		toastGui.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(null, getIconItemStack(delta), 8, 8);

		return delta >= 5000L ? Visibility.HIDE : Visibility.SHOW;
	}

	protected abstract int getTextureIndex();

	protected abstract ITextComponent getTitle();

	protected abstract int getTitleColor();

	protected abstract ITextComponent getDescription();

	protected ItemStack getIconItemStack(long delta)
	{
		return ItemStack.EMPTY;
	}
}