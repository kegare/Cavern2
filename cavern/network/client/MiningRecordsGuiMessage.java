package cavern.network.client;

import cavern.client.gui.GuiMiningRecords;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MiningRecordsGuiMessage implements IClientMessage<MiningRecordsGuiMessage, IMessage>
{
	@SideOnly(Side.CLIENT)
	@Override
	public IMessage process(Minecraft mc)
	{
		mc.displayGuiScreen(new GuiMiningRecords());

		return null;
	}
}