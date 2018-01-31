package cavern.client;

import cavern.core.CaveSounds;
import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraftforge.client.EnumHelperClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CaveMusics
{
	public static MusicType CAVES;
	public static MusicType AQUA_CAVES;
	public static MusicType CAVELAND;
	public static MusicType CAVENIA;

	public static void registerMusics()
	{
		CAVES = EnumHelperClient.addMusicType("CAVERN_CAVES", CaveSounds.MUSIC_CAVES, 12000, 24000);
		AQUA_CAVES = EnumHelperClient.addMusicType("AQUA_CAVES", CaveSounds.MUSIC_AQUA_CAVES, 12000, 24000);
		CAVELAND = EnumHelperClient.addMusicType("CAVELAND", CaveSounds.MUSIC_CAVELAND, 12000, 24000);
		CAVENIA = EnumHelperClient.addMusicType("CAVENIA", CaveSounds.MUSIC_CAVENIA, 0, 0);
	}
}