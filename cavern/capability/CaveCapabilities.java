package cavern.capability;

import javax.annotation.Nullable;

import cavern.api.data.IMiner;
import cavern.api.data.IMiningData;
import cavern.data.PlayerData;
import cavern.data.PortalCache;
import cavern.inventory.InventoryMagicStorage;
import cavern.item.ItemMagicBook;
import cavern.item.OreCompass;
import cavern.magic.MagicBook;
import cavern.miningassist.MiningAssistUnit;
import cavern.util.CaveUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CaveCapabilities
{
	@CapabilityInject(PortalCache.class)
	public static Capability<PortalCache> PORTAL_CACHE = null;
	@CapabilityInject(PlayerData.class)
	public static Capability<PlayerData> PLAYER_DATA = null;
	@CapabilityInject(IMiner.class)
	public static Capability<IMiner> MINER = null;
	@CapabilityInject(IMiningData.class)
	public static Capability<IMiningData> MINING_DATA = null;
	@CapabilityInject(MiningAssistUnit.class)
	public static Capability<MiningAssistUnit> MINING_ASSIST = null;
	@CapabilityInject(OreCompass.class)
	public static Capability<OreCompass> ORE_COMPASS = null;
	@CapabilityInject(MagicBook.class)
	public static Capability<MagicBook> MAGIC_BOOK = null;
	@CapabilityInject(InventoryMagicStorage.class)
	public static Capability<InventoryMagicStorage> MAGIC_STORAGE = null;

	public static void registerCapabilities()
	{
		CapabilityPortalCache.register();
		CapabilityPlayerData.register();
		CapabilityMiner.register();
		CapabilityMiningData.register();
		CapabilityMiningAssistUnit.register();
		CapabilityOreCompass.register();
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
			event.addCapability(CaveUtils.getKey("miner_stats"), new CapabilityMiner(player));
			event.addCapability(CaveUtils.getKey("mining_data"), new CapabilityMiningData(player));
			event.addCapability(CaveUtils.getKey("mining_assist"), new CapabilityMiningAssistUnit(player));
			event.addCapability(CaveUtils.getKey("magic_book"), new CapabilityMagicBook());
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

		PortalCache originalPortalCache = getCapability(original, PORTAL_CACHE);
		PortalCache portalCache = getCapability(player, PORTAL_CACHE);

		if (originalPortalCache != null && portalCache != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();

			originalPortalCache.writeToNBT(nbt);
			portalCache.readFromNBT(nbt);
		}

		PlayerData originalPlayerData = getCapability(original, PLAYER_DATA);
		PlayerData playerData = getCapability(player, PLAYER_DATA);

		if (originalPlayerData != null && playerData != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();

			originalPlayerData.writeToNBT(nbt);
			playerData.readFromNBT(nbt);
		}

		IMiner originalMiner = getCapability(original, MINER);
		IMiner miner = getCapability(player, MINER);

		if (originalMiner != null && miner != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();

			originalMiner.writeToNBT(nbt);
			miner.readFromNBT(nbt);
		}
	}
}