package cavern.network.client;

import cavern.world.CustomSeedData;
import cavern.world.CustomSeedProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage process(Minecraft mc)
	{
		if (mc.world.provider instanceof CustomSeedProvider)
		{
			CustomSeedData data = ((CustomSeedProvider)mc.world.provider).getSeedData();

			if (data != null)
			{
				data.setSeed(seed);
			}
		}

		return null;
	}
}