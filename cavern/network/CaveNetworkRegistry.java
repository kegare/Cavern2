package cavern.network;

import cavern.core.Cavern;
import cavern.network.client.CustomSeedMessage;
import cavern.network.client.MinerStatsAdjustMessage;
import cavern.network.client.MiningRecordMessage;
import cavern.network.client.MiningRecordsGuiMessage;
import cavern.network.client.MirageSelectMessage;
import cavern.network.client.RegenerationGuiMessage;
import cavern.network.client.ToastMessage;
import cavern.network.server.MineBonusMessage;
import cavern.network.server.MiningAssistMessage;
import cavern.network.server.MirageTeleportMessage;
import cavern.network.server.RegenerationMessage;
import cavern.network.server.StatsAdjustRequestMessage;
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
		registerMessage(MinerStatsAdjustMessage.class, MinerStatsAdjustMessage.class, Side.CLIENT);
		registerMessage(MiningRecordMessage.class, MiningRecordMessage.class, Side.CLIENT);
		registerMessage(RegenerationGuiMessage.class, RegenerationGuiMessage.class, Side.CLIENT);
		registerMessage(MiningRecordsGuiMessage.class, MiningRecordsGuiMessage.class, Side.CLIENT);
		registerMessage(ToastMessage.class, ToastMessage.class, Side.CLIENT);
		registerMessage(MirageSelectMessage.class, MirageSelectMessage.class, Side.CLIENT);

		registerMessage(MineBonusMessage.class, MineBonusMessage.class, Side.SERVER);
		registerMessage(RegenerationMessage.class, RegenerationMessage.class, Side.SERVER);
		registerMessage(MiningAssistMessage.class, MiningAssistMessage.class, Side.SERVER);
		registerMessage(StatsAdjustRequestMessage.class, StatsAdjustRequestMessage.class, Side.SERVER);
		registerMessage(MirageTeleportMessage.class, MirageTeleportMessage.class, Side.SERVER);
	}
}