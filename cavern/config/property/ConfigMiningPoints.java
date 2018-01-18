package cavern.config.property;

import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import cavern.block.BlockCave;
import cavern.block.CaveBlocks;
import cavern.config.GeneralConfig;
import cavern.stats.MinerStats;
import cavern.util.BlockMeta;
import cavern.util.CaveUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;

public class ConfigMiningPoints
{
	private String[] values;

	private boolean init;

	public String[] getValues()
	{
		if (values == null)
		{
			values = new String[0];
		}

		return values;
	}

	public void setValues(String[] entries)
	{
		values = entries;
	}

	public boolean shouldInit()
	{
		return init;
	}

	public void setInit(boolean flag)
	{
		init = flag;
	}

	public void init()
	{
		Set<PointEntry> entries = Sets.newTreeSet();

		entries.add(new PointEntry("oreCoal", 1));
		entries.add(new PointEntry("oreIron", 1));
		entries.add(new PointEntry("oreGold", 1));
		entries.add(new PointEntry("oreRedstone", 2));
		entries.add(new PointEntry(new BlockMeta(Blocks.LIT_REDSTONE_ORE, 0), 2));
		entries.add(new PointEntry("oreLapis", 3));
		entries.add(new PointEntry("oreEmerald", 3));
		entries.add(new PointEntry("oreDiamond", 5));
		entries.add(new PointEntry("oreQuartz", 2));
		entries.add(new PointEntry("oreCopper", 1));
		entries.add(new PointEntry("oreTin", 1));
		entries.add(new PointEntry("oreLead", 1));
		entries.add(new PointEntry("oreSilver", 1));
		entries.add(new PointEntry("oreAdamantium", 1));
		entries.add(new PointEntry("oreAluminum", 1));
		entries.add(new PointEntry("oreApatite", 1));
		entries.add(new PointEntry("oreMythril", 1));
		entries.add(new PointEntry("oreOnyx", 1));
		entries.add(new PointEntry("oreUranium", 2));
		entries.add(new PointEntry("oreSapphire", 3));
		entries.add(new PointEntry("oreRuby", 3));
		entries.add(new PointEntry("oreTopaz", 2));
		entries.add(new PointEntry("oreChrome", 1));
		entries.add(new PointEntry("orePlatinum", 1));
		entries.add(new PointEntry("oreTitanium", 1));
		entries.add(new PointEntry("oreSulfur", 1));
		entries.add(new PointEntry("oreSaltpeter", 1));
		entries.add(new PointEntry("oreFirestone", 2));
		entries.add(new PointEntry("oreSalt", 1));
		entries.add(new PointEntry("oreJade", 1));
		entries.add(new PointEntry("oreManganese", 1));
		entries.add(new PointEntry("oreLanite", 1));
		entries.add(new PointEntry("oreMeurodite", 1));
		entries.add(new PointEntry("oreSoul", 1));
		entries.add(new PointEntry("oreSunstone", 1));
		entries.add(new PointEntry("oreZinc", 1));
		entries.add(new PointEntry("oreCrocoite", 3));
		entries.add(new PointEntry("glowstone", 2));
		entries.add(new PointEntry("oreGypsum", 1));
		entries.add(new PointEntry("oreChalcedonyB", 1));
		entries.add(new PointEntry("oreChalcedonyW", 1));
		entries.add(new PointEntry("oreMagnetite", 1));
		entries.add(new PointEntry("oreNiter", 1));
		entries.add(new PointEntry("oreSchorl", 1));
		entries.add(new PointEntry("oreCobalt", 1));
		entries.add(new PointEntry("oreArdite", 1));
		entries.add(new PointEntry("oreAquamarine", 2));
		entries.add(new PointEntry("oreMagnite", 1));
		entries.add(new PointEntry("oreRandomite", 2));
		entries.add(new PointEntry("oreHexcite", 4));
		entries.add(new PointEntry(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.FISSURED_STONE.getMetadata()), 3));
		entries.add(new PointEntry(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.FISSURED_PACKED_ICE.getMetadata()), 3));

		ConfigCategory category = GeneralConfig.config.getCategory(Configuration.CATEGORY_GENERAL);
		Property prop = category.get("miningPoints");

		if (prop != null)
		{
			String[] data = entries.stream().filter(entry -> entry.isValid()).map(PointEntry::toString).toArray(String[]::new);

			prop.set(data);

			setValues(data);
		}
	}

	public void refreshPoints()
	{
		MinerStats.MINING_POINTS.clear();

		for (String value : values)
		{
			if (!Strings.isNullOrEmpty(value) && value.contains(","))
			{
				value = value.trim();

				int i = value.indexOf(',');
				String str = value.substring(0, i).trim();
				int point = NumberUtils.toInt(value.substring(i + 1));

				if (OreDictionary.doesOreNameExist(str))
				{
					MinerStats.setPointAmount(str, point);
				}
				else
				{
					if (!str.contains(":"))
					{
						str = "minecraft:" + str;
					}

					BlockMeta blockMeta;

					if (str.indexOf(':') != str.lastIndexOf(':'))
					{
						i = str.lastIndexOf(':');

						blockMeta = new BlockMeta(str.substring(0, i), str.substring(i + 1));
					}
					else
					{
						blockMeta = new BlockMeta(str, 0);
					}

					if (blockMeta.isNotAir())
					{
						MinerStats.setPointAmount(blockMeta, point);
					}
				}
			}
		}
	}

	public class PointEntry implements Comparable<PointEntry>
	{
		private String name;
		private int point;

		public PointEntry(String name, int point)
		{
			this.name = name;
			this.point = point;
		}

		public PointEntry(BlockMeta blockMeta, int point)
		{
			this(blockMeta.getName(true), point);
		}

		public String getName()
		{
			return name;
		}

		public int getPoint()
		{
			return point;
		}

		public boolean isOreDict()
		{
			return OreDictionary.doesOreNameExist(name);
		}

		public boolean isNotOreDictEmpty()
		{
			return OreDictionary.getOres(name, false).size() > 0;
		}

		public boolean isValid()
		{
			if (Strings.isNullOrEmpty(name))
			{
				return false;
			}

			return isOreDict() && isNotOreDictEmpty() || Block.getBlockFromName(name) != null;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			else if (!(obj instanceof PointEntry))
			{
				return false;
			}

			PointEntry entry = (PointEntry)obj;

			return name.equals(entry.name);
		}

		@Override
		public int hashCode()
		{
			return name.hashCode();
		}

		@Override
		public String toString()
		{
			return name + "," + point;
		}

		@Override
		public int compareTo(PointEntry entry)
		{
			int i = CaveUtils.compareWithNull(this, entry);

			if (i == 0 && entry != null)
			{
				i = Boolean.compare(!isOreDict(), !entry.isOreDict());

				if (i == 0)
				{
					i = name.compareTo(entry.name);
				}
			}

			return i;
		}
	}
}