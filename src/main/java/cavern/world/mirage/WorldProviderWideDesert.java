package cavern.world.mirage;

import cavern.world.CaveDimensions;
import net.minecraft.init.Biomes;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderWideDesert extends WorldProviderMirageWorld
{
	@Override
	protected void init()
	{
		super.init();
		biomeProvider = new BiomeProviderSingle(Biomes.DESERT);
	}

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkGeneratorWideDesert(world);
	}

	@Override
	public DimensionType getDimensionType()
	{
		return CaveDimensions.WIDE_DESERT;
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
}