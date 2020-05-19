package cavern.client.renderer;

import cavern.entity.monster.EntityCaveman;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCaveman extends ModelBiped
{
	public ModelRenderer bipedBack;

	private final float
	headPtY = -1.0F,
	bodyPtY = 2.0F,
	backPtY = 3.0F,
	armPtY = 4.0F,
	legPtY = 14.0F,
	sit = 8.5F;

	public ModelCaveman()
	{
		super();
		this.bipedHead = new ModelRenderer(this, 0, 0);
		this.bipedHead.addBox(-4.0F, -5.0F, -4.0F, 8, 8, 8);
		this.bipedHead.setRotationPoint(0.0F, headPtY, 0.0F);
		this.bipedHeadwear = bipedHead;
		this.bipedBody = new ModelRenderer(this, 0, 16);
		this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4);
		this.bipedBody.setRotationPoint(0.0F, bodyPtY, 0.0F);
		this.bipedBack = new ModelRenderer(this, 32, 0);
		this.bipedBack.addBox(0.0F, 0.0F, 0.0F, 6, 11, 3);
		this.bipedBack.setRotationPoint(-3.0F, backPtY, 2.0F);
		this.bipedRightArm = new ModelRenderer(this, 24, 18);
		this.bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2);
		this.bipedRightArm.setRotationPoint(-5.0F, armPtY, 0.0F);
		this.bipedRightArm.rotateAngleZ = 1.5F;
		this.bipedLeftArm = new ModelRenderer(this, 24, 18);
		this.bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2);
		this.bipedLeftArm.setRotationPoint(5.0F, armPtY, 0.0F);
		this.bipedLeftArm.rotateAngleZ = -1.5F;
		this.bipedLeftArm.mirror = true;
		this.bipedRightLeg = new ModelRenderer(this, 24, 18);
		this.bipedRightLeg.addBox(-1.0F, 0F, -1.0F, 2, 12, 2);
		this.bipedRightLeg.setRotationPoint(-2.0F, legPtY, 0.0F);
		this.bipedLeftLeg = new ModelRenderer(this, 24, 18);
		this.bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2);
		this.bipedLeftLeg.setRotationPoint(2.0F, legPtY, 0.0F);
		this.bipedLeftLeg.mirror = true;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, Entity entity)
	{
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);

		if (entity instanceof EntityCaveman && ((EntityCaveman)entity).isSitting())
		{
			bipedRightArm.rotateAngleX += -((float)Math.PI / 5.0F);
			bipedLeftArm.rotateAngleX += -((float)Math.PI / 5.0F);
			bipedRightLeg.rotateAngleX = -((float)Math.PI * 2.35F / 5.0F);
			bipedLeftLeg.rotateAngleX = -((float)Math.PI * 2.35F / 5.0F);
			bipedRightLeg.rotateAngleY = (float)Math.PI / 10.0F;
			bipedLeftLeg.rotateAngleY = -((float)Math.PI / 10.0F);
			bipedHead.rotationPointY = headPtY + sit;
			bipedHeadwear.rotationPointY = bipedHead.rotationPointY;
			bipedBody.rotationPointY = bodyPtY + sit;
			bipedBack.rotationPointY = backPtY + sit;
			bipedRightArm.rotationPointY = armPtY + sit;
			bipedLeftArm.rotationPointY = bipedRightArm.rotationPointY;
			bipedRightLeg.rotationPointY = legPtY + sit;
			bipedLeftLeg.rotationPointY = bipedRightLeg.rotationPointY;
		}
		else
		{
			bipedHead.rotationPointY = headPtY;
			bipedHeadwear.rotationPointY = bipedHead.rotationPointY;
			bipedBody.rotationPointY = bodyPtY;
			bipedBack.rotationPointY = backPtY;
			bipedRightArm.rotationPointY = armPtY;
			bipedLeftArm.rotationPointY = bipedRightArm.rotationPointY;
			bipedRightLeg.rotationPointY = legPtY;
			bipedLeftLeg.rotationPointY = bipedRightLeg.rotationPointY;
		}
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);

		bipedHead.render(scale);
		bipedBody.render(scale);
		bipedRightArm.render(scale);
		bipedLeftArm.render(scale);
		bipedRightLeg.render(scale);
		bipedLeftLeg.render(scale);
		bipedBack.render(scale);
	}

	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);

		bipedBack.showModel = visible;
	}
}