package cavern.world;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import cavern.block.BlockPortalCavern;
import cavern.capability.CaveCapabilities;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class WorldCachedData
{
	private final WorldServer worldServer;
	private final Map<ResourceLocation, Teleporter> teleporters = Maps.newHashMap();

	public WorldCachedData(WorldServer world)
	{
		this.worldServer = world;
	}

	public Teleporter getCachedTeleporter(ResourceLocation key, @Nullable Teleporter teleporter)
	{
		if (teleporters.containsKey(key))
		{
			return teleporters.get(key);
		}

		if (teleporter != null)
		{
			teleporters.put(key, teleporter);

			worldServer.customTeleporters.add(teleporter);
		}

		return teleporter;
	}

	public Teleporter getPortalTeleporter(BlockPortalCavern portal)
	{
		return getCachedTeleporter(portal.getRegistryName(), new TeleporterCavern(worldServer, portal));
	}

	public static WorldCachedData get(WorldServer world)
	{
		WorldCachedData data = CaveCapabilities.getCapability(world, CaveCapabilities.WORLD_CACHED_DATA);

		if (data == null)
		{
			return new WorldCachedData(world);
		}

		return data;
	}
}