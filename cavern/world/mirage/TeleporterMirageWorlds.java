package cavern.world.mirage;

import cavern.api.CavernAPI;
import cavern.api.IPortalCache;
import cavern.stats.PortalCache;
import cavern.util.CaveUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterMirageWorlds extends Teleporter
{
	public TeleporterMirageWorlds(WorldServer worldServer)
	{
		super(worldServer);
	}

	public ResourceLocation getKey()
	{
		return CaveUtils.getKey("mirage_worlds");
	}

	@Override
	public void placeInPortal(Entity entity, float rotationYaw)
	{
		if (attemptToLastPos(entity) || attemptRandomly(entity) || attemptToVoid(entity))
		{
			entity.motionX = 0.0D;
			entity.motionY = 0.0D;
			entity.motionZ = 0.0D;

			if (entity instanceof EntityLivingBase)
			{
				((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 25, 0, false, false));
			}
		}
	}

	protected boolean attemptToLastPos(Entity entity)
	{
		IPortalCache cache = PortalCache.get(entity);
		ResourceLocation key = getKey();
		DimensionType type = world.provider.getDimensionType();

		if (cache.hasLastPos(key, type))
		{
			BlockPos pos = cache.getLastPos(key, type);

			if (world.getBlockState(pos.down()).getMaterial().isSolid() && world.getBlockState(pos).getBlock().canSpawnInBlock() && world.getBlockState(pos.up()).getBlock().canSpawnInBlock())
			{
				CaveUtils.setPositionAndUpdate(entity, pos);

				return true;
			}

			cache.setLastPos(key, type, null);
		}

		return false;
	}

	protected boolean attemptRandomly(Entity entity)
	{
		int count = 0;

		while (++count < 50)
		{
			int x = MathHelper.floor(entity.posX) + random.nextInt(64) - 32;
			int z = MathHelper.floor(entity.posZ) + random.nextInt(64) - 32;
			int y = CavernAPI.dimension.isInCaves(entity) ? random.nextInt(30) + 20 : random.nextInt(20) + 60;
			BlockPos pos = new BlockPos(x, y, z);

			while (pos.getY() > 1 && world.isAirBlock(pos))
			{
				pos = pos.down();
			}

			while (pos.getY() < world.getActualHeight() - 3 && !world.isAirBlock(pos))
			{
				pos = pos.up();
			}

			if (world.getBlockState(pos.down()).getMaterial().isSolid() && world.getBlockState(pos).getBlock().canSpawnInBlock() && world.getBlockState(pos.up()).getBlock().canSpawnInBlock())
			{
				for (BlockPos around : BlockPos.getAllInBoxMutable(pos.add(-4, 0, -4), pos.add(4, 0, 4)))
				{
					if (world.getBlockState(around).getMaterial().isLiquid())
					{
						return false;
					}
				}

				CaveUtils.setPositionAndUpdate(entity, pos);

				return true;
			}
		}

		return false;
	}

	protected boolean attemptToVoid(Entity entity)
	{
		if (!CavernAPI.dimension.isInTheVoid(entity))
		{
			return false;
		}

		BlockPos pos = new BlockPos(entity.posX, 0.0D, entity.posZ);
		BlockPos from = pos.add(-1, 0, -1);
		BlockPos to = pos.add(1, 0, 1);

		BlockPos.getAllInBoxMutable(from, to).forEach(blockPos -> world.setBlockState(blockPos, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 2));

		CaveUtils.setPositionAndUpdate(entity, pos.up());

		return true;
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, float rotationYaw)
	{
		return false;
	}

	@Override
	public boolean makePortal(Entity entity)
	{
		return false;
	}

	@Override
	public void removeStalePortalLocations(long worldTime) {}
}