package cavern.network.client;

import cavern.client.gui.GuiRegeneration;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RegenerationGuiMessage implements IClientMessage<RegenerationGuiMessage, IMessage>
{
	private int type;

	public RegenerationGuiMessage() {}

	public RegenerationGuiMessage(EnumType type)
	{
		this.type = type.ordinal();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		type = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(type);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage process(Minecraft mc)
	{
		EnumType actionType = EnumType.values()[type];
		boolean isOpen = mc.currentScreen != null && mc.currentScreen instanceof GuiRegeneration;

		if (actionType == EnumType.OPEN)
		{
			if (!isOpen)
			{
				mc.displayGuiScreen(new GuiRegeneration());
			}
		}
		else if (isOpen)
		{
			((GuiRegeneration)mc.currentScreen).updateProgress(actionType);
		}

		return null;
	}

	public enum EnumType
	{
		OPEN,
		START,
		BACKUP,
		REGENERATED,
		FAILED
	}
}