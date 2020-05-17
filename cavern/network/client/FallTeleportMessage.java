package cavern.network.client;

import cavern.client.audio.MovingSoundSkyFalling;
import cavern.handler.MirageEventHooks;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FallTeleportMessage implements IClientMessage<FallTeleportMessage, IMessage>
{
	@SideOnly(Side.CLIENT)
	@Override
	public IMessage process(Minecraft mc)
	{
		if (MovingSoundSkyFalling.prevSound == null || MovingSoundSkyFalling.prevSound.isDonePlaying())
		{
			MovingSoundSkyFalling sound = new MovingSoundSkyFalling();

			mc.getSoundHandler().playSound(sound);

			MovingSoundSkyFalling.prevSound = sound;

			MirageEventHooks.fallDamageCancel = true;
		}

		return null;
	}
}