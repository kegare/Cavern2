package cavern.client;

import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.collect.Maps;

import cavern.client.renderer.RenderCaveman;
import cavern.client.renderer.RenderCavenicBear;
import cavern.client.renderer.RenderCavenicCreeper;
import cavern.client.renderer.RenderCavenicSkeleton;
import cavern.client.renderer.RenderCavenicSpider;
import cavern.client.renderer.RenderCavenicWitch;
import cavern.client.renderer.RenderCavenicZombie;
import cavern.client.renderer.RenderCrazyCreeper;
import cavern.client.renderer.RenderCrazySkeleton;
import cavern.client.renderer.RenderCrazySpider;
import cavern.client.renderer.RenderCrazyZombie;
import cavern.entity.EntityCaveman;
import cavern.entity.EntityCavenicBear;
import cavern.entity.EntityCavenicCreeper;
import cavern.entity.EntityCavenicSkeleton;
import cavern.entity.EntityCavenicSpider;
import cavern.entity.EntityCavenicWitch;
import cavern.entity.EntityCavenicZombie;
import cavern.entity.EntityCrazyCreeper;
import cavern.entity.EntityCrazySkeleton;
import cavern.entity.EntityCrazySpider;
import cavern.entity.EntityCrazyZombie;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CaveRenderingRegistry
{
	public static final Map<Block, Block> RENDER_BLOCK_MAP = Maps.newHashMap();

	public static void registerRenderers()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicSkeleton.class, RenderCavenicSkeleton::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicCreeper.class, RenderCavenicCreeper::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicZombie.class, RenderCavenicZombie::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicSpider.class, RenderCavenicSpider::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicWitch.class, RenderCavenicWitch::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCrazySkeleton.class, RenderCrazySkeleton::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCrazyCreeper.class, RenderCrazyCreeper::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCrazyZombie.class, RenderCrazyZombie::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCrazySpider.class, RenderCrazySpider::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicBear.class, RenderCavenicBear::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCaveman.class, RenderCaveman::new);
	}

	public static void registerRenderBlocks()
	{
		RENDER_BLOCK_MAP.put(Blocks.LIT_REDSTONE_ORE, Blocks.REDSTONE_ORE);
	}

	public static Block getRenderBlock(Block block)
	{
		Block ret = RENDER_BLOCK_MAP.get(block);

		return ObjectUtils.defaultIfNull(ret, block);
	}
}