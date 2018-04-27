package cavern.miningassist;

import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.collect.Maps;

import cavern.capability.CaveCapabilities;
import cavern.config.MiningAssistConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

public class MiningAssistUnit
{
	private final EntityPlayer player;

	private MiningSnapshot snapshot;
	private boolean captureDrops, captureExperiences;

	private Map<BlockPos, NonNullList<ItemStack>> capturedDrops;
	private Map<BlockPos, Integer> capturedExperiences;

	public MiningAssistUnit(EntityPlayer player)
	{
		this.player = player;
	}

	public MiningSnapshot getSnapshot(MiningAssist type, BlockPos pos, IBlockState state)
	{
		return getSnapshot(type, pos, state, true);
	}

	public MiningSnapshot getSnapshot(MiningAssist type, BlockPos pos, IBlockState state, boolean refresh)
	{
		if (snapshot == null || refresh && !snapshot.equals(player.world, pos))
		{
			if (MiningAssistConfig.priorityQuickMining && MiningAssist.QUICK.isEffectiveTarget(player.getHeldItemMainhand(), state))
			{
				snapshot = new QuickMiningSnapshot(player.world, pos, state, player);
			}
			else switch (type)
			{
				case QUICK:
					snapshot = new QuickMiningSnapshot(player.world, pos, state, player);
					break;
				case RANGED:
					snapshot = new RangedMiningSnapshot(player.world, pos, state, player);
					break;
				case ADIT:
					snapshot = new AditMiningSnapshot(player.world, pos, state, player);
					break;
				default:
			}
		}

		if (snapshot != null && !snapshot.isChecked())
		{
			snapshot.checkForMining();
		}

		return snapshot;
	}

	@Nullable
	public MiningSnapshot getCachedSnapshot()
	{
		return snapshot;
	}

	public void clearCache()
	{
		snapshot = null;
	}

	public float getBreakSpeed(MiningSnapshot snapshot)
	{
		return snapshot.getBreakSpeed();
	}

	public boolean getCaptureDrops()
	{
		return captureDrops;
	}

	@Nullable
	public Map<BlockPos, NonNullList<ItemStack>> captureDrops(boolean value)
	{
		captureDrops = value;

		if (value)
		{
			capturedDrops = Maps.newHashMap();

			return null;
		}

		return capturedDrops;
	}

	public boolean addDrops(BlockPos pos, NonNullList<ItemStack> drops)
	{
		if (!captureDrops || capturedDrops == null || pos == null || drops == null || drops.isEmpty())
		{
			return false;
		}

		capturedDrops.put(pos, drops);

		return true;
	}

	public boolean getCaptureExperiences()
	{
		return captureExperiences;
	}

	@Nullable
	public Map<BlockPos, Integer> captureExperiences(boolean value)
	{
		captureExperiences = value;

		if (value)
		{
			capturedExperiences = Maps.newHashMap();

			return null;
		}

		return capturedExperiences;
	}

	public boolean addExperience(BlockPos pos, int experience)
	{
		if (!captureExperiences || capturedExperiences == null || pos == null || experience <= 0)
		{
			return false;
		}

		capturedExperiences.put(pos, Integer.valueOf(experience));

		return true;
	}

	public static MiningAssistUnit get(EntityPlayer player)
	{
		return ObjectUtils.defaultIfNull(CaveCapabilities.getCapability(player, CaveCapabilities.MINING_ASSIST), new MiningAssistUnit(player));
	}
}