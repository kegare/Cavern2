package cavern.world.mirage;

import cavern.world.CaveDimensions;
import cavern.world.CustomSeedData;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderCrownCliffs extends WorldProviderMirageWorld
{
	@Override
	protected void init()
	{
		hasSkyLight = true;
		biomeProvider = new BiomeProvider(world.getWorldInfo());
		seedData = world instanceof WorldServer ? new CustomSeedData(world.getWorldInfo().getDimensionData(getDimension())) : new CustomSeedData();
	}

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkGeneratorCrownCliffs(world);
	}

	@Override
	public DimensionType getDimensionType()
	{
		return CaveDimensions.CROWN_CLIFFS;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float getCloudHeight()
	{
		return 180.0F;
	}
}