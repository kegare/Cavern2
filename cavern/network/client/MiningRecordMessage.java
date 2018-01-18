package cavern.network.client;

import cavern.api.IMineBonus;
import cavern.config.GeneralConfig;
import cavern.network.server.MineBonusMessage;
import cavern.stats.MinerStats;
import cavern.util.BlockMeta;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.GameData;

public class MiningRecordMessage implements IPlayerMessage<MiningRecordMessage, IMessage>
{
	private IBlockState state;
	private int point;

	public MiningRecordMessage() {}

	public MiningRecordMessage(IBlockState state, int point)
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
		BlockMeta blockMeta = new BlockMeta(state);

		MinerStats.get(player).addMiningRecord(blockMeta);
		MinerStats.setLastMine(blockMeta, point);

		long time = Minecraft.getSystemTime();

		if (GeneralConfig.miningCombo)
		{
			if (time - MinerStats.lastMineTime <= 15000L)
			{
				++MinerStats.mineCombo;

				int combo = MinerStats.mineCombo;
				boolean flag = false;

				for (IMineBonus bonus : MinerStats.MINE_BONUS)
				{
					if (bonus.canMineBonus(combo, player))
					{
						bonus.onMineBonus(true, combo, player);

						flag = true;
					}
				}

				if (flag)
				{
					return new MineBonusMessage(combo);
				}
			}
			else
			{
				MinerStats.mineCombo = 0;
			}
		}

		MinerStats.lastMineTime = time;

		return null;
	}
}