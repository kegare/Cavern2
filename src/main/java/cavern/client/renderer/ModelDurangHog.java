package cavern.client.renderer;

import cavern.entity.passive.EntityDurangHog;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class ModelDurangHog extends ModelBase
{
	private float headRotationAngleX;

	protected float childYOffset = 4.0F;
	protected float childZOffset = 4.0F;

	public ModelRenderer head;
	public ModelRenderer nose;
	public ModelRenderer legR;
	public ModelRenderer backLegR;
	public ModelRenderer legL;
	public ModelRenderer backLegL;
	public ModelRenderer body;
	public ModelRenderer fangR;
	public ModelRenderer fangL;

	public ModelDurangHog()
	{
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.legL = new ModelRenderer(this, 0, 16);
		this.legL.setRotationPoint(3.0F, 18.0F, -5.0F);
		this.legL.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
		this.backLegL = new ModelRenderer(this, 0, 16);
		this.backLegL.setRotationPoint(3.0F, 18.0F, 7.0F);
		this.backLegL.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
		this.backLegR = new ModelRenderer(this, 0, 16);
		this.backLegR.setRotationPoint(-3.0F, 18.0F, 7.0F);
		this.backLegR.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
		this.nose = new ModelRenderer(this, 16, 16);
		this.nose.setRotationPoint(0.0F, 12.0F, -6.0F);
		this.nose.addBox(-2.0F, 0.0F, -9.0F, 4, 3, 1, 0.0F);
		this.legR = new ModelRenderer(this, 0, 16);
		this.legR.setRotationPoint(-3.0F, 18.0F, -5.0F);
		this.legR.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
		this.body = new ModelRenderer(this, 28, 8);
		this.body.setRotationPoint(0.0F, 11.0F, 2.0F);
		this.body.addBox(-5.0F, -10.0F, -7.0F, 10, 16, 8, 0.0F);
		this.setRotateAngle(body, 1.5707963267948966F, 0.0F, 0.0F);
		this.fangR = new ModelRenderer(this, 0, 0);
		this.fangR.setRotationPoint(-2.0F, 2.1F, -8.5F);
		this.fangR.addBox(-1.0F, -3.0F, -0.5F, 1, 3, 1, 0.0F);
		this.setRotateAngle(fangR, 0.36425021489121656F, 0.0F, 0.0F);
		this.fangL = new ModelRenderer(this, 0, 0);
		this.fangL.setRotationPoint(2.0F, 2.1F, -8.5F);
		this.fangL.addBox(0.0F, -3.0F, -0.5F, 1, 3, 1, 0.0F);
		this.setRotateAngle(fangL, 0.36425021489121656F, 0.0F, 0.0F);
		this.head = new ModelRenderer(this, 0, 0);
		this.head.setRotationPoint(0.0F, 12.0F, -6.0F);
		this.head.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, 0.0F);
		this.nose.addChild(this.fangR);
		this.nose.addChild(this.fangL);
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		if (this.isChild)
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, this.childYOffset * scale, this.childZOffset * scale);
			this.head.render(scale);
			this.nose.render(scale);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);

			this.body.render(scale);
			this.legR.render(scale);
			this.legL.render(scale);
			this.backLegL.render(scale);
			this.backLegR.render(scale);
			GlStateManager.popMatrix();
		}
		else
		{
			this.head.render(scale);
			this.nose.render(scale);
			this.body.render(scale);
			this.legR.render(scale);
			this.legL.render(scale);
			this.backLegL.render(scale);
			this.backLegR.render(scale);
		}

	}

	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime)
	{
		super.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTickTime);
		this.headRotationAngleX = ((EntityDurangHog) entitylivingbaseIn).getHeadRotationAngleX(partialTickTime);
	}

	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
	{
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
		this.head.rotateAngleX = this.headRotationAngleX;
		this.head.rotateAngleY = netHeadYaw * 0.017453292F;
		this.body.rotateAngleX = ((float) Math.PI / 2F);
		this.legR.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.legL.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		this.backLegR.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		this.backLegL.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

		copyModelAngles(this.head, this.nose);
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
