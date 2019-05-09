package cavern.network.server;

import cavern.api.IMinerStats;
import cavern.config.MiningAssistConfig;
import cavern.miningassist.MiningAssist;
import cavern.stats.MinerRank;
import cavern.stats.MinerStats;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MiningAssistMessage implements IPlayerMessage<MiningAssistMessage, IMessage>
{
	@Override
	public IMessage process(EntityPlayerMP player)
	{
		IMinerStats stats = MinerStats.get(player);

		if (stats.getRank() < MiningAssistConfig.minerRank.getValue())
		{
			ITextComponent component = new TextComponentTranslation(MinerRank.get(MiningAssistConfig.minerRank.getValue()).getTranslationKey());
			component.getStyle().setItalic(Boolean.valueOf(true));
			component = new TextComponentTranslation("cavern.miningassist.toggle.failed.message", component);
			component.getStyle().setColor(TextFormatting.RED);

			player.sendMessage(component);
		}
		else
		{
			stats.toggleMiningAssist();
			stats.adjustData();

			ITextComponent component = new TextComponentTranslation(MiningAssist.get(stats.getMiningAssist()).getUnlocalizedName());
			component.getStyle().setColor(TextFormatting.GRAY).setItalic(Boolean.valueOf(true));
			component = new TextComponentTranslation("cavern.miningassist.toggle.message", component);

			player.sendMessage(component);
		}

		return null;
	}
}