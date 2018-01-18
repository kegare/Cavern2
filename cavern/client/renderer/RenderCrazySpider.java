package cavern.client.renderer;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCrazySpider extends RenderSpider<EntitySpider>
{
	private static final ResourceLocation CRAZY_SPIDER_TEXTURE = new ResourceLocation("cavern", "textures/entity/crazy_spider.png");

	public RenderCrazySpider(RenderManager manager)
	{
		super(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySpider entity)
	{
		return CRAZY_SPIDER_TEXTURE;
	}
}