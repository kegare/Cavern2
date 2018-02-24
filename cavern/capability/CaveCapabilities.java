package cavern.capability;

import javax.annotation.Nullable;

import cavern.api.IMinerStats;
import cavern.api.IMiningData;
import cavern.api.IPlayerData;
import cavern.api.IPortalCache;
import cavern.inventory.InventoryMagicStorage;
import cavern.item.ItemMagicBook;
import cavern.item.OreCompass;
import cavern.magic.MagicBook;
import cavern.miningassist.MiningAssistUnit;
import cavern.util.CaveUtils;
import cavern.world.WorldCachedData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CaveCapabilities
{
	@CapabilityInject(IPortalCache.class)
	public static Capability<IPortalCache> PORTAL_CACHE = null;
	@CapabilityInject(IPlayerData.class)
	public static Capability<IPlayerData> PLAYER_DATA = null;
	@CapabilityInject(IMinerStats.class)
	public static Capability<IMinerStats> MINER_STATS = null;
	@CapabilityInject(IMiningData.class)
	public static Capability<IMiningData> MINING_DATA = null;
	@CapabilityInject(MiningAssistUnit.class)
	public static Capability<MiningAssistUnit> MINING_ASSIST = null;
	@CapabilityInject(OreCompass.class)
	public static Capability<OreCompass> ORE_COMPASS = null;
	@CapabilityInject(WorldCachedData.class)
	public static Capability<WorldCachedData> WORLD_CACHED_DATA = null;
	@CapabilityInject(MagicBook.class)
	public static Capability<MagicBook> MAGIC_BOOK = null;
	@CapabilityInject(InventoryMagicStorage.class)
	public static Capability<InventoryMagicStorage> MAGIC_STORAGE = null;

	public static void registerCapabilities()
	{
		CapabilityPortalCache.register();
		CapabilityPlayerData.register();
		CapabilityMinerStats.register();
		CapabilityMiningData.register();
		CapabilityMiningAssistUnit.register();
		CapabilityOreCompass.register();
		CapabilityWorldCachedData.register();
		CapabilityMagicBook.register();
		CapabilityMagicStorage.register();

		MinecraftForge.EVENT_BUS.register(new CaveCapabilities());
	}

	public static <T> boolean isValid(Capability<T> capability)
	{
		return capability != null;
	}

	public static <T> boolean hasCapability(ICapabilityProvider entry, Capability<T> capability)
	{
		return entry != null && isValid(capability) && entry.hasCapability(capability, null);
	}

	@Nullable
	public static <T> T getCapability(ICapabilityProvider entry, Capability<T> capability)
	{
		return hasCapability(entry, capability) ? entry.getCapability(capability, null) : null;
	}

	@SubscribeEvent
	public void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
	{
		event.addCapability(CaveUtils.getKey("portal_cache"), new CapabilityPortalCache());

		if (event.getObject() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.getObject();

			event.addCapability(CaveUtils.getKey("player_data"), new CapabilityPlayerData());
			event.addCapability(CaveUtils.getKey("miner_stats"), new CapabilityMinerStats(player));
			event.addCapability(CaveUtils.getKey("mining_data"), new CapabilityMiningData(player));
			event.addCapability(CaveUtils.getKey("mining_assist"), new CapabilityMiningAssistUnit(player));
			event.addCapability(CaveUtils.getKey("magic_book"), new CapabilityMagicBook());
		}
	}

	@SubscribeEvent
	public void onAttachWorldCapabilities(AttachCapabilitiesEvent<World> event)
	{
		World world = event.getObject();

		if (world instanceof WorldServer)
		{
			WorldServer worldServer = (WorldServer)world;

			event.addCapability(CaveUtils.getKey("world_cache"), new CapabilityWorldCachedData(worldServer));
		}
	}

	@SubscribeEvent
	public void onAttachItemStackCapabilities(AttachCapabilitiesEvent<ItemStack> event)
	{
		ItemStack stack = event.getObject();

		if (!stack.isEmpty())
		{
			if (stack.getItem() instanceof ItemMagicBook && ItemMagicBook.EnumType.byItemStack(stack) == ItemMagicBook.EnumType.STORAGE)
			{
				event.addCapability(stack.getItem().getRegistryName(), new CapabilityMagicStorage());
			}
		}
	}

	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event)
	{
		EntityPlayer player = event.getEntityPlayer();

		if (player.world.isRemote)
		{
			return;
		}

		EntityPlayer original = event.getOriginal();

		IPortalCache originalPortalCache = getCapability(original, PORTAL_CACHE);
		IPortalCache portalCache = getCapability(player, PORTAL_CACHE);

		if (originalPortalCache != null && portalCache != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();

			originalPortalCache.writeToNBT(nbt);
			portalCache.readFromNBT(nbt);
		}

		IPlayerData originalPlayerData = getCapability(original, PLAYER_DATA);
		IPlayerData playerData = getCapability(player, PLAYER_DATA);

		if (originalPlayerData != null && playerData != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();

			originalPlayerData.writeToNBT(nbt);
			playerData.readFromNBT(nbt);
		}

		IMinerStats originalMinerStats = getCapability(original, MINER_STATS);
		IMinerStats minerStats = getCapability(player, MINER_STATS);

		if (originalMinerStats != null && minerStats != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();

			originalMinerStats.writeToNBT(nbt);
			minerStats.readFromNBT(nbt);
		}
	}
}