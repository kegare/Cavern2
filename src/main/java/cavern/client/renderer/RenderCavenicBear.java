package cavern.client.renderer;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPolarBear;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCavenicBear extends RenderPolarBear
{
	private static final ResourceLocation CAVENIC_BEAR_TEXTURE = new ResourceLocation("cavern", "textures/entity/cavenic_bear.png");

	public RenderCavenicBear(RenderManager manager)
	{
		super(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityPolarBear entity)
	{
		return CAVENIC_BEAR_TEXTURE;
	}
}