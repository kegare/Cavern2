package cavern.client;

import cavern.client.renderer.*;
import cavern.entity.*;
import cavern.entity.passive.EntityDurangHog;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Map;

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
		RenderingRegistry.registerEntityRenderingHandler(EntityDurangHog.class, RenderDurangHog::new);

		RenderingRegistry.registerEntityRenderingHandler(EntityMagicTorcher.class, RenderMagicTorcher::new);
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