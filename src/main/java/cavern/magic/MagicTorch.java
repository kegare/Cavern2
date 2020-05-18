package cavern.magic;

import cavern.entity.EntityMagicTorcher;
import cavern.util.PlayerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class MagicTorch extends Magic
{
	public MagicTorch(World world, EntityPlayer player, EnumHand hand)
	{
		super(world, player, hand);
	}

	@Override
	public long getSpellTime()
	{
		return 10000L;
	}

	@Override
	public ActionResult<ITextComponent> fireMagic()
	{
		if (world.isRemote)
		{
			if (player.isInWater() || player.isInLava())
			{
				return new ActionResult<>(EnumActionResult.FAIL, new TextComponentTranslation("item.magicBook.fail.place"));
			}

			if (!player.inventory.hasItemStack(new ItemStack(Blocks.TORCH)))
			{
				return new ActionResult<>(EnumActionResult.FAIL, new TextComponentTranslation("item.magicBook.torch.none"));
			}

			return new ActionResult<>(EnumActionResult.PASS, null);
		}

		boolean tracking = !player.isSneaking();
		double range = tracking ? 6.0D : 12.0D;

		for (EntityMagicTorcher torcher : world.getEntitiesWithinAABB(EntityMagicTorcher.class, player.getEntityBoundingBox().expand(range, 5.0D, range), EntitySelectors.IS_ALIVE))
		{
			EntityPlayer entityPlayer = torcher.getPlayer();

			if (entityPlayer != null && entityPlayer.getCachedUniqueIdString().equals(player.getCachedUniqueIdString()))
			{
				return new ActionResult<>(EnumActionResult.FAIL, new TextComponentTranslation("item.magicBook.torch.exist"));
			}
		}

		EntityMagicTorcher torcher = new EntityMagicTorcher(world, player, MathHelper.ceil(range));

		torcher.setLifeTime((tracking ? 120 : 60) * 20);
		torcher.setTracking(tracking);

		world.spawnEntity(torcher);

		PlayerHelper.grantAdvancement(player, "magic_torch");

		return new ActionResult<>(EnumActionResult.SUCCESS, null);
	}
}