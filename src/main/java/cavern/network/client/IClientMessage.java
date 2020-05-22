package cavern.network.client;

import cavern.network.CaveNetworkRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface IClientMessage<REQ extends IClientMessage<REQ, REPLY>, REPLY extends IMessage> extends IMessage, IMessageHandler<REQ, REPLY>
{
	@Override
	default void fromBytes(ByteBuf buf) {}

	@Override
	default void toBytes(ByteBuf buf) {}

	REPLY process(Minecraft mc);

	@Override
	default REPLY onMessage(final REQ message, final MessageContext ctx)
	{
		IThreadListener thread = FMLCommonHandler.instance().getWorldThread(ctx.netHandler);
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (thread.isCallingFromMinecraftThread())
		{
			return message.process(mc);
		}

		thread.addScheduledTask(() ->
		{
			final REPLY result = message.process(mc);

			if (result != null)
			{
				CaveNetworkRegistry.NETWORK.sendToServer(result);
			}
		});

		return null;
	}
}