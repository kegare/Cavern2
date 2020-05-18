package cavern.config;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.TreeMap;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.Level;

import com.google.common.collect.Maps;

import cavern.util.CaveLog;
import cavern.util.CaveUtils;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class CaveConfiguration extends Configuration implements Comparator<String>
{
	public CaveConfiguration() {}

	public CaveConfiguration(File file)
	{
		super(file);
	}

	public CaveConfiguration(File file, String configVersion)
	{
		super(file, configVersion);
	}

	public CaveConfiguration(File file, String configVersion, boolean caseSensitiveCustomCategories)
	{
		super(file, configVersion, caseSensitiveCustomCategories);
	}

	public CaveConfiguration(File file, boolean caseSensitiveCustomCategories)
	{
		super(file, caseSensitiveCustomCategories);
	}

	@Override
	public void save()
	{
		setNewCategoriesMap();

		super.save();
	}

	@SuppressWarnings("unchecked")
	private void setNewCategoriesMap()
	{
		try
		{
			Field field = Configuration.class.getDeclaredField("categories");
			field.setAccessible(true);

			TreeMap<String, ConfigCategory> treeMap = (TreeMap<String, ConfigCategory>)field.get(this);
			TreeMap<String, ConfigCategory> newMap = Maps.newTreeMap(this);
			newMap.putAll(treeMap);

			field.set(this, newMap);
		}
		catch (Throwable e)
		{
			CaveLog.log(Level.WARN, e, "An error occurred on replace configuration categories map.");
		}
	}

	@Override
	public int compare(String o1, String o2)
	{
		int result = CaveUtils.compareWithNull(o1, o2);

		if (result == 0 && o1 != null && o2 != null)
		{
			boolean flag1 = NumberUtils.isCreatable(o1);
			boolean flag2 = NumberUtils.isCreatable(o2);
			result = Boolean.compare(flag1, flag2);

			if (result == 0)
			{
				if (flag1 && flag2)
				{
					result = Integer.compare(NumberUtils.toInt(o1), NumberUtils.toInt(o2));
				}
				else if (!flag1 && !flag2)
				{
					result = o1.compareTo(o2);
				}
			}
		}

		return result;
	}
}