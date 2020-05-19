package cavern.core;

import cavern.util.CaveUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

public final class CaveSounds
{
	public static final CaveSoundEvent CAVE_PORTAL = new CaveSoundEvent("cave_portal");
	public static final CaveSoundEvent RANK_PROMOTE = new CaveSoundEvent("rank.promote");
	public static final CaveSoundEvent FALLING = new CaveSoundEvent("falling");

	public static final CaveSoundEvent MUSIC_CAVES = new CaveSoundEvent("music.caves");
	public static final CaveSoundEvent MUSIC_AQUA_CAVES = new CaveSoundEvent("music.aqua_caves");
	public static final CaveSoundEvent MUSIC_CAVELAND = new CaveSoundEvent("music.caveland");
	public static final CaveSoundEvent MUSIC_CAVENIA = new CaveSoundEvent("music.cavenia");
	public static final CaveSoundEvent MUSIC_SKYLAND = new CaveSoundEvent("music.skyland");

	public static final CaveSoundEvent MAGIC_SPELLING = new CaveSoundEvent("magic.spelling");
	public static final CaveSoundEvent MAGIC_CLOSE_BOOK  = new CaveSoundEvent("magic.close_book");
	public static final CaveSoundEvent MAGIC_SUCCESS_BUFF  = new CaveSoundEvent("magic.success_buff");
	public static final CaveSoundEvent MAGIC_SUCCESS_MISC  = new CaveSoundEvent("magic.success_misc");
	public static final CaveSoundEvent MAGIC_SUCCESS_STRONG  = new CaveSoundEvent("magic.success_strong");
	public static final CaveSoundEvent MAGIC_BREAK  = new CaveSoundEvent("magic.break");
	public static final CaveSoundEvent MAGIC_REVERSE  = new CaveSoundEvent("magic.reverse");
	public static final CaveSoundEvent MAGIC_BLAZE  = new CaveSoundEvent("magic.blaze");

	public static void registerSounds(IForgeRegistry<SoundEvent> registry)
	{
		registry.register(CAVE_PORTAL);
		registry.register(RANK_PROMOTE);
		registry.register(FALLING);

		registry.register(MUSIC_CAVES);
		registry.register(MUSIC_AQUA_CAVES);
		registry.register(MUSIC_CAVELAND);
		registry.register(MUSIC_CAVENIA);
		registry.register(MUSIC_SKYLAND);

		registry.register(MAGIC_SPELLING);
		registry.register(MAGIC_CLOSE_BOOK);
		registry.register(MAGIC_SUCCESS_BUFF);
		registry.register(MAGIC_SUCCESS_MISC);
		registry.register(MAGIC_SUCCESS_STRONG);
		registry.register(MAGIC_BREAK);
		registry.register(MAGIC_REVERSE);
		registry.register(MAGIC_BLAZE);
	}

	private static class CaveSoundEvent extends SoundEvent
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