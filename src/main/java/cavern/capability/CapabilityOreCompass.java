package cavern.capability;

import cavern.item.OreCompass;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class CapabilityOreCompass implements ICapabilityProvider
{
	private final OreCompass compass;

	public CapabilityOreCompass()
	{
		this.compass = new OreCompass();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return CaveCapabilities.ORE_COMPASS != null && capability == CaveCapabilities.ORE_COMPASS;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (CaveCapabilities.ORE_COMPASS != null && capability == CaveCapabilities.ORE_COMPASS)
		{
			return CaveCapabilities.ORE_COMPASS.cast(compass);
		}

		return null;
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(OreCompass.class, new EmptyStorage<>(), OreCompass::new);
	}
}