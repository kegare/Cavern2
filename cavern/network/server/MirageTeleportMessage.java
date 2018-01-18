package cavern.network.server;

import cavern.core.CaveSounds;
import cavern.item.CaveItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.SoundCategory;
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
		if (type == null)
		{
			return null;
		}

		double x = player.posX;
		double y = player.posY + player.getEyeHeight();
		double z = player.posZ;

		player.getServerWorld().playSound(player, x, y, z, CaveSounds.CAVE_PORTAL, SoundCategory.BLOCKS, 0.5F, 1.0F);

		CaveItems.MIRAGE_BOOK.transferTo(type, player);

		return null;
	}
}