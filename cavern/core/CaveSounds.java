package cavern.core;

import cavern.util.CaveUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class CaveSounds
{
	public static final CaveSoundEvent CAVE_PORTAL = new CaveSoundEvent("cave_portal");
	public static final CaveSoundEvent RANK_PROMOTE = new CaveSoundEvent("rank.promote");

	public static final CaveSoundEvent MUSIC_CAVE = new CaveSoundEvent("cavemusic.cave");
	public static final CaveSoundEvent MUSIC_UNREST = new CaveSoundEvent("cavemusic.unrest");
	public static final CaveSoundEvent MUSIC_AQUA = new CaveSoundEvent("cavemusic.aqua");
	public static final CaveSoundEvent MUSIC_HOPE = new CaveSoundEvent("cavemusic.hope");
	public static final CaveSoundEvent MUSIC_CAVENIA1 = new CaveSoundEvent("cavemusic.cavenia1");
	public static final CaveSoundEvent MUSIC_CAVENIA2 = new CaveSoundEvent("cavemusic.cavenia2");

	public static void registerSounds(IForgeRegistry<SoundEvent> registry)
	{
		registry.register(CAVE_PORTAL);
		registry.register(RANK_PROMOTE);

		registry.register(MUSIC_CAVE);
		registry.register(MUSIC_UNREST);
		registry.register(MUSIC_AQUA);
		registry.register(MUSIC_HOPE);
		registry.register(MUSIC_CAVENIA1);
		registry.register(MUSIC_CAVENIA2);
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