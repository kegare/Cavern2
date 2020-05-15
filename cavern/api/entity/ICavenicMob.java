package cavern.api.entity;

public interface ICavenicMob
{
	default boolean canSpawnInCavenia()
	{
		return true;
	}
}