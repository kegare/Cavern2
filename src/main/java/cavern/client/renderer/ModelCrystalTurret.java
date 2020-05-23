package cavern.client.renderer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * CrystalTurret - bagu_chan
 * Created using Tabula 7.1.0
 */
@SideOnly(Side.CLIENT)
public class ModelCrystalTurret extends ModelBase
{
	public ModelRenderer core;
	public ModelRenderer cube;
	public ModelRenderer cube2;
	public ModelRenderer cube3;
	public ModelRenderer cube4;

	public ModelCrystalTurret()
	{
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.core = new ModelRenderer(this, 0, 0);
		this.core.setRotationPoint(0.0F, 18.0F, 0.0F);
		this.core.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, 0.0F);
		this.cube = new ModelRenderer(this, 0, 16);
		this.cube.setRotationPoint(-4.5F, 3.0F, -4.5F);
		this.cube.addBox(-1.5F, 0.0F, -1.5F, 3, 3, 3, 0.0F);
		this.cube2 = new ModelRenderer(this, 0, 16);
		this.cube2.setRotationPoint(4.5F, 3.0F, -4.5F);
		this.cube2.addBox(-1.5F, 0.0F, -1.5F, 3, 3, 3, 0.0F);
		this.cube4 = new ModelRenderer(this, 0, 16);
		this.cube4.setRotationPoint(4.5F, 3.0F, 4.5F);
		this.cube4.addBox(-1.5F, 0.0F, -1.5F, 3, 3, 3, 0.0F);
		this.cube3 = new ModelRenderer(this, 0, 16);
		this.cube3.setRotationPoint(-4.5F, 3.0F, 4.5F);
		this.cube3.addBox(-1.5F, 0.0F, -1.5F, 3, 3, 3, 0.0F);
		this.core.addChild(this.cube);
		this.core.addChild(this.cube2);
		this.core.addChild(this.cube4);
		this.core.addChild(this.cube3);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		GlStateManager.pushMatrix();
		GlStateManager.scale(1.1F, 1.1F, 1.1F);
		this.core.render(f5);
		GlStateManager.popMatrix();
	}


	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks)
	{
		float tick = entity.ticksExisted + partialTicks;

		GlStateManager.translate(0F, -0.2F - MathHelper.sin(tick * 0.1F) * 0.1F, 0F);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
	{
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
