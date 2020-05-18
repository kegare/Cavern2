package cavern.client.particle;

import net.minecraft.client.particle.ParticlePortal;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleCrazyMob extends ParticlePortal
{
	public ParticleCrazyMob(World world, double x, double y, double z, double motionX, double motionY, double motionZ)
	{
		super(world, x, y, z, motionX, motionY, motionZ);
		float f = rand.nextFloat() * 0.5F + 0.4F;
		this.particleRed = this.particleGreen = this.particleBlue = 0.65F * f * 0.8F;
	}
}