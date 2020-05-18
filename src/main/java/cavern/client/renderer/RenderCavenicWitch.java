package cavern.client.renderer;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderWitch;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCavenicWitch extends RenderWitch
{
	private static final ResourceLocation CAVENIC_WITCH_TEXTURE = new ResourceLocation("cavern", "textures/entity/cavenic_witch.png");

	public RenderCavenicWitch(RenderManager manager)
	{
		super(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityWitch entity)
	{
		return CAVENIC_WITCH_TEXTURE;
	}
}