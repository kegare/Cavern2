package cavern.client.renderer;

import cavern.client.renderer.layer.LayerGlowEye;
import cavern.entity.monster.EntityCrystalTurret;
import cavern.util.CaveUtils;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCrystalTurret extends RenderLiving<EntityCrystalTurret>
{
	private static final ResourceLocation crystal_turret_TEXTURES = CaveUtils.getKey("textures/entity/crystal_turret/crystal_turret.png");

	public RenderCrystalTurret(RenderManager renderManager)
	{
		super(renderManager, new ModelCrystalTurret(), 0.48F);

		this.addLayer(new LayerGlowEye<>(this, "textures/entity/crystal_turret/crystal_turret_eye.png"));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCrystalTurret entity)
	{
		return crystal_turret_TEXTURES;
	}
}