package cavern.item;

import cavern.block.CaveBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

public class ItemAcresia extends ItemBlock implements IPlantable
{
	public ItemAcresia(Block block)
	{
		super(block);
		this.setRegistryName(block.getRegistryName());
		this.setUnlocalizedName("acresia");
		this.setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return "item." + EnumType.byItemStack(stack).getTranslationKey();
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
		if (!isInCreativeTab(tab))
		{
			return;
		}

		for (EnumType type : EnumType.VALUES)
		{
			subItems.add(type.getItemStack());
		}
	}

	public boolean isSeeds(ItemStack stack)
	{
		return !stack.isEmpty() && EnumType.byItemStack(stack) == EnumType.SEEDS;
	}

	public boolean isFruits(ItemStack stack)
	{
		return !stack.isEmpty() && EnumType.byItemStack(stack) == EnumType.FRUITS;
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos)
	{
		return world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP) ? EnumPlantType.Cave : EnumPlantType.Plains;
	}

	@Override
	public IBlockState getPlant(IBlockAccess world, BlockPos pos)
	{
		return Block.getBlockFromItem(this).getDefaultState();
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack held = player.getHeldItem(hand);

		if (isSeeds(held))
		{
			if (facing == EnumFacing.UP && player.canPlayerEdit(pos, facing, held) && player.canPlayerEdit(pos.up(), facing, held))
			{
				IBlockState state = world.getBlockState(pos);
				Block soil = state.getBlock();

				if (soil != Blocks.BEDROCK && soil.canSustainPlant(state, world, pos, facing, this) && world.isAirBlock(pos.up()))
				{
					world.setBlockState(pos.up(), getPlant(world, pos));

					held.shrink(1);

					return EnumActionResult.SUCCESS;
				}
			}
		}

		return EnumActionResult.PASS;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving)
	{
		if (isFruits(stack))
		{
			stack.shrink(1);

			if (entityLiving instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)entityLiving;

				player.getFoodStats().addStats(getHealAmount(stack), getSaturationModifier(stack));

				world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);

				player.addStat(StatList.getObjectUseStats(this));
			}

	        return stack;
		}

		return stack;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack held = player.getHeldItem(hand);

		if (isFruits(held))
		{
			if (player.canEat(false))
			{
				player.setActiveHand(hand);

				return new ActionResult<>(EnumActionResult.SUCCESS, held);
			}
		}

		return new ActionResult<>(EnumActionResult.FAIL, held);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return isFruits(stack) ? EnumAction.EAT : EnumAction.NONE;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return isFruits(stack) ? 20 : super.getMaxItemUseDuration(stack);
	}

	public int getHealAmount(ItemStack stack)
	{
		return 1;
	}

	public float getSaturationModifier(ItemStack stack)
	{
		return 0.001F;
	}

	public enum EnumType
	{
		SEEDS(0, "seedsAcresia"),
		FRUITS(1, "fruitsAcresia");

		public static final EnumType[] VALUES = new EnumType[values().length];

		private final int meta;
		private final String translationKey;

		private EnumType(int meta, String name)
		{
			this.meta = meta;
			this.translationKey = name;
		}

		public int getMetadata()
		{
			return meta;
		}

		public String getTranslationKey()
		{
			return translationKey;
		}

		public ItemStack getItemStack()
		{
			return getItemStack(1);
		}

		public ItemStack getItemStack(int amount)
		{
			return new ItemStack(CaveBlocks.ACRESIA, amount, getMetadata());
		}

		public static EnumType byMetadata(int meta)
		{
			if (meta < 0 || meta >= VALUES.length)
			{
				meta = 0;
			}

			return VALUES[meta];
		}

		public static EnumType byItemStack(ItemStack stack)
		{
			return byMetadata(stack.isEmpty() ? 0 : stack.getMetadata());
		}

		static
		{
			for (EnumType type : values())
			{
				VALUES[type.getMetadata()] = type;
			}
		}
	}
}