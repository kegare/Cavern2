package cavern.block.event;

import java.util.Random;

import cavern.api.IFissureBreakEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FissureEventExplosion implements IFissureBreakEvent
{
	@Override
	public boolean onBreakBlock(World world, BlockPos pos, IBlockState state, float chance, int fortune, EntityPlayer player, Random random)
	{
		float posX = pos.getX() + 0.5F;
		float posY = pos.getY() + 0.5F;
		float posZ = pos.getZ() + 0.5F;
		float strength = 1.45F;

		if (random.nextDouble() < 0.15D)
		{
			strength = 3.0F;
		}

		if (strength > 0.0F)
		{
			world.newExplosion(null, posX, posY, posZ, strength, false, true);

			return true;
		}

		return false;
	}
}