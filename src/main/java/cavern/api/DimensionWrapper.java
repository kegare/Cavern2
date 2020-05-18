package cavern.api;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.world.DimensionType;

public interface DimensionWrapper
{
	@Nullable
	DimensionType getCavern();

	boolean isInCavern(@Nullable Entity entity);

	@Nullable
	DimensionType getHugeCavern();

	boolean isInHugeCavern(@Nullable Entity entity);

	@Nullable
	DimensionType getAquaCavern();

	boolean isInAquaCavern(@Nullable Entity entity);

	@Nullable
	DimensionType getCaveland();

	boolean isInCaveland(@Nullable Entity entity);

	@Nullable
	DimensionType getCavenia();

	boolean isInCavenia(@Nullable Entity entity);

	@Nullable
	DimensionType getFrostMountains();

	boolean isInFrostMountains(@Nullable Entity entity);

	@Nullable
	DimensionType getWideDesert();

	boolean isInWideDesert(@Nullable Entity entity);

	@Nullable
	DimensionType getTheVoid();

	boolean isInTheVoid(@Nullable Entity entity);

	@Nullable
	DimensionType getDarkForest();

	boolean isInDarkForest(@Nullable Entity entity);

	@Nullable
	DimensionType getCrownCliffs();

	boolean isInCrownCliffs(@Nullable Entity entity);

	@Nullable
	DimensionType getSkyland();

	boolean isInSkyland(@Nullable Entity entity);

	boolean isInCaveDimensions(@Nullable Entity entity);

	boolean isCaveDimensions(@Nullable DimensionType type);

	boolean isInCaverns(@Nullable Entity entity);

	boolean isCaverns(@Nullable DimensionType type);

	boolean isInCaves(@Nullable Entity entity);

	boolean isCaves(@Nullable DimensionType type);

	boolean isInMirageWorlds(@Nullable Entity entity);

	boolean isMirageWorlds(@Nullable DimensionType type);
}