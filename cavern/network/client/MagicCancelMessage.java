package cavern.network.client;

import cavern.magic.MagicBook;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicCancelMessage implements IPlayerMessage<MagicCancelMessage, IMessage>
{
	@SideOnly(Side.CLIENT)
	@Override
	public IMessage process(EntityPlayerSP player)
	{
		MagicBook.get(player).setSpellingCanceled();

		return null;
	}
}