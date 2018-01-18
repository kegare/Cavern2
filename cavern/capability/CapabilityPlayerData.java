package cavern.capability;

import cavern.api.IPlayerData;
import cavern.stats.PlayerData;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilityPlayerData implements ICapabilitySerializable<NBTTagCompound>
{
	private final IPlayerData playerData;

	public CapabilityPlayerData()
	{
		this.playerData = new PlayerData();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == CaveCapabilities.PLAYER_DATA;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (capability == CaveCapabilities.PLAYER_DATA)
		{
			return CaveCapabilities.PLAYER_DATA.cast(playerData);
		}

		return null;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		if (CaveCapabilities.PLAYER_DATA != null)
		{
			return (NBTTagCompound)CaveCapabilities.PLAYER_DATA.getStorage().writeNBT(CaveCapabilities.PLAYER_DATA, playerData, null);
		}

		return new NBTTagCompound();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (CaveCapabilities.PLAYER_DATA != null)
		{
			CaveCapabilities.PLAYER_DATA.getStorage().readNBT(CaveCapabilities.PLAYER_DATA, playerData, null, nbt);
		}
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(IPlayerData.class,
			new Capability.IStorage<IPlayerData>()
			{
				@Override
				public NBTBase writeNBT(Capability<IPlayerData> capability, IPlayerData instance, EnumFacing side)
				{
					NBTTagCompound nbt = new NBTTagCompound();

					instance.writeToNBT(nbt);

					return nbt;
				}

				@Override
				public void readNBT(Capability<IPlayerData> capability, IPlayerData instance, EnumFacing side, NBTBase nbt)
				{
					instance.readFromNBT((NBTTagCompound)nbt);
				}
			},
			PlayerData::new
		);
	}
}