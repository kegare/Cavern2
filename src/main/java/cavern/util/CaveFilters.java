package cavern.util;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;

public final class CaveFilters
{
	public static boolean blockFilter(@Nullable BlockMeta blockMeta, @Nullable String filter)
	{
		if (blockMeta == null || Strings.isNullOrEmpty(filter))
		{
			return false;
		}

		if (StringUtils.containsIgnoreCase(blockMeta.getName(), filter))
		{
			return true;
		}

		if (StringUtils.containsIgnoreCase(blockMeta.getMetaString(), filter))
		{
			return true;
		}

		Block block = blockMeta.getBlock();
		ItemStack stack = new ItemStack(block, 1, blockMeta.getMeta());

		if (stack.getItem() == Items.AIR)
		{
			if (StringUtils.containsIgnoreCase(block.getLocalizedName(), filter))
			{
				return true;
			}

			if (StringUtils.containsIgnoreCase(block.getUnlocalizedName(), filter))
			{
				return true;
			}
		}
		else
		{
			if (StringUtils.containsIgnoreCase(stack.getDisplayName(), filter))
			{
				return true;
			}

			if (StringUtils.containsIgnoreCase(stack.getUnlocalizedName(), filter))
			{
				return true;
			}
		}

		if (StringUtils.containsIgnoreCase(block.getHarvestTool(blockMeta.getBlockState()), filter))
		{
			return true;
		}

		return false;
	}

	public static boolean itemFilter(@Nullable ItemMeta itemMeta, @Nullable String filter)
	{
		if (itemMeta == null || Strings.isNullOrEmpty(filter))
		{
			return false;
		}

		if (StringUtils.containsIgnoreCase(itemMeta.getName(), filter))
		{
			return true;
		}

		ItemStack stack = itemMeta.getItemStack();

		if (StringUtils.containsIgnoreCase(stack.getDisplayName(), filter))
		{
			return true;
		}

		if (StringUtils.containsIgnoreCase(stack.getUnlocalizedName(), filter))
		{
			return true;
		}

		if (stack.getItem().getToolClasses(stack).contains(filter))
		{
			return true;
		}

		return false;
	}

	public static boolean biomeFilter(@Nullable Biome biome, @Nullable String filter)
	{
		if (biome == null || Strings.isNullOrEmpty(filter))
		{
			return false;
		}

		if (Biome.getIdForBiome(biome) == NumberUtils.toInt(filter, -1) || StringUtils.containsIgnoreCase(biome.getRegistryName().toString(), filter))
		{
			return true;
		}

		for (BiomeDictionary.Type type : BiomeDictionary.getTypes(biome))
		{
			if (type.getName().equalsIgnoreCase(filter))
			{
				return true;
			}
		}

		if (blockFilter(new BlockMeta(biome.topBlock), filter))
		{
			return true;
		}

		if (blockFilter(new BlockMeta(biome.fillerBlock), filter))
		{
			return true;
		}

		BiomeType type;

		try
		{
			type = BiomeType.valueOf(filter.toUpperCase());
		}
		catch (IllegalArgumentException e)
		{
			type = null;
		}

		if (type != null)
		{
			ImmutableList<BiomeEntry> list = BiomeManager.getBiomes(type);

			if (list != null)
			{
				for (BiomeEntry entry : list)
				{
					if (entry != null && entry.biome.getRegistryName().equals(biome.getRegistryName()))
					{
						return true;
					}
				}
			}
		}

		return false;
	}
}