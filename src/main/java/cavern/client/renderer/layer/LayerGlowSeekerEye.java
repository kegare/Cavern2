package cavern.client.renderer.layer;

import cavern.entity.boss.EntitySkySeeker;
import cavern.util.CaveUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerGlowSeekerEye<T extends EntitySkySeeker> implements LayerRenderer<T>
{
	private final ResourceLocation eyeTexture;
	private final RenderLiving<T> render;

	public LayerGlowSeekerEye(RenderLiving<T> render, String key)
	{
		this.render = render;
		this.eyeTexture = CaveUtils.getKey(key);
	}

	@Override
	public void doRenderLayer(T living, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		if (!living.isSleep())
		{
			render.bindTexture(eyeTexture);
			GlStateManager.enableBlend();
			GlStateManager.disableAlpha();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
			GlStateManager.disableLighting();
			GlStateManager.depthMask(!living.isInvisible());
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);
			GlStateManager.enableLighting();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
			render.getMainModel().render(living, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
			render.setLightmap(living);
			GlStateManager.depthMask(true);
			GlStateManager.disableBlend();
			GlStateManager.enableAlpha();
		}
	}

	@Override
	public boolean shouldCombineTextures()
	{
		return false;
	}
}