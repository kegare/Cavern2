package cavern.network;

import cavern.core.Cavern;
import cavern.network.client.CustomSeedMessage;
import cavern.network.client.ExplosionMessage;
import cavern.network.client.FallTeleportMessage;
import cavern.network.client.MagicCancelMessage;
import cavern.network.client.MinerDataMessage;
import cavern.network.client.MiningMessage;
import cavern.network.client.MiningRecordsGuiMessage;
import cavern.network.client.MiningRecordsMessage;
import cavern.network.client.MirageSelectMessage;
import cavern.network.client.RegenerationGuiMessage;
import cavern.network.client.ToastMessage;
import cavern.network.server.MagicBookMessage;
import cavern.network.server.MagicInvisibleMessage;
import cavern.network.server.MagicResultMessage;
import cavern.network.server.MiningAssistMessage;
import cavern.network.server.MirageTeleportMessage;
import cavern.network.server.RegenerationMessage;
import cavern.network.server.SpecialMagicMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class CaveNetworkRegistry
{
	public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Cavern.MODID);

	public static int messageId;

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side)
	{
		NETWORK.registerMessage(messageHandler, requestMessageType, messageId++, side);
	}

	public static void sendToAll(IMessage message)
	{
		NETWORK.sendToAll(message);
	}

	public static void sendTo(IMessage message, EntityPlayerMP player)
	{
		NETWORK.sendTo(message, player);
	}

	public static void sendToDimension(IMessage message, int dimensionId)
	{
		NETWORK.sendToDimension(message, dimensionId);
	}

	public static void sendToServer(IMessage message)
	{
		NETWORK.sendToServer(message);
	}

	public static void registerMessages()
	{
		registerMessage(CustomSeedMessage.class, CustomSeedMessage.class, Side.CLIENT);
		registerMessage(MiningMessage.class, MiningMessage.class, Side.CLIENT);
		registerMessage(MinerDataMessage.class, MinerDataMessage.class, Side.CLIENT);
		registerMessage(MiningRecordsMessage.class, MiningRecordsMessage.class, Side.CLIENT);
		registerMessage(RegenerationGuiMessage.class, RegenerationGuiMessage.class, Side.CLIENT);
		registerMessage(MiningRecordsGuiMessage.class, MiningRecordsGuiMessage.class, Side.CLIENT);
		registerMessage(ToastMessage.class, ToastMessage.class, Side.CLIENT);
		registerMessage(MirageSelectMessage.class, MirageSelectMessage.class, Side.CLIENT);
		registerMessage(MagicCancelMessage.class, MagicCancelMessage.class, Side.CLIENT);
		registerMessage(ExplosionMessage.class, ExplosionMessage.class, Side.CLIENT);
		registerMessage(FallTeleportMessage.class, FallTeleportMessage.class, Side.CLIENT);

		registerMessage(RegenerationMessage.class, RegenerationMessage.class, Side.SERVER);
		registerMessage(MiningAssistMessage.class, MiningAssistMessage.class, Side.SERVER);
		registerMessage(MirageTeleportMessage.class, MirageTeleportMessage.class, Side.SERVER);
		registerMessage(MagicBookMessage.class, MagicBookMessage.class, Side.SERVER);
		registerMessage(MagicResultMessage.class, MagicResultMessage.class, Side.SERVER);
		registerMessage(MagicInvisibleMessage.class, MagicInvisibleMessage.class, Side.SERVER);
		registerMessage(SpecialMagicMessage.class, SpecialMagicMessage.class, Side.SERVER);
	}
}