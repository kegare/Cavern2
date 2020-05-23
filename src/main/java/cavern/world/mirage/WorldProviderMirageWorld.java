package cavern.world.mirage;

import cavern.world.CustomSeedData;
import cavern.world.CustomSeedProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeProvider;

public abstract class WorldProviderMirageWorld extends WorldProvider implements CustomSeedProvider
{
	protected CustomSeedData seedData;

	@Override
	protected void init()
	{
		hasSkyLight = true;
		biomeProvider = createBiomeProvider();
		seedData = world instanceof WorldServer ? new CustomSeedData(world.getWorldInfo().getDimensionData(getDimension())) : new CustomSeedData();
	}

	protected BiomeProvider createBiomeProvider()
	{
		return new BiomeProvider(world.getWorldInfo());
	}

	@Override
	public abstract DimensionType getDimensionType();

	@Override
	public CustomSeedData getSeedData()
	{
		return seedData;
	}

	@Override
	public long getSeed()
	{
		if (seedData != null)
		{
			if (world instanceof WorldServer)
			{
				return seedData.getSeed();
			}

			return seedData.getSeedValue(world.getWorldInfo().getSeed());
		}

		return super.getSeed();
	}

	@Override
	public void onWorldSave()
	{
		if (seedData != null)
		{
			NBTTagCompound nbt = world.getWorldInfo().getDimensionData(getDimension());

			world.getWorldInfo().setDimensionData(getDimension(), seedData.getCompound(nbt));
		}
	}

	@Override
	public boolean shouldClientCheckLighting()
	{
		return false;
	}

	@Override
	public WorldSleepResult canSleepAt(EntityPlayer player, BlockPos pos)
	{
		return WorldSleepResult.ALLOW;
	}
}