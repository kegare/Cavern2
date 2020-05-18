package cavern.network.server;

import cavern.item.CaveItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MirageTeleportMessage implements IPlayerMessage<MirageTeleportMessage, IMessage>
{
	private DimensionType type;

	public MirageTeleportMessage() {}

	public MirageTeleportMessage(DimensionType type)
	{
		this.type = type;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		try
		{
			type = DimensionType.getById(buf.readInt());
		}
		catch (IllegalArgumentException e)
		{
			type = null;
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(type.getId());
	}

	@Override
	public IMessage process(EntityPlayerMP player)
	{
		if (type != null)
		{
			CaveItems.MIRAGE_BOOK.transferTo(type, player);
		}

		return null;
	}
}