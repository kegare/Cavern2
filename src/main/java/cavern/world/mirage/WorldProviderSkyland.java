package cavern.world.mirage;

import cavern.client.CaveMusics;
import cavern.client.renderer.EmptyRenderer;
import cavern.world.CaveDimensions;
import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderSkyland extends WorldProviderMirageWorld
{
	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkGeneratorSkyland(world);
	}

	@Override
	protected void generateLightBrightnessTable()
	{
		float f = 0.075F;

		for (int i = 0; i <= 15; ++i)
		{
			float f1 = 1.0F - i / 15.0F;

			lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
		}
	}

	@Override
	public boolean canCoordinateBeSpawn(int x, int z)
	{
		return !world.isAirBlock(world.getHeight(new BlockPos(x, 0, z)));
	}

	@Override
	public DimensionType getDimensionType()
	{
		return CaveDimensions.SKYLAND;
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
	public BlockPos getSpawnPoint()
	{
		return BlockPos.ORIGIN.up(50);
	}

	@Override
	public BlockPos getRandomizedSpawnPoint()
	{
		return getSpawnPoint();
	}

	@Override
	public int getActualHeight()
	{
		return 128;
	}

	@Override
	public boolean shouldMapSpin(String entity, double x, double y, double z)
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public MusicType getMusicType()
	{
		return CaveMusics.SKYLAND;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float getCloudHeight()
	{
		return 1.5F;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public double getVoidFogYFactor()
	{
		return 1.0D;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float getSunBrightness(float ticks)
	{
		return super.getSunBrightness(ticks) * 1.25F;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float getStarBrightness(float ticks)
	{
		return super.getStarBrightness(ticks) * 1.5F;
	}

	@Override
	public void calculateInitialWeather()
	{
		updateWeather();
	}

	@Override
	public void updateWeather()
	{
		world.prevRainingStrength = 0.0F;
		world.rainingStrength = 0.0F;
		world.prevThunderingStrength = 0.0F;
		world.thunderingStrength = 0.0F;
	}

	@Override
	public void resetRainAndThunder()
	{
		super.resetRainAndThunder();

		if (world.getGameRules().getBoolean("doDaylightCycle"))
		{
			WorldInfo worldInfo = world.getWorldInfo();
			long i = worldInfo.getWorldTime() + 24000L;

			worldInfo.setWorldTime(i - i % 24000L);
		}
	}

	@Override
	public boolean isBlockHighHumidity(BlockPos pos)
	{
		return false;
	}

	@Override
	public double getHorizon()
	{
		return 0.0D;
	}

	@Override
	public boolean canSnowAt(BlockPos pos, boolean checkLight)
	{
		return false;
	}

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