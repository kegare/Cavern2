package cavern.magic;

import java.util.Random;

import javax.annotation.Nullable;

import cavern.core.CaveSounds;
import cavern.handler.CaveEventHooks;
import cavern.item.ItemMagicBook;
import cavern.item.ItemMagicBook.EnumType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class Magic
{
	protected static final Random RANDOM = CaveEventHooks.RANDOM;

	protected final World world;
	protected final EntityPlayer player;
	protected final EnumHand hand;
	protected final EnumType magicType;
	protected final long createdTime;

	public Magic(World world, EntityPlayer player, EnumHand hand)
	{
		this.world = world;
		this.player = player;
		this.hand = hand;
		this.magicType = EnumType.byItemStack(player.getHeldItem(hand));
		this.createdTime = System.currentTimeMillis();
	}

	public World getWorld()
	{
		return world;
	}

	public EntityPlayer getEntityPlayer()
	{
		return player;
	}

	public EnumHand getSpellingHand()
	{
		return hand;
	}

	public ItemStack getHeldItem()
	{
		return player.getHeldItem(hand);
	}

	public EnumType getMagicType()
	{
		return magicType;
	}

	public int getMana()
	{
		return ItemMagicBook.getMana(getHeldItem());
	}

	public long getCreatedTime()
	{
		return createdTime;
	}

	@Nullable
	public SoundEvent getSpellingSound()
	{
		return CaveSounds.MAGIC_SPELLING;
	}

	@Nullable
	public SoundEvent getSuccessSound()
	{
		return CaveSounds.MAGIC_SUCCESS_MISC;
	}

	@Nullable
	public SoundEvent getCloseSound()
	{
		return CaveSounds.MAGIC_CLOSE_BOOK;
	}

	public abstract long getSpellTime();

	public double getSpellingProgress()
	{
		return MathHelper.clamp((double)(System.currentTimeMillis() - getCreatedTime()) / (double)getSpellTime(), 0.0D, 1.0D);
	}

	@SideOnly(Side.CLIENT)
	public EnumActionResult onSpelling()
	{
		return EnumActionResult.PASS;
	}

	public abstract ActionResult<ITextComponent> fireMagic();

	public void onCloseBook() {}

	public int getCost()
	{
		return 1;
	}

	public boolean canOverload()
	{
		return false;
	}

	public boolean isOverload()
	{
		if (!canOverload())
		{
			return false;
		}

		SpecialMagic magic = MagicBook.get(player).getSpecialMagic();

		return magic != null && magic instanceof MagicOverload;
	}
}