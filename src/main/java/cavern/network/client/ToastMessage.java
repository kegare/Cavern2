package cavern.network.client;

import cavern.client.gui.toasts.DelayedToast;
import cavern.client.gui.toasts.MiningAssistToast;
import cavern.client.handler.ClientEventHooks;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ToastMessage implements ISimpleMessage<ToastMessage, IMessage>
{
	private String key;

	public ToastMessage() {}

	public ToastMessage(String key)
	{
		this.key = key;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		key = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, key);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage process()
	{
		switch (key)
		{
			case "mining_assist":
				ClientEventHooks.DELAYED_TOAST.add(new DelayedToast(new MiningAssistToast(), 10000L));
				break;
		}

		return null;
	}
}