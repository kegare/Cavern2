package cavern.block;

import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import cavern.client.gui.GuiRegeneration;
import cavern.core.CaveSounds;
import cavern.item.CaveItems;
import cavern.item.ItemMirageBook;
import cavern.item.ItemMirageBook.EnumType;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.MirageSelectMessage;
import cavern.stats.PlayerData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPortalMirageWorlds extends BlockPortalCavern
{
	public BlockPortalMirageWorlds()
	{
		super();
		this.setTranslationKey("portal.mirageWorlds");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void openRegeneration(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side)
	{
		GuiRegeneration regeneration = new GuiRegeneration();
		DimensionType type = getDimension(player.getHeldItem(hand));

		if (type != null)
		{
			regeneration.dimensions.add(type);
		}

		FMLClientHandler.instance().showGuiScreen(regeneration);
	}

	@Override
	public boolean isTriggerItem(ItemStack stack)
	{
		return !stack.isEmpty() && stack.getItem() instanceof ItemMirageBook;
	}

	@Nullable
	public DimensionType getDimension(ItemStack stack)
	{
		if (stack.isEmpty() || !(stack.getItem() instanceof ItemMirageBook))
		{
			return null;
		}

		return EnumType.byItemStack(stack).getDimension();
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity)
	{
		if (world.isRemote || entity.isDead || entity.isRiding() || entity.isBeingRidden() || !(entity instanceof EntityPlayerMP))
		{
			return;
		}

		EntityPlayerMP player = (EntityPlayerMP)entity;

		if (player.timeUntilPortal <= 0)
		{
			player.timeUntilPortal = player.getPortalCooldown();

			Set<DimensionType> types = Sets.newHashSet();

			for (ItemStack stack : player.inventory.mainInventory)
			{
				DimensionType type = getDimension(stack);

				if (type != null)
				{
					types.add(type);
				}
			}

			for (ItemStack stack : player.getHeldEquipment())
			{
				DimensionType type = getDimension(stack);

				if (type != null)
				{
					types.add(type);
				}
			}

			if (types.isEmpty())
			{
				player.sendStatusMessage(new TextComponentTranslation("cavern.message.portal.mirage"), true);

				return;
			}

			MirageSelectMessage message = new MirageSelectMessage();

			for (DimensionType type : types)
			{
				if (player.capabilities.isCreativeMode)
				{
					message.dimensions.add(type);

					continue;
				}

				long teleportTime = PlayerData.get(player).getLastTeleportTime(type);

				if (teleportTime <= 0L || teleportTime + 6000L < world.getTotalWorldTime())
				{
					message.dimensions.add(type);
				}
			}

			if (message.dimensions.isEmpty())
			{
				player.sendStatusMessage(new TextComponentTranslation("cavern.message.portal.wait"), true);

				return;
			}
			else if (message.dimensions.size() == 1)
			{
				for (DimensionType type : message.dimensions)
				{
					double x = player.posX;
					double y = player.posY + player.getEyeHeight();
					double z = player.posZ;

					player.getServerWorld().playSound(player, x, y, z, CaveSounds.CAVE_PORTAL, SoundCategory.BLOCKS, 0.5F, 1.0F);

					CaveItems.MIRAGE_BOOK.transferTo(type, player);

					return;
				}
			}

			CaveNetworkRegistry.sendTo(message, player);
		}
		else
		{
			player.timeUntilPortal = player.getPortalCooldown();
		}
	}
}