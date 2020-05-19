package cavern.api.event;

import cavern.api.data.IMiner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class MinerEvent extends PlayerEvent
{
	private final IMiner miner;

	public MinerEvent(EntityPlayer player, IMiner stats)
	{
		super(player);
		this.miner = stats;
	}

	public IMiner getMiner()
	{
		return miner;
	}

	@Cancelable
	public static class AddPoint extends MinerEvent
	{
		private final int originalPoint;

		private int newPoint;

		public AddPoint(EntityPlayer player, IMiner stats, int point)
		{
			super(player, stats);
			this.originalPoint = point;
			this.newPoint = point;
		}

		public int getPoint()
		{
			return originalPoint;
		}

		public int getNewPoint()
		{
			return newPoint;
		}

		public void setNewPoint(int point)
		{
			newPoint = point;
		}
	}

	public static class PromoteRank extends MinerEvent
	{
		public PromoteRank(EntityPlayer player, IMiner stats)
		{
			super(player, stats);
		}
	}
}