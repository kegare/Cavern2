package cavern.capability;

import cavern.world.CavePortalList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilityCavePortalList implements ICapabilitySerializable<NBTTagCompound>
{
	private final CavePortalList portalList;

	public CapabilityCavePortalList()
	{
		this.portalList = new CavePortalList();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == CaveCapabilities.CAVE_PORTAL_LIST;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (capability == CaveCapabilities.CAVE_PORTAL_LIST)
		{
			return CaveCapabilities.CAVE_PORTAL_LIST.cast(portalList);
		}

		return null;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		if (CaveCapabilities.CAVE_PORTAL_LIST != null)
		{
			return (NBTTagCompound)CaveCapabilities.CAVE_PORTAL_LIST.getStorage().writeNBT(CaveCapabilities.CAVE_PORTAL_LIST, portalList, null);
		}

		return new NBTTagCompound();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (CaveCapabilities.CAVE_PORTAL_LIST != null)
		{
			CaveCapabilities.CAVE_PORTAL_LIST.getStorage().readNBT(CaveCapabilities.CAVE_PORTAL_LIST, portalList, null, nbt);
		}
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(CavePortalList.class,
			new Capability.IStorage<CavePortalList>()
			{
				@Override
				public NBTBase writeNBT(Capability<CavePortalList> capability, CavePortalList instance, EnumFacing side)
				{
					return instance.serializeNBT();
				}

				@Override
				public void readNBT(Capability<CavePortalList> capability, CavePortalList instance, EnumFacing side, NBTBase nbt)
				{
					instance.deserializeNBT((NBTTagCompound)nbt);
				}
			},
			CavePortalList::new
		);
	}
}