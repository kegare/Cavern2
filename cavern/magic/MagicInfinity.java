package cavern.magic;

import cavern.util.PlayerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class MagicInfinity extends SpecialMagic
{
	public MagicInfinity(World world, EntityPlayer player, EnumHand hand)
	{
		super(world, player, hand);
	}

	@Override
	public long getSpellTime()
	{
		return 15000L;
	}

	@Override
	public long getEffectTime()
	{
		return 30000L;
	}

	@Override
	public ActionResult<ITextComponent> fireMagic()
	{
		ActionResult<ITextComponent> result = super.fireMagic();

		if (result.getType() == EnumActionResult.SUCCESS)
		{
			PlayerHelper.grantAdvancement(player, "magic_infinity");
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
		return 0;
	}
}