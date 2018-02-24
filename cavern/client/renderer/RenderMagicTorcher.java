package cavern.client.renderer;

import cavern.entity.EntityMagicTorcher;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMagicTorcher extends Render<EntityMagicTorcher>
{
	public RenderMagicTorcher(RenderManager renderManager)
	{
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMagicTorcher entity)
	{
		return null;
	}
}