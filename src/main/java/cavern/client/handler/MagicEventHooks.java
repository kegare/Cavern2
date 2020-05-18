package cavern.client.handler;

import java.util.Random;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import cavern.client.CaveKeyBindings;
import cavern.client.particle.ParticleMagicSpell;
import cavern.handler.CaveEventHooks;
import cavern.item.ItemMagicBook;
import cavern.item.ItemMagicBook.EnumType;
import cavern.magic.Magic;
import cavern.magic.MagicBook;
import cavern.magic.MagicInfinity;
import cavern.magic.SpecialMagic;
import cavern.network.CaveNetworkRegistry;
import cavern.network.server.MagicBookMessage;
import cavern.network.server.MagicResultMessage;
import cavern.network.server.SpecialMagicMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class MagicEventHooks
{
	private final Random rand = CaveEventHooks.RANDOM;

	private int spellingSlot;
	private int spellingSoundTicks;

	private EnumActionResult spellingResult;

	private boolean sendResult;

	@SubscribeEvent
	public void onTick(ClientTickEvent event)
	{
		if (event.phase != Phase.END)
		{
			return;
		}

		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc.world == null || mc.player == null || mc.currentScreen != null)
		{
			return;
		}

		MagicBook book = MagicBook.get(mc.player);
		boolean isMagicKeyDown = CaveKeyBindings.KEY_MAGIC_BOOK.isKeyDown();

		if (isMagicKeyDown || mc.gameSettings.keyBindUseItem.isKeyDown())
		{
			ItemStack stack = book.getSpellingMagicBook();
			Magic magic = book.getSpellingMagic();

			if (spellingResult != null && spellingResult != EnumActionResult.PASS)
			{
				if (!sendResult)
				{
					if (magic != null)
					{
						if (spellingResult == EnumActionResult.SUCCESS)
						{
							ActionResult<ITextComponent> result = magic.fireMagic();

							CaveNetworkRegistry.sendToServer(new MagicResultMessage(result.getType()));

							ITextComponent message = result.getResult();

							if (message != null)
							{
								mc.ingameGUI.setOverlayMessage(message, true);
							}

							if (magic.isOverload())
							{
								book.setSpecialMagic(null);
							}
						}
						else
						{
							CaveNetworkRegistry.sendToServer(new MagicResultMessage(EnumActionResult.FAIL));
						}

						magic.onCloseBook();

						SoundEvent sound = magic.getCloseSound();

						if (sound != null)
						{
							mc.getSoundHandler().playDelayedSound(PositionedSoundRecord.getMasterRecord(sound, 1.0F), 5);
						}
					}

					book.setSpellingMagic(null);

					sendResult = true;
				}

				return;
			}

			if (book.isSpellingCanceled())
			{
				spellingResult = EnumActionResult.FAIL;

				return;
			}

			if (magic == null)
			{
				Pair<EnumHand, ItemStack> magicBook = getMagicBook(mc.player);

				if (magicBook == null)
				{
					spellingResult = EnumActionResult.FAIL;

					return;
				}

				stack = magicBook.getRight();
				magic = EnumType.byItemStack(stack).createMagic(mc.world, mc.player, magicBook.getLeft());

				if (magic == null)
				{
					spellingResult = EnumActionResult.FAIL;

					return;
				}

				book.setSpellingMagic(magic);

				if (magic.getSpellingHand() == EnumHand.MAIN_HAND)
				{
					spellingSlot = mc.player.inventory.currentItem;
				}

				CaveNetworkRegistry.sendToServer(new MagicBookMessage(magic.getSpellingHand()));
			}

			boolean hasSpecialMagic = book.getSpecialMagic() != null;
			boolean infinity = hasSpecialMagic && book.getSpecialMagic() instanceof MagicInfinity;

			if (hasSpecialMagic && magic instanceof SpecialMagic)
			{
				spellingResult = EnumActionResult.FAIL;

				return;
			}

			if (!mc.player.capabilities.isCreativeMode && ItemMagicBook.isInCoolTime(mc.player, stack))
			{
				mc.ingameGUI.setOverlayMessage(new TextComponentTranslation("item.magicBook.fail.time"), false);

				spellingResult = EnumActionResult.FAIL;

				return;
			}

			if (magic.getSpellingHand() == EnumHand.MAIN_HAND)
			{
				mc.player.inventory.currentItem = spellingSlot;
			}

			double progress = magic.getSpellingProgress();

			if (infinity)
			{
				progress = MathHelper.clamp(progress * 2.5D, 0.0D, 1.0D);
			}

			if (progress >= 1.0D)
			{
				spellingResult = EnumActionResult.SUCCESS;

				return;
			}

			EnumActionResult result = magic.onSpelling();

			if (result != EnumActionResult.PASS)
			{
				spellingResult = result;

				return;
			}

			if (++spellingSoundTicks >= (infinity ? 8 : 12))
			{
				SoundEvent sound = magic.getSpellingSound();

				if (sound != null)
				{
					mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(sound, 1.0F));
				}

				spellingSoundTicks = 0;
			}

			for (int i = 0; i < 2; ++i)
			{
				int var1 = rand.nextInt(2) * 2 - 1;
				int var2 = rand.nextInt(2) * 2 - 1;
				double ptX = mc.player.posX + 0.25D * var1;
				double ptY = mc.player.posY + 0.7D + rand.nextFloat();
				double ptZ = mc.player.posZ + 0.25D * var2;
				double motionX = rand.nextFloat() * 1.0F * var1;
				double motionY = (rand.nextFloat() - 0.25D) * 0.125D;
				double motionZ = rand.nextFloat() * 1.0F * var2;
				ParticleMagicSpell particle = new ParticleMagicSpell(mc.world, ptX, ptY, ptZ, motionX, motionY, motionZ);

				mc.effectRenderer.addEffect(particle);
			}
		}
		else
		{
			Magic magic = book.getSpellingMagic();

			if (magic != null)
			{
				CaveNetworkRegistry.sendToServer(new MagicResultMessage(EnumActionResult.FAIL));

				magic.onCloseBook();

				SoundEvent sound = magic.getCloseSound();

				if (sound != null)
				{
					mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(sound, 1.0F));
				}
			}

			book.setSpellingMagic(null);

			spellingResult = null;
			spellingSlot = 0;
			spellingSoundTicks = 0;
			sendResult = false;
		}

		SpecialMagic magic = book.getSpecialMagic();

		if (magic != null && magic.getEffectProgress() >= 1.0D)
		{
			ITextComponent message = magic.finishMagic();

			if (message != null)
			{
				mc.ingameGUI.setOverlayMessage(message, false);
			}

			CaveNetworkRegistry.sendToServer(new SpecialMagicMessage());

			book.setSpecialMagic(null);
		}
	}

	@Nullable
	private Pair<EnumHand, ItemStack> getMagicBook(EntityPlayer player)
	{
		ItemStack stack = player.getHeldItemMainhand();

		if (!stack.isEmpty() && stack.getItem() instanceof ItemMagicBook)
		{
			if (ItemMagicBook.getMana(stack) > 0)
			{
				return Pair.of(EnumHand.MAIN_HAND, stack);
			}
		}

		stack = player.getHeldItemOffhand();

		if (!stack.isEmpty() && stack.getItem() instanceof ItemMagicBook)
		{
			if (ItemMagicBook.getMana(stack) > 0)
			{
				return Pair.of(EnumHand.OFF_HAND, stack);
			}
		}

		return null;
	}
}