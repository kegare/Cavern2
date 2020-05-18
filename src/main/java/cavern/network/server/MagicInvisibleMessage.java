package cavern.network.server;

import cavern.magic.Magic;
import cavern.magic.MagicBook;
import cavern.network.client.MagicCancelMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MagicInvisibleMessage implements IPlayerMessage<MagicInvisibleMessage, IMessage>
{
	@Override
	public IMessage process(EntityPlayerMP player)
	{
		MagicBook book = MagicBook.get(player);
		Magic magic = book.getSpellingMagic();

		if (magic != null)
		{
			if (magic.getMana() < magic.getCost())
			{
				return new MagicCancelMessage();
			}

			player.setInvisible(true);

			SoundEvent sound = magic.getSuccessSound();

			if (sound != null)
			{
				player.world.playSound(null, player.posX, player.posY + 0.25D, player.posZ, sound, SoundCategory.PLAYERS, 1.0F, 1.0F);
			}
		}

		return null;
	}
}