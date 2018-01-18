package cavern.network.client;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import cavern.api.IMinerStats;
import cavern.stats.MinerStats;
import cavern.util.BlockMeta;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.GameData;

public class MinerStatsAdjustMessage implements IPlayerMessage<MinerStatsAdjustMessage, IMessage>
{
	private int point;
	private int rank;
	private int miningAssist;
	private Map<BlockMeta, Integer> records;

	public MinerStatsAdjustMessage() {}

	public MinerStatsAdjustMessage(IMinerStats stats)
	{
		this.point = stats.getPoint();
		this.rank = stats.getRank();
		this.miningAssist = stats.getMiningAssist();
		this.records = stats.getMiningRecords();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		point = buf.readInt();
		rank = buf.readInt();
		miningAssist = buf.readInt();
		records = Maps.newHashMap();

		int size = buf.readInt();

		for (int i = 0; i < size; ++i)
		{
			IBlockState state = GameData.getBlockStateIDMap().getByValue(buf.readInt());
			int count = buf.readInt();

			records.put(new BlockMeta(state), count);
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(point);
		buf.writeInt(rank);
		buf.writeInt(miningAssist);
		buf.writeInt(records.size());

		for (Entry<BlockMeta, Integer> record : records.entrySet())
		{
			buf.writeInt(GameData.getBlockStateIDMap().get(record.getKey().getBlockState()));
			buf.writeInt(record.getValue().intValue());
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage process(EntityPlayerSP player)
	{
		IMinerStats stats = MinerStats.get(player, true);

		if (stats != null)
		{
			stats.setPoint(point, false);
			stats.setRank(rank, false);
			stats.setMiningAssist(miningAssist, false);

			for (Entry<BlockMeta, Integer> record : records.entrySet())
			{
				stats.setMiningRecord(record.getKey(), record.getValue().intValue());
			}
		}

		return null;
	}
}