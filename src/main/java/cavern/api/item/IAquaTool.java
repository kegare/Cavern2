package cavern.api.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface IAquaTool
{
	default float getAquaBreakSpeed(ItemStack stack, EntityPlayer player, BlockPos pos, IBlockState state, float originalSpeed)
	{
		return originalSpeed * 10.0F;
	}
}