package cavern.world.gen;

import java.util.Random;

import cavern.util.CaveUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class WorldGenRuinedHouse extends WorldGenerator
{
	private static final ResourceLocation RUINED_HOUSE = CaveUtils.getKey("ruined_house");

	@Override
	public boolean generate(World world, Random rand, BlockPos pos)
	{
		Rotation rotation = Rotation.values()[rand.nextInt(Rotation.values().length)];
		Mirror mirror = Mirror.values()[rand.nextInt(Mirror.values().length)];
		MinecraftServer server = world.getMinecraftServer();
		TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
		PlacementSettings settings = new PlacementSettings().setRotation(rotation).setMirror(mirror);
		Template template = templateManager.getTemplate(server, RUINED_HOUSE);
		BlockPos center = pos.add(template.getSize().getX() / 2, 0, template.getSize().getZ() / 2);

		if (world.getBlockState(center).isOpaqueCube())
		{
			template.addBlocksToWorldChunk(world, center, settings);

			return true;
		}

		return false;
	}
}