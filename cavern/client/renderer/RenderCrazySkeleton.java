package cavern.client.renderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCrazySkeleton extends RenderSkeleton
{
	private static final ResourceLocation CRAZY_SKELETON_TEXTURE = new ResourceLocation("cavern", "textures/entity/crazy_skeleton.png");

	public RenderCrazySkeleton(RenderManager manager)
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
		return CRAZY_SKELETON_TEXTURE;
	}
}