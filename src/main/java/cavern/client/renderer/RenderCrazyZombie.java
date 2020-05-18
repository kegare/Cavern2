package cavern.client.renderer;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCrazyZombie extends RenderZombie
{
	private static final ResourceLocation CRAZY_ZOMBIE_TEXTURE = new ResourceLocation("cavern", "textures/entity/crazy_zombie.png");

	public RenderCrazyZombie(RenderManager manager)
	{
		super(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityZombie entity)
	{
		return CRAZY_ZOMBIE_TEXTURE;
	}
}