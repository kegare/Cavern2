package cavern.util;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

import cavern.core.Cavern;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.CheckResult;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

public final class Version
{
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
		return Strings.nullToEmpty(Cavern.metadata.version);
	}

	public static ComparableVersion getLatest()
	{
		return ObjectUtils.defaultIfNull(getTarget(), new ComparableVersion(getCurrent()));
	}

	public static boolean isOutdated()
	{
		return getStatus() == Status.OUTDATED || getStatus() == Status.BETA_OUTDATED;
	}

	public static boolean isDev()
	{
		return FMLLaunchHandler.isDeobfuscatedEnvironment();
	}

	public static boolean isBeta()
	{
		return StringUtils.containsIgnoreCase(getCurrent(), "beta");
	}

	public static boolean isAlpha()
	{
		return StringUtils.containsIgnoreCase(getCurrent(), "alpha");
	}
}