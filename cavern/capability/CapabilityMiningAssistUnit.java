package cavern.capability;

import cavern.miningassist.MiningAssistUnit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class CapabilityMiningAssistUnit implements ICapabilityProvider
{
	private final MiningAssistUnit assist;

	public CapabilityMiningAssistUnit(EntityPlayer player)
	{
		this.assist = new MiningAssistUnit(player);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == CaveCapabilities.MINING_ASSIST;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (capability == CaveCapabilities.MINING_ASSIST)
		{
			return CaveCapabilities.MINING_ASSIST.cast(assist);
		}

		return null;
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(MiningAssistUnit.class, new EmptyStorage<>(), () -> new MiningAssistUnit(null));
	}
}