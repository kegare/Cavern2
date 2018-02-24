package cavern.api;

import java.util.Map;

import cavern.util.BlockMeta;
import net.minecraft.nbt.NBTTagCompound;

public interface IMinerStats
{
	int getPoint();

	void setPoint(int value);

	void setPoint(int value, boolean adjust);

	void addPoint(int value);

	void addPoint(int value, boolean adjust);

	int getRank();

	void setRank(int value);

	void setRank(int value, boolean adjust);

	int getMiningAssist();

	void setMiningAssist(int type);

	void setMiningAssist(int type, boolean adjust);

	void toggleMiningAssist();

	void setMiningRecord(BlockMeta blockMeta, int count);

	void addMiningRecord(BlockMeta blockMeta);

	Map<BlockMeta, Integer> getMiningRecords();

	void adjustData();

	void writeToNBT(NBTTagCompound nbt);

	void readFromNBT(NBTTagCompound nbt);
}