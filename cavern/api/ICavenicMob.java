package cavern.api;

public interface ICavenicMob
{
	default boolean canSpawnInCavenia()
	{
		return true;
	}
}