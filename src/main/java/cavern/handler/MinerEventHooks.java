package cavern.handler;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import cavern.api.CavernAPI;
import cavern.api.data.IMiner;
import cavern.api.data.IMiningData;
import cavern.api.event.CriticalMiningEvent;
import cavern.config.GeneralConfig;
import cavern.data.Miner;
import cavern.data.MinerRank;
import cavern.data.MiningData;
import cavern.item.ItemCave;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.MiningMessage;
import cavern.util.BlockMeta;
import cavern.util.PlayerHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public final class MinerEventHooks
{
	private final Random rand = CaveEventHooks.RANDOM;

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;

			Miner.adjustData(player);
		}
	}

	@SubscribeEvent
	public void onBlockBreak(BreakEvent event)
	{
		EntityPlayer entityPlayer = event.getPlayer();

		if (entityPlayer == null || entityPlayer instanceof FakePlayer || !(entityPlayer instanceof EntityPlayerMP))
		{
			return;
		}

		EntityPlayerMP player = (EntityPlayerMP)entityPlayer;

		if (!CavernAPI.dimension.isInCaveDimensions(player))
		{
			return;
		}

		ItemStack stack = player.getHeldItemMainhand();

		if (!GeneralConfig.isMiningPointItem(stack))
		{
			return;
		}

		World world = event.getWorld();
		IBlockState state = event.getState();
		int point = Miner.getPointAmount(state);

		if (point <= 0)
		{
			return;
		}

		IMiner miner = Miner.get(player);
		IMiningData data = MiningData.get(player);

		if (player.inventory.hasItemStack(ItemCave.EnumType.MINER_ORB.getItemStack()) && rand.nextDouble() < 0.1D)
		{
			point += Math.max(point / 2, 1);
		}

		miner.addPoint(point);
		miner.addMiningRecord(new BlockMeta(state));

		data.notifyMining(state, point);

		int combo = data.getMiningCombo();

		if (combo > 0 && combo % 10 == 0)
		{
			world.playSound(null, player.posX, player.posY + 0.25D, player.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
				SoundCategory.PLAYERS, 0.1F, 0.5F * ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.8F));

			player.addExperience(combo / 10);
		}

		if (combo >= 50)
		{
			PlayerHelper.grantAdvancement(player, "good_mine");
		}

		final MiningMessage message = new MiningMessage(state, point);

		CaveNetworkRegistry.sendTo(() -> message, player);
	}

	@SubscribeEvent
	public void onHarvestDrops(HarvestDropsEvent event)
	{
		if (!GeneralConfig.criticalMining || event.isSilkTouching())
		{
			return;
		}

		World world = event.getWorld();

		if (world.isRemote)
		{
			return;
		}

		EntityPlayer player = event.getHarvester();

		if (player == null || player instanceof FakePlayer || !CavernAPI.dimension.isInCaveDimensions(player))
		{
			return;
		}

		IBlockState state = event.getState();

		if (Miner.getPointAmount(state) <= 0)
		{
			return;
		}

		if (state.getMaterial() != Material.ROCK)
		{
			return;
		}

		MinerRank rank = MinerRank.get(Miner.get(player).getRank());
		float f = rank.getBoost();

		if (f <= 1.0F)
		{
			return;
		}

		f = (f - 1.0F) * 0.3F;

		ItemStack held = player.getHeldItemMainhand();
		String tool = state.getBlock().getHarvestTool(state);

		if (held.isEmpty() || tool == null)
		{
			return;
		}

		int toolLevel = held.getItem().getHarvestLevel(held, tool, player, state);

		if (toolLevel <= 0)
		{
			return;
		}

		f *= 1.0F + toolLevel * 0.1F;

		List<ItemStack> originalDrops = event.getDrops();
		List<ItemStack> drops = Lists.newArrayList();

		for (ItemStack stack : originalDrops)
		{
			if (!stack.isEmpty() && !(stack.getItem() instanceof ItemBlock) && rand.nextFloat() <= f)
			{
				drops.add(stack.copy());
			}
		}

		if (!drops.isEmpty())
		{
			CriticalMiningEvent criticalEvent = new CriticalMiningEvent(world, event.getPos(), state, player, event.getFortuneLevel(), originalDrops, drops);

			if (MinecraftForge.EVENT_BUS.post(criticalEvent))
			{
				return;
			}

			player.sendStatusMessage(new TextComponentTranslation("cavern.message.mining.critical"), true);

			originalDrops.addAll(criticalEvent.getBonusDrops());
		}
	}
}