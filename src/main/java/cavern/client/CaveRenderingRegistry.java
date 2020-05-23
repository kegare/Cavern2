package cavern.client;

import cavern.client.renderer.RenderBeam;
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
import cavern.client.renderer.RenderCrystalTurret;
import cavern.client.renderer.RenderDurangHog;
import cavern.client.renderer.RenderMagicTorcher;
import cavern.client.renderer.RenderSkySeeker;
import cavern.entity.boss.EntitySkySeeker;
import cavern.entity.monster.EntityCaveman;
import cavern.entity.monster.EntityCavenicBear;
import cavern.entity.monster.EntityCavenicCreeper;
import cavern.entity.monster.EntityCavenicSkeleton;
import cavern.entity.monster.EntityCavenicSpider;
import cavern.entity.monster.EntityCavenicWitch;
import cavern.entity.monster.EntityCavenicZombie;
import cavern.entity.monster.EntityCrazyCreeper;
import cavern.entity.monster.EntityCrazySkeleton;
import cavern.entity.monster.EntityCrazySpider;
import cavern.entity.monster.EntityCrazyZombie;
import cavern.entity.monster.EntityCrystalTurret;
import cavern.entity.passive.EntityDurangHog;
import cavern.entity.projectile.EntityBeam;
import cavern.entity.projectile.EntityMagicTorcher;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Map;

@SideOnly(Side.CLIENT)
public final class CaveRenderingRegistry
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
		RenderingRegistry.registerEntityRenderingHandler(EntityCrystalTurret.class, RenderCrystalTurret::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySkySeeker.class, RenderSkySeeker::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityDurangHog.class, RenderDurangHog::new);

		RenderingRegistry.registerEntityRenderingHandler(EntityMagicTorcher.class, RenderMagicTorcher::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBeam.class, RenderBeam::new);
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