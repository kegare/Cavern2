package cavern.capability;

import cavern.api.IMiningData;
import cavern.stats.MiningData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class CapabilityMiningData implements ICapabilityProvider
{
	private IMiningData data;

	public CapabilityMiningData(EntityPlayer player)
	{
		this.data = new MiningData(player);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == CaveCapabilities.MINING_DATA;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (capability == CaveCapabilities.MINING_DATA)
		{
			return CaveCapabilities.MINING_DATA.cast(data);
		}

		return null;
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(IMiningData.class, new EmptyStorage<>(), () -> new MiningData(null));
	}
}