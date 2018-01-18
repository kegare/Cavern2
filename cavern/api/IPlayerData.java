package cavern.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DimensionType;

public interface IPlayerData
{
	public long getLastTeleportTime(DimensionType type);

	public void setLastTeleportTime(DimensionType type, long time);

	public long getLastSleepTime();

	public void setLastSleepTime(long time);

	public void writeToNBT(NBTTagCompound nbt);

	public void readFromNBT(NBTTagCompound nbt);
}