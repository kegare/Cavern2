package cavern.network.client;

import cavern.magic.MagicBook;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MagicCancelMessage implements IPlayerMessage<MagicCancelMessage, IMessage>
{
	@Override
	public IMessage process(EntityPlayerSP player)
	{
		MagicBook.get(player).setSpellingCanceled();

		return null;
	}
}