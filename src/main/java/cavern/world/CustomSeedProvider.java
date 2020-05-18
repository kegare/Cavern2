package cavern.world;

import javax.annotation.Nullable;

public interface CustomSeedProvider
{
	@Nullable
	CustomSeedData getSeedData();
}