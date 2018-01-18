package cavern.util;

import java.io.File;

import javax.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

import cavern.core.Cavern;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.CheckResult;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.versioning.ComparableVersion;

public class Version
{
	private static String CURRENT;

	public static boolean DEV_DEBUG = false;

	public static void initVersion()
	{
		CURRENT = Strings.nullToEmpty(Cavern.metadata.version);

		ModContainer mod = CaveUtils.getModContainer();
		File file = mod == null ? null : mod.getSource();

		if (file != null && file.exists())
		{
			if (file.isFile())
			{
				String name = FilenameUtils.getBaseName(file.getName());

				if (StringUtils.endsWithIgnoreCase(name, "dev"))
				{
					DEV_DEBUG = true;
				}
			}
			else if (file.isDirectory())
			{
				DEV_DEBUG = true;
			}
		}
		else if (!FMLForgePlugin.RUNTIME_DEOBF)
		{
			DEV_DEBUG = true;
		}

		if (Cavern.metadata.version.endsWith("dev"))
		{
			DEV_DEBUG = true;
		}
		else if (DEV_DEBUG)
		{
			Cavern.metadata.version += "-dev";
		}
	}

	public static CheckResult getResult()
	{
		return ForgeVersion.getResult(CaveUtils.getModContainer());
	}

	public static Status getStatus()
	{
		return getResult().status;
	}

	@Nullable
	public static ComparableVersion getTarget()
	{
		return getResult().target;
	}

	public static String getCurrent()
	{
		return CURRENT;
	}

	public static ComparableVersion getLatest()
	{
		return ObjectUtils.defaultIfNull(getTarget(), new ComparableVersion(CURRENT));
	}

	public static boolean isOutdated()
	{
		return getStatus() == Status.OUTDATED || getStatus() == Status.BETA_OUTDATED;
	}

	public static boolean isBeta()
	{
		return StringUtils.containsIgnoreCase(CURRENT, "beta");
	}

	public static boolean isAlpha()
	{
		return StringUtils.containsIgnoreCase(CURRENT, "alpha");
	}
}