package cavern.network.client;

import cavern.data.Miner;
import cavern.data.MiningData;
import cavern.util.BlockMeta;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.GameData;

public class MiningMessage implements IPlayerMessage<MiningMessage, IMessage>
{
	private IBlockState state;
	private int point;

	public MiningMessage() {}

	public MiningMessage(IBlockState state, int point)
	{
		this.state = state;
		this.point = point;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		state = GameData.getBlockStateIDMap().getByValue(buf.readInt());
		point = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(GameData.getBlockStateIDMap().get(state));
		buf.writeInt(point);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage process(EntityPlayerSP player)
	{
		Miner.get(player).addMiningRecord(new BlockMeta(state));

		MiningData.get(player).notifyMining(state, point);

		return null;
	}
}