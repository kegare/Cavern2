package cavern.world.mirage;

import java.util.List;

import cavern.api.entity.ICavenicMob;
import cavern.client.CaveMusics;
import cavern.config.CaveniaConfig;
import cavern.config.manager.CaveBiomeManager;
import cavern.config.property.ConfigBiomeType;
import cavern.entity.CaveEntityRegistry;
import cavern.world.CaveDimensions;
import cavern.world.WorldProviderCavern;
import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderCavenia extends WorldProviderCavern
{
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

	@SideOnly(Side.CLIENT)
	@Override
	public MusicType getMusicType()
	{
		return CaveMusics.CAVENIA;
	}

	@Override
	public EntityLiving createSpawnCreature(WorldServer world, EnumCreatureType type, BlockPos pos, Biome.SpawnListEntry entry)
	{
		if (type != EnumCreatureType.MONSTER)
		{
			return null;
		}

		List<SpawnListEntry> list = CaveEntityRegistry.SPAWNS;
		double chance = CaveniaConfig.crazySpawnChance;

		if (chance > 0.0D && world.rand.nextDouble() <= chance)
		{
			int range = 0;

			if (chance <= 0.1D)
			{
				range = 50;
			}
			else if (chance <= 0.2D)
			{
				range = 32;
			}
			else if (chance <= 0.4D)
			{
				range = 16;
			}
			else if (chance <= 0.6D)
			{
				range = 8;
			}
			else if (chance <= 0.8D)
			{
				range = 4;
			}

			int rangeY = range > 1 ? range / 2 : range;

			if (range <= 0 || world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.add(-range, -rangeY, -range), pos.add(range, rangeY, range)),
				(entity) -> entity instanceof ICavenicMob && !entity.isNonBoss()).isEmpty())
			{
				list = CaveEntityRegistry.CRAZY_SPAWNS;
			}
		}

		Biome.SpawnListEntry spawnEntry = WeightedRandom.getRandomItem(world.rand, list);

		try
		{
			return spawnEntry.newInstance(world);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}
}