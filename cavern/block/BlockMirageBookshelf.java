package cavern.block;

import cavern.core.Cavern;
import cavern.item.ItemMirageBook.EnumType;
import net.minecraft.block.BlockBookshelf;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

public class BlockMirageBookshelf extends BlockBookshelf
{
	public BlockMirageBookshelf()
	{
		super();
		this.setUnlocalizedName("mirageBookshelf");
		this.setHardness(1.5F);
		this.setSoundType(SoundType.WOOD);
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		super.getDrops(drops, world, pos, state, fortune);

		if (RANDOM.nextDouble() < 0.05D)
		{
			int i = MathHelper.floor(RANDOM.nextDouble() * EnumType.VALUES.length);
			EnumType type = EnumType.VALUES[i];

			if (type.getDimension() != null)
			{
				drops.add(type.getItemStack());
			}
		}
	}

	@Override
	protected ItemStack getSilkTouchDrop(IBlockState state)
	{
		return new ItemStack(Blocks.BOOKSHELF);
	}
}