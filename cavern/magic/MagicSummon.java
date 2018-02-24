package cavern.magic;

import javax.annotation.Nullable;

import cavern.entity.EntitySummonCavenicSkeleton;
import cavern.entity.EntitySummonCavenicZombie;
import cavern.entity.EntitySummonSkeleton;
import cavern.entity.EntitySummonZombie;
import cavern.handler.CaveEventHooks;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class MagicSummon extends Magic
{
	private int count;

	public MagicSummon(World world, EntityPlayer player, EnumHand hand)
	{
		super(world, player, hand);
	}

	@Override
	public long getSpellTime()
	{
		return 8000L;
	}

	@Override
	public ActionResult<ITextComponent> fireMagic()
	{
		if (!world.isRemote)
		{
			int chance = MathHelper.clamp(isOverload() ? 3 : CaveEventHooks.RANDOM.nextInt(3) + 1, 1, getMana());

			for (int i = 0; i < chance; ++i)
			{
				if (fireSummonMagic())
				{
					++count;
				}
			}

			return new ActionResult<>(count > 0 ? EnumActionResult.SUCCESS : EnumActionResult.FAIL, null);
		}

		return new ActionResult<>(EnumActionResult.PASS, null);
	}

	@Override
	public int getCost()
	{
		return count;
	}

	@Override
	public boolean canOverload()
	{
		return true;
	}

	public boolean fireSummonMagic()
	{
		BlockPos summonPos;
		Vec3d hitVec = ForgeHooks.rayTraceEyeHitVec(player, 5.0D);

		if (hitVec != null)
		{
			summonPos = getSummonPos(new BlockPos(hitVec.x, hitVec.y + 1, hitVec.z));

			if (summonPos != null)
			{
				summon(summonPos);

				return true;
			}
		}

		BlockPos origin = player.getPosition();
		EnumFacing frontFace = player.getHorizontalFacing();

		for (int i = 0; i < 3; ++i)
		{
			summonPos = getSummonPos(origin.offset(frontFace, i));

			if (summonPos != null)
			{
				summon(summonPos);

				return true;
			}
		}

		for (BlockPos pos : BlockPos.getAllInBoxMutable(origin.add(2, 0, 2), origin.add(-2, 0, -2)))
		{
			summonPos = getSummonPos(pos);

			if (summonPos != null)
			{
				summon(summonPos);

				return true;
			}
		}

		return false;
	}

	@Nullable
	public BlockPos getSummonPos(BlockPos checkPos)
	{
		BlockPos pos = checkPos;
		int diff = 0;

		if (world.isAirBlock(pos))
		{
			while (diff < 5 && world.isAirBlock(pos))
			{
				pos = pos.down();

				++diff;
			}

			pos = pos.up();
		}
		else while (diff < 5 && !world.isAirBlock(pos))
		{
			pos = pos.up();

			++diff;
		}

		if (!world.isAirBlock(pos) || !world.isAirBlock(pos.up()) || world.isAirBlock(pos.down()))
		{
			return null;
		}

		if (!world.checkNoEntityCollision(new AxisAlignedBB(pos)))
		{
			return null;
		}

		if (world.rayTraceBlocks(new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ), new Vec3d(pos), false, true, false) != null)
		{
			return null;
		}

		BlockPos blockpos = player.getPosition();
		double prev = pos.distanceSq(blockpos);

		if (prev - pos.distanceSq(blockpos.offset(player.getHorizontalFacing())) < 0.0D)
		{
			return null;
		}

		return pos;
	}

	public EntityLivingBase createSummonMob()
	{
		if (isOverload())
		{
			return CaveEventHooks.RANDOM.nextInt(2) == 0 ? new EntitySummonCavenicSkeleton(world, player) : new EntitySummonCavenicZombie(world, player);
		}

		if (CaveEventHooks.RANDOM.nextDouble() < 0.3D)
		{
			return new EntitySummonCavenicSkeleton(world, player);
		}

		if (CaveEventHooks.RANDOM.nextDouble() < 0.3D)
		{
			return new EntitySummonCavenicZombie(world, player);
		}

		if (CaveEventHooks.RANDOM.nextDouble() < 0.5D)
		{
			return new EntitySummonSkeleton(world, player);
		}

		return new EntitySummonZombie(world, player);
	}

	public void summon(BlockPos pos)
	{
		EntityLivingBase entity = createSummonMob();

		entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D, world.rand.nextFloat() * 360.0F, 0.0F);

		if (entity instanceof EntityLiving)
		{
			((EntityLiving)entity).onInitialSpawn(world.getDifficultyForLocation(pos), null);
		}

		world.spawnEntity(entity);
	}
}