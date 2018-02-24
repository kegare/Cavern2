package cavern.magic;

import cavern.core.CaveSounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class MagicHeal extends Magic
{
	public MagicHeal(World world, EntityPlayer player, EnumHand hand)
	{
		super(world, player, hand);
	}

	@Override
	public SoundEvent getSuccessSound()
	{
		return CaveSounds.MAGIC_SUCCESS_BUFF;
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
			float overload = 0.0F;

			if (isOverload())
			{
				overload = getMana() * 1.0F;
			}

			player.heal(10.0F + overload);

			return new ActionResult<>(EnumActionResult.SUCCESS, null);
		}

		return new ActionResult<>(player.shouldHeal() ? EnumActionResult.PASS : EnumActionResult.FAIL, null);
	}

	@Override
	public boolean canOverload()
	{
		return true;
	}
}