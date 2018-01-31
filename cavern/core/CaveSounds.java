package cavern.core;

import cavern.util.CaveUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class CaveSounds
{
	public static final CaveSoundEvent CAVE_PORTAL = new CaveSoundEvent("cave_portal");
	public static final CaveSoundEvent RANK_PROMOTE = new CaveSoundEvent("rank.promote");

	public static final CaveSoundEvent MUSIC_CAVES = new CaveSoundEvent("music.caves");
	public static final CaveSoundEvent MUSIC_AQUA_CAVES = new CaveSoundEvent("music.aqua_caves");
	public static final CaveSoundEvent MUSIC_CAVELAND = new CaveSoundEvent("music.caveland");
	public static final CaveSoundEvent MUSIC_CAVENIA = new CaveSoundEvent("music.cavenia");

	public static void registerSounds(IForgeRegistry<SoundEvent> registry)
	{
		registry.register(CAVE_PORTAL);
		registry.register(RANK_PROMOTE);

		registry.register(MUSIC_CAVES);
		registry.register(MUSIC_AQUA_CAVES);
		registry.register(MUSIC_CAVELAND);
		registry.register(MUSIC_CAVENIA);
	}

	public static class CaveSoundEvent extends SoundEvent
	{
		public CaveSoundEvent(ResourceLocation key)
		{
			super(key);
			this.setRegistryName(key);
		}

		public CaveSoundEvent(String key)
		{
			this(CaveUtils.getKey(key));
		}
	}
}