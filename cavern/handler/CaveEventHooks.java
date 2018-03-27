package cavern.handler;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cavern.api.CavernAPI;
import cavern.api.ICavenicMob;
import cavern.api.IIceEquipment;
import cavern.api.IMinerStats;
import cavern.api.IMiningData;
import cavern.api.IPlayerData;
import cavern.api.event.CriticalMiningEvent;
import cavern.block.BlockLeavesPerverted;
import cavern.block.BlockLogPerverted;
import cavern.block.BlockPortalCavern;
import cavern.block.BlockSaplingPerverted;
import cavern.block.CaveBlocks;
import cavern.block.RandomiteHelper;
import cavern.config.GeneralConfig;
import cavern.item.CaveItems;
import cavern.item.IAquaTool;
import cavern.item.ItemCave;
import cavern.magic.Magic;
import cavern.magic.MagicBook;
import cavern.magic.MagicInvisible;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.CustomSeedMessage;
import cavern.network.client.MiningMessage;
import cavern.stats.MinerRank;
import cavern.stats.MinerStats;
import cavern.stats.MiningData;
import cavern.stats.PlayerData;
import cavern.util.BlockMeta;
import cavern.util.CaveUtils;
import cavern.world.CaveDimensions;
import cavern.world.CustomSeedData;
import cavern.world.ICustomSeed;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class CaveEventHooks
{
	public static final Random RANDOM = new Random();

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		World world = event.getWorld();

		if (!world.isRemote && world.provider instanceof ICustomSeed)
		{
			CustomSeedData data = ((ICustomSeed)world.provider).getSeedData();

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
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;

			MinerStats.adjustData(player);
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

		if (CavernAPI.dimension.isInCaves(player))
		{
			WorldServer world = player.getServerWorld();
			DimensionType type = world.provider.getDimensionType();

			if (type != CaveDimensions.CAVERN)
			{
				String name = type.getName();

				CaveUtils.grantCriterion(player, "enter_the_" + name, "entered_" + name);
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

		World world = event.getWorld();
		IBlockState state = event.getState();
		ItemStack stack = player.getHeldItemMainhand();

		if (GeneralConfig.isMiningPointItem(stack))
		{
			int point = MinerStats.getPointAmount(state);

			if (point != 0)
			{
				IMinerStats stats = MinerStats.get(player);
				IMiningData data = MiningData.get(player);

				if (player.inventory.hasItemStack(ItemCave.EnumType.MINER_ORB.getItemStack()))
				{
					if (RANDOM.nextDouble() < 0.15D)
					{
						point += Math.max(point / 2, 1);
					}
				}

				stats.addPoint(point);
				stats.addMiningRecord(new BlockMeta(state));

				data.notifyMining(state, point);

				int combo = data.getMiningCombo();

				if (combo > 0 && combo % 10 == 0)
				{
					world.playSound(null, player.posX, player.posY + 0.25D, player.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
						SoundCategory.PLAYERS, 0.1F, 0.5F * ((RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.7F + 1.8F));

					player.addExperience(combo / 10);
				}

				if (combo >= 50)
				{
					CaveUtils.grantAdvancement(player, "good_mine");
				}

				CaveNetworkRegistry.sendTo(new MiningMessage(state, point), player);
			}
		}

		if (CavernAPI.dimension.isInFrostMountains(player))
		{
			if (player.capabilities.isCreativeMode)
			{
				return;
			}

			if (state.getBlock() != Blocks.PACKED_ICE || EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0)
			{
				return;
			}

			BlockPos pos = event.getPos();
			Biome biome = world.getBiome(pos);

			if (!biome.isSnowyBiome())
			{
				return;
			}

			if (RANDOM.nextDouble() < 0.05D)
			{
				Block.spawnAsEntity(world, pos, new ItemStack(Blocks.ICE));
			}
			else if (RANDOM.nextDouble() < 0.005D)
			{
				Block.spawnAsEntity(world, pos, RandomiteHelper.getDropItem());
			}
			else if (stack.getItem() instanceof IIceEquipment && RANDOM.nextDouble() < 0.03D || RANDOM.nextDouble() < 0.01D)
			{
				Block.spawnAsEntity(world, pos, new ItemStack(Blocks.PACKED_ICE));
			}
		}
	}

	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event)
	{
		EntityPlayer player = event.getEntityPlayer();
		ItemStack stack = player.getHeldItemMainhand();

		if (stack.isEmpty())
		{
			return;
		}

		float original = event.getOriginalSpeed();
		boolean flag = EnchantmentHelper.getAquaAffinityModifier(player);

		if (player.isInWater() && stack.getItem() instanceof IAquaTool)
		{
			IAquaTool tool = (IAquaTool)stack.getItem();
			float speed = tool.getAquaBreakSpeed(stack, player, event.getPos(), event.getState(), original);

			if (flag)
			{
				speed *= 0.5F;
			}

			event.setNewSpeed(speed);

			flag = true;
		}

		if (CavernAPI.dimension.isInCaveDimensions(player) && CaveUtils.isItemPickaxe(stack))
		{
			int rank = MinerStats.get(player).getRank();

			if (!flag && player.isInWater() && rank >= MinerRank.AQUA_MINER.getRank())
			{
				event.setNewSpeed(original * 5.0F);
			}

			event.setNewSpeed(event.getNewSpeed() * MinerRank.get(rank).getBoost());
		}
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

		if (MinerStats.getPointAmount(state) <= 0)
		{
			return;
		}

		if (state.getMaterial() != Material.ROCK)
		{
			return;
		}

		MinerRank rank = MinerRank.get(MinerStats.get(player).getRank());
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
			if (!stack.isEmpty() && !(stack.getItem() instanceof ItemBlock) && RANDOM.nextFloat() <= f)
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

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();

		if (!(entity instanceof EntityPlayer) || entity instanceof FakePlayer)
		{
			return;
		}

		EntityPlayer player = (EntityPlayer)entity;

		MiningData.get(player).onUpdate();

		if (!player.isInWater() || !CavernAPI.dimension.isInCaveDimensions(player))
		{
			return;
		}

		IMinerStats stats = MinerStats.get(player);

		if (stats.getRank() < MinerRank.AQUA_MINER.getRank())
		{
			return;
		}

		if (!player.canBreatheUnderwater() && !player.isPotionActive(MobEffects.WATER_BREATHING) && player.ticksExisted % 20 == 0)
		{
			player.setAir(300);
		}

		if (player.capabilities.isFlying || EnchantmentHelper.getDepthStriderModifier(player) > 0)
		{
			return;
		}

		double prevY = player.posY;
		float vec1 = 0.6F;
		float vec2 = 0.01F;
		float vec3 = 0.4F;

		if (!player.onGround)
		{
			vec3 *= 0.5F;
		}

		if (player.getArmorVisibility() >= 0.75F)
		{
			vec3 *= 0.5F;
		}

		if (vec3 > 0.0F)
		{
			vec1 += (0.54600006F - vec1) * vec3 / 3.0F;
			vec2 += (player.getAIMoveSpeed() - vec2) * vec3 / 3.0F;
		}

		player.moveRelative(player.moveStrafing, player.moveVertical, player.moveForward, vec2);
		player.move(MoverType.SELF, player.motionX, player.motionY, player.motionZ);
		player.motionX *= vec1;
		player.motionY *= 0.800000011920929D;
		player.motionZ *= vec1;

		if (player.collidedHorizontally && player.isOffsetPositionInLiquid(player.motionX, player.motionY + 0.6000000238418579D - player.posY + prevY, player.motionZ))
		{
			player.motionY = 0.30000001192092896D;
		}

		if (player.isSwingInProgress || player.isSneaking())
		{
			player.motionY *= 0.3D;
		}
	}

	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();

		if (CavernAPI.dimension.isInMirageWorlds(entity))
		{
			if (entity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)entity;

				player.setHealth(0.1F);

				CaveItems.MIRAGE_BOOK.transferTo(null, player);

				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onLivingSpawn(LivingSpawnEvent.CheckSpawn event)
	{
		EntityLivingBase entity = event.getEntityLiving();

		if (CavernAPI.dimension.isInCavenia(entity))
		{
			if (entity instanceof ICavenicMob && ((ICavenicMob)entity).canSpawnInCavenia())
			{
				return;
			}

			event.setResult(Result.DENY);
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

		entity.setRevengeTarget(null);

		if (entity instanceof EntityLiving)
		{
			((EntityLiving)entity).setAttackTarget(null);
		}
	}

	@SubscribeEvent
	public void onSleepInBed(PlayerSleepInBedEvent event)
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
			IPlayerData data = PlayerData.get(player);
			long worldTime = world.getTotalWorldTime();
			long sleepTime = data.getLastSleepTime();

			if (sleepTime <= 0L)
			{
				sleepTime = worldTime;

				data.setLastSleepTime(sleepTime);
			}

			long requireTime = GeneralConfig.sleepWaitTime * 20;

			if (sleepTime + requireTime > worldTime)
			{
				result = SleepResult.OTHER_PROBLEM;

				long remainTime = requireTime - (worldTime - sleepTime);
				int min = MathHelper.ceil(remainTime / 20 / 60 + 1);

				player.sendStatusMessage(new TextComponentTranslation("cavern.message.sleep.still", min), true);
			}
		}

		if (result == null)
		{
			result = CaveUtils.trySleep(player, event.getPos());
		}

		if (!world.isRemote && result == SleepResult.OK)
		{
			if (GeneralConfig.sleepRefresh)
			{
				if (player.shouldHeal())
				{
					player.heal(player.getMaxHealth() * 0.5F);
				}
			}

			PlayerData.get(player).setLastSleepTime(world.getTotalWorldTime());
		}

		event.setResult(result);
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
					CaveUtils.grantAdvancement(player, "ice_charge");

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