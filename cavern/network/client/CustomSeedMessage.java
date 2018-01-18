package cavern.network.client;

import cavern.world.CustomSeedData;
import cavern.world.ICustomSeed;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CustomSeedMessage implements IClientMessage<CustomSeedMessage, IMessage>
{
	private long seed;

	public CustomSeedMessage() {}

	public CustomSeedMessage(long seed)
	{
		this.seed = seed;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		seed = buf.readLong();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(seed);
	}

	@Override
	public IMessage process(Minecraft mc)
	{
		if (mc.world.provider instanceof ICustomSeed)
		{
			CustomSeedData data = ((ICustomSeed)mc.world.provider).getSeedData();

			if (data != null)
			{
				data.setSeed(seed);
			}
		}

		return null;
	}
}