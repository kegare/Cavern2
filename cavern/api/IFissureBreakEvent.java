package cavern.api;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IFissureBreakEvent
{
	boolean onBreakBlock(World world, BlockPos pos, IBlockState state, float chance, int fortune, @Nullable EntityPlayer player, Random random);
}