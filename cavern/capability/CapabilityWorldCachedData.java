package cavern.capability;

import cavern.world.WorldCachedData;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class CapabilityWorldCachedData implements ICapabilityProvider
{
	private final WorldCachedData worldData;

	public CapabilityWorldCachedData(WorldServer world)
	{
		this.worldData = new WorldCachedData(world);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return CaveCapabilities.WORLD_CACHED_DATA != null && capability == CaveCapabilities.WORLD_CACHED_DATA;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (CaveCapabilities.WORLD_CACHED_DATA != null && capability == CaveCapabilities.WORLD_CACHED_DATA)
		{
			return CaveCapabilities.WORLD_CACHED_DATA.cast(worldData);
		}

		return null;
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(WorldCachedData.class, new EmptyStorage<>(), () -> new WorldCachedData(null));
	}
}