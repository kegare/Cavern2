package cavern.client.audio;

import javax.annotation.Nullable;

import cavern.core.CaveSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MovingSoundSkyFalling extends MovingSound
{
	public static MovingSoundSkyFalling prevSound;

	public MovingSoundSkyFalling()
	{
		super(CaveSounds.FALLING, SoundCategory.PLAYERS);
		this.repeat = true;
		this.repeatDelay = 0;
	}

	@Override
	public void update()
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		EntityPlayer player = mc.player;
		World world = mc.world;

		if (player == null || player.isDead || player.onGround || isInLiquid(world, player))
		{
			donePlaying = true;

			if (player != null && player.onGround)
			{
				ISound sound = PositionedSoundRecord.getRecordSoundRecord(SoundEvents.ENTITY_HOSTILE_SMALL_FALL, (float)player.posX, (float)player.posY - (float)player.getYOffset(), (float)player.posZ);

				mc.getSoundHandler().playSound(sound);
			}
		}
		else
		{
			xPosF = (float)player.posX;
			yPosF = (float)player.posY;
			zPosF = (float)player.posZ;

			if (MathHelper.sqrt(player.motionY * player.motionY) >= 0.01D)
			{
				volume = MathHelper.clamp((yPosF - world.getPrecipitationHeight(player.getPosition()).getY()) * 0.05F, 0.0F, 1.0F);
			}
			else
			{
				volume = 0.0F;
			}
		}
	}

	public boolean isInLiquid(@Nullable World world, @Nullable EntityPlayer player)
	{
		if (world == null || player == null)
		{
			return false;
		}

		AxisAlignedBB bb = player.getEntityBoundingBox().grow(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D);
		int minX = MathHelper.floor(bb.minX);
		int maxX = MathHelper.ceil(bb.maxX);
		int minY = MathHelper.floor(bb.minY);
		int maxY = MathHelper.ceil(bb.maxY);
		int minZ = MathHelper.floor(bb.minZ);
		int maxZ = MathHelper.ceil(bb.maxZ);
		BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();

		for (int x = minX; x < maxX; ++x)
		{
			for (int y = minY; y < maxY; ++y)
			{
				for (int z = minZ; z < maxZ; ++z)
				{
					if (world.getBlockState(pos.setPos(x, y, z)).getMaterial().isLiquid())
					{
						pos.release();

						return true;
					}
				}
			}
		}

		pos.release();

		return false;
	}
}