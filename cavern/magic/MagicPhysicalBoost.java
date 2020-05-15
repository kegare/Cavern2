package cavern.magic;

import cavern.core.CaveSounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class MagicPhysicalBoost extends Magic
{
	public MagicPhysicalBoost(World world, EntityPlayer player, EnumHand hand)
	{
		super(world, player, hand);
	}

	@Override
	public SoundEvent getSuccessSound()
	{
		return CaveSounds.MAGIC_BLAZE;
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
			int duration = 15 * 20;

			if (isOverload())
			{
				duration += 15 * 20;
			}

			player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, duration, 2));
			player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, duration, 2));
			player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, duration, 2));
			player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, duration, 2));
			player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, duration, 2));
			player.addPotionEffect(new PotionEffect(MobEffects.HASTE, duration, 2));
			player.addPotionEffect(new PotionEffect(MobEffects.SPEED, duration, 2));
			player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, duration, 1));
			player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, duration, 0));
			player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, duration, 0));

			return new ActionResult<>(EnumActionResult.SUCCESS, null);
		}

		return new ActionResult<>(EnumActionResult.PASS, null);
	}

	@Override
	public boolean canOverload()
	{
		return true;
	}
}