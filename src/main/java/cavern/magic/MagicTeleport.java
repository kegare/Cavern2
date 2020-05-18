package cavern.magic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class MagicTeleport extends Magic
{
	private int teleportDistance;

	public MagicTeleport(World world, EntityPlayer player, EnumHand hand)
	{
		super(world, player, hand);
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
			int mana = getMana();
			int amount = mana >= 5 ? 30 : mana * 4;

			if (isOverload())
			{
				amount += mana;
			}

			teleportDistance = teleportToFront(amount);

			return new ActionResult<>(teleportDistance > 0 ? EnumActionResult.SUCCESS : EnumActionResult.FAIL, null);
		}

		return new ActionResult<>(EnumActionResult.PASS, null);
	}

	public int teleportToFront(int amount)
	{
		int distance = amount;
		EnumFacing front = player.getHorizontalFacing();
		BlockPos origin = player.getPosition();
		BlockPos pos = null;

		while (distance > 0)
		{
			pos = origin.offset(front, distance);

			BlockPos prev = pos;
			int count = 0;

			while (!world.isAirBlock(pos) && ++count <= 3)
			{
				pos = pos.up();
			}

			if (count > 4)
			{
				pos = prev;
			}

			prev = pos;
			count = 0;

			while (world.isAirBlock(pos.down()) && ++count <= 3)
			{
				pos = pos.down();
			}

			if (count > 4)
			{
				pos = prev;
			}

			if (!world.isAirBlock(pos))
			{
				--distance;
			}
			else break;
		}

		if (distance <= 0 || pos == null)
		{
			return 0;
		}

		player.setPositionAndUpdate(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

		return distance;
	}

	@Override
	public int getCost()
	{
		return MathHelper.clamp(teleportDistance / 4, 1, 5);
	}

	@Override
	public boolean canOverload()
	{
		return true;
	}
}