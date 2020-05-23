package cavern.handler;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import cavern.api.data.IMiner;
import cavern.config.MiningAssistConfig;
import cavern.core.Cavern;
import cavern.data.Miner;
import cavern.miningassist.MiningAssist;
import cavern.miningassist.MiningAssistUnit;
import cavern.miningassist.MiningSnapshot;
import cavern.util.CaveUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class MiningAssistEventHooks
{
	private boolean isActive(EntityPlayer player, IBlockState state)
	{
		ItemStack held = player.getHeldItemMainhand();

		if (!MiningAssistConfig.isEffectiveItem(held))
		{
			return false;
		}

		IMiner miner = Miner.get(player);

		if (miner.getRank() < MiningAssistConfig.minerRank.getValue())
		{
			return false;
		}

		MiningAssist type = MiningAssist.get(miner.getMiningAssist());

		if (type == MiningAssist.DISABLED)
		{
			return false;
		}

		return type.isEffectiveTarget(held, state);
	}

	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event)
	{
		if (!MiningAssistConfig.modifiedHardness || !Cavern.proxy.isSinglePlayer())
		{
			return;
		}

		EntityPlayer player = event.getEntityPlayer();
		MiningAssistUnit assist = MiningAssistUnit.get(player);

		if (assist.isChecking())
		{
			return;
		}

		IBlockState state = event.getState();

		if (!isActive(player, state))
		{
			return;
		}

		MiningAssist type = MiningAssist.byPlayer(player);
		BlockPos pos = event.getPos();

		assist.setChecking(true);

		MiningSnapshot snapshot = assist.getSnapshot(type, pos, state);

		if (snapshot != null && !snapshot.isEmpty())
		{
			event.setNewSpeed(assist.getBreakSpeed(snapshot));
		}

		assist.setChecking(false);
	}

	@SubscribeEvent
	public void onBlockBreak(BreakEvent event)
	{
		World world = event.getWorld();

		if (world.isRemote)
		{
			return;
		}

		EntityPlayer player = event.getPlayer();

		if (player == null || player instanceof FakePlayer)
		{
			return;
		}

		BlockPos pos = event.getPos();
		MiningAssistUnit assist = MiningAssistUnit.get(player);

		if (assist.addExperience(pos, event.getExpToDrop()))
		{
			event.setExpToDrop(0);
		}

		if (assist.isBreaking())
		{
			return;
		}

		if (!(player instanceof EntityPlayerMP))
		{
			return;
		}

		IBlockState state = event.getState();

		if (!isActive(player, state))
		{
			return;
		}

		MiningAssist type = MiningAssist.byPlayer(player);
		MiningSnapshot snapshot = assist.getSnapshot(type, pos, state);

		if (snapshot == null || snapshot.isEmpty())
		{
			return;
		}

		PlayerInteractionManager im = ((EntityPlayerMP)player).interactionManager;

		assist.captureDrops(MiningAssistConfig.collectDrops);
		assist.captureExperiences(MiningAssistConfig.collectExps);

		assist.setBreaking(true);

		for (BlockPos target : snapshot.getTargets())
		{
			if (snapshot.validTarget(target) && !harvestBlock(im, target))
			{
				break;
			}
		}

		assist.setBreaking(false);

		Map<BlockPos, NonNullList<ItemStack>> drops = assist.captureDrops(false);

		if (drops != null && !drops.isEmpty())
		{
			for (NonNullList<ItemStack> items : drops.values())
			{
				for (ItemStack stack : items)
				{
					Block.spawnAsEntity(world, pos, stack);
				}
			}
		}

		Map<BlockPos, Integer> experiences = assist.captureExperiences(false);

		if (experiences != null && !experiences.isEmpty() && !im.isCreative() && world.getGameRules().getBoolean("doTileDrops"))
		{
			int exp = experiences.values().stream().mapToInt(Integer::intValue).sum();

			while (exp > 0)
			{
				int i = EntityXPOrb.getXPSplit(exp);
				exp -= i;

				world.spawnEntity(new EntityXPOrb(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, i));
			}
		}
	}

	private boolean harvestBlock(PlayerInteractionManager im, @Nullable BlockPos pos)
	{
		if (pos == null)
		{
			return false;
		}

		if (Cavern.proxy.isSinglePlayer())
		{
			World world = im.world;
			IBlockState state = world.getBlockState(pos);

			if (im.tryHarvestBlock(pos))
			{
				if (!im.isCreative())
				{
					world.playEvent(2001, pos, Block.getStateId(state));
				}

				return true;
			}
		}
		else if (im.tryHarvestBlock(pos))
		{
			return true;
		}

		return false;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onHarvestDrops(HarvestDropsEvent event)
	{
		World world = event.getWorld();

		if (world.isRemote)
		{
			return;
		}

		EntityPlayer player = event.getHarvester();

		if (player == null || player instanceof FakePlayer)
		{
			return;
		}

		BlockPos pos = event.getPos();
		MiningAssistUnit assist = MiningAssistUnit.get(player);

		if (!assist.getCaptureDrops())
		{
			return;
		}

		NonNullList<ItemStack> items = NonNullList.create();
		List<ItemStack> drops = event.getDrops();
		float chance = event.getDropChance();

		for (ItemStack stack : drops)
		{
			if (CaveEventHooks.RANDOM.nextFloat() < chance)
			{
				items.add(stack);
			}
		}

		drops.clear();

		assist.addDrops(pos, items);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerLeftClick(PlayerInteractEvent.LeftClickBlock event)
	{
		EntityPlayer player = event.getEntityPlayer();
		MiningAssist assist = MiningAssist.byPlayer(player);

		if (assist != MiningAssist.AUTO && assist != MiningAssist.AUTO_QUICK && assist != MiningAssist.AUTO_ADIT)
		{
			return;
		}

		World world = event.getWorld();
		BlockPos pos = event.getPos();
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		int level = block.getHarvestLevel(state);

		if (level < 0)
		{
			return;
		}

		ItemStack heldMain = player.getHeldItemMainhand();
		ItemStack heldOff = player.getHeldItemOffhand();

		if (block.isToolEffective("pickaxe", state))
		{
			if (CaveUtils.isPickaxe(heldMain))
			{
				return;
			}

			if (CaveUtils.isPickaxe(heldOff))
			{
				player.setHeldItem(EnumHand.OFF_HAND, heldMain);
				player.setHeldItem(EnumHand.MAIN_HAND, heldOff);

				return;
			}

			NonNullList<ItemStack> mainInventory = player.inventory.mainInventory;
			int slot = -1;

			for (int i = 0, size = mainInventory.size(); i < size; ++i)
			{
				ItemStack stack = mainInventory.get(i);

				if (CaveUtils.isPickaxe(stack))
				{
					if (level > 0 && stack.getItem().getHarvestLevel(stack, "pickaxe", player, state) < level)
					{
						if (slot < 0)
						{
							slot = i;
						}

						continue;
					}

					slot = i;
				}
			}

			if (slot >= 0)
			{
				ItemStack prev = player.inventory.getCurrentItem();

				player.inventory.setInventorySlotContents(player.inventory.currentItem, player.inventory.getStackInSlot(slot));
				player.inventory.setInventorySlotContents(slot, prev);
			}
		}
		else if (block.isToolEffective("axe", state))
		{
			if (CaveUtils.isAxe(heldMain))
			{
				return;
			}

			if (CaveUtils.isAxe(heldOff))
			{
				player.setHeldItem(EnumHand.OFF_HAND, heldMain);
				player.setHeldItem(EnumHand.MAIN_HAND, heldOff);

				return;
			}

			NonNullList<ItemStack> mainInventory = player.inventory.mainInventory;
			int slot = -1;

			for (int i = 0, size = mainInventory.size(); i < size; ++i)
			{
				ItemStack stack = mainInventory.get(i);

				if (CaveUtils.isAxe(stack))
				{
					if (level > 0 && stack.getItem().getHarvestLevel(stack, "axe", player, state) < level)
					{
						if (slot < 0)
						{
							slot = i;
						}

						continue;
					}

					slot = i;
				}
			}

			if (slot >= 0)
			{
				ItemStack prev = player.inventory.getCurrentItem();

				player.inventory.setInventorySlotContents(player.inventory.currentItem, player.inventory.getStackInSlot(slot));
				player.inventory.setInventorySlotContents(slot, prev);
			}
		}
		else if (block.isToolEffective("shovel", state))
		{
			if (CaveUtils.isShovel(heldMain))
			{
				return;
			}

			if (CaveUtils.isShovel(heldOff))
			{
				player.setHeldItem(EnumHand.OFF_HAND, heldMain);
				player.setHeldItem(EnumHand.MAIN_HAND, heldOff);

				return;
			}

			NonNullList<ItemStack> mainInventory = player.inventory.mainInventory;
			int slot = -1;

			for (int i = 0, size = mainInventory.size(); i < size; ++i)
			{
				ItemStack stack = mainInventory.get(i);

				if (CaveUtils.isShovel(stack))
				{
					if (level > 0 && stack.getItem().getHarvestLevel(stack, "shovel", player, state) < level)
					{
						if (slot < 0)
						{
							slot = i;
						}

						continue;
					}

					slot = i;
				}
			}

			if (slot >= 0)
			{
				ItemStack prev = player.inventory.getCurrentItem();

				player.inventory.setInventorySlotContents(player.inventory.currentItem, player.inventory.getStackInSlot(slot));
				player.inventory.setInventorySlotContents(slot, prev);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onAttackEntity(AttackEntityEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		MiningAssist assist = MiningAssist.byPlayer(player);

		if (assist != MiningAssist.AUTO && assist != MiningAssist.AUTO_QUICK && assist != MiningAssist.AUTO_ADIT)
		{
			return;
		}

		ItemStack heldMain = player.getHeldItemMainhand();
		ItemStack heldOff = player.getHeldItemOffhand();

		if (heldMain.getItem() instanceof ItemSword)
		{
			return;
		}

		if (heldOff.getItem() instanceof ItemSword)
		{
			player.setHeldItem(EnumHand.OFF_HAND, heldMain);
			player.setHeldItem(EnumHand.MAIN_HAND, heldOff);

			return;
		}

		NonNullList<ItemStack> mainInventory = player.inventory.mainInventory;
		int slot = -1;

		for (int i = 0, size = mainInventory.size(); i < size; ++i)
		{
			if (mainInventory.get(i).getItem() instanceof ItemSword)
			{
				slot = i;

				break;
			}
		}

		if (slot >= 0)
		{
			ItemStack prev = player.inventory.getCurrentItem();

			player.inventory.setInventorySlotContents(player.inventory.currentItem, player.inventory.getStackInSlot(slot));
			player.inventory.setInventorySlotContents(slot, prev);
		}
	}
}