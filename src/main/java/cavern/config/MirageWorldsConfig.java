package cavern.config;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.core.Cavern;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class MirageWorldsConfig
{
	public static Configuration config;

	public static int frostMountains;
	public static int wideDesert;
	public static int theVoid;
	public static int darkForest;
	public static int crownCliffs;
	public static int skyland;

	public static void syncConfig()
	{
		String category = "dimension";
		Property prop;
		String comment;
		List<String> propOrder = Lists.newArrayList();

		if (config == null)
		{
			config = Config.loadConfig("mirageworlds", category);
		}

		prop = config.get(category, "frostMountains", -55);
		prop.setRequiresMcRestart(true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		frostMountains = prop.getInt(frostMountains);

		prop = config.get(category, "wideDesert", -56);
		prop.setRequiresMcRestart(true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		wideDesert = prop.getInt(wideDesert);

		prop = config.get(category, "theVoid", -57);
		prop.setRequiresMcRestart(true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		theVoid = prop.getInt(theVoid);

		prop = config.get(category, "darkForest", -58);
		prop.setRequiresMcRestart(true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		darkForest = prop.getInt(darkForest);

		prop = config.get(category, "crownCliffs", -59);
		prop.setRequiresMcRestart(true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		crownCliffs = prop.getInt(crownCliffs);

		prop = config.get(category, "skyland", -60);
		prop.setRequiresMcRestart(true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		skyland = prop.getInt(skyland);

		config.setCategoryPropertyOrder(category, propOrder);

		Config.saveConfig(config);
	}
}