package cavern.block;

import java.util.Random;

import cavern.core.Cavern;
import cavern.item.ItemAcresia;
import cavern.util.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class BlockAcresia extends BlockCrops
{
	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 4);

	public BlockAcresia()
	{
		super();
		this.setUnlocalizedName("acresia");
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}

	@Override
	protected Item getCrop()
	{
		return Item.getItemFromBlock(this);
	}

	protected int getCropDamage()
	{
		return ItemAcresia.EnumType.FRUITS.getMetadata();
	}

	public ItemStack getCropItem()
	{
		return getCropItem(1);
	}

	public ItemStack getCropItem(int amount)
	{
		return new ItemStack(getCrop(), amount, getCropDamage());
	}

	@Override
	protected Item getSeed()
	{
		return Item.getItemFromBlock(this);
	}

	protected int getSeedDamage()
	{
		return ItemAcresia.EnumType.SEEDS.getMetadata();
	}

	public ItemStack getSeedItem()
	{
		return getSeedItem(1);
	}

	public ItemStack getSeedItem(int amount)
	{
		return new ItemStack(getSeed(), amount, getSeedDamage());
	}

	@Override
	protected boolean canSustainBush(IBlockState state)
	{
		return state.getBlock() != Blocks.BEDROCK && (state.isNormalCube() || state.getBlock() instanceof BlockFarmland);
	}

	@Override
	public boolean canBlockStay(World world, BlockPos pos, IBlockState state)
	{
		BlockPos down = pos.down();
		IBlockState soil = world.getBlockState(down);

		return soil.getBlock().canSustainPlant(soil, world, down, EnumFacing.UP, this);
	}

	@Override
	protected PropertyInteger getAgeProperty()
	{
		return AGE;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, getAgeProperty());
	}

	@Override
	public int getMaxAge()
	{
		return 4;
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return isMaxAge(state) ? getCropDamage() : getSeedDamage();
	}

	@Override
	public ItemStack getItem(World world, BlockPos pos, IBlockState state)
	{
		return getCropItem();
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		checkAndDropBlock(world, pos, state);

		int i = getAge(state);

		if (i < getMaxAge())
		{
			float f = getGrowthChance(this, world, pos);

			if (rand.nextInt((int)(25.0F / f) + 1) == 0)
			{
				world.setBlockState(pos, withAge(i + 1), 2);
			}
		}
	}

	protected static float getGrowthChance(Block block, World world, BlockPos pos)
	{
		float chance = 1.0F;
		BlockPos soil = pos.down();

		for (int i = -1; i <= 1; ++i)
		{
			for (int j = -1; j <= 1; ++j)
			{
				float rate = 0.0F;
				IBlockState state = world.getBlockState(soil.add(i, 0, j));

				if (state.getBlock().canSustainPlant(state, world, soil.add(i, 0, j), EnumFacing.UP, (IPlantable)block))
				{
					rate = 4.0F;

					if (state.getBlock().isFertile(world, soil.add(i, 0, j)))
					{
						rate = 8.0F;
					}
				}

				if (i != 0 || j != 0)
				{
					rate /= 4.0F;
				}

				chance += rate;
			}
		}

		BlockPos north = pos.north();
		BlockPos south = pos.south();
		BlockPos west = pos.west();
		BlockPos east = pos.east();
		boolean flag = block == world.getBlockState(west).getBlock() || block == world.getBlockState(east).getBlock();
		boolean flag1 = block == world.getBlockState(north).getBlock() || block == world.getBlockState(south).getBlock();

		if (flag && flag1)
		{
			chance /= 2.0F;
		}
		else
		{
			boolean flag2 = block == world.getBlockState(west.north()).getBlock() || block == world.getBlockState(east.north()).getBlock() || block == world.getBlockState(east.south()).getBlock() || block == world.getBlockState(west.south()).getBlock();

			if (flag2)
			{
				chance /= 2.0F;
			}
		}

		return chance;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!canGrow(world, pos, state, world.isRemote))
		{
			ItemStack held = player.getHeldItem(hand);

			if (!held.isEmpty() && held.getItem() instanceof ItemShears)
			{
				if (!world.isRemote)
				{
					int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, held);
					ItemStack crop = getCropItem(4 + world.rand.nextInt(3) + fortune);
					EntityItem drop = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D, crop);

					drop.setPickupDelay(10);

					world.spawnEntity(drop);
					world.setBlockState(pos, withAge(2), 2);

					held.damageItem(1, player);

					drop.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.25F);

					PlayerHelper.grantAdvancement(player, "harvest_acresia");
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		Random rand = world instanceof World ? ((World)world).rand : RANDOM;
		int count = quantityDropped(state, fortune, rand);

		for (int i = 0; i < count; ++i)
		{
			Item item = getItemDropped(state, rand, fortune);

			if (item != null)
			{
				drops.add(new ItemStack(item, 1, damageDropped(state)));
			}
		}

		int age = getAge(state);

		if (age >= getMaxAge())
		{
			for (int i = 0; i < 3 + fortune; ++i)
			{
				if (rand.nextInt(2 * getMaxAge()) <= age)
				{
					drops.add(getSeedItem());
				}
			}
		}
	}
}