package cavern.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CaveMusicMessage implements IClientMessage<CaveMusicMessage, IMessage>
{
	private static ISound prevMusic;

	private String name;
	private boolean stop;

	public CaveMusicMessage() {}

	public CaveMusicMessage(SoundEvent sound, boolean stop)
	{
		this.name = sound.getRegistryName().toString();
		this.stop = stop;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		name = ByteBufUtils.readUTF8String(buf);
		stop = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, name);
		buf.writeBoolean(stop);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage process(Minecraft mc)
	{
		SoundEvent sound = SoundEvent.REGISTRY.getObject(new ResourceLocation(name));

		if (sound == null)
		{
			return null;
		}

		SoundHandler handler = mc.getSoundHandler();

		if (prevMusic != null)
		{
			if (stop)
			{
				handler.stopSound(prevMusic);

				prevMusic = null;
			}
			else if (handler.isSoundPlaying(prevMusic))
			{
				return null;
			}
		}

		float volume = mc.gameSettings.getSoundLevel(SoundCategory.MUSIC);

		if (volume > 0.0F)
		{
			PositionedSound music = PositionedSoundRecord.getMasterRecord(sound, 1.0F);

			ObfuscationReflectionHelper.setPrivateValue(PositionedSound.class, music, volume, "volume", "field_147662_b");

			handler.playSound(music);

			prevMusic = music;
		}

		return null;
	}
}