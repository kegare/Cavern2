package cavern.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import cavern.core.Cavern;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.oredict.OreDictionary;

public final class CaveUtils
{
	public static ModContainer getModContainer()
	{
		ModContainer mod = Loader.instance().getIndexedModList().get(Cavern.MODID);

		if (mod == null)
		{
			mod = Loader.instance().activeModContainer();

			if (mod == null || !Cavern.MODID.equals(mod.getModId()))
			{
				return new DummyModContainer(Cavern.metadata);
			}
		}

		return mod;
	}

	public static ResourceLocation getKey(String key)
	{
		return new ResourceLocation(Cavern.MODID, key);
	}

	public static int compareWithNull(Object o1, Object o2)
	{
		return (o1 == null ? 1 : 0) - (o2 == null ? 1 : 0);
	}

	public static boolean archiveDirectory(File dir, File dest)
	{
		Path dirPath = dir.toPath();
		String parent = dir.getName();
		Map<String, String> env = Maps.newHashMap();
		env.put("create", "true");
		URI uri = dest.toURI();

		try
		{
			uri = new URI("jar:" + uri.getScheme(), uri.getPath(), null);
		}
		catch (URISyntaxException e)
		{
			return false;
		}

		try (FileSystem zipfs = FileSystems.newFileSystem(uri, env))
		{
			Files.createDirectory(zipfs.getPath(parent));

			for (File file : dir.listFiles())
			{
				if (file.isDirectory())
				{
					Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>()
					{
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
						{
							Files.copy(file, zipfs.getPath(parent, dirPath.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);

							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
						{
							Files.createDirectory(zipfs.getPath(parent, dirPath.relativize(dir).toString()));

							return FileVisitResult.CONTINUE;
						}
					});
				}
				else
				{
					Files.copy(file.toPath(), zipfs.getPath(parent, file.getName()), StandardCopyOption.REPLACE_EXISTING);
				}
			}

			return true;
		}
		catch (IOException e)
		{
			CaveLog.log(Level.WARN, e, "An error occurred archiving the " + parent + "directory.");
		}

		return false;
	}

	public static boolean areBlockStatesEqual(@Nullable IBlockState stateA, @Nullable IBlockState stateB)
	{
		if (stateA == stateB)
		{
			return true;
		}

		if (stateA == null || stateB == null)
		{
			return false;
		}

		return stateA.getBlock() == stateB.getBlock() && stateA.getBlock().getMetaFromState(stateA) == stateB.getBlock().getMetaFromState(stateB);
	}

	public static boolean isItemEqual(ItemStack target, ItemStack input)
	{
		if (target.getHasSubtypes())
		{
			return OreDictionary.itemMatches(target, input, false);
		}

		return target.getItem() == input.getItem();
	}

	@Nullable
	public static <E> E getRandomObject(@Nullable List<E> list)
	{
		return getRandomObject(list, null);
	}

	public static <E> E getRandomObject(@Nullable List<E> list, @Nullable E nullDefault)
	{
		if (list == null || list.isEmpty())
		{
			return nullDefault;
		}

		if (list.size() == 1)
		{
			return list.get(0);
		}

		return list.get(MathHelper.floor(Math.random() * list.size()));
	}

	public static boolean isPickaxe(ItemStack stack)
	{
		if (stack.isEmpty())
		{
			return false;
		}

		Item item = stack.getItem();

		if (item instanceof ItemPickaxe)
		{
			return true;
		}

		if (item.getToolClasses(stack).contains("pickaxe"))
		{
			return true;
		}

		return false;
	}

	public static boolean isAxe(ItemStack stack)
	{
		if (stack.isEmpty())
		{
			return false;
		}

		Item item = stack.getItem();

		if (item instanceof ItemAxe)
		{
			return true;
		}

		if (item.getToolClasses(stack).contains("axe"))
		{
			return true;
		}

		return false;
	}

	public static boolean isShovel(ItemStack stack)
	{
		if (stack.isEmpty())
		{
			return false;
		}

		Item item = stack.getItem();

		if (item instanceof ItemSpade)
		{
			return true;
		}

		if (item.getToolClasses(stack).contains("shovel"))
		{
			return true;
		}

		return false;
	}

	@Nullable
	public static String getEntityName(ResourceLocation key)
	{
		if (key == null)
		{
			return null;
		}

		String entityName = EntityList.getTranslationName(key);

		if (!Strings.isNullOrEmpty(entityName))
		{
			return Cavern.proxy.translate("entity." + entityName + ".name");
		}

		return key.toString();
	}

	@Nullable
	public static String getEntityName(Class<? extends Entity> entityClass)
	{
		return getEntityName(EntityList.getKey(entityClass));
	}

	public static boolean isMoving(@Nullable Entity entity)
	{
		if (entity == null)
		{
			return false;
		}

		double motionX = entity.motionX;
		double motionY = entity.motionY;
		double motionZ = entity.motionZ;

		return motionX * motionX + motionY * motionY + motionZ * motionZ > 0.01D;
	}

	public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, String deobfName, String reobfName)
	{
		if (FMLLaunchHandler.isDeobfuscatedEnvironment())
		{
			return ObfuscationReflectionHelper.getPrivateValue(classToAccess, instance, deobfName);
		}
		else
		{
			return ObfuscationReflectionHelper.getPrivateValue(classToAccess, instance, reobfName);
		}
	}

	public static <T, E> void setPrivateValue(Class<? super T> classToAccess, T instance, E value, String deobfName, String reobfName)
	{
		if (FMLLaunchHandler.isDeobfuscatedEnvironment())
		{
			ObfuscationReflectionHelper.setPrivateValue(classToAccess, instance, value, deobfName);
		}
		else
		{
			ObfuscationReflectionHelper.setPrivateValue(classToAccess, instance, value, reobfName);
		}
	}
}