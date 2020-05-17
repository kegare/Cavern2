package cavern.handler;

import cavern.api.CavernAPI;
import cavern.api.item.IAquaTool;
import cavern.data.Miner;
import cavern.data.MinerRank;
import cavern.data.MiningData;
import cavern.util.CaveUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AquaEventHooks
{
	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event)
	{
		EntityPlayer player = event.getEntityPlayer();
		ItemStack stack = player.getHeldItemMainhand();

		if (stack.isEmpty())
		{
			return;
		}

		float original = event.getOriginalSpeed();
		boolean flag = EnchantmentHelper.getAquaAffinityModifier(player);

		if (player.isInWater() && stack.getItem() instanceof IAquaTool)
		{
			IAquaTool tool = (IAquaTool)stack.getItem();
			float speed = tool.getAquaBreakSpeed(stack, player, event.getPos(), event.getState(), original);

			if (flag)
			{
				speed *= 0.5F;
			}

			event.setNewSpeed(speed);

			flag = true;
		}

		if (CavernAPI.dimension.isInCaveDimensions(player) && CaveUtils.isPickaxe(stack))
		{
			int rank = Miner.get(player).getRank();

			if (!flag && player.isInWater() && rank >= MinerRank.AQUA_MINER.getRank())
			{
				event.setNewSpeed(original * 5.0F);
			}

			event.setNewSpeed(event.getNewSpeed() * MinerRank.get(rank).getBoost());
		}
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();

		if (!(entity instanceof EntityPlayer) || entity instanceof FakePlayer)
		{
			return;
		}

		EntityPlayer player = (EntityPlayer)entity;

		MiningData.get(player).onUpdate();

		if (!player.isInWater() || !CavernAPI.dimension.isInCaveDimensions(player))
		{
			return;
		}

		if (Miner.get(player).getRank() < MinerRank.AQUA_MINER.getRank())
		{
			return;
		}

		if (!player.canBreatheUnderwater() && !player.isPotionActive(MobEffects.WATER_BREATHING) && player.ticksExisted % 20 == 0)
		{
			player.setAir(300);
		}

		if (player.capabilities.isFlying || EnchantmentHelper.getDepthStriderModifier(player) > 0)
		{
			return;
		}

		double prevY = player.posY;
		float vec1 = 0.6F;
		float vec2 = 0.01F;
		float vec3 = 0.4F;

		if (!player.onGround)
		{
			vec3 *= 0.5F;
		}

		if (player.getArmorVisibility() >= 0.75F)
		{
			vec3 *= 0.5F;
		}

		if (vec3 > 0.0F)
		{
			vec1 += (0.54600006F - vec1) * vec3 / 3.0F;
			vec2 += (player.getAIMoveSpeed() - vec2) * vec3 / 3.0F;
		}

		player.moveRelative(player.moveStrafing, player.moveVertical, player.moveForward, vec2);
		player.move(MoverType.SELF, player.motionX, player.motionY, player.motionZ);
		player.motionX *= vec1;
		player.motionY *= 0.800000011920929D;
		player.motionZ *= vec1;

		if (player.collidedHorizontally && player.isOffsetPositionInLiquid(player.motionX, player.motionY + 0.6000000238418579D - player.posY + prevY, player.motionZ))
		{
			player.motionY = 0.30000001192092896D;
		}

		if (player.isSwingInProgress || player.isSneaking())
		{
			player.motionY *= 0.3D;
		}
	}
}