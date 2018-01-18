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
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.ToastMessage;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.oredict.OreDictionary;

public class CaveUtils
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

	@Nullable
	public static IBlockState getBlockStateFromMeta(Block block, int meta)
	{
		try
		{
			return block.getStateFromMeta(meta);
		}
		catch (IllegalArgumentException e)
		{
			return null;
		}
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

		return list.get(MathHelper.floor(Math.random() * list.size()));
	}

	public static boolean isItemPickaxe(ItemStack stack)
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

	public static void setPositionAndUpdate(@Nullable Entity entity, @Nullable BlockPos pos)
	{
		if (entity != null && pos != null)
		{
			entity.setPositionAndUpdate(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		}
	}

	public static void setDimensionChange(EntityPlayerMP player)
	{
		if (!player.capabilities.isCreativeMode)
		{
			ObfuscationReflectionHelper.setPrivateValue(EntityPlayerMP.class, player, true, "invulnerableDimensionChange", "field_184851_cj");
		}
	}

	public static void transferPlayerToDimension(EntityPlayerMP player, DimensionType type, Teleporter teleporter)
	{
		setDimensionChange(player);

		player.mcServer.getPlayerList().transferPlayerToDimension(player, type.getId(), teleporter);
		player.addExperienceLevel(0);
	}

	public static SleepResult trySleep(EntityPlayer player, BlockPos pos)
	{
		World world = player.world;
		EnumFacing facing = world.getBlockState(pos).getValue(BlockHorizontal.FACING);

		if (!world.isRemote)
		{
			if (player.isPlayerSleeping() || !player.isEntityAlive())
			{
				return SleepResult.OTHER_PROBLEM;
			}

			if (!bedInRange(player, pos, facing))
			{
				return SleepResult.TOO_FAR_AWAY;
			}

			List<EntityMob> list = world.getEntitiesWithinAABB(EntityMob.class,
				new AxisAlignedBB(pos.getX() - 8.0D, pos.getY() - 5.0D, pos.getZ() - 8.0D, pos.getX() + 8.0D, pos.getY() + 5.0D, pos.getZ() + 8.0D));

			if (!list.isEmpty())
			{
				return SleepResult.NOT_SAFE;
			}
		}

		if (player.isRiding())
		{
			player.dismountRidingEntity();
		}

		setSize(player, 0.2F, 0.2F);

		IBlockState state = null;

		if (world.isBlockLoaded(pos))
		{
			state = world.getBlockState(pos);
		}

		if (state != null && state.getBlock().isBed(state, world, pos, player))
		{
			float offsetX = 0.5F + facing.getFrontOffsetX() * 0.4F;
			float offsetZ = 0.5F + facing.getFrontOffsetZ() * 0.4F;

			setRenderOffsetForSleep(player, facing);

			player.setPosition(pos.getX() + offsetX, pos.getY() + 0.6875F, pos.getZ() + offsetZ);
		}
		else
		{
			player.setPosition(pos.getX() + 0.5F, pos.getY() + 0.6875F, pos.getZ() + 0.5F);
		}

		ObfuscationReflectionHelper.setPrivateValue(EntityPlayer.class, player, true, "sleeping", "field_71083_bS");
		ObfuscationReflectionHelper.setPrivateValue(EntityPlayer.class, player, 0, "sleepTimer", "field_71076_b");

		player.bedLocation = pos;
		player.motionX = 0.0D;
		player.motionY = 0.0D;
		player.motionZ = 0.0D;

		if (!world.isRemote)
		{
			world.updateAllPlayersSleepingFlag();
		}

		return SleepResult.OK;
	}

	public static boolean bedInRange(Entity entity, BlockPos pos, EnumFacing facing)
	{
		if (Math.abs(entity.posX - pos.getX()) <= 3.0D && Math.abs(entity.posY - pos.getY()) <= 2.0D && Math.abs(entity.posZ - pos.getZ()) <= 3.0D)
		{
			return true;
		}
		else
		{
			BlockPos blockpos = pos.offset(facing.getOpposite());

			return Math.abs(entity.posX - blockpos.getX()) <= 3.0D && Math.abs(entity.posY - blockpos.getY()) <= 2.0D && Math.abs(entity.posZ - blockpos.getZ()) <= 3.0D;
		}
	}

	public static void setSize(Entity entity, float width, float height)
	{
		if (width != entity.width || height != entity.height)
		{
			float f = entity.width;

			entity.width = width;
			entity.height = height;

			if (entity.width < f)
			{
				double half = width / 2.0D;

				entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - half, entity.posY, entity.posZ - half, entity.posX + half, entity.posY + entity.height, entity.posZ + half));

				return;
			}

			AxisAlignedBB box = entity.getEntityBoundingBox();

			entity.setEntityBoundingBox(new AxisAlignedBB(box.minX, box.minY, box.minZ, box.minX + entity.width, box.minY + entity.height, box.minZ + entity.width));

			if (entity.width > f && !entity.world.isRemote)
			{
				boolean firstUpdate = ObfuscationReflectionHelper.getPrivateValue(Entity.class, entity, "firstUpdate", "field_70148_d");

				if (!firstUpdate)
				{
					entity.move(MoverType.SELF, f - entity.width, 0.0D, f - entity.width);
				}
			}
		}
	}

	public static void setRenderOffsetForSleep(EntityPlayer player, EnumFacing facing)
	{
		player.renderOffsetX = -1.8F * facing.getFrontOffsetX();
		player.renderOffsetZ = -1.8F * facing.getFrontOffsetZ();
	}

	public static boolean grantAdvancement(EntityPlayer entityPlayer, String key)
	{
		return grantCriterion(entityPlayer, key, key);
	}

	public static boolean grantCriterion(EntityPlayer entityPlayer, String key, String criterion)
	{
		if (entityPlayer == null || !(entityPlayer instanceof EntityPlayerMP))
		{
			return false;
		}

		EntityPlayerMP player = (EntityPlayerMP)entityPlayer;
		Advancement advancement = player.mcServer.getAdvancementManager().getAdvancement(getKey(key));

		return advancement != null && player.getAdvancements().grantCriterion(advancement, criterion);
	}

	public static boolean grantToast(EntityPlayer player, String key)
	{
		if (grantCriterion(player, "toasts/" + key, key))
		{
			CaveNetworkRegistry.sendTo(new ToastMessage(key), (EntityPlayerMP)player);

			return true;
		}

		return false;
	}
}