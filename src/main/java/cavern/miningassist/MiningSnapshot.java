package cavern.miningassist;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ObjectUtils;

import cavern.config.property.ConfigBlocks;
import cavern.util.CaveUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class MiningSnapshot implements Comparator<BlockPos>
{
	protected final World world;
	protected final BlockPos originPos;
	protected final IBlockState originState;

	protected EntityLivingBase miner;

	protected Set<BlockPos> miningTargets;
	protected float breakSpeed;

	public MiningSnapshot(World world, BlockPos pos, IBlockState state)
	{
		this.world = world;
		this.originPos = pos;
		this.originState = state;
	}

	public MiningSnapshot(World world, BlockPos pos, IBlockState state, EntityLivingBase entity)
	{
		this(world, pos, state);
		this.miner = entity;
	}

	public World getWorld()
	{
		return world;
	}

	public BlockPos getOriginPos()
	{
		return originPos;
	}

	public IBlockState getOriginState()
	{
		return originState;
	}

	@Nullable
	public EntityLivingBase getMiner()
	{
		return miner;
	}

	public boolean isChecked()
	{
		return miningTargets != null;
	}

	public boolean isEmpty()
	{
		return miningTargets == null || miningTargets.isEmpty();
	}

	public boolean equals(World worldIn, BlockPos pos)
	{
		if (worldIn == null || pos == null)
		{
			return false;
		}

		return world.provider.getDimensionType() == worldIn.provider.getDimensionType() && originPos.equals(pos);
	}

	public int getTargetCount()
	{
		return isEmpty() ? 0 : miningTargets.size();
	}

	public Set<BlockPos> getTargets()
	{
		return ObjectUtils.defaultIfNull(miningTargets, Collections.emptySet());
	}

	@Nullable
	public ConfigBlocks getValidTargetBlocks()
	{
		return null;
	}

	public boolean hasValidTargetBlocks()
	{
		return getValidTargetBlocks() != null && !getValidTargetBlocks().isEmpty();
	}

	public void checkForMining()
	{
		breakSpeed = 0.0F;

		if (miningTargets != null && miner != null && miner instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)miner;
			int count = miningTargets.size();

			breakSpeed = player.getDigSpeed(originState, originPos);

			if (count <= 1)
			{
				return;
			}

			float origin = world.getBlockState(originPos).getBlockHardness(world, originPos);
			float max = origin;
			float total = 0.0F;

			for (BlockPos pos : miningTargets)
			{
				float hardness = world.getBlockState(pos).getBlockHardness(world, pos);

				if (hardness > max)
				{
					max = hardness;
				}

				total += hardness;
			}

			breakSpeed /= total * 0.25F;

			float dist = max - origin;

			if (dist >= 5.0F)
			{
				breakSpeed /= dist * 0.05F;
			}
		}
	}

	public float getBreakSpeed()
	{
		return breakSpeed;
	}

	public boolean offer(BlockPos pos)
	{
		if (miningTargets == null)
		{
			return false;
		}

		if (validTarget(pos) && !miningTargets.contains(pos))
		{
			return miningTargets.add(pos);
		}

		return false;
	}

	public boolean validTarget(BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);

		if (state.getBlock().isAir(state, world, pos) || state.getBlockHardness(world, pos) < 0.0F)
		{
			return false;
		}

		if (hasValidTargetBlocks())
		{
			return getValidTargetBlocks().hasBlockState(state);
		}

		if (miner != null)
		{
			return miner.getHeldItemMainhand().canHarvestBlock(state);
		}

		return CaveUtils.areBlockStatesEqual(state, originState);
	}

	@Override
	public int compare(BlockPos o1, BlockPos o2)
	{
		int i = CaveUtils.compareWithNull(o1, o2);

		if (i == 0 && o1 != null && o2 != null)
		{
			i = Integer.compare(originPos.compareTo(o1), originPos.compareTo(o2));

			if (i == 0)
			{
				i = o1.compareTo(o2);
			}
		}

		return i;
	}
}