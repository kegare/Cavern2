package cavern.client;

import cavern.core.CaveSounds;
import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.EnumHelperClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class CaveMusics
{
	public static final MusicType CAVES = create("CAVERN_CAVES", CaveSounds.MUSIC_CAVES);
	public static final MusicType AQUA_CAVES = create("AQUA_CAVES", CaveSounds.MUSIC_AQUA_CAVES);
	public static final MusicType CAVELAND = create("CAVELAND", CaveSounds.MUSIC_CAVELAND);
	public static final MusicType CAVENIA = create("CAVENIA", CaveSounds.MUSIC_CAVENIA, 6000, 12000);
	public static final MusicType SKYLAND = create("SKYLAND", CaveSounds.MUSIC_SKYLAND);

	private static MusicType create(String name, SoundEvent sound)
	{
		return create(name, sound, 12000, 24000);
	}

	private static MusicType create(String name, SoundEvent sound, int minDelay, int maxDelay)
	{
		return EnumHelperClient.addMusicType(name, sound, minDelay, maxDelay);
	}
}