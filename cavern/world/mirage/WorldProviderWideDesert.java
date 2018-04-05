package cavern.world.mirage;

import cavern.world.CaveDimensions;
import cavern.world.CustomSeedData;
import net.minecraft.init.Biomes;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderWideDesert extends WorldProviderMirageWorld
{
	@Override
	protected void init()
	{
		hasSkyLight = true;
		biomeProvider = new BiomeProviderSingle(Biomes.DESERT);
		seedData = world instanceof WorldServer ? new CustomSeedData(world.getWorldInfo().getDimensionData(getDimension())) : new CustomSeedData();
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