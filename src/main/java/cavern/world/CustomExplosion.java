package cavern.world;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class CustomExplosion extends Explosion
{
	private final boolean causesFire;
	private final boolean damagesTerrain;

	private final World world;
	private final Random random;
	private final double x;
	private final double y;
	private final double z;
	private final Entity exploder;
	private final float size;

	private final List<BlockPos> affectedBlockPositions = Lists.newArrayList();
	private final Map<EntityPlayer, Vec3d> playerKnockbackMap = Maps.newHashMap();

	private final Vec3d position;

	public CustomExplosion(World world, Entity entity, double x, double y, double z, float size, boolean flaming, boolean damagesTerrain)
	{
		super(world, entity, x, y, z, size, flaming, damagesTerrain);
		this.world = world;
		this.random = new Random();
		this.x = x;
		this.y = y;
		this.z = z;
		this.exploder = entity;
		this.size = size;
		this.causesFire = flaming;
		this.damagesTerrain = damagesTerrain;
		this.position = new Vec3d(x, y, z);
	}

	public boolean canExplodeEntity(Entity entity)
	{
		return !entity.isImmuneToExplosions();
	}

	@Override
	public void doExplosionA()
	{
		Set<BlockPos> set = Sets.newHashSet();

		for (int i = 0; i < 16; ++i)
		{
			for (int j = 0; j < 16; ++j)
			{
				for (int k = 0; k < 16; ++k)
				{
					if (i == 0 || i == 15 || j == 0 || j == 15 || k == 0 || k == 15)
					{
						double d0 = i / 15.0F * 2.0F - 1.0F;
						double d1 = j / 15.0F * 2.0F - 1.0F;
						double d2 = k / 15.0F * 2.0F - 1.0F;
						double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
						d0 = d0 / d3;
						d1 = d1 / d3;
						d2 = d2 / d3;
						float f = size * (0.7F + random.nextFloat() * 0.6F);
						double posX = x;
						double posY = y;
						double posZ = z;

						for (; f > 0.0F; f -= 0.22500001F)
						{
							BlockPos pos = new BlockPos(posX, posY, posZ);
							IBlockState state = world.getBlockState(pos);

							if (state.getMaterial() != Material.AIR)
							{
								float resistance = exploder != null ? exploder.getExplosionResistance(this, world, pos, state) : state.getBlock().getExplosionResistance(world, pos, null, this);

								f -= (resistance + 0.3F) * 0.3F;
							}

							if (f > 0.0F && (exploder == null || exploder.canExplosionDestroyBlock(this, world, pos, state, f)))
							{
								set.add(pos);
							}

							posX += d0 * 0.30000001192092896D;
							posY += d1 * 0.30000001192092896D;
							posZ += d2 * 0.30000001192092896D;
						}
					}
				}
			}
		}

		affectedBlockPositions.addAll(set);
	}

	@Override
	public void doExplosionB(boolean spawnParticles)
	{
		world.playSound(null, x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F) * 0.7F);

		if (spawnParticles)
		{
			if (size >= 2.0F && damagesTerrain)
			{
				world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, x, y, z, 1.0D, 0.0D, 0.0D);
			}
			else
			{
				world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, x, y, z, 1.0D, 0.0D, 0.0D);
			}
		}

		if (damagesTerrain)
		{
			for (BlockPos pos : affectedBlockPositions)
			{
				IBlockState state = world.getBlockState(pos);
				Block block = state.getBlock();

				if (spawnParticles)
				{
					double ex = pos.getX() + random.nextFloat();
					double ey = pos.getY() + random.nextFloat();
					double ez = pos.getZ() + random.nextFloat();
					double rx = ex - x;
					double ry = ey - y;
					double rz = ez - z;
					double rd = MathHelper.sqrt(rx * rx + ry * ry + rz * rz);
					rx = rx / rd;
					ry = ry / rd;
					rz = rz / rd;
					double d7 = 0.5D / (rd / size + 0.1D);
					d7 = d7 * (random.nextFloat() * random.nextFloat() + 0.3F);
					rx = rx * d7;
					ry = ry * d7;
					rz = rz * d7;

					world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (ex + x) / 2.0D, (ey + y) / 2.0D, (ez + z) / 2.0D, rx, ry, rz);
					world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, ex, ey, ez, rx, ry, rz);
				}

				if (state.getMaterial() != Material.AIR)
				{
					if (block.canDropFromExplosion(this))
					{
						block.dropBlockAsItemWithChance(world, pos, world.getBlockState(pos), 1.0F / size, 0);
					}

					block.onBlockExploded(world, pos, this);
				}
			}
		}

		if (causesFire)
		{
			for (BlockPos pos : affectedBlockPositions)
			{
				if (world.getBlockState(pos).getMaterial() == Material.AIR && world.getBlockState(pos.down()).isFullBlock() && random.nextInt(3) == 0)
				{
					world.setBlockState(pos, Blocks.FIRE.getDefaultState());
				}
			}
		}
	}

	public void doExplosionEntities()
	{
		float f = size * 2.0F;
		int minX = MathHelper.floor(x - f - 1.0D);
		int maxX = MathHelper.floor(x + f + 1.0D);
		int minY = MathHelper.floor(y - f - 1.0D);
		int maxY = MathHelper.floor(y + f + 1.0D);
		int minZ = MathHelper.floor(z - f - 1.0D);
		int maxZ = MathHelper.floor(z + f + 1.0D);

		List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(exploder, new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));

		ForgeEventFactory.onExplosionDetonate(world, this, entities, f);

		if (entities.isEmpty())
		{
			return;
		}

		entities.stream().filter(this::canExplodeEntity).forEach(entity ->
		{
			double dist = entity.getDistance(x, y, z) / f;

			if (dist <= 1.0D)
			{
				double ex = entity.posX - x;
				double ey = entity.posY + entity.getEyeHeight() - y;
				double ez = entity.posZ - z;
				double ed = MathHelper.sqrt(ex * ex + ey * ey + ez * ez);

				if (ed != 0.0D)
				{
					ex = ex / ed;
					ey = ey / ed;
					ez = ez / ed;
					double density = world.getBlockDensity(position, entity.getEntityBoundingBox());
					double damage = (1.0D - dist) * density;
					entity.attackEntityFrom(DamageSource.causeExplosionDamage(this), getExplosionAttackDamage(entity, (int)((damage * damage + damage) / 2.0D * 7.0D * f + 1.0D)));
					double blast = damage;

					if (entity instanceof EntityLivingBase)
					{
						blast = EnchantmentProtection.getBlastDamageReduction((EntityLivingBase)entity, damage);
					}

					entity.motionX += ex * blast;
					entity.motionY += ey * blast;
					entity.motionZ += ez * blast;
				}
			}
		});
	}

	protected int getExplosionAttackDamage(Entity entity, int damage)
	{
		return damage;
	}

	@Override
	public Map<EntityPlayer, Vec3d> getPlayerKnockbackMap()
	{
		return playerKnockbackMap;
	}

	@Override
	public EntityLivingBase getExplosivePlacedBy()
	{
		return exploder == null ? null : exploder instanceof EntityTNTPrimed ? ((EntityTNTPrimed)exploder).getTntPlacedBy() : exploder instanceof EntityLivingBase ? (EntityLivingBase)exploder : null;
	}

	@Override
	public void clearAffectedBlockPositions()
	{
		affectedBlockPositions.clear();
	}

	@Override
	public List<BlockPos> getAffectedBlockPositions()
	{
		return affectedBlockPositions;
	}

	@Override
	public Vec3d getPosition()
	{
		return position;
	}
}