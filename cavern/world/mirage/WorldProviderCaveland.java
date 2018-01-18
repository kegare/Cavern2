package cavern.world.mirage;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.config.CavelandConfig;
import cavern.config.manager.CaveBiomeManager;
import cavern.config.property.ConfigBiomeType;
import cavern.core.CaveSounds;
import cavern.util.WeightedItemStack;
import cavern.world.CaveDimensions;
import cavern.world.WorldProviderCavern;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderCaveland extends WorldProviderCavern
{
	public static final List<WeightedItemStack> HIBERNATE_ITEMS = Lists.newArrayList();

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkGeneratorCaveland(world);
	}

	@Override
	public DimensionType getDimensionType()
	{
		return CaveDimensions.CAVELAND;
	}

	@Override
	public ConfigBiomeType.Type getBiomeType()
	{
		return ConfigBiomeType.Type.NATURAL;
	}

	@Override
	public int getWorldHeight()
	{
		return CavelandConfig.worldHeight;
	}

	@Override
	public CaveBiomeManager getBiomeManager()
	{
		return null;
	}

	@Override
	public int getMonsterSpawn()
	{
		return CavelandConfig.monsterSpawn;
	}

	@Override
	public double getBrightness()
	{
		return CavelandConfig.caveBrightness;
	}

	@Override
	public SoundEvent getMusicSound()
	{
		return CaveSounds.MUSIC_HOPE;
	}
}