package cavern.client;

import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientExplosion
{
	private final Minecraft mc;
	private final Random random;
	private final double x;
	private final double y;
	private final double z;
	private final float size;
	private final List<BlockPos> affectedBlockPositions;

	public ClientExplosion(Minecraft mc, double x, double y, double z, float size, List<BlockPos> positions)
	{
		this.mc = mc;
		this.random = new Random();
		this.x = x;
		this.y = y;
		this.z = z;
		this.size = size;
		this.affectedBlockPositions = positions;
	}

	public void doExplosion()
	{
		spawnParticle(size >= 2.0F ? EnumParticleTypes.EXPLOSION_HUGE : EnumParticleTypes.EXPLOSION_LARGE, x, y, z, 1.0D, 0.0D, 0.0D);

		if (affectedBlockPositions != null)
		{
			for (BlockPos pos : affectedBlockPositions)
			{
				double ex = pos.getX() + random.nextFloat();
				double ey = pos.getY() + random.nextFloat();
				double ez = pos.getZ() + random.nextFloat();
				double rx = ex - x;
				double ry = ey - y;
				double rz = ez - z;
				double rd = MathHelper.sqrt(rx * rx + ry * ry + rz * rz);
				rx = rx / rd;
				ry = ry / rd;
				rz = rz / rd;
				double d7 = 0.5D / (rd / size + 0.1D);
				d7 = d7 * (random.nextFloat() * random.nextFloat() + 0.3F);
				rx = rx * d7;
				ry = ry * d7;
				rz = rz * d7;

				spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (ex + x) / 2.0D, (ey + y) / 2.0D, (ez + z) / 2.0D, rx, ry, rz);
				spawnParticle(EnumParticleTypes.SMOKE_NORMAL, ex, ey, ez, rx, ry, rz);
			}
		}
	}

	protected void spawnParticle(EnumParticleTypes type, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
	{
		mc.effectRenderer.spawnEffectParticle(type.getParticleID(), x, y, z, xSpeed, ySpeed, zSpeed);
	}
}