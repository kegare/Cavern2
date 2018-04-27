package cavern.magic;

import com.google.common.base.Predicate;

import cavern.api.ISummonMob;
import cavern.util.PlayerHelper;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class MagicThunderbolt extends Magic implements Predicate<Entity>
{
	private int targetCount;

	public MagicThunderbolt(World world, EntityPlayer player, EnumHand hand)
	{
		super(world, player, hand);
	}

	@Override
	public long getSpellTime()
	{
		return 5000L;
	}

	@Override
	public ActionResult<ITextComponent> fireMagic()
	{
		if (!world.isRemote)
		{
			if (isOverload())
			{
				int mana = getMana();
				BlockPos pos = player.getPosition().up();

				for (int count = 0; count < 5;)
				{
					BlockPos blockpos = pos.add(RANDOM.nextInt(12) - 6, 0, RANDOM.nextInt(12) - 6);

					if (!world.isAirBlock(blockpos))
					{
						continue;
					}

					while (world.isAirBlock(blockpos) && blockpos.getY() > 0)
					{
						blockpos = blockpos.down();
					}

					spawnThunderbolt(blockpos.getX() + 0.5D, blockpos.getY(), blockpos.getZ() + 0.5D, false);

					++count;
				}

				if (mana >= 10)
				{
					for (Entity entity : world.getEntitiesInAABBexcluding(player, player.getEntityBoundingBox().grow(10.0D), this))
					{
						if (++targetCount > mana / 3)
						{
							break;
						}

						spawnThunderbolt(entity.posX, entity.posY, entity.posZ, true);
					}
				}
			}
			else
			{
				EnumFacing front = player.getHorizontalFacing();
				BlockPos pos = player.getPosition().up();
				int i = 0;

				do
				{
					pos = pos.offset(front);

					++i;
				}
				while (i < 8 && world.isAirBlock(pos));

				pos = pos.offset(front.getOpposite());

				while (world.isAirBlock(pos) && pos.getY() > 0)
				{
					pos = pos.down();
				}

				int range = Math.max(5, i);

				for (Entity entity : world.getEntitiesInAABBexcluding(player, new AxisAlignedBB(pos.add(-range, -range, -range), pos.add(range, range, range)), this))
				{
					if (++targetCount > 3)
					{
						break;
					}

					spawnThunderbolt(entity.posX, entity.posY, entity.posZ, true);
				}

				if (targetCount <= 0)
				{
					spawnThunderbolt(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, false);
				}
			}

			PlayerHelper.grantAdvancement(player, "magic_thunderbolt");

			return new ActionResult<>(EnumActionResult.SUCCESS, null);
		}

		return new ActionResult<>(EnumActionResult.PASS, null);
	}

	@Override
	public boolean canOverload()
	{
		return true;
	}

	@Override
	public boolean apply(Entity entity)
	{
		if (player == entity || player.isEntityEqual(entity))
		{
			return false;
		}

		if (!(entity instanceof IMob) || entity instanceof ISummonMob)
		{
			return false;
		}

		if (entity instanceof IEntityOwnable && ((IEntityOwnable)entity).getOwner() != null)
		{
			return false;
		}

		return player.canEntityBeSeen(entity);
	}

	public boolean spawnThunderbolt(double x, double y, double z, boolean targetStriker)
	{
		EntityThunderbolt thunderbolt = new EntityThunderbolt(world, x, y, z, targetStriker);

		return world.addWeatherEffect(thunderbolt);
	}

	public class EntityThunderbolt extends EntityLightningBolt
	{
		protected int lightningState;
		protected long boltVertex;
		protected int boltLivingTime;

		public final boolean targetStriker;

		public EntityThunderbolt(World world, double x, double y, double z, boolean targetStriker)
		{
			super(world, x, y, z, targetStriker);
			this.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
			this.lightningState = 2;
			this.boltVertex = rand.nextLong();
			this.boltLivingTime = rand.nextInt(3) + 1;
			this.targetStriker = targetStriker;

			BlockPos pos = new BlockPos(this);

			if (!targetStriker && !world.isRemote && world.getGameRules().getBoolean("doFireTick") && (world.getDifficulty() == EnumDifficulty.NORMAL || world.getDifficulty() == EnumDifficulty.HARD) && world.isAreaLoaded(pos, 10))
			{
				if (world.getBlockState(pos).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(world, pos))
				{
					world.setBlockState(pos, Blocks.FIRE.getDefaultState());
				}

				for (int i = 0; i < 4; ++i)
				{
					BlockPos blockpos = pos.add(rand.nextInt(3) - 1, rand.nextInt(3) - 1, rand.nextInt(3) - 1);

					if (world.getBlockState(blockpos).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(world, blockpos))
					{
						world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
					}
				}
			}
		}

		@Override
		public SoundCategory getSoundCategory()
		{
			return SoundCategory.WEATHER;
		}

		@Override
		public void onUpdate()
		{
			super.onUpdate();

			if (lightningState == 2)
			{
				world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + rand.nextFloat() * 0.2F);
				world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + rand.nextFloat() * 0.2F);
			}

			--lightningState;

			if (lightningState < 0)
			{
				if (boltLivingTime == 0)
				{
					setDead();
				}
				else if (lightningState < -rand.nextInt(10))
				{
					--boltLivingTime;

					lightningState = 1;

					if (!targetStriker && !world.isRemote)
					{
						boltVertex = rand.nextLong();

						BlockPos pos = new BlockPos(this);

						if (world.getGameRules().getBoolean("doFireTick") && world.isAreaLoaded(pos, 10) && world.getBlockState(pos).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(world, pos))
						{
							world.setBlockState(pos, Blocks.FIRE.getDefaultState());
						}
					}
				}
			}

			if (lightningState >= 0)
			{
				if (world.isRemote)
				{
					world.setLastLightningBolt(2);
				}
				else if (targetStriker)
				{
					for (Entity entity : world.getEntitiesInAABBexcluding(this, new AxisAlignedBB(posX - 3.0D, posY - 3.0D, posZ - 3.0D, posX + 3.0D, posY + 6.0D + 3.0D, posZ + 3.0D), MagicThunderbolt.this))
					{
						if (!ForgeEventFactory.onEntityStruckByLightning(entity, this))
						{
							entity.attackEntityFrom(DamageSource.LIGHTNING_BOLT, 6.0F);
							entity.setFire(15);

							if (entity instanceof EntityLivingBase)
							{
								((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20 * 20, rand.nextInt(2) + 1, false, false));
							}
						}
					}
				}
			}
		}
	}
}