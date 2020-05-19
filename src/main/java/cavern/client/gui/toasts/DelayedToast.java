package cavern.client.gui.toasts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DelayedToast
{
	private final IToast toast;
	private final long delayedTime;

	public DelayedToast(IToast toast, long delay)
	{
		this.toast = toast;
		this.delayedTime = Minecraft.getSystemTime() + delay;
	}

	public IToast getToast()
	{
		return toast;
	}

	public long getDelayedTime()
	{
		return delayedTime;
	}

	public boolean onUpdate()
	{
		if (Minecraft.getSystemTime() > delayedTime)
		{
			FMLClientHandler.instance().getClient().getToastGui().add(toast);

			return false;
		}

		return true;
	}
}