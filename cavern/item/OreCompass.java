package cavern.item;

import javax.annotation.Nullable;

import cavern.capability.CaveCapabilities;
import cavern.stats.MinerStats;
import cavern.util.CaveUtils;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OreCompass
{
	@SideOnly(Side.CLIENT)
	private double rotation;
	@SideOnly(Side.CLIENT)
	private double rota;
	@SideOnly(Side.CLIENT)
	private long lastUpdateTick;

	@SideOnly(Side.CLIENT)
	private BlockPos orePos;

	@SideOnly(Side.CLIENT)
	public double wobble(World world, double dir)
	{
		if (world.getTotalWorldTime() != lastUpdateTick)
		{
			lastUpdateTick = world.getTotalWorldTime();
			double d0 = dir - rotation;
			d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
			rota += d0 * 0.1D;
			rota *= 0.8D;
			rotation = MathHelper.positiveModulo(rotation + rota, 1.0D);
		}

		return rotation;
	}

	@SideOnly(Side.CLIENT)
	@Nullable
	public BlockPos getOrePos()
	{
		return orePos;
	}

	@SideOnly(Side.CLIENT)
	public boolean refreshOrePos()
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc == null || !mc.isIntegratedServerRunning())
		{
			return false;
		}

		if (mc.world == null || mc.player == null || CaveUtils.isMoving(mc.player))
		{
			return false;
		}

		BlockPos pos = mc.player.getPosition();
		IBlockAccess blockAccess = MinecraftForgeClient.getRegionRenderCache(mc.world, pos);

		orePos = findOrePos(blockAccess, pos, 50);

		return orePos != null;
	}

	public static OreCompass get(ItemStack stack)
	{
		OreCompass compass = CaveCapabilities.getCapability(stack, CaveCapabilities.ORE_COMPASS);

		if (compass == null)
		{
			return new OreCompass();
		}

		return compass;
	}

	@Nullable
	public static BlockPos findOrePos(@Nullable IBlockAccess world, @Nullable BlockPos origin, int range)
	{
		if (world == null || origin == null || range <= 0)
		{
			return null;
		}

		int dist = 0;

		while (++dist < range)
		{
			BlockPos from = origin.add(dist, 3, dist);
			BlockPos to = origin.add(-dist, -3, -dist);

			for (BlockPos pos : BlockPos.getAllInBoxMutable(from, to))
			{
				if (world.isAirBlock(pos))
				{
					continue;
				}

				IBlockState state = world.getBlockState(pos);

				if (state.getBlock() instanceof BlockOre || state.getBlock() instanceof BlockRedstoneOre)
				{
					return new BlockPos(pos);
				}

				if (MinerStats.getPointAmount(state) > 0)
				{
					return new BlockPos(pos);
				}
			}
		}

		return null;
	}
}