package cavern.capability;

import cavern.inventory.InventoryMagicStorage;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilityMagicStorage implements ICapabilitySerializable<NBTTagCompound>
{
	private final InventoryMagicStorage storage;

	public CapabilityMagicStorage()
	{
		this.storage = new InventoryMagicStorage();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return CaveCapabilities.MAGIC_STORAGE != null && capability == CaveCapabilities.MAGIC_STORAGE;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (CaveCapabilities.MAGIC_STORAGE != null && capability == CaveCapabilities.MAGIC_STORAGE)
		{
			return CaveCapabilities.MAGIC_STORAGE.cast(storage);
		}

		return null;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		if (CaveCapabilities.MAGIC_STORAGE != null)
		{
			return (NBTTagCompound)CaveCapabilities.MAGIC_STORAGE.getStorage().writeNBT(CaveCapabilities.MAGIC_STORAGE, storage, null);
		}

		return new NBTTagCompound();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (CaveCapabilities.MAGIC_STORAGE != null)
		{
			CaveCapabilities.MAGIC_STORAGE.getStorage().readNBT(CaveCapabilities.MAGIC_STORAGE, storage, null, nbt);
		}
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(InventoryMagicStorage.class,
			new Capability.IStorage<InventoryMagicStorage>()
			{
				@Override
				public NBTBase writeNBT(Capability<InventoryMagicStorage> capability, InventoryMagicStorage instance, EnumFacing side)
				{
					NBTTagCompound nbt = new NBTTagCompound();

					instance.writeToNBT(nbt);

					return nbt;
				}

				@Override
				public void readNBT(Capability<InventoryMagicStorage> capability, InventoryMagicStorage instance, EnumFacing side, NBTBase nbt)
				{
					instance.readFromNBT((NBTTagCompound)nbt);
				}
			},
			InventoryMagicStorage::new
		);
	}
}