package cavern.world.mirage;

import cavern.client.renderer.EmptyRenderer;
import cavern.world.CaveDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderVoid extends WorldProviderMirageWorld
{
	@Override
	protected void init()
	{
		hasSkyLight = false;
		biomeProvider = createBiomeProvider();
	}

	@Override
	protected BiomeProvider createBiomeProvider()
	{
		return new BiomeProviderSingle(Biomes.VOID);
	}

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkGeneratorVoid(world);
	}

	@Override
	public DimensionType getDimensionType()
	{
		return CaveDimensions.THE_VOID;
	}

	@Override
	public boolean isSurfaceWorld()
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks)
	{
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Vec3d getFogColor(float celestialAngle, float partialTicks)
	{
		return new Vec3d(0.0D, 0.0D, 0.0D);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isSkyColored()
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Vec3d getSkyColor(Entity cameraEntity, float partialTicks)
	{
		return new Vec3d(0.0D, 0.0D, 0.0D);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getSkyRenderer()
	{
		if (super.getSkyRenderer() == null)
		{
			setSkyRenderer(EmptyRenderer.INSTANCE);
		}

		return super.getSkyRenderer();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getCloudRenderer()
	{
		if (super.getCloudRenderer() == null)
		{
			setCloudRenderer(EmptyRenderer.INSTANCE);
		}

		return super.getCloudRenderer();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getWeatherRenderer()
	{
		if (super.getWeatherRenderer() == null)
		{
			setWeatherRenderer(EmptyRenderer.INSTANCE);
		}

		return super.getWeatherRenderer();
	}

	@Override
	public boolean isDaytime()
	{
		return false;
	}

	@Override
	public void calculateInitialWeather() {}

	@Override
	public void updateWeather() {}

	@Override
	public boolean canDoLightning(Chunk chunk)
	{
		return false;
	}

	@Override
	public boolean canDoRainSnowIce(Chunk chunk)
	{
		return false;
	}
}