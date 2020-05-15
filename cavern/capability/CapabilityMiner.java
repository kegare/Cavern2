package cavern.capability;

import cavern.api.data.IMiner;
import cavern.data.Miner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilityMiner implements ICapabilitySerializable<NBTTagCompound>
{
	private final IMiner miner;

	public CapabilityMiner(EntityPlayer player)
	{
		this.miner = new Miner(player);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return CaveCapabilities.MINER != null && capability == CaveCapabilities.MINER;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (CaveCapabilities.MINER != null && capability == CaveCapabilities.MINER)
		{
			return CaveCapabilities.MINER.cast(miner);
		}

		return null;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		if (CaveCapabilities.MINER != null)
		{
			return (NBTTagCompound)CaveCapabilities.MINER.getStorage().writeNBT(CaveCapabilities.MINER, miner, null);
		}

		return new NBTTagCompound();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (CaveCapabilities.MINER != null)
		{
			CaveCapabilities.MINER.getStorage().readNBT(CaveCapabilities.MINER, miner, null, nbt);
		}
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(IMiner.class,
			new Capability.IStorage<IMiner>()
			{
				@Override
				public NBTBase writeNBT(Capability<IMiner> capability, IMiner instance, EnumFacing side)
				{
					NBTTagCompound nbt = new NBTTagCompound();

					instance.writeToNBT(nbt);

					return nbt;
				}

				@Override
				public void readNBT(Capability<IMiner> capability, IMiner instance, EnumFacing side, NBTBase nbt)
				{
					instance.readFromNBT((NBTTagCompound)nbt);
				}
			},
			() -> new Miner(null)
		);
	}
}