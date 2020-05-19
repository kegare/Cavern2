package cavern.client.renderer;

import cavern.util.CaveUtils;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCavenicCreeper extends RenderCreeper
{
	private static final ResourceLocation CAVENIC_CREEPER_TEXTURE = CaveUtils.getKey("textures/entity/cavenic_creeper.png");

	public RenderCavenicCreeper(RenderManager manager)
	{
		super(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCreeper entity)
	{
		return CAVENIC_CREEPER_TEXTURE;
	}
}