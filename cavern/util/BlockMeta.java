package cavern.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.oredict.OreDictionary;

public class BlockMeta implements Comparable<BlockMeta>
{
	private Block block;
	private int meta;

	public BlockMeta(Block block, int meta)
	{
		this.block = block;
		this.meta = meta;
	}

	public BlockMeta(IBlockState state)
	{
		this(state.getBlock(), state.getBlock().getMetaFromState(state));
	}

	public BlockMeta(String name, int meta)
	{
		this(ObjectUtils.defaultIfNull(Block.getBlockFromName(name), Blocks.AIR), meta);
	}

	public BlockMeta(String name, String meta)
	{
		this(name, -1);
		this.meta = getMetaFromString(block, meta);
	}

	public Block getBlock()
	{
		return block;
	}

	public boolean isNotAir()
	{
		return block != Blocks.AIR;
	}

	public int getMeta()
	{
		return meta;
	}

	public IBlockState getBlockState()
	{
		return block.getStateFromMeta(meta);
	}

	public String getBlockName()
	{
		return block.getRegistryName().toString();
	}

	public String getMetaName()
	{
		return getMetaName(block, meta);
	}

	public String getMetaString()
	{
		return getMetaString(block, meta);
	}

	public String getName()
	{
		return getName(false);
	}

	public String getName(boolean metaName)
	{
		String name = getBlockName();

		if (meta < 0 || meta == OreDictionary.WILDCARD_VALUE || !Item.getItemFromBlock(block).getHasSubtypes())
		{
			return name;
		}

		return name + ":" + (metaName ? getMetaString() : meta);
	}

	@Override
	public String toString()
	{
		String name = getBlockName();

		if (!Item.getItemFromBlock(block).getHasSubtypes())
		{
			return name;
		}

		if (meta < 0 || meta == OreDictionary.WILDCARD_VALUE)
		{
			return name + ",meta=all";
		}

		return name + ",meta=" + meta;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		else if (obj == null || !(obj instanceof BlockMeta))
		{
			return false;
		}

		BlockMeta blockMeta = (BlockMeta)obj;

		if (block != blockMeta.block)
		{
			return false;
		}
		else if (meta < 0 || meta == OreDictionary.WILDCARD_VALUE || blockMeta.meta < 0 || blockMeta.meta == OreDictionary.WILDCARD_VALUE)
		{
			return true;
		}
		else if (!Item.getItemFromBlock(block).getHasSubtypes() && !Item.getItemFromBlock(blockMeta.block).getHasSubtypes())
		{
			return true;
		}

		return meta == blockMeta.meta;
	}

	@Override
	public int hashCode()
	{
		if (meta < 0 || meta == OreDictionary.WILDCARD_VALUE || !Item.getItemFromBlock(block).getHasSubtypes())
		{
			return block.hashCode();
		}

		return Objects.hashCode(block, meta);
	}

	@Override
	public int compareTo(BlockMeta blockMeta)
	{
		int i = CaveUtils.compareWithNull(this, blockMeta);

		if (i == 0 && blockMeta != null)
		{
			i = getName().compareTo(blockMeta.getName());
		}

		return i;
	}

	public static final Pattern NUMBER_PATTERN = Pattern.compile("^[0-9]+$");

	private static final LoadingCache<Pair<Block, String>, Integer> STRING_META_CACHE = CacheBuilder.newBuilder().build(new CacheLoader<Pair<Block, String>, Integer>()
	{
		@Override
		public Integer load(Pair<Block, String> key) throws Exception
		{
			Block block = key.getLeft();
			String str = key.getRight();

			if (block == null || Strings.isNullOrEmpty(str) || str.equalsIgnoreCase("all") || str.equalsIgnoreCase("null"))
			{
				return -1;
			}

			str = str.trim();

			if (NUMBER_PATTERN.matcher(str).matches())
			{
				try
				{
					return Integer.parseInt(str, 10);
				}
				catch (Exception e) {}
			}

			Class<?> clazz = null;

			for (Field field : block.getClass().getDeclaredFields())
			{
				if ((field.getModifiers() & 0x1) != 0 && (field.getModifiers() & 0x8) != 0)
				{
					if (field.getType() == PropertyEnum.class)
					{
						try
						{
							clazz = ((PropertyEnum<?>)field.get(null)).getValueClass();
						}
						catch (Exception e) {}
					}
				}
			}

			if (clazz == null)
			{
				return -1;
			}

			for (Object obj : clazz.getEnumConstants())
			{
				if (obj instanceof IStringSerializable)
				{
					String name = ((IStringSerializable)obj).getName();

					if (str.equalsIgnoreCase(name))
					{
						for (Method method : obj.getClass().getDeclaredMethods())
						{
							if (method.getReturnType() == Integer.TYPE && method.getParameterTypes().length == 0)
							{
								try
								{
									return ((Integer)method.invoke(obj, new Object[0])).intValue();
								}
								catch (Exception e) {}
							}
						}
					}
				}
			}

			return -1;
		};
	});

	private static final LoadingCache<Pair<Block, Integer>, String> META_STRING_CACHE = CacheBuilder.newBuilder().build(new CacheLoader<Pair<Block, Integer>, String>()
	{
		@Override
		public String load(Pair<Block, Integer> key) throws Exception
		{
			Block block = key.getLeft();
			int meta = key.getRight().intValue();

			if (block == null)
			{
				return null;
			}

			if (meta < 0)
			{
				return "all";
			}

			Class<?> clazz = null;

			for (Field field : block.getClass().getDeclaredFields())
			{
				if ((field.getModifiers() & 0x1) != 0 && (field.getModifiers() & 0x8) != 0)
				{
					if (field.getType() == PropertyEnum.class)
					{
						try
						{
							clazz = ((PropertyEnum<?>)field.get(null)).getValueClass();
						}
						catch (Exception e) {}
					}
				}
			}

			if (clazz == null)
			{
				return "null";
			}

			for (Object obj : clazz.getEnumConstants())
			{
				if (obj instanceof IStringSerializable)
				{
					String name = ((IStringSerializable)obj).getName();

					for (Method method : obj.getClass().getDeclaredMethods())
					{
						if (method.getReturnType() == Integer.TYPE && method.getParameterTypes().length == 0)
						{
							try
							{
								if (((Integer)method.invoke(obj, new Object[0])).intValue() == meta)
								{
									return name;
								}
							}
							catch (Exception e) {}
						}
					}
				}
			}

			return "null";
		}
	});

	public static int getMetaFromString(Block block, String str)
	{
		return STRING_META_CACHE.getUnchecked(Pair.of(block, str));
	}

	public static String getMetaName(Block block, int meta)
	{
		if (block.getRegistryName().getResourceDomain().equals("minecraft"))
		{
			return META_STRING_CACHE.getUnchecked(Pair.of(block, meta));
		}

		return Integer.toString(meta);
	}

	public static String getMetaString(Block block, int meta)
	{
		String name = getMetaName(block, meta);

		if (Strings.isNullOrEmpty(name) || name.equals("null"))
		{
			return Integer.toString(0);
		}

		return name;
	}
}