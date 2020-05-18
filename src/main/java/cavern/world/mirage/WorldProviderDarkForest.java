package cavern.world.mirage;

import cavern.world.CaveDimensions;
import cavern.world.CustomSeedData;
import net.minecraft.init.Biomes;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderDarkForest extends WorldProviderMirageWorld
{
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