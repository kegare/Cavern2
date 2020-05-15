package cavern.handler;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import cavern.api.data.IMiner;
import cavern.client.CaveKeyBindings;
import cavern.config.MiningAssistConfig;
import cavern.core.Cavern;
import cavern.data.Miner;
import cavern.miningassist.MiningAssist;
import cavern.miningassist.MiningAssistUnit;
import cavern.miningassist.MiningSnapshot;
import cavern.network.CaveNetworkRegistry;
import cavern.network.server.MiningAssistMessage;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MiningAssistEventHooks
{
	private boolean breaking;
	private boolean checking;

	private boolean isActive(EntityPlayer player, IBlockState state)
	{
		ItemStack held = player.getHeldItemMainhand();

		if (!MiningAssistConfig.isEffectiveItem(held))
		{
			return false;
		}

		IMiner stats = Miner.get(player);

		if (stats.getRank() < MiningAssistConfig.minerRank.getValue())
		{
			return false;
		}

		MiningAssist type = MiningAssist.get(stats.getMiningAssist());

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

		if (checking)
		{
			return;
		}

		EntityPlayer player = event.getEntityPlayer();
		IBlockState state = event.getState();

		if (!isActive(player, state))
		{
			return;
		}

		MiningAssistUnit assist = MiningAssistUnit.get(player);
		MiningAssist type = MiningAssist.byPlayer(player);
		BlockPos pos = event.getPos();

		checking = true;

		MiningSnapshot snapshot = assist.getSnapshot(type, pos, state);

		if (!snapshot.isEmpty())
		{
			event.setNewSpeed(assist.getBreakSpeed(snapshot));
		}

		checking = false;
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

		if (breaking)
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

		if (snapshot.isEmpty())
		{
			return;
		}

		PlayerInteractionManager im = ((EntityPlayerMP)player).interactionManager;

		assist.captureDrops(MiningAssistConfig.collectDrops);
		assist.captureExperiences(MiningAssistConfig.collectExps);

		breaking = true;

		for (BlockPos target : snapshot.getTargets())
		{
			if (snapshot.validTarget(target) && !harvestBlock(im, target))
			{
				break;
			}
		}

		breaking = false;

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

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event)
	{
		if (!Keyboard.getEventKeyState())
		{
			return;
		}

		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc.player == null)
		{
			return;
		}

		int key = Keyboard.getEventKey();

		if (CaveKeyBindings.KEY_MINING_ASSIST.isActiveAndMatches(key))
		{
			CaveNetworkRegistry.sendToServer(new MiningAssistMessage());
		}
	}
}