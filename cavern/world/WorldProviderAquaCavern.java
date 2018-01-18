package cavern.world;

import cavern.config.AquaCavernConfig;
import cavern.config.manager.CaveBiomeManager;
import cavern.config.property.ConfigBiomeType;
import cavern.core.CaveSounds;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderAquaCavern extends WorldProviderCavern
{
	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkGeneratorAquaCavern(world);
	}

	@Override
	public DimensionType getDimensionType()
	{
		return CaveDimensions.AQUA_CAVERN;
	}

	@Override
	public ConfigBiomeType.Type getBiomeType()
	{
		return ConfigBiomeType.Type.NATURAL;
	}

	@Override
	public int getWorldHeight()
	{
		return AquaCavernConfig.worldHeight;
	}

	@Override
	public CaveBiomeManager getBiomeManager()
	{
		return AquaCavernConfig.biomeManager;
	}

	@Override
	public int getMonsterSpawn()
	{
		return 0;
	}

	@Override
	public double getBrightness()
	{
		return AquaCavernConfig.caveBrightness;
	}

	@Override
	public SoundEvent getMusicSound()
	{
		return CaveSounds.MUSIC_AQUA;
	}

	@Override
	public void onWorldUpdateEntities()
	{
		if (world instanceof WorldServer)
		{
			WorldServer worldServer = (WorldServer)world;

			if (worldServer.getWorldInfo().getTerrainType() != WorldType.DEBUG_ALL_BLOCK_STATES)
			{
				entitySpawner.findChunksForSpawning(worldServer, false, true, worldServer.getWorldInfo().getWorldTotalTime() % 400L == 0L);
			}
		}
	}

	@Override
	public Integer getMaxNumberOfCreature(WorldServer world, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, EnumCreatureType type)
	{
		if (type == EnumCreatureType.WATER_CREATURE)
		{
			return Integer.valueOf(100);
		}

		return null;
	}
}