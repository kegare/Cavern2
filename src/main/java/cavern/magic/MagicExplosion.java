package cavern.magic;

import cavern.api.entity.IEntitySummonable;
import cavern.core.CaveSounds;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.ExplosionMessage;
import cavern.util.PlayerHelper;
import cavern.world.CustomExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class MagicExplosion extends Magic
{
	public MagicExplosion(World world, EntityPlayer player, EnumHand hand)
	{
		super(world, player, hand);
	}

	@Override
	public SoundEvent getSuccessSound()
	{
		return CaveSounds.MAGIC_SUCCESS_STRONG;
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
			Vec3d hitVec = ForgeHooks.rayTraceEyeHitVec(player, 6.0D);

			if (hitVec != null)
			{
				doExplosion(new BlockPos(hitVec));
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
				while (i < 6 && world.isAirBlock(pos));

				doExplosion(pos);
			}

			PlayerHelper.grantAdvancement(player, "magic_explosion");

			return new ActionResult<>(EnumActionResult.SUCCESS, null);
		}

		return new ActionResult<>(EnumActionResult.PASS, null);
	}

	@Override
	public boolean canOverload()
	{
		return true;
	}

	private void doExplosion(BlockPos pos)
	{
		boolean grief = world.getGameRules().getBoolean("mobGriefing");
		float overload = 0.0F;

		if (isOverload())
		{
			overload = getMana() * 0.5F;
		}

		createExplosion(pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D, 8.0F + overload, grief);
	}

	private Explosion createExplosion(double x, double y, double z, float strength, boolean damagesTerrain)
	{
		return newExplosion(x, y, z, strength, false, damagesTerrain, true);
	}

	private Explosion newExplosion(double x, double y, double z, float strength, boolean flaming, boolean damagesTerrain, boolean attackEntities)
	{
		if (FMLCommonHandler.instance().getSide().isServer())
		{
			damagesTerrain = false;
		}

		Explosion explosion = new Explosion(world, player, x, y, z, strength, flaming, damagesTerrain);

		if (ForgeEventFactory.onExplosionStart(world, explosion))
		{
			return explosion;
		}

		explosion.doExplosionA();
		explosion.doExplosionB(false);

		if (!damagesTerrain)
		{
			explosion.clearAffectedBlockPositions();
		}

		if (attackEntities)
		{
			explosion.doExplosionEntities();
		}

		if (!world.isRemote)
		{
			for (EntityPlayer player : world.playerEntities)
			{
				if (player.getDistanceSq(x, y, z) < 4096.0D)
				{
					CaveNetworkRegistry.sendTo(() -> new ExplosionMessage(x, y, z, strength, explosion.getAffectedBlockPositions()), player);
				}
			}
		}

		return explosion;
	}

	private class Explosion extends CustomExplosion
	{
		private Explosion(World world, Entity entity, double x, double y, double z, float size, boolean flaming, boolean damagesTerrain)
		{
			super(world, entity, x, y, z, size, flaming, damagesTerrain);
		}

		@Override
		public boolean canExplodeEntity(Entity entity)
		{
			if (!super.canExplodeEntity(entity))
			{
				return false;
			}

			if (entity instanceof EntityItem)
			{
				return false;
			}

			if (entity instanceof EntityPlayer)
			{
				return false;
			}

			if (entity instanceof IEntitySummonable)
			{
				return false;
			}

			if (entity instanceof IEntityOwnable && ((IEntityOwnable)entity).getOwner() != null)
			{
				return false;
			}

			return true;
		}

		@Override
		protected int getExplosionAttackDamage(Entity entity, int damage)
		{
			entity.hurtResistantTime = 0;

			if (entity.isBurning())
			{
				return MathHelper.ceil(damage * 2.0F);
			}

			if (!entity.onGround || entity.isAirBorne)
			{
				return MathHelper.ceil(damage * 1.75F);
			}

			if (entity instanceof IMob)
			{
				return MathHelper.ceil(damage * 1.45F);
			}

			return damage;
		}
	}
}