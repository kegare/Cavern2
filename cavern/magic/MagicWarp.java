package cavern.magic;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import cavern.util.PlayerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class MagicWarp extends Magic
{
	private double distance;
	private boolean warp;

	public MagicWarp(World world, EntityPlayer player, EnumHand hand)
	{
		super(world, player, hand);
	}

	@Override
	public long getSpellTime()
	{
		return hasWarpPoint(getHeldItem()) ? 10000L : 5000L;
	}

	@Override
	public ActionResult<ITextComponent> fireMagic()
	{
		if (!world.isRemote)
		{
			ItemStack stack = getHeldItem();
			DimensionType type = world.provider.getDimensionType();
			Pair<BlockPos, DimensionType> warpPoint = getWarpPoint(stack);

			if (warpPoint == null)
			{
				setWarpPoint(stack, player.getPosition(), type);

				return new ActionResult<>(EnumActionResult.SUCCESS, new TextComponentTranslation("item.magicBook.warp.set"));
			}

			BlockPos pos = warpPoint.getLeft();

			if (type != warpPoint.getRight())
			{
				return new ActionResult<>(EnumActionResult.FAIL, new TextComponentTranslation("item.magicBook.warp.far"));
			}

			distance = Math.sqrt(player.getDistanceSqToCenter(pos));

			int mana = getMana();

			if (mana < 5 && distance > 100.0D * mana)
			{
				return new ActionResult<>(EnumActionResult.FAIL, new TextComponentTranslation("item.magicBook.warp.far"));
			}

			if (!player.attemptTeleport(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D))
			{
				return new ActionResult<>(EnumActionResult.FAIL, null);
			}

			warp = true;

			setWarpPoint(stack, null, type);

			PlayerHelper.grantAdvancement(player, "magic_warp");

			return new ActionResult<>(EnumActionResult.SUCCESS, null);
		}

		return new ActionResult<>(EnumActionResult.PASS, null);
	}

	@Override
	public int getCost()
	{
		return warp ? MathHelper.clamp(MathHelper.floor(distance * 0.01D) + 1, 1, 5) : 1;
	}

	public static boolean hasWarpPoint(ItemStack stack)
	{
		NBTTagCompound nbt = stack.getTagCompound();

		return nbt != null && nbt.hasKey("WarpPoint", NBT.TAG_COMPOUND);
	}

	@Nullable
	public static Pair<BlockPos, DimensionType> getWarpPoint(ItemStack stack)
	{
		if (!hasWarpPoint(stack))
		{
			return null;
		}

		NBTTagCompound nbt = stack.getTagCompound();
		NBTTagCompound compound = nbt.getCompoundTag("WarpPoint");
		BlockPos pos = NBTUtil.getPosFromTag(compound);
		DimensionType type;

		try
		{
			type = DimensionType.getById(compound.getInteger("Dim"));
		}
		catch (IllegalArgumentException e)
		{
			nbt.removeTag("WarpPoint");

			return null;
		}

		return Pair.of(pos, type);
	}

	public static void setWarpPoint(ItemStack stack, @Nullable BlockPos pos, DimensionType type)
	{
		NBTTagCompound nbt = stack.getTagCompound();

		if (nbt == null)
		{
			nbt = new NBTTagCompound();
		}

		if (pos == null)
		{
			nbt.removeTag("WarpPoint");
		}
		else
		{
			NBTTagCompound compound = NBTUtil.createPosTag(pos);
			compound.setInteger("Dim", type.getId());

			nbt.setTag("WarpPoint", compound);
		}

		stack.setTagCompound(nbt);
	}
}