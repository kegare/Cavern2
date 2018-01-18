package cavern.handler;

import java.util.Set;

import com.google.common.collect.Sets;

import cavern.block.BlockPortalCavern;
import cavern.config.GeneralConfig;
import cavern.config.property.ConfigCaveborn;
import cavern.util.CaveUtils;
import cavern.util.ItemMeta;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class CavebornEventHooks
{
	public static final Set<String> FIRST_PLAYERS = Sets.newHashSet();

	@SubscribeEvent
	public void onPlayerLoadFromFile(PlayerEvent.LoadFromFile event)
	{
		String uuid = event.getPlayerUUID();

		for (String str : event.getPlayerDirectory().list())
		{
			if (str.startsWith(uuid))
			{
				return;
			}
		}

		FIRST_PLAYERS.add(uuid);
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)
	{
		if (!(event.player instanceof EntityPlayerMP))
		{
			return;
		}

		EntityPlayerMP player = (EntityPlayerMP)event.player;
		ConfigCaveborn.Type caveborn = GeneralConfig.caveborn.getType();

		if (caveborn == ConfigCaveborn.Type.DISABLED || !FIRST_PLAYERS.contains(player.getCachedUniqueIdString()))
		{
			return;
		}

		MinecraftServer server = player.mcServer;
		BlockPortalCavern portal = caveborn.getPortalBlock();

		if (portal != null && portal.getDimension() != null)
		{
			DimensionType type = portal.getDimension();
			Teleporter teleporter = portal.getTeleporter(server.getWorld(type.getId()));

			boolean force = player.forceSpawn;

			player.forceSpawn = true;
			player.timeUntilPortal = player.getPortalCooldown();

			CaveUtils.transferPlayerToDimension(player, type, teleporter);

			player.forceSpawn = force;

			WorldServer world = player.getServerWorld();
			BlockPos pos = player.getPosition();

			for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-1, -1, -1), pos.add(1, 1, 1)))
			{
				if (world.getBlockState(blockpos).getBlock() == portal)
				{
					world.setBlockToAir(blockpos);

					break;
				}
			}

			double x = player.posX;
			double y = player.posY + player.getEyeHeight();
			double z = player.posZ;

			world.playSound(null, x, y, z, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 0.65F);
		}

		WorldServer world = player.getServerWorld();
		double x = player.posX;
		double y = player.posY + 0.25D;
		double z = player.posZ;

		for (ItemMeta itemMeta : GeneralConfig.cavebornBonusItems.getItems())
		{
			ItemStack stack = itemMeta.getItemStack();

			if (stack.isStackable())
			{
				stack = itemMeta.getItemStack(MathHelper.getInt(CaveEventHooks.RANDOM, 4, 16));
			}

			InventoryHelper.spawnItemStack(world, x, y, z, stack);
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event)
	{
		FIRST_PLAYERS.remove(event.player.getCachedUniqueIdString());
	}
}