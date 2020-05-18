package cavern.client.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLoadCaveTerrain extends GuiDownloadCaveTerrain
{
	private boolean loading;
	private int progress;

	private boolean renderWorker;

	@Override
	public String getInfoText()
	{
		if (progress > 200 && progress <= 300)
		{
			return I18n.format("cavern.terrain.almost");
		}

		return I18n.format("cavern.terrain.load");
	}

	public boolean isTerrainLoaded()
	{
		if (mc.getRenderViewEntity() == null)
		{
			return true;
		}

		if (!mc.getRenderViewEntity().addedToChunk)
		{
			renderWorker = true;
		}
		else if (renderWorker)
		{
			return true;
		}

		return false;
	}

	@Override
	public void updateScreen()
	{
		if (!isTerrainLoaded())
		{
			loading = renderWorker;
		}
		else if (loading)
		{
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}

		if (++progress > 400)
		{
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}

		super.updateScreen();
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
}