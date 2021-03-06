package cavern.capability;

import cavern.data.PortalCache;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilityPortalCache implements ICapabilitySerializable<NBTTagCompound>
{
	private final PortalCache cache;

	public CapabilityPortalCache()
	{
		this.cache = new PortalCache();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == CaveCapabilities.PORTAL_CACHE;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (capability == CaveCapabilities.PORTAL_CACHE)
		{
			return CaveCapabilities.PORTAL_CACHE.cast(cache);
		}

		return null;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		if (CaveCapabilities.PORTAL_CACHE != null)
		{
			return (NBTTagCompound)CaveCapabilities.PORTAL_CACHE.getStorage().writeNBT(CaveCapabilities.PORTAL_CACHE, cache, null);
		}

		return new NBTTagCompound();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (CaveCapabilities.PORTAL_CACHE != null)
		{
			CaveCapabilities.PORTAL_CACHE.getStorage().readNBT(CaveCapabilities.PORTAL_CACHE, cache, null, nbt);
		}
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(PortalCache.class,
			new Capability.IStorage<PortalCache>()
			{
				@Override
				public NBTBase writeNBT(Capability<PortalCache> capability, PortalCache instance, EnumFacing side)
				{
					NBTTagCompound nbt = new NBTTagCompound();

					instance.writeToNBT(nbt);

					return nbt;
				}

				@Override
				public void readNBT(Capability<PortalCache> capability, PortalCache instance, EnumFacing side, NBTBase nbt)
				{
					instance.readFromNBT((NBTTagCompound)nbt);
				}
			},
			PortalCache::new
		);
	}
}