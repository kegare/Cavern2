package cavern.world;

import cavern.client.CaveMusics;
import cavern.config.AquaCavernConfig;
import cavern.entity.passive.EntityAquaSquid;
import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderAquaCavern extends WorldProviderCavern
{
	@Override
	protected BiomeProvider createBiomeProvider()
	{
		return new CaveBiomeProvider(world, AquaCavernConfig.BIOMES);
	}

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
	public int getWorldHeight()
	{
		return AquaCavernConfig.worldHeight;
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

	@SideOnly(Side.CLIENT)
	@Override
	public MusicType getMusicType()
	{
		return CaveMusics.AQUA_CAVES;
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
			return 100;
		}

		return null;
	}

	@Override
	public EntityLiving createSpawnCreature(WorldServer world, EnumCreatureType type, BlockPos pos, Biome.SpawnListEntry entry)
	{
		if (type != EnumCreatureType.WATER_CREATURE)
		{
			return null;
		}

		return new EntityAquaSquid(world);
	}
}