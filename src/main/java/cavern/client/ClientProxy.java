package cavern.client;

import cavern.core.CommonProxy;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public String translate(String key)
	{
		return I18n.format(key);
	}

	@Override
	public String translateFormat(String key, Object... format)
	{
		return I18n.format(key, format);
	}

	@Override
	public boolean isSinglePlayer()
	{
		return FMLClientHandler.instance().getClient().isSingleplayer();
	}

	@Override
	public void loadChunk(World world, int x, int z)
	{
		if (world instanceof WorldClient)
		{
			((WorldClient)world).getChunkProvider().loadChunk(x, z);
		}
	}
}