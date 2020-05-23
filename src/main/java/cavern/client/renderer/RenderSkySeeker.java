package cavern.client.renderer;

import cavern.client.renderer.layer.LayerGlowSeekerEye;
import cavern.entity.boss.EntitySkySeeker;
import cavern.util.CaveUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSkySeeker extends RenderLiving<EntitySkySeeker>
{
	private static final ResourceLocation sky_seeker_TEXTURES = CaveUtils.getKey("textures/entity/sky_seeker/sky_seeker_core.png");

	public RenderSkySeeker(RenderManager renderManager)
	{
		super(renderManager, new ModelSkySeeker(), 0.55F);

		this.addLayer(new LayerGlowSeekerEye<>(this, "textures/entity/sky_seeker/sky_seeker_core_eye.png"));
	}

	@Override
	protected void preRenderCallback(EntitySkySeeker entitylivingbaseIn, float partialTickTime)
	{
		GlStateManager.scale(1.5F, 1.5F, 1.5F);
		super.preRenderCallback(entitylivingbaseIn, partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySkySeeker entity)
	{
		return sky_seeker_TEXTURES;
	}
}