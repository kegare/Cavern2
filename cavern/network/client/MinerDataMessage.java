package cavern.network.client;

import cavern.api.data.IMiner;
import cavern.data.Miner;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MinerDataMessage implements IPlayerMessage<MinerDataMessage, IMessage>
{
	private int point;
	private int rank;
	private int miningAssist;

	public MinerDataMessage() {}

	public MinerDataMessage(IMiner stats)
	{
		this.point = stats.getPoint();
		this.rank = stats.getRank();
		this.miningAssist = stats.getMiningAssist();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		point = buf.readInt();
		rank = buf.readInt();
		miningAssist = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(point);
		buf.writeInt(rank);
		buf.writeInt(miningAssist);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage process(EntityPlayerSP player)
	{
		IMiner miner = Miner.get(player, true);

		if (miner != null)
		{
			miner.setPoint(point, false);
			miner.setRank(rank, false);
			miner.setMiningAssist(miningAssist, false);
		}

		return null;
	}
}