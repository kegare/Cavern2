package cavern.network.server;

import cavern.api.data.IMiner;
import cavern.config.MiningAssistConfig;
import cavern.data.Miner;
import cavern.data.MinerRank;
import cavern.miningassist.MiningAssist;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MiningAssistMessage implements IPlayerMessage<MiningAssistMessage, IMessage>
{
	private MiningAssist assist;

	public MiningAssistMessage() {}

	public MiningAssistMessage(MiningAssist assist)
	{
		this.assist = assist;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		try
		{
			assist = MiningAssist.get(buf.readInt());
		}
		catch (IllegalArgumentException e)
		{
			assist = null;
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		if (assist != null)
		{
			buf.writeInt(assist.getType());
		}
	}

	@Override
	public IMessage process(EntityPlayerMP player)
	{
		IMiner miner = Miner.get(player);

		if (miner.getRank() < MiningAssistConfig.minerRank.getValue())
		{
			ITextComponent component = new TextComponentTranslation(MinerRank.get(MiningAssistConfig.minerRank.getValue()).getUnlocalizedName());
			component.getStyle().setItalic(true);
			component = new TextComponentTranslation("cavern.miningassist.toggle.failed.message", component);
			component.getStyle().setColor(TextFormatting.RED);

			player.sendMessage(component);
		}
		else
		{
			if (assist == null)
			{
				miner.toggleMiningAssist();
			}
			else
			{
				miner.setMiningAssist(assist.getType());
			}

			miner.adjustData();

			ITextComponent component = new TextComponentTranslation(MiningAssist.get(miner.getMiningAssist()).getUnlocalizedName());
			component.getStyle().setColor(TextFormatting.GRAY).setItalic(true);
			component = new TextComponentTranslation("cavern.miningassist.toggle.message", component);

			player.sendMessage(component);
		}

		return null;
	}
}