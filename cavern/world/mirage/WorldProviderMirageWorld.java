package cavern.world.mirage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;

public abstract class WorldProviderMirageWorld extends WorldProvider
{
	@Override
	public abstract DimensionType getDimensionType();

	@Override
	public boolean shouldClientCheckLighting()
	{
		return false;
	}

	@Override
	public WorldSleepResult canSleepAt(EntityPlayer player, BlockPos pos)
	{
		return WorldSleepResult.ALLOW;
	}
}