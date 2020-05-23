package cavern.network;

import java.util.function.Supplier;

import javax.annotation.Nullable;

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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class CaveNetworkRegistry
{
	public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Cavern.MODID);

	private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, int id, Side side)
	{
		NETWORK.registerMessage(messageHandler, requestMessageType, id, side);
	}

	public static void sendTo(Supplier<IMessage> message, @Nullable EntityPlayer player)
	{
		if (player != null && player instanceof EntityPlayerMP && !(player instanceof FakePlayer))
		{
			NETWORK.sendTo(message.get(), (EntityPlayerMP)player);
		}
	}

	public static void registerMessages()
	{
		int id = 0;

		registerMessage(CustomSeedMessage.class, CustomSeedMessage.class, id++, Side.CLIENT);
		registerMessage(MiningMessage.class, MiningMessage.class, id++, Side.CLIENT);
		registerMessage(MinerDataMessage.class, MinerDataMessage.class, id++, Side.CLIENT);
		registerMessage(MiningRecordsMessage.class, MiningRecordsMessage.class, id++, Side.CLIENT);
		registerMessage(RegenerationGuiMessage.class, RegenerationGuiMessage.class, id++, Side.CLIENT);
		registerMessage(MiningRecordsGuiMessage.class, MiningRecordsGuiMessage.class, id++, Side.CLIENT);
		registerMessage(ToastMessage.class, ToastMessage.class, id++, Side.CLIENT);
		registerMessage(MirageSelectMessage.class, MirageSelectMessage.class, id++, Side.CLIENT);
		registerMessage(MagicCancelMessage.class, MagicCancelMessage.class, id++, Side.CLIENT);
		registerMessage(ExplosionMessage.class, ExplosionMessage.class, id++, Side.CLIENT);
		registerMessage(FallTeleportMessage.class, FallTeleportMessage.class, id++, Side.CLIENT);

		registerMessage(RegenerationMessage.class, RegenerationMessage.class, id++, Side.SERVER);
		registerMessage(MiningAssistMessage.class, MiningAssistMessage.class, id++, Side.SERVER);
		registerMessage(MirageTeleportMessage.class, MirageTeleportMessage.class, id++, Side.SERVER);
		registerMessage(MagicBookMessage.class, MagicBookMessage.class, id++, Side.SERVER);
		registerMessage(MagicResultMessage.class, MagicResultMessage.class, id++, Side.SERVER);
		registerMessage(MagicInvisibleMessage.class, MagicInvisibleMessage.class, id++, Side.SERVER);
		registerMessage(SpecialMagicMessage.class, SpecialMagicMessage.class, id++, Side.SERVER);
	}
}