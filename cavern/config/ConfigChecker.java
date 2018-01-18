package cavern.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.google.common.base.Strings;

import cavern.util.CaveLog;
import net.minecraftforge.fml.common.versioning.ComparableVersion;

public class ConfigChecker implements FilenameFilter
{
	public static final String CONFIG_VERSION = "2.0";

	private final File internalFile;
	private final File configDir;

	private Properties properties;
	private boolean isUpdated;
	private boolean isNotified;

	public ConfigChecker(File file, File dir)
	{
		this.internalFile = file;
		this.configDir = dir;
	}

	@Nullable
	public Properties getProperties()
	{
		return properties;
	}

	public boolean isUpdated()
	{
		return isUpdated;
	}

	public boolean isNotified()
	{
		return isNotified;
	}

	public void setNotified(boolean notify)
	{
		isNotified = notify;
	}

	public boolean isOutdated()
	{
		if (!internalFile.exists())
		{
			return true;
		}

		if (properties == null || !properties.containsKey("config.version"))
		{
			properties = new Properties();

			try (FileInputStream stream = new FileInputStream(internalFile))
			{
				properties.load(stream);
			}
			catch (IOException e)
			{
				CaveLog.log(Level.WARN, e, "An error occurred reading the %s file.", internalFile.getName());
			}
		}

		String value = properties.getProperty("config.update", "true").toLowerCase(Locale.ENGLISH);

		if (value.equals("false") || value.equals("0"))
		{
			return false;
		}

		value = properties.getProperty("config.version");

		if (Strings.isNullOrEmpty(value))
		{
			return true;
		}

		ComparableVersion configVersion = new ComparableVersion(CONFIG_VERSION);
		ComparableVersion currentVersion = new ComparableVersion(value);

		return configVersion.compareTo(currentVersion) > 0;
	}

	public void updateFile()
	{
		if (internalFile.exists())
		{
			internalFile.delete();
		}

		properties = new Properties();
		properties.setProperty("config.version", CONFIG_VERSION);
		properties.setProperty("config.update", "true");

		try (FileOutputStream stream = new FileOutputStream(internalFile))
		{
			properties.store(stream, null);
		}
		catch (IOException e)
		{
			CaveLog.log(Level.WARN, e, "An error occurred writing the %s file.", internalFile.getName());
		}
	}

	public boolean updateConfigFiles()
	{
		if (!isOutdated())
		{
			return false;
		}

		updateFile();

		for (File file : configDir.listFiles(this))
		{
			if (file.delete())
			{
				isUpdated = true;
			}
		}

		return isUpdated;
	}

	@Override
	public boolean accept(File dir, String name)
	{
		return !Strings.isNullOrEmpty(name) && name.endsWith(".cfg");
	}
}