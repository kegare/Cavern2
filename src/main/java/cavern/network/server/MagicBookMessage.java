package cavern.network.server;

import cavern.item.ItemMagicBook.EnumType;
import cavern.magic.Magic;
import cavern.magic.MagicBook;
import cavern.network.client.MagicCancelMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MagicBookMessage implements IPlayerMessage<MagicBookMessage, IMessage>
{
	private EnumHand hand;

	public MagicBookMessage() {}

	public MagicBookMessage(EnumHand hand)
	{
		this.hand = hand;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		hand = EnumHand.values()[buf.readInt()];
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(hand.ordinal());
	}

	@Override
	public IMessage process(EntityPlayerMP player)
	{
		MagicBook book = MagicBook.get(player);
		Magic magic = EnumType.byItemStack(player.getHeldItem(hand)).createMagic(player.getServerWorld(), player, hand);

		if (magic == null)
		{
			return new MagicCancelMessage();
		}

		book.setSpellingMagic(magic);

		return null;
	}
}