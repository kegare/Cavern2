package cavern.world;

import cavern.config.HugeCavernConfig;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderHugeCavern extends WorldProviderCavern
{
	@Override
	protected BiomeProvider createBiomeProvider()
	{
		return new CaveBiomeProvider(world, HugeCavernConfig.biomeManager);
	}

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkGeneratorHugeCavern(world);
	}

	@Override
	public DimensionType getDimensionType()
	{
		return CaveDimensions.HUGE_CAVERN;
	}

	@Override
	public int getWorldHeight()
	{
		return HugeCavernConfig.worldHeight;
	}

	@Override
	public int getMonsterSpawn()
	{
		return HugeCavernConfig.monsterSpawn;
	}

	@Override
	public double getBrightness()
	{
		return HugeCavernConfig.caveBrightness;
	}
}