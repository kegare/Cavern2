package cavern.api;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;

public interface IMiningData
{
	@Nullable
	IBlockState getLastMiningBlock();

	int getLastMiningPoint();

	int getMiningCombo();

	long getLastMiningTime();

	void notifyMining(IBlockState state, int point);

	void onUpdate();
}