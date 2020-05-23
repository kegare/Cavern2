package cavern.handler;

import java.util.Random;
import java.util.Set;

import com.google.common.collect.Sets;

import cavern.api.CavernAPI;
import cavern.api.item.IIceEquipment;
import cavern.block.RandomiteHelper;
import cavern.block.RandomiteHelper.Category;
import cavern.item.CaveItems;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.FallTeleportMessage;
import cavern.world.CaveDimensions;
import cavern.world.gen.WorldGenDarkLibrary;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class MirageEventHooks
{
	public static boolean fallDamageCancel;

	private final Random rand = CaveEventHooks.RANDOM;
	private final Set<String> fallCancelPlayers = Sets.newHashSet();

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

			if (rand.nextDouble() < 0.05D)
			{
				Block.spawnAsEntity(world, pos, new ItemStack(Blocks.ICE));
			}
			else if (rand.nextDouble() < 0.005D)
			{
				Category category = Category.COMMON;

				if (rand.nextInt(5) == 0)
				{
					category = Category.FOOD;
				}

				Block.spawnAsEntity(world, pos, RandomiteHelper.getDropItem(category));
			}
			else if (stack.getItem() instanceof IIceEquipment && rand.nextDouble() < 0.03D || rand.nextDouble() < 0.01D)
			{
				Block.spawnAsEntity(world, pos, new ItemStack(Blocks.PACKED_ICE));
			}
		}
	}

	@SubscribeEvent
	public void onDecorateBiome(DecorateBiomeEvent.Pre event)
	{
		World world = event.getWorld();
		Random rand = event.getRand();
		ChunkPos chunkPos = event.getChunkPos();

		if (world.provider.getDimensionType() == CaveDimensions.DARK_FOREST && rand.nextInt(50) == 0)
		{
			int x = rand.nextInt(8) + 4;
			int z = rand.nextInt(8) + 4;
			int top = world.getHeight(chunkPos.getXStart() + x, chunkPos.getZStart() + z);
			int seaLevel = world.getSeaLevel();

			if (top >= seaLevel && top - seaLevel < 6)
			{
				new WorldGenDarkLibrary().generate(world, rand, new BlockPos(chunkPos.getXStart() + x, seaLevel, chunkPos.getZStart() + z));
			}
		}
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();

		if (!CavernAPI.dimension.isInSkyland(entity))
		{
			return;
		}

		if (entity instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)entity;

			if (!player.onGround && player.getEntityBoundingBox().minY <= -20.0D)
			{
				CaveItems.MIRAGE_BOOK.transferTo(null, player);

				if (player.dimension == 0)
				{
					player.connection.setPlayerLocation(player.posX, 305.0D, player.posZ, player.rotationYaw, 60.0F);

					fallCancelPlayers.add(player.getCachedUniqueIdString());
				}

				CaveNetworkRegistry.sendTo(FallTeleportMessage::new, player);
			}
		}
	}

	@SubscribeEvent
	public void onLivingFall(LivingFallEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();

		if (entity instanceof EntityPlayer)
		{
			if (fallDamageCancel || fallCancelPlayers.remove(entity.getCachedUniqueIdString()))
			{
				event.setCanceled(true);
			}
		}
	}
}