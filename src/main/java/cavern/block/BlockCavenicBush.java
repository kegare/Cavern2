package cavern.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import cavern.core.Cavern;
import cavern.util.CaveLog;
import cavern.util.CaveUtils;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCavenicBush extends BlockBush
{
	protected static final AxisAlignedBB MUSHROOM_AABB = new AxisAlignedBB(0.30000001192092896D, 0.0D, 0.30000001192092896D, 0.699999988079071D, 0.4000000059604645D, 0.699999988079071D);

	private static final Map<ResourceLocation, ResourceLocation> TRANSFORM_MAP = new HashMap<>();

	static
	{
		registerTransformation(new ResourceLocation("minecraft", "zombie"), CaveUtils.getKey("cavenic_zombie"));
		registerTransformation(new ResourceLocation("minecraft", "skeleton"), CaveUtils.getKey("cavenic_skeleton"));
		registerTransformation(new ResourceLocation("minecraft", "spider"), CaveUtils.getKey("cavenic_spider"));
		registerTransformation(new ResourceLocation("minecraft", "creeper"), CaveUtils.getKey("cavenic_creeper"));
		registerTransformation(new ResourceLocation("minecraft", "witch"), CaveUtils.getKey("cavenic_witch"));
		registerTransformation(new ResourceLocation("minecraft", "polar_bear"), CaveUtils.getKey("cavenic_bear"));
		registerTransformation(new ResourceLocation("tofucraft", "tofunian"), CaveUtils.getKey("caveman"));
	}

	public static void registerTransformation(ResourceLocation from, ResourceLocation to)
	{
		TRANSFORM_MAP.put(from, to);
	}

	public BlockCavenicBush()
	{
		super();
		this.setUnlocalizedName("cavenicShroom");
		this.setTickRandomly(true);
		this.setSoundType(SoundType.PLANT);
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return MUSHROOM_AABB;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if (rand.nextInt(25) == 0)
		{
			int count = 5;

			for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-4, -1, -4), pos.add(4, 1, 4)))
			{
				if (world.getBlockState(blockpos).getBlock() instanceof BlockCavenicBush)
				{
					if (--count <= 0)
					{
						return;
					}
				}
			}

			BlockPos blockpos = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);

			for (int k = 0; k < 4; ++k)
			{
				if (world.isAirBlock(blockpos) && canBlockStay(world, blockpos, getDefaultState()))
				{
					pos = blockpos;
				}

				blockpos = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
			}

			if (world.isAirBlock(blockpos) && canBlockStay(world, blockpos, getDefaultState()))
			{
				world.setBlockState(blockpos, getDefaultState(), 2);
			}
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
	{
		super.onEntityCollidedWithBlock(world, pos, state, entity);

		if (!world.isRemote && entity instanceof EntityLivingBase)
		{
			EntityLivingBase living = (EntityLivingBase)entity;

			living.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 40));

			if (world.rand.nextFloat() < 0.005F && !entity.isDead)
			{
				ResourceLocation key = TRANSFORM_MAP.get(EntityList.getKey(entity));

				if (key == null)
				{
					return;
				}

				Entity newEntity = EntityList.createEntityByIDFromName(key, world);

				if (newEntity == null)
				{
					return;
				}

				newEntity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);

				if (newEntity instanceof EntityMob)
				{
					((EntityMob)newEntity).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);
				}

				try
				{
					UUID uuid = newEntity.getUniqueID();

					newEntity.readFromNBT(entity.writeToNBT(newEntity.writeToNBT(new NBTTagCompound())));
					newEntity.setUniqueId(uuid);
				}
				catch (Exception e)
				{
					CaveLog.log(Level.WARN, e, "Couldn't transform entity NBT data: {%s}", key.toString());
				}

				world.spawnEntity(newEntity);

				entity.setDead();
				entity.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F + world.rand.nextFloat(), world.rand.nextFloat() * 0.7F + 0.3F);
			}
		}
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos)
	{
		return super.canPlaceBlockAt(world, pos) && canBlockStay(world, pos, getDefaultState());
	}

	@Override
	protected boolean canSustainBush(IBlockState state)
	{
		return state.isFullBlock();
	}

	@Override
	public boolean canBlockStay(World world, BlockPos pos, IBlockState state)
	{
		if (pos.getY() >= 0 && pos.getY() < 256)
		{
			IBlockState blockstate = world.getBlockState(pos.down());

			if (blockstate.getBlock() == Blocks.MYCELIUM)
			{
				return true;
			}
			else if (blockstate.getBlock() == Blocks.DIRT && blockstate.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.PODZOL)
			{
				return true;
			}
			else if (blockstate.getBlock() == Blocks.STONE)
			{
				return true;
			}

			return world.getLight(pos) < 13 && blockstate.getBlock().canSustainPlant(blockstate, world, pos.down(), EnumFacing.UP, this);
		}

		return false;
	}
}