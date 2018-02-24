package cavern.network.server;

import cavern.magic.MagicBook;
import cavern.magic.SpecialMagic;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SpecialMagicMessage implements IPlayerMessage<SpecialMagicMessage, IMessage>
{
	@Override
	public IMessage process(EntityPlayerMP player)
	{
		MagicBook book = MagicBook.get(player);
		SpecialMagic magic = book.getSpecialMagic();

		if (magic != null)
		{
			ITextComponent message = magic.finishMagic();

			if (message != null)
			{
				player.sendStatusMessage(message, true);
			}

			SoundEvent sound = magic.getFinishSound();

			if (sound != null)
			{
				player.world.playSound(null, player.posX, player.posY + 0.25D, player.posZ, sound, SoundCategory.PLAYERS, 1.0F, 1.0F);
			}

			book.setSpecialMagic(null);
		}

		return null;
	}
}