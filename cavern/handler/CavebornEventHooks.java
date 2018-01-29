package cavern.handler;

import java.util.Set;

import com.google.common.collect.Sets;

import cavern.block.BlockPortalCavern;
import cavern.config.GeneralConfig;
import cavern.config.property.ConfigCaveborn;
import cavern.util.CaveUtils;
import cavern.util.ItemMeta;
import cavern.world.CaveDimensions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

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

		ConfigCaveborn.Type caveborn = GeneralConfig.caveborn.getType();

		if (caveborn == ConfigCaveborn.Type.DISABLED)
		{
			return;
		}

		BlockPortalCavern portal = caveborn.getPortalBlock();

		if (portal == null || portal.getDimension() == null)
		{
			return;
		}

		EntityPlayer player = event.getEntityPlayer();

		player.dimension = portal.getDimension().getId();

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

		if (!FIRST_PLAYERS.contains(player.getCachedUniqueIdString()))
		{
			return;
		}

		WorldServer world = player.getServerWorld();
		ConfigCaveborn.Type caveborn = GeneralConfig.caveborn.getType();
		BlockPortalCavern portal = caveborn.getPortalBlock();

		player.timeUntilPortal = player.getPortalCooldown();

		portal.getTeleporter(world).placeInPortal(player, player.rotationYaw);

		FIRST_PLAYERS.remove(event.player.getCachedUniqueIdString());

		DimensionType type = portal.getDimension();

		if (type == CaveDimensions.CAVERN)
		{
			CaveUtils.grantCriterion(player, "root", "entered_cavern");
		}
		else
		{
			String name = type.getName();

			CaveUtils.grantCriterion(player, "enter_the_" + name, "entered_" + name);
		}

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
		double y = player.posY + 0.25D;
		double z = player.posZ;

		world.playSound(null, x, y, z, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 0.65F);

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
}