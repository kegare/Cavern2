package cavern.api;

import cavern.api.data.IMiner;
import cavern.api.data.IMiningData;
import net.minecraft.entity.player.EntityPlayer;

public interface DataWrapper
{
	IMiner getMiner(EntityPlayer player);

	IMiningData getMiningData(EntityPlayer player);
}