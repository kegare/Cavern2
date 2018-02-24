package cavern.magic;

import cavern.item.ItemMagicBook;
import cavern.network.CaveNetworkRegistry;
import cavern.network.server.MagicInvisibleMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicInvisible extends Magic
{
	private boolean invisible;

	public MagicInvisible(World world, EntityPlayer player, EnumHand hand)
	{
		super(world, player, hand);
	}

	@Override
	public long getSpellTime()
	{
		return 10000L;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public EnumActionResult onSpelling()
	{
		if (!invisible && getSpellingProgress() >= 0.15D)
		{
			CaveNetworkRegistry.sendToServer(new MagicInvisibleMessage());

			invisible = true;
		}

		return EnumActionResult.PASS;
	}

	@Override
	public ActionResult<ITextComponent> fireMagic()
	{
		return new ActionResult<>(EnumActionResult.FAIL, null);
	}

	@Override
	public void onCloseBook()
	{
		if (!world.isRemote && player.isInvisible())
		{
			player.setInvisible(false);

			ItemStack stack = getHeldItem();

			if (ItemMagicBook.consumeMana(stack, getCost()) > 0)
			{
				ItemMagicBook.setLastUseTime(stack, world.getTotalWorldTime());
			}
			else
			{
				player.setHeldItem(getSpellingHand(), new ItemStack(Items.BOOK));
			}
		}
	}
}