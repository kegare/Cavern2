package cavern.capability;

import cavern.api.IMinerStats;
import cavern.stats.MinerStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilityMinerStats implements ICapabilitySerializable<NBTTagCompound>
{
	private final IMinerStats stats;

	public CapabilityMinerStats(EntityPlayer player)
	{
		this.stats = new MinerStats(player);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return CaveCapabilities.MINER_STATS != null && capability == CaveCapabilities.MINER_STATS;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (CaveCapabilities.MINER_STATS != null && capability == CaveCapabilities.MINER_STATS)
		{
			return CaveCapabilities.MINER_STATS.cast(stats);
		}

		return null;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		if (CaveCapabilities.MINER_STATS != null)
		{
			return (NBTTagCompound)CaveCapabilities.MINER_STATS.getStorage().writeNBT(CaveCapabilities.MINER_STATS, stats, null);
		}

		return new NBTTagCompound();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (CaveCapabilities.MINER_STATS != null)
		{
			CaveCapabilities.MINER_STATS.getStorage().readNBT(CaveCapabilities.MINER_STATS, stats, null, nbt);
		}
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(IMinerStats.class,
			new Capability.IStorage<IMinerStats>()
			{
				@Override
				public NBTBase writeNBT(Capability<IMinerStats> capability, IMinerStats instance, EnumFacing side)
				{
					NBTTagCompound nbt = new NBTTagCompound();

					instance.writeToNBT(nbt);

					return nbt;
				}

				@Override
				public void readNBT(Capability<IMinerStats> capability, IMinerStats instance, EnumFacing side, NBTBase nbt)
				{
					instance.readFromNBT((NBTTagCompound)nbt);
				}
			},
			() -> new MinerStats(null)
		);
	}
}