package cavern.client.renderer;

import cavern.entity.monster.EntityCaveman;
import cavern.util.CaveUtils;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCaveman extends RenderBiped<EntityCaveman>
{
	private static final ResourceLocation CAVEMAN_TEXTURES = CaveUtils.getKey("textures/entity/caveman.png");

	public RenderCaveman(RenderManager renderManager)
	{
		super(renderManager, new ModelCaveman(), 0.48F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCaveman entity)
	{
		return CAVEMAN_TEXTURES;
	}
}