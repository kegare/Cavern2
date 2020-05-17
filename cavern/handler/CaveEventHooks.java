package cavern.handler;

import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.collect.Sets;

import cavern.api.CavernAPI;
import cavern.api.item.IIceEquipment;
import cavern.block.BlockLeavesPerverted;
import cavern.block.BlockLogPerverted;
import cavern.block.BlockPortalCavern;
import cavern.block.BlockSaplingPerverted;
import cavern.block.CaveBlocks;
import cavern.config.GeneralConfig;
import cavern.data.Miner;
import cavern.data.PlayerData;
import cavern.item.CaveItems;
import cavern.magic.Magic;
import cavern.magic.MagicBook;
import cavern.magic.MagicInvisible;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.CustomSeedMessage;
import cavern.util.PlayerHelper;
import cavern.world.CaveDimensions;
import cavern.world.CustomSeedData;
import cavern.world.CustomSeedProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.world.BlockEvent.PortalSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class CaveEventHooks
{
	public static final Random RANDOM = new Random();

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		World world = event.getWorld();

		if (!world.isRemote && world.provider instanceof CustomSeedProvider)
		{
			CustomSeedData data = ((CustomSeedProvider)world.provider).getSeedData();

			if (data == null)
			{
				return;
			}

			if (event.getEntity() instanceof EntityPlayerMP)
			{
				CaveNetworkRegistry.sendTo(new CustomSeedMessage(data.getSeedValue(world.getWorldInfo().getSeed())), (EntityPlayerMP)event.getEntity());
			}
		}
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event)
	{
		if (!(event.player instanceof EntityPlayerMP))
		{
			return;
		}

		EntityPlayerMP player = (EntityPlayerMP)event.player;

		if (CavernAPI.dimension.isInCaveDimensions(player))
		{
			WorldServer world = player.getServerWorld();
			DimensionType type = world.provider.getDimensionType();

			if (CavernAPI.dimension.isCaves(type))
			{
				PlayerHelper.grantCriterion(player, "root", "entered_cavern");
			}

			if (type != CaveDimensions.CAVERN)
			{
				String name = type.getName();

				if (name.startsWith("the_"))
				{
					name = name.substring(4);
				}

				PlayerHelper.grantCriterion(player, "enter_the_" + name, "entered_" + name);
			}
		}

		Miner.adjustData(player);
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		if (!(event.player instanceof EntityPlayerMP))
		{
			return;
		}

		EntityPlayerMP player = (EntityPlayerMP)event.player;

		if (CavernAPI.dimension.isInCaverns(player))
		{
			WorldServer world = player.getServerWorld();
			BlockPos pos = player.getPosition().down();

			if (world.getBlockState(pos).getBlockHardness(world, pos) < 0.0F)
			{
				CaveItems.MIRAGE_BOOK.placeEntity(world, player, player.rotationYaw);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		ItemStack stack = event.getItemStack();

		if (stack.isEmpty())
		{
			return;
		}

		World world = event.getWorld();
		BlockPos pos = event.getPos();
		IBlockState state = world.getBlockState(pos);

		if (state.getBlock() != Blocks.MOSSY_COBBLESTONE && (state.getBlock() != Blocks.STONEBRICK || state.getBlock().getMetaFromState(state) != BlockStoneBrick.MOSSY_META))
		{
			return;
		}

		EntityPlayer player = event.getEntityPlayer();
		Set<BlockPortalCavern> portals = Sets.newHashSet();

		portals.add(CaveBlocks.CAVERN_PORTAL);
		portals.add(CaveBlocks.HUGE_CAVERN_PORTAL);
		portals.add(CaveBlocks.AQUA_CAVERN_PORTAL);
		portals.add(CaveBlocks.MIRAGE_PORTAL);

		Item portalItem = Items.AIR;

		for (BlockPortalCavern portal : portals)
		{
			if (portal.isTriggerItem(stack))
			{
				portalItem = Item.getItemFromBlock(portal);

				break;
			}
		}

		if (portalItem != Items.AIR)
		{
			EnumFacing facing = ObjectUtils.defaultIfNull(event.getFace(), EnumFacing.UP);
			Vec3d hit = event.getHitVec();
			EnumActionResult result = portalItem.onItemUse(player, world, pos, event.getHand(), facing, (float)hit.x, (float)hit.y, (float)hit.z);

			if (result == EnumActionResult.SUCCESS)
			{
				event.setCancellationResult(result);
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onPortalSpawn(PortalSpawnEvent event)
	{
		World world = event.getWorld();

		if (CavernAPI.dimension.isCaveDimensions(world.provider.getDimensionType()))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onLivingSetAttackTarget(LivingSetAttackTargetEvent event)
	{
		EntityLivingBase target = event.getTarget();

		if (target == null || !(target instanceof EntityPlayer))
		{
			return;
		}

		EntityPlayer player = (EntityPlayer)target;
		Magic magic = MagicBook.get(player).getSpellingMagic();

		if (magic == null || !(magic instanceof MagicInvisible))
		{
			return;
		}

		EntityLivingBase entity = event.getEntityLiving();

		if (entity instanceof EntityLiving)
		{
			((EntityLiving)entity).setAttackTarget(null);
		}

		entity.getCombatTracker().reset();
	}

	@SubscribeEvent
	public void onPlayerSleepInBed(PlayerSleepInBedEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();

		if (!CavernAPI.dimension.isInCaveDimensions(player))
		{
			return;
		}

		SleepResult result = null;
		World world = player.world;

		if (!world.isRemote)
		{
			PlayerData data = PlayerData.get(player);
			long worldTime = world.getTotalWorldTime();
			long sleepTime = data.getLastSleepTime();
			long requireTime = GeneralConfig.sleepWaitTime * 20;

			if (sleepTime <= 0L)
			{
				sleepTime = worldTime;
				requireTime = 0L;

				data.setLastSleepTime(sleepTime);
			}

			if (requireTime > 0L && sleepTime + requireTime > worldTime)
			{
				result = SleepResult.OTHER_PROBLEM;

				long remainTime = requireTime - (worldTime - sleepTime);
				int min = MathHelper.ceil(remainTime / 20 / 60 + 1);

				player.sendStatusMessage(new TextComponentTranslation("cavern.message.sleep.still", min), true);
			}
		}

		if (result == null)
		{
			result = PlayerHelper.trySleep(player, event.getPos());
		}

		if (!world.isRemote && result == SleepResult.OK)
		{
			PlayerData.get(player).setLastSleepTime(world.getTotalWorldTime());
		}

		event.setResult(result);
	}

	@SubscribeEvent
	public void onPlayerWakeUp(PlayerWakeUpEvent event)
	{
		if (!GeneralConfig.sleepRefresh)
		{
			return;
		}

		EntityPlayer player = event.getEntityPlayer();
		World world = player.world;

		if (world.isRemote || !player.shouldHeal())
		{
			return;
		}

		if (CavernAPI.dimension.isInCaveDimensions(player))
		{
			player.heal(player.getMaxHealth() * 0.5F);
		}
	}

	@SubscribeEvent
	public void onItemCrafted(ItemCraftedEvent event)
	{
		EntityPlayer player = event.player;
		ItemStack stack = event.crafting;
		World world = player.world;

		if (!world.isRemote && !stack.isEmpty())
		{
			if (stack.getItem() instanceof IIceEquipment)
			{
				int charge = ((IIceEquipment)stack.getItem()).getCharge(stack);

				if (charge > 0 && stack.getTagCompound().getBoolean("AfterIceCharge"))
				{
					PlayerHelper.grantAdvancement(player, "ice_charge");

					stack.getTagCompound().removeTag("AfterIceCharge");
				}
			}
		}
	}

	@SubscribeEvent
	public void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event)
	{
		ItemStack stack = event.getItemStack();
		Block block = Block.getBlockFromItem(stack.getItem());

		if (block == null)
		{
			return;
		}

		if (block instanceof BlockLogPerverted)
		{
			event.setBurnTime(100);
		}
		else if (block instanceof BlockLeavesPerverted || block instanceof BlockSaplingPerverted)
		{
			event.setBurnTime(35);
		}
	}
}