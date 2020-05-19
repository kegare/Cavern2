package cavern.magic;

import cavern.core.CaveSounds;
import cavern.util.PlayerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class MagicOverload extends SpecialMagic
{
	public MagicOverload(World world, EntityPlayer player, EnumHand hand)
	{
		super(world, player, hand);
	}

	@Override
	public SoundEvent getSuccessSound()
	{
		return CaveSounds.MAGIC_BLAZE;
	}

	@Override
	public SoundEvent getFinishSound()
	{
		return SoundEvents.BLOCK_FIRE_EXTINGUISH;
	}

	@Override
	public long getSpellTime()
	{
		return 5000L;
	}

	@Override
	public long getEffectTime()
	{
		return 60000L;
	}

	@Override
	public ActionResult<ITextComponent> fireMagic()
	{
		ActionResult<ITextComponent> result = super.fireMagic();

		if (result.getType() == EnumActionResult.SUCCESS)
		{
			PlayerHelper.grantAdvancement(player, "magic_overload");
		}

		return result;
	}

	@Override
	public boolean hasSpecialCost(Magic magic)
	{
		return true;
	}

	@Override
	public int getSpecialCost(Magic magic)
	{
		return magic.getMagicType().getMana();
	}
}