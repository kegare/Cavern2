package cavern.network.server;

import cavern.item.ItemMagicBook;
import cavern.magic.Magic;
import cavern.magic.MagicBook;
import cavern.magic.SpecialMagic;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MagicResultMessage implements IPlayerMessage<MagicResultMessage, IMessage>
{
	private EnumActionResult clientResult;

	public MagicResultMessage() {}

	public MagicResultMessage(EnumActionResult clientResult)
	{
		this.clientResult = clientResult;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		clientResult = EnumActionResult.values()[buf.readInt()];
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(clientResult.ordinal());
	}

	@Override
	public IMessage process(EntityPlayerMP player)
	{
		MagicBook book = MagicBook.get(player);
		Magic magic = book.getSpellingMagic();

		if (magic != null)
		{
			EnumActionResult type;
			ITextComponent message;

			if (clientResult == EnumActionResult.PASS)
			{
				ActionResult<ITextComponent> result = magic.fireMagic();

				type = result.getType();
				message = result.getResult();
			}
			else
			{
				type = clientResult;
				message = null;
			}

			if (type == EnumActionResult.SUCCESS)
			{
				ItemStack stack = book.getSpellingMagicBook();
				World world = player.getServerWorld();
				int cost = 0;
				SpecialMagic specialMagic = book.getSpecialMagic();

				if (specialMagic != null && specialMagic != magic && specialMagic.hasSpecialCost(magic))
				{
					cost = specialMagic.getSpecialCost(magic);
				}
				else
				{
					cost = magic.getCost();
				}

				if (cost == 0)
				{
					ItemMagicBook.setLastUseTime(stack, world.getTotalWorldTime());
				}
				else
				{
					if (ItemMagicBook.consumeMana(stack, cost) > 0)
					{
						ItemMagicBook.setLastUseTime(stack, world.getTotalWorldTime());
					}
					else
					{
						player.setHeldItem(magic.getSpellingHand(), new ItemStack(Items.BOOK));
					}
				}

				SoundEvent sound = magic.getSuccessSound();

				if (sound != null)
				{
					world.playSound(null, player.posX, player.posY + 0.25D, player.posZ, sound, SoundCategory.PLAYERS, 1.0F, 1.0F);
				}

				if (magic.isOverload())
				{
					book.setSpecialMagic(null);
				}
			}

			if (message != null)
			{
				player.sendStatusMessage(message, true);
			}

			magic.onCloseBook();
		}

		book.setSpellingMagic(null);

		return null;
	}
}