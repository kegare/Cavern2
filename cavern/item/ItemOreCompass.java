package cavern.item;

import javax.annotation.Nullable;

import cavern.capability.CapabilityOreCompass;
import cavern.core.Cavern;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemOreCompass extends Item
{
	@SideOnly(Side.CLIENT)
	private long prevTime;

	public ItemOreCompass()
	{
		this.setTranslationKey("compassOre");
		this.setMaxStackSize(1);
		this.setCreativeTab(Cavern.TAB_CAVERN);
		this.addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			@Override
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase living)
			{
				if (living == null && !stack.isOnItemFrame())
				{
					return 0.0F;
				}
				else
				{
					boolean flag = living != null;
					Entity entity = flag ? living : stack.getItemFrame();

					if (world == null)
					{
						world = entity.world;
					}

					double dir;
					OreCompass compass = OreCompass.get(stack);
					BlockPos pos = compass.getOrePos();

					if (pos != null && entity.getDistanceSqToCenter(pos) < 50.0D * 50.0D)
					{
						double d1 = flag ? (double)entity.rotationYaw : getFrameRotation((EntityItemFrame)entity);
						d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);
						double d2 = Math.atan2(pos.getZ() - entity.posZ, pos.getX() - entity.posX) / (Math.PI * 2D);
						dir = 0.5D - (d1 - 0.25D - d2);
					}
					else
					{
						dir = Math.random();
					}

					if (flag)
					{
						dir = compass.wobble(world, dir);
					}

					return MathHelper.positiveModulo((float)dir, 1.0F);
				}
			}

			@SideOnly(Side.CLIENT)
			private double getFrameRotation(EntityItemFrame entity)
			{
				return MathHelper.wrapDegrees(180 + entity.facingDirection.getHorizontalIndex() * 90);
			}
		});
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack held = player.getHeldItem(hand);

		if (OreCompass.get(held).refreshOrePos())
		{
			return new ActionResult<>(EnumActionResult.SUCCESS, held);
		}

		return new ActionResult<>(EnumActionResult.PASS, held);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
	{
		OreCompass.get(stack).refreshOrePos();

		return super.onEntitySwing(entityLiving, stack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
	{
		if (isSelected && entity != null && entity instanceof EntityPlayer)
		{
			if (prevTime > 0 && Minecraft.getSystemTime() - prevTime < 5000L)
			{
				return;
			}

			OreCompass.get(stack).refreshOrePos();

			prevTime = Minecraft.getSystemTime();
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();

		if (player == null)
		{
			return false;
		}

		if (player.getHeldItemMainhand() != stack && player.getHeldItemOffhand() != stack)
		{
			return false;
		}

		BlockPos pos = OreCompass.get(stack).getOrePos();

		if (pos == null || player.getDistanceSqToCenter(pos) > 50.0D * 50.0D)
		{
			return false;
		}

		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
		BlockPos pos = OreCompass.get(stack).getOrePos();

		return 1.0D - Math.sqrt(player.getDistanceSqToCenter(pos)) / 50.0D;
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack)
	{
		return 0xE0E0E0;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
	{
		return new CapabilityOreCompass();
	}
}