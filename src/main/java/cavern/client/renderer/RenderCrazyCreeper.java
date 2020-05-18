package cavern.client.renderer;

import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCrazyCreeper extends RenderCreeper
{
	private static final ResourceLocation CRAZY_CREEPER_TEXTURE = new ResourceLocation("cavern", "textures/entity/crazy_creeper.png");

	public RenderCrazyCreeper(RenderManager manager)
	{
		super(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCreeper entity)
	{
		return CRAZY_CREEPER_TEXTURE;
	}
}