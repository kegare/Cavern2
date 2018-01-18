package cavern.world.mirage;

import cavern.world.CaveDimensions;
import cavern.world.CustomSeedData;
import cavern.world.ICustomSeed;
import net.minecraft.init.Biomes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderDarkForest extends WorldProviderSurface implements ICustomSeed
{
	private CustomSeedData seedData;

	@Override
	protected void init()
	{
		hasSkyLight = true;
		biomeProvider = new BiomeProviderSingle(Biomes.ROOFED_FOREST);
		seedData = world instanceof WorldServer ? new CustomSeedData(world.getWorldInfo().getDimensionData(getDimension())) : new CustomSeedData();
	}

	@Override
	public DimensionType getDimensionType()
	{
		return CaveDimensions.DARK_FOREST;
	}

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
	public boolean canDropChunk(int x, int z)
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float getSunBrightness(float ticks)
	{
		return super.getSunBrightness(ticks) * 0.8F;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float getStarBrightness(float ticks)
	{
		return super.getStarBrightness(ticks) * 1.5F;
	}
}