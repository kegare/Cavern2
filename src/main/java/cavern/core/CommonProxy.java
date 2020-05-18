package cavern.core;

import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

@SuppressWarnings("deprecation")
public class CommonProxy
{
	public String translate(String key)
	{
		return I18n.translateToLocal(key);
	}

	public String translateFormat(String key, Object... format)
	{
		return I18n.translateToLocalFormatted(key, format);
	}

	public boolean isSinglePlayer()
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer();
	}

	public void loadChunk(World world, int x, int z)
	{
		if (world instanceof WorldServer)
		{
			((WorldServer)world).getChunkProvider().provideChunk(x, z);
		}
	}

	public void loadChunks(World world, int x, int z, int range)
	{
		loadChunk(world, x, z);

		if (range <= 0)
		{
			return;
		}

		for (int i = 1; i <= range; ++i)
		{
			for (int cx = -i; cx <= i; ++cx)
			{
				for (int cz = -i; cz <= i; ++cz)
				{
					if (Math.abs(cx) < i && Math.abs(cz) < i) continue;

					loadChunk(world, x + cx, z + cz);
				}
			}
		}
	}
}