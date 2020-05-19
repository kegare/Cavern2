package cavern.item;

import java.util.List;

import javax.annotation.Nullable;

import cavern.core.Cavern;
import cavern.entity.EntityRapidArrow;
import cavern.entity.EntityTorchArrow;
import cavern.util.CaveUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBowCavenic extends ItemBow
{
	public ItemBowCavenic()
	{
		super();
		this.setUnlocalizedName("bowCavenic");
		this.setCreativeTab(Cavern.TAB_CAVERN);
		this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			@Override
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
			{
				if (entity == null || entity.getActiveItemStack().getItem() != ItemBowCavenic.this)
				{
					return 0.0F;
				}

				return (stack.getMaxItemUseDuration() - entity.getItemInUseCount()) / BowMode.byItemStack(stack).getPullingSpeed();
			}
		});
	}

	public BowMode toggleBowMode(ItemStack stack)
	{
		BowMode current = BowMode.byItemStack(stack);
		NBTTagCompound nbt = stack.getTagCompound();

		if (nbt == null)
		{
			nbt = new NBTTagCompound();

			stack.setTagCompound(nbt);
		}

		BowMode next = BowMode.byType(current.getType() + 1);

		nbt.setInteger("Mode", next.getType());

		return next;
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack)
	{
		toggleBowMode(stack);

		entity.playSound(SoundEvents.UI_BUTTON_CLICK, 0.5F, 1.75F);

		if (entity.world.isRemote && entity instanceof EntityPlayer)
		{
			((EntityPlayer)entity).sendStatusMessage(getBowModeMessage(stack), true);
		}

		return false;
	}

	public ITextComponent getBowModeMessage(ItemStack stack)
	{
		BowMode mode = BowMode.byItemStack(stack);
		ITextComponent name = new TextComponentTranslation(mode.getUnlocalizedName(stack));
		ITextComponent title = new TextComponentTranslation(stack.getUnlocalizedName() + ".mode");

		return title.appendText(": ").appendSibling(name);
	}

	@Override
	protected ItemStack findAmmo(EntityPlayer player)
	{
		if (isArrow(player.getHeldItem(EnumHand.OFF_HAND)))
		{
			return player.getHeldItem(EnumHand.OFF_HAND);
		}
		else if (isArrow(player.getHeldItem(EnumHand.MAIN_HAND)))
		{
			return player.getHeldItem(EnumHand.MAIN_HAND);
		}
		else
		{
			for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
			{
				ItemStack stack = player.inventory.getStackInSlot(i);

				if (isArrow(stack))
				{
					return stack;
				}
			}

			return ItemStack.EMPTY;
		}
	}

	protected ItemStack findTorch(EntityPlayer player)
	{
		if (isTorch(player.getHeldItem(EnumHand.OFF_HAND)))
		{
			return player.getHeldItem(EnumHand.OFF_HAND);
		}
		else if (isTorch(player.getHeldItem(EnumHand.MAIN_HAND)))
		{
			return player.getHeldItem(EnumHand.MAIN_HAND);
		}
		else
		{
			for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
			{
				ItemStack stack = player.inventory.getStackInSlot(i);

				if (isTorch(stack))
				{
					return stack;
				}
			}

			return ItemStack.EMPTY;
		}
	}

	protected boolean isTorch(ItemStack stack)
	{
		Block block = Block.getBlockFromItem(stack.getItem());

		if (block != null && block instanceof BlockTorch)
		{
			return true;
		}

		return false;
	}

	@Nullable
	protected EntityArrow createCustomArrow(BowMode mode, World world, EntityLivingBase shooter, ItemStack subAmmo)
	{
		switch (mode)
		{
			case RAPID:
				return new EntityRapidArrow(world, shooter);
			case TORCH:
				return new EntityTorchArrow(world, shooter).setTorchItem(subAmmo);
			default:
		}

		return null;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft)
	{
		if (!(entityLiving instanceof EntityPlayer))
		{
			return;
		}

		EntityPlayer player = (EntityPlayer)entityLiving;
		BowMode mode = BowMode.byItemStack(stack);
		boolean infinity = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
		ItemStack ammo = findAmmo(player);
		ItemStack torch = mode == BowMode.TORCH ? findTorch(player) : ItemStack.EMPTY;

		int i = getMaxItemUseDuration(stack) - timeLeft;
		i = ForgeEventFactory.onArrowLoose(stack, world, player, i, !ammo.isEmpty() || infinity);

		if (i < 0 || ammo.isEmpty() && !infinity || mode == BowMode.TORCH && torch.isEmpty() && !infinity)
		{
			return;
		}

		if (ammo.isEmpty())
		{
			ammo = new ItemStack(Items.ARROW);
		}

		if (mode == BowMode.TORCH && torch.isEmpty())
		{
			torch = new ItemStack(Blocks.TORCH);
		}

		float f = getArrowVelocity(i);

		if (f < 0.1D)
		{
			return;
		}

		boolean flag = player.capabilities.isCreativeMode || ammo.getItem() instanceof ItemArrow && ((ItemArrow)ammo.getItem()).isInfinite(ammo, stack, player);

		if (!world.isRemote)
		{
			EntityArrow entityArrow = createCustomArrow(mode, world, player, torch);

			if (entityArrow == null)
			{
				ItemArrow arrow = (ItemArrow)(ammo.getItem() instanceof ItemArrow ? ammo.getItem() : Items.ARROW);

				entityArrow = arrow.createArrow(world, ammo, player);
			}

			entityArrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 3.0F, 1.0F);

			if (f >= 1.0D || mode == BowMode.SNIPE && f >= 0.65D)
			{
				entityArrow.setIsCritical(true);
			}

			int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);

			if (power > 0)
			{
				entityArrow.setDamage(entityArrow.getDamage() + power * 0.5D + 0.5D);
			}

			int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);

			if (punch > 0)
			{
				entityArrow.setKnockbackStrength(punch);
			}

			if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0)
			{
				entityArrow.setFire(100);
			}

			stack.damageItem(1, player);

			if (flag || player.capabilities.isCreativeMode && (ammo.getItem() == Items.SPECTRAL_ARROW || ammo.getItem() == Items.TIPPED_ARROW))
			{
				entityArrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
			}

			entityArrow.setDamage(entityArrow.getDamage() * mode.getAttackPower());

			world.spawnEntity(entityArrow);
		}

		world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

		if (!flag && !player.capabilities.isCreativeMode)
		{
			ammo.shrink(1);

			if (ammo.isEmpty())
			{
				player.inventory.deleteStack(ammo);
			}
		}

		player.addStat(StatList.getObjectUseStats(this));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ActionResult<ItemStack> result = super.onItemRightClick(world, player, hand);

		if (result.getType() == EnumActionResult.SUCCESS)
		{
			ItemStack heldItem = player.getHeldItem(hand);
			BowMode mode = BowMode.byItemStack(heldItem);

			switch (mode)
			{
				case RAPID:
					CaveUtils.setPrivateValue(EntityLivingBase.class, player, 20, "activeItemStackUseCount", "field_184628_bn");

					player.stopActiveHand();
					break;
				case TORCH:
					boolean flag = !findTorch(player).isEmpty();

					if (!player.capabilities.isCreativeMode && !flag)
					{
						return flag ? new ActionResult<>(EnumActionResult.PASS, heldItem) : new ActionResult<>(EnumActionResult.FAIL, heldItem);
					}

					break;
				default:
			}
		}

		return result;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag)
	{
		tooltip.add(getBowModeMessage(stack).getUnformattedText());
	}

	public enum BowMode
	{
		NORMAL(0, "normal", 1.0D, 20.0F, 0.15F),
		RAPID(1, "rapid", 0.65D, 0.0F, 0.0F),
		SNIPE(2, "snipe", 3.0D, 80.0F, 0.75F),
		TORCH(3, "torch", 1.0D, 40.0F, 0.225F);

		private static final BowMode[] TYPE_LOOKUP = new BowMode[values().length];

		private int modeType;
		private String modeName;
		private double attackPower;
		private float pullingSpeed;
		private float zoomScale;

		private BowMode(int type, String name, double power, float pulling, float zoom)
		{
			this.modeType = type;
			this.modeName = name;
			this.attackPower = power;
			this.pullingSpeed = pulling;
			this.zoomScale = zoom;
		}

		public int getType()
		{
			return modeType;
		}

		public String getModeName()
		{
			return modeName;
		}

		public String getUnlocalizedName(ItemStack stack)
		{
			return stack.getUnlocalizedName() + "." + modeName;
		}

		public double getAttackPower()
		{
			return attackPower;
		}

		public float getPullingSpeed()
		{
			return pullingSpeed;
		}

		public float getZoomScale()
		{
			return zoomScale;
		}

		public static BowMode byItemStack(ItemStack stack)
		{
			if (stack.isEmpty())
			{
				return NORMAL;
			}

			NBTTagCompound nbt = stack.getTagCompound();

			if (nbt == null || !nbt.hasKey("Mode", NBT.TAG_ANY_NUMERIC))
			{
				return NORMAL;
			}

			return byType(nbt.getInteger("Mode"));
		}

		public static BowMode byType(int type)
		{
			if (type < 0 || type >= TYPE_LOOKUP.length)
			{
				type = 0;
			}

			return TYPE_LOOKUP[type];
		}

		static
		{
			for (BowMode mode : values())
			{
				TYPE_LOOKUP[mode.getType()] = mode;
			}
		}
	}
}