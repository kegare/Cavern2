package cavern.client.renderer;

import cavern.util.CaveUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCavenicSkeleton extends RenderSkeleton
{
	private static final ResourceLocation CAVENIC_SKELETON_TEXTURE = CaveUtils.getKey("textures/entity/cavenic_skeleton.png");

	public RenderCavenicSkeleton(RenderManager manager)
	{
		super(manager);
	}

	@Override
	protected void preRenderCallback(AbstractSkeleton entity, float ticks)
	{
		GlStateManager.scale(1.1F, 1.1F, 1.1F);
	}

	@Override
	protected ResourceLocation getEntityTexture(AbstractSkeleton entity)
	{
		return CAVENIC_SKELETON_TEXTURE;
	}
}