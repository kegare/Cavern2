package cavern.world;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;

import cavern.block.BlockPortalCavern;
import cavern.block.CaveBlocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;

public class CavePortalList implements INBTSerializable<NBTTagCompound>
{
	private final SetMultimap<BlockPortalCavern, BlockPos> cavePortals = HashMultimap.create();

	public boolean addPortal(BlockPortalCavern portal, BlockPos pos)
	{
		return !hasPortal(portal, pos.getX() << 4, pos.getZ() << 4) && cavePortals.put(portal, pos);
	}

	public boolean removePortal(BlockPortalCavern portal, BlockPos pos)
	{
		return cavePortals.remove(portal, pos);
	}

	public ImmutableSet<BlockPos> getPortalPositions()
	{
		return ImmutableSet.copyOf(cavePortals.values());
	}

	public ImmutableSet<BlockPos> getPortalPositions(BlockPortalCavern portal)
	{
		return ImmutableSet.copyOf(cavePortals.get(portal));
	}

	public boolean hasPortal(int chunkX, int chunkZ)
	{
		return cavePortals.values().stream().anyMatch(pos -> pos.getX() >> 4 == chunkX && pos.getZ() >> 4 == chunkZ);
	}

	public boolean hasPortal(ChunkPos pos)
	{
		return hasPortal(pos.x, pos.z);
	}

	public boolean hasPortal(BlockPortalCavern portal, int chunkX, int chunkZ)
	{
		return cavePortals.get(portal).stream().anyMatch(pos -> pos.getX() >> 4 == chunkX && pos.getZ() >> 4 == chunkZ);
	}

	public boolean isPortalEmpty()
	{
		return cavePortals.values().isEmpty();
	}

	public boolean isPortalEmpty(BlockPortalCavern portal)
	{
		return cavePortals.get(portal).isEmpty();
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();

		for (BlockPortalCavern portal : CaveBlocks.PORTALS)
		{
			NBTTagList list = new NBTTagList();

			for (BlockPos pos : cavePortals.get(portal))
			{
				list.appendTag(NBTUtil.createPosTag(pos));
			}

			nbt.setTag(portal.getRegistryName().toString(), list);
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		for (BlockPortalCavern portal : CaveBlocks.PORTALS)
		{
			NBTTagList list = nbt.getTagList(portal.getRegistryName().toString(), NBT.TAG_COMPOUND);

			for (NBTBase entry : list)
			{
				cavePortals.put(portal, NBTUtil.getPosFromTag((NBTTagCompound)entry));
			}
		}
	}
}