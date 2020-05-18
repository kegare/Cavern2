package cavern.magic;

import cavern.core.Cavern;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class MagicStorage extends Magic
{
	public MagicStorage(World world, EntityPlayer player, EnumHand hand)
	{
		super(world, player, hand);
	}

	@Override
	public SoundEvent getSuccessSound()
	{
		return SoundEvents.UI_BUTTON_CLICK;
	}

	@Override
	public long getSpellTime()
	{
		return 2000L;
	}

	@Override
	public ActionResult<ITextComponent> fireMagic()
	{
		if (!world.isRemote)
		{
			player.openGui(Cavern.instance, 0, world, hand.ordinal(), 0, 0);

			return new ActionResult<>(EnumActionResult.SUCCESS, null);
		}

		return new ActionResult<>(EnumActionResult.PASS, null);
	}
}