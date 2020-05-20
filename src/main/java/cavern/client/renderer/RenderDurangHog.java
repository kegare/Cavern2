package cavern.client.renderer;

import cavern.entity.passive.EntityDurangHog;
import cavern.util.CaveUtils;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderDurangHog extends RenderLiving<EntityDurangHog>
{
	private static final ResourceLocation DURANGHOG_TEXTURES = CaveUtils.getKey("textures/entity/durang_hog.png");

	public RenderDurangHog(RenderManager renderManager)
	{
		super(renderManager, new ModelDurangHog(), 0.48F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDurangHog entity)
	{
		return DURANGHOG_TEXTURES;
	}
}