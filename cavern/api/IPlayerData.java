package cavern.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DimensionType;

public interface IPlayerData
{
	long getLastTeleportTime(DimensionType type);

	void setLastTeleportTime(DimensionType type, long time);

	long getLastSleepTime();

	void setLastSleepTime(long time);

	void writeToNBT(NBTTagCompound nbt);

	void readFromNBT(NBTTagCompound nbt);
}