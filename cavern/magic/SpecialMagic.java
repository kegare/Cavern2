package cavern.magic;

import javax.annotation.Nullable;

import cavern.core.CaveSounds;
import cavern.core.Cavern;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public abstract class SpecialMagic extends Magic
{
	private long spelledTime;

	public SpecialMagic(World world, EntityPlayer player, EnumHand hand)
	{
		super(world, player, hand);
	}

	public long getSpelledTime()
	{
		return spelledTime;
	}

	@Override
	public SoundEvent getSuccessSound()
	{
		return CaveSounds.MAGIC_BREAK;
	}

	@Nullable
	public SoundEvent getFinishSound()
	{
		return CaveSounds.MAGIC_REVERSE;
	}

	@Override
	public abstract long getSpellTime();

	public abstract long getEffectTime();

	public double getEffectProgress()
	{
		return MathHelper.clamp((double)(System.currentTimeMillis() - getSpelledTime()) / (double)getEffectTime(), 0.0D, 1.0D);
	}

	@Override
	public ActionResult<ITextComponent> fireMagic()
	{
		MagicBook.get(player).setSpecialMagic(this);

		spelledTime = System.currentTimeMillis();

		return new ActionResult<>(world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS, null);
	}

	@Nullable
	public ITextComponent finishMagic()
	{
		if (world.isRemote)
		{
			return new TextComponentTranslation("item.magicBook.finish", Cavern.proxy.translate("item.magicBook." + getMagicType().getUnlocalizedName() + ".name"));
		}

		return null;
	}

	public boolean hasSpecialCost(Magic magic)
	{
		return false;
	}

	public int getSpecialCost(Magic magic)
	{
		return magic.getCost();
	}
}