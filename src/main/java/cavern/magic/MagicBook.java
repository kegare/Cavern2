package cavern.magic;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ObjectUtils;

import cavern.capability.CaveCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicBook
{
	private Magic spellingMagic;
	private boolean spellingCanceled;

	private SpecialMagic specialMagic;

	public ItemStack getSpellingMagicBook()
	{
		if (spellingMagic == null)
		{
			return ItemStack.EMPTY;
		}

		return spellingMagic.getHeldItem();
	}

	@Nullable
	public Magic getSpellingMagic()
	{
		return spellingMagic;
	}

	public boolean setSpellingMagic(@Nullable Magic magic)
	{
		Magic prev = spellingMagic;

		spellingMagic = magic;
		spellingCanceled = false;

		return prev != spellingMagic;
	}

	@SideOnly(Side.CLIENT)
	public void setSpellingCanceled()
	{
		spellingMagic = null;
		spellingCanceled = true;
	}

	@SideOnly(Side.CLIENT)
	public boolean isSpellingCanceled()
	{
		return spellingCanceled;
	}

	@Nullable
	public SpecialMagic getSpecialMagic()
	{
		return specialMagic;
	}

	public boolean setSpecialMagic(@Nullable SpecialMagic magic)
	{
		SpecialMagic prev = specialMagic;

		specialMagic = magic;

		return prev != specialMagic;
	}

	public static MagicBook get(EntityPlayer player)
	{
		return ObjectUtils.defaultIfNull(CaveCapabilities.getCapability(player, CaveCapabilities.MAGIC_BOOK), new MagicBook());
	}
}