package cavern.config;

import java.io.File;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.Level;

import com.google.common.base.Strings;

import cavern.util.CaveLog;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;

public class Config
{
	public static final String LANG_KEY = "cavern.config.";

	protected static final Side SIDE = FMLLaunchHandler.side();

	public static ConfigChecker configChecker;

	public static boolean highProfiles;

	public static boolean updateConfig()
	{
		if (configChecker == null)
		{
			File dir = getConfigDir();

			if (!dir.exists())
			{
				dir.mkdirs();
			}

			File file = new File(dir, "internal.properties");

			configChecker = new ConfigChecker(file, dir);
		}

		return configChecker.updateConfigFiles();
	}

	public static File getConfigDir()
	{
		return new File(Loader.instance().getConfigDir(), "cavern");
	}

	public static File getConfigFile(String name)
	{
		File dir = getConfigDir();

		if (!dir.exists())
		{
			dir.mkdirs();
		}

		return new File(dir, name + ".cfg");
	}

	public static Configuration loadConfig(String name)
	{
		File file = getConfigFile(name);
		Configuration config = new CaveConfiguration(file, true);

		try
		{
			config.load();
		}
		catch (Exception e)
		{
			File dest = new File(file.getParentFile(), file.getName() + ".bak");

			if (dest.exists())
			{
				dest.delete();
			}

			file.renameTo(dest);

			CaveLog.log(Level.ERROR, e, "A critical error occured reading the " + file.getName() + " file, defaults will be used - the invalid file is backed up at " + dest.getName());
		}

		return config;
	}

	public static File getConfigFile(String name, String category)
	{
		File dir = getConfigDir();

		if (!dir.exists())
		{
			dir.mkdirs();
		}

		return new File(dir, name + "-" + category + ".cfg");
	}

	public static Configuration loadConfig(String name, String category)
	{
		File file = getConfigFile(name, category);
		Configuration config = new CaveConfiguration(file, true);

		try
		{
			config.load();
		}
		catch (Exception e)
		{
			File dest = new File(file.getParentFile(), file.getName() + ".bak");

			if (dest.exists())
			{
				dest.delete();
			}

			file.renameTo(dest);

			CaveLog.log(Level.ERROR, e, "A critical error occured reading the " + file.getName() + " file, defaults will be used - the invalid file is backed up at " + dest.getName());
		}

		return config;
	}

	public static void saveConfig(@Nullable Configuration config)
	{
		if (config != null && config.hasChanged())
		{
			config.save();
		}
	}

	@Nullable
	public static Biome getBiomeFromString(@Nullable String str)
	{
		return getBiomeFromString(str, null);
	}

	public static Biome getBiomeFromString(@Nullable String str, @Nullable Biome fallback)
	{
		if (Strings.isNullOrEmpty(str))
		{
			return null;
		}

		if (NumberUtils.isCreatable(str))
		{
			int id = NumberUtils.toInt(str, -1);

			if (id < 0 || id > 255)
			{
				return null;
			}

			return Biome.getBiome(id, fallback);
		}

		ResourceLocation key = new ResourceLocation(str);

		return ObjectUtils.defaultIfNull(Biome.REGISTRY.getObject(key), fallback);
	}

	public static boolean containsBiome(@Nullable String[] biomes, @Nullable Biome biome)
	{
		if (biomes == null || biomes.length <= 0)
		{
			return false;
		}

		if (biome == null)
		{
			return false;
		}

		for (String str : biomes)
		{
			if (NumberUtils.isCreatable(str))
			{
				if (NumberUtils.toInt(str, -1) == Biome.getIdForBiome(biome))
				{
					return true;
				}
			}
			else if (str.equals(biome.getRegistryName().toString()))
			{
				return true;
			}
		}

		return false;
	}
}