package cavern.world.mirage;

import cavern.config.CaveniaConfig;
import cavern.config.manager.CaveBiomeManager;
import cavern.config.property.ConfigBiomeType;
import cavern.core.CaveSounds;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.CaveMusicMessage;
import cavern.world.CaveDimensions;
import cavern.world.WorldProviderCavern;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderCavenia extends WorldProviderCavern
{
	private int musicType;

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkGeneratorCavenia(world);
	}

	@Override
	public DimensionType getDimensionType()
	{
		return CaveDimensions.CAVENIA;
	}

	@Override
	public ConfigBiomeType.Type getBiomeType()
	{
		return CaveniaConfig.biomeType.getType();
	}

	@Override
	public int getWorldHeight()
	{
		return CaveniaConfig.worldHeight;
	}

	@Override
	public CaveBiomeManager getBiomeManager()
	{
		return CaveniaConfig.biomeManager;
	}

	@Override
	public int getMonsterSpawn()
	{
		return CaveniaConfig.monsterSpawn;
	}

	@Override
	public double getBrightness()
	{
		return CaveniaConfig.caveBrightness;
	}

	@Override
	public SoundEvent getMusicSound()
	{
		if (++musicType > 2)
		{
			musicType = 0;
		}

		switch (musicType)
		{
			case 0:
				return CaveSounds.MUSIC_CAVENIA1;
			case 2:
				return CaveSounds.MUSIC_CAVENIA2;
		}

		return super.getMusicSound();
	}

	@Override
	public void calculateInitialWeather()
	{
		if (!world.isRemote)
		{
			musicTime = 1200;
		}
	}

	@Override
	public void updateWeather()
	{
		if (!world.isRemote)
		{
			if (--musicTime <= 0)
			{
				musicTime = world.rand.nextInt(1000) + 3000;

				CaveNetworkRegistry.sendToDimension(new CaveMusicMessage(getMusicSound(), false), getDimension());
			}
		}
	}
}