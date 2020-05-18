package cavern.config.property;

import java.util.Arrays;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import cavern.util.BlockMeta;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ConfigBlocks
{
	private String[] values;

	private final Set<BlockMeta> blocks = Sets.newHashSet();

	public String[] getValues()
	{
		if (values == null)
		{
			values = new String[0];
		}

		return values;
	}

	public void setValues(String[] blocks)
	{
		values = blocks;
	}

	public Set<BlockMeta> getBlocks()
	{
		return blocks;
	}

	public boolean isEmpty()
	{
		return blocks.isEmpty();
	}

	public boolean hasBlock(Block block, int meta)
	{
		if (block == null)
		{
			return false;
		}

		for (BlockMeta blockMeta : blocks)
		{
			if (blockMeta.getBlock() == block)
			{
				if (meta < 0 || meta == OreDictionary.WILDCARD_VALUE)
				{
					return true;
				}

				if (blockMeta.getMeta() == meta)
				{
					return true;
				}
			}
		}

		return false;
	}

	public boolean hasBlockState(IBlockState state)
	{
		if (state == null)
		{
			return false;
		}

		Block block = state.getBlock();

		return hasBlock(block, block.getMetaFromState(state));
	}

	public void refreshBlocks()
	{
		blocks.clear();

		Arrays.stream(getValues()).filter(value -> !Strings.isNullOrEmpty(value)).forEach(value ->
		{
			value = value.trim();

			if (OreDictionary.doesOreNameExist(value))
			{
				for (ItemStack stack : OreDictionary.getOres(value, false))
				{
					if (!stack.isEmpty() && stack.getItem() instanceof ItemBlock)
					{
						Block block = ((ItemBlock)stack.getItem()).getBlock();

						if (block != null)
						{
							blocks.add(new BlockMeta(block, stack.getMetadata()));
						}
					}
				}
			}
			else
			{
				if (!value.contains(":"))
				{
					value = "minecraft:" + value;
				}

				BlockMeta blockMeta;

				if (value.indexOf(':') != value.lastIndexOf(':'))
				{
					int i = value.lastIndexOf(':');

					blockMeta = new BlockMeta(value.substring(0, i), value.substring(i + 1));
				}
				else
				{
					blockMeta = new BlockMeta(value, 0);
				}

				if (blockMeta.getBlock() != null)
				{
					blocks.add(blockMeta);
				}
			}
		});
	}
}