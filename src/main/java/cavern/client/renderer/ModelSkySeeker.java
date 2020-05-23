package cavern.client.renderer;

import cavern.entity.boss.EntitySkySeeker;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

/**
 * TofuTurret - bagu
 * Created using Tabula 7.1.0
 */
public class ModelSkySeeker extends ModelBase
{
	public ModelRenderer body;
	public ModelRenderer core;
	public ModelRenderer handR;
	public ModelRenderer handL;
	public ModelRenderer body2;

	public ModelSkySeeker()
	{
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.body2 = new ModelRenderer(this, 32, 17);
		this.body2.setRotationPoint(0.0F, 11.0F, 0.0F);
		this.body2.addBox(-3.0F, 0.0F, -2.0F, 6, 1, 4, 0.0F);
		this.body = new ModelRenderer(this, 32, 0);
		this.body.setRotationPoint(0.0F, 12.0F, 0.0F);
		this.body.addBox(-4.0F, 0.0F, -3.0F, 8, 11, 6, 0.0F);
		this.core = new ModelRenderer(this, 0, 0);
		this.core.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.core.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		this.handR = new ModelRenderer(this, 0, 16);
		this.handR.setRotationPoint(-6.0F, 0.0F, 0.0F);
		this.handR.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 4, 0.0F);
		this.handL = new ModelRenderer(this, 0, 16);
		this.handL.setRotationPoint(6.0F, 0.0F, 0.0F);
		this.handL.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 4, 0.0F);
		this.body.addChild(this.body2);
		this.body.addChild(this.core);
		this.body.addChild(this.handR);
		this.body.addChild(this.handL);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		this.body.render(f5);
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks)
	{
		float tick = entity.ticksExisted + partialTicks;

		if (entity instanceof EntitySkySeeker)
		{
			if (!((EntitySkySeeker) entity).isSleep())
			{
				GlStateManager.translate(0F, -0.2F - MathHelper.sin(tick * 0.1F) * 0.1F, 0F);
			}
		}
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
	{
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

		this.core.rotateAngleX = headPitch * 0.017453292F;
		this.core.rotateAngleY = netHeadYaw * 0.017453292F;
		this.handR.rotateAngleX = 0.0F;
		this.handL.rotateAngleX = 0.0F;

		if (entityIn instanceof EntitySkySeeker)
		{
			if (((EntitySkySeeker) entityIn).isMagicPreAttack())
			{
				this.handR.rotateAngleX = (float) Math.PI;
				this.handL.rotateAngleX = (float) Math.PI;
			}
			else if (((EntitySkySeeker) entityIn).isMagicAttack())
			{
				this.handR.rotateAngleX = (float) (-Math.PI / 2F);
				this.handL.rotateAngleX = (float) (-Math.PI / 2F);
			}
		}

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
