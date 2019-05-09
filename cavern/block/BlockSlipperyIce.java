package cavern.block;

import cavern.core.Cavern;
import cavern.plugin.HaCPlugin;
import cavern.util.PlayerHelper;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IHeatTile;
import defeatedcrow.hac.api.climate.IHumidityTile;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;

@InterfaceList
({
	@Interface(iface = "defeatedcrow.hac.api.climate.IHeatTile", modid = HaCPlugin.LIB_MODID, striprefs = true),
	@Interface(iface = "defeatedcrow.hac.api.climate.IHumidityTile", modid = HaCPlugin.LIB_MODID, striprefs = true)
})
public class BlockSlipperyIce extends BlockPackedIce implements IHeatTile, IHumidityTile
{
	public BlockSlipperyIce()
	{
		super();
		this.setTranslationKey("slipperyIce");
		this.setHardness(0.5F);
		this.setSoundType(SoundType.GLASS);
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}

	@Override
	public void onEntityWalk(World world, BlockPos pos, Entity entity)
	{
		super.onEntityWalk(world, pos, entity);

		if (!world.isRemote && entity.ticksExisted % 20 == 0 && entity instanceof EntityPlayer)
		{
			PlayerHelper.grantAdvancement((EntityPlayer)entity, "slip_ice");
		}
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face)
	{
		return world.isAirBlock(pos.up()) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
	}

	@Override
	public float getSlipperiness(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity)
	{
		return 1.05F;
	}

	@Override
	public DCHeatTier getHeatTier(World world, BlockPos target, BlockPos pos)
	{
		return DCHeatTier.COLD;
	}

	@Override
	public DCHumidity getHumdiity(World world, BlockPos target, BlockPos pos)
	{
		return DCHumidity.WET;
	}
}