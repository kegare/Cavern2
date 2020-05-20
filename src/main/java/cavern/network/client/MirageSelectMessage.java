package cavern.network.client;

import java.util.Set;

import com.google.common.collect.Sets;

import cavern.client.gui.GuiSelectMirageWorld;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MirageSelectMessage implements IClientMessage<MirageSelectMessage, IMessage>
{
	public final Set<DimensionType> dimensions = Sets.newHashSet();

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int i = buf.readInt();

		if (i <= 0)
		{
			return;
		}

		while (i-- > 0)
		{
			int dim = buf.readInt();

			try
			{
				dimensions.add(DimensionType.getById(dim));
			}
			catch (IllegalArgumentException e)
			{
				continue;
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(dimensions.size());

		for (DimensionType type : dimensions)
		{
			if (type != null)
			{
				buf.writeInt(type.getId());
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage process(Minecraft mc)
	{
		GuiSelectMirageWorld gui = new GuiSelectMirageWorld();

		if (!dimensions.isEmpty())
		{
			gui.dimensions.addAll(dimensions);
		}

		mc.displayGuiScreen(gui);

		return null;
	}
}