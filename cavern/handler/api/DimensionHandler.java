package cavern.handler.api;

import javax.annotation.Nullable;

import cavern.api.DimensionWrapper;
import cavern.world.CaveDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.world.DimensionType;

public class DimensionHandler implements DimensionWrapper
{
	private boolean isInDimension(@Nullable Entity entity, @Nullable DimensionType type)
	{
		return entity != null && type != null && entity.dimension == type.getId();
	}

	@Override
	public DimensionType getCavern()
	{
		return CaveDimensions.CAVERN;
	}

	@Override
	public boolean isInCavern(Entity entity)
	{
		return isInDimension(entity, getCavern());
	}

	@Override
	public DimensionType getHugeCavern()
	{
		return CaveDimensions.HUGE_CAVERN;
	}

	@Override
	public boolean isInHugeCavern(Entity entity)
	{
		return isInDimension(entity, getHugeCavern());
	}

	@Override
	public DimensionType getAquaCavern()
	{
		return CaveDimensions.AQUA_CAVERN;
	}

	@Override
	public boolean isInAquaCavern(Entity entity)
	{
		return isInDimension(entity, getAquaCavern());
	}

	@Override
	public DimensionType getCaveland()
	{
		return CaveDimensions.CAVELAND;
	}

	@Override
	public boolean isInCaveland(Entity entity)
	{
		return isInDimension(entity, getCaveland());
	}

	@Override
	public DimensionType getCavenia()
	{
		return CaveDimensions.CAVENIA;
	}

	@Override
	public boolean isInCavenia(Entity entity)
	{
		return isInDimension(entity, getCavenia());
	}

	@Override
	public DimensionType getFrostMountains()
	{
		return CaveDimensions.FROST_MOUNTAINS;
	}

	@Override
	public boolean isInFrostMountains(Entity entity)
	{
		return isInDimension(entity, getFrostMountains());
	}

	@Override
	public DimensionType getWideDesert()
	{
		return CaveDimensions.WIDE_DESERT;
	}

	@Override
	public boolean isInWideDesert(Entity entity)
	{
		return isInDimension(entity, getWideDesert());
	}

	@Override
	public DimensionType getTheVoid()
	{
		return CaveDimensions.THE_VOID;
	}

	@Override
	public boolean isInTheVoid(Entity entity)
	{
		return isInDimension(entity, getTheVoid());
	}

	@Override
	public DimensionType getDarkForest()
	{
		return CaveDimensions.DARK_FOREST;
	}

	@Override
	public boolean isInDarkForest(Entity entity)
	{
		return isInDimension(entity, getDarkForest());
	}

	@Override
	public DimensionType getCrownCliffs()
	{
		return CaveDimensions.CROWN_CLIFFS;
	}

	@Override
	public boolean isInCrownCliffs(Entity entity)
	{
		return isInDimension(entity, getCrownCliffs());
	}

	@Override
	public boolean isInCaveDimensions(Entity entity)
	{
		return isInCaverns(entity) || isInMirageWorlds(entity);
	}

	@Override
	public boolean isCaveDimensions(DimensionType type)
	{
		return isCaverns(type) || isMirageWorlds(type);
	}

	@Override
	public boolean isInCaverns(Entity entity)
	{
		return isInCavern(entity) || isInHugeCavern(entity) || isInAquaCavern(entity);
	}

	@Override
	public boolean isCaverns(DimensionType type)
	{
		return type != null && (type == getCavern() || type == getHugeCavern() || type == getAquaCavern());
	}

	@Override
	public boolean isInCaves(Entity entity)
	{
		return isInCaverns(entity) || isInCaveland(entity) || isInCavenia(entity);
	}

	@Override
	public boolean isCaves(DimensionType type)
	{
		return type != null && (isCaverns(type) || type == getCaveland() || type == getCavenia());
	}

	@Override
	public boolean isInMirageWorlds(Entity entity)
	{
		return isInCaveland(entity) || isInCavenia(entity) || isInFrostMountains(entity) || isInWideDesert(entity) || isInTheVoid(entity) || isInDarkForest(entity) || isInCrownCliffs(entity);
	}

	@Override
	public boolean isMirageWorlds(DimensionType type)
	{
		return type != null && (type == getCaveland() || type == getCavenia() || type == getFrostMountains() || type == getWideDesert() || type == getTheVoid() || type == getDarkForest() || type == getCrownCliffs());
	}
}