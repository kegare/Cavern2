package cavern.world.mirage;

import cavern.client.CaveMusics;
import cavern.config.CavelandConfig;
import cavern.entity.CaveEntityRegistry;
import cavern.world.CaveDimensions;
import cavern.world.WorldProviderCavern;
import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderCaveland extends WorldProviderCavern
{
	@Override
	protected BiomeProvider createBiomeProvider()
	{
		return new BiomeProvider(world.getWorldInfo());
	}

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
	public int getWorldHeight()
	{
		return CavelandConfig.worldHeight;
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

	@SideOnly(Side.CLIENT)
	@Override
	public MusicType getMusicType()
	{
		return CaveMusics.CAVELAND;
	}

	@Override
	public EntityLiving createSpawnCreature(WorldServer world, EnumCreatureType type, BlockPos pos, Biome.SpawnListEntry entry)
	{
		if (world.rand.nextInt(20) == 0)
		{
			Biome.SpawnListEntry spawnEntry = WeightedRandom.getRandomItem(world.rand, CaveEntityRegistry.ANIMAL_SPAWNS);

			try
			{
				return spawnEntry.newInstance(world);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return super.createSpawnCreature(world, type, pos, entry);
	}
}