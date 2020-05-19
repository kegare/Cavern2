package cavern.world.mirage;

import cavern.world.CaveDimensions;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderFrostMountains extends WorldProviderMirageWorld
{
	@Override
	protected void init()
	{
		super.init();
		biomeProvider = new BiomeProviderSingle(Biomes.ICE_MOUNTAINS);
	}

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkGeneratorFrostMountains(world);
	}

	@Override
	public DimensionType getDimensionType()
	{
		return CaveDimensions.FROST_MOUNTAINS;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float getSunBrightness(float ticks)
	{
		return super.getSunBrightness(ticks) * 0.75F;
	}

	@Override
	public void calculateInitialWeather()
	{
		if (world.isDaytime())
		{
			world.rainingStrength = 0.5F;
		}
		else
		{
			world.rainingStrength = 1.0F;
		}
	}

	@Override
	public void updateWeather()
	{
		world.prevRainingStrength = world.rainingStrength;

		if (world.isDaytime())
		{
			world.rainingStrength = (float)(world.rainingStrength - 0.01D);
		}
		else
		{
			world.rainingStrength = (float)(world.rainingStrength + 0.01D);
		}

		world.rainingStrength = MathHelper.clamp(world.rainingStrength, 0.5F, 1.0F);
	}
}