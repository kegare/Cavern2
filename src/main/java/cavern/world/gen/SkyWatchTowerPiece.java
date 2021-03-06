package cavern.world.gen;

import java.util.List;
import java.util.Random;

import cavern.entity.boss.EntitySkySeeker;
import cavern.entity.monster.EntityCrystalTurret;
import cavern.util.CaveUtils;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponentTemplate;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class SkyWatchTowerPiece
{
	private static final ResourceLocation LOOT_CHEST = LootTableList.register(CaveUtils.getKey("chests/sky_watchtower"));

	public static void registerSkyCastlePiece()
	{
		MapGenStructureIO.registerStructureComponent(SkyWatchTowerPiece.SkyCastleTemplate.class, "SCT");
	}

	public static void generateCore(TemplateManager templateManager, BlockPos pos, Rotation rotation, List<SkyCastleTemplate> list, Random random)
	{
		generateMain(templateManager, pos, rotation, list, random);
		generateWatchTower(templateManager, pos, rotation, list, random);
	}

	private static void generateWatchTower(TemplateManager templateManager, BlockPos pos, Rotation rotation, List<SkyCastleTemplate> list, Random random)
	{
		BlockPos blockpos2 = pos.offset(rotation.rotate(EnumFacing.WEST), 32);

		addSubCastle(templateManager, list, blockpos2, rotation, EnumFacing.WEST, random);

		blockpos2 = pos.offset(rotation.rotate(EnumFacing.EAST), 32);

		addSubCastle(templateManager, list, blockpos2, rotation, EnumFacing.EAST, random);

		blockpos2 = pos.offset(rotation.rotate(EnumFacing.NORTH), 32);

		addSubCastle(templateManager, list, blockpos2, rotation, EnumFacing.NORTH, random);

		blockpos2 = pos.offset(rotation.rotate(EnumFacing.SOUTH), 32);

		addSubCastle(templateManager, list, blockpos2, rotation, EnumFacing.SOUTH, random);
	}

	private static void generateMain(TemplateManager templateManager, BlockPos pos, Rotation rotation, List<SkyCastleTemplate> list, Random random)
	{
		BlockPos pos1 = new BlockPos(pos.up(15));

		list.add(new SkyWatchTowerPiece.SkyCastleTemplate(templateManager, pos, rotation, "sky_watchtower_main"));
		list.add(new SkyWatchTowerPiece.SkyCastleTemplate(templateManager, pos1, rotation, "sky_watchtower_main2"));
	}

	private static void addSubCastle(TemplateManager templateManager, List<SkyCastleTemplate> list, BlockPos pos, Rotation rotationIn, EnumFacing facing, Random random)
	{
		Rotation rotation = Rotation.NONE;
		String s = get1x1(random);

		if (facing != EnumFacing.EAST)
		{
			if (facing == EnumFacing.NORTH)
			{
				rotation = rotation.add(Rotation.COUNTERCLOCKWISE_90);
			}
			else if (facing == EnumFacing.WEST)
			{
				rotation = rotation.add(Rotation.CLOCKWISE_180);
			}
			else if (facing == EnumFacing.SOUTH)
			{
				rotation = rotation.add(Rotation.CLOCKWISE_90);
			}
		}

		BlockPos blockpos = Template.getZeroPositionWithTransform(new BlockPos(1, 0, 0), Mirror.NONE, rotation, 19, 19);

		rotation = rotation.add(rotationIn);
		blockpos = blockpos.rotate(rotationIn);

		BlockPos blockpos1 = pos.add(blockpos.getX(), 0, blockpos.getZ());

		list.add(new SkyCastleTemplate(templateManager, blockpos1, rotation, s));
	}

	private static String get1x1(Random random)
	{
		return "sky_watchtower_sub_1x1_" + (random.nextInt(1) + 1);
	}

	public static class SkyCastleTemplate extends StructureComponentTemplate
	{
		private Rotation rotation;
		private Mirror mirror;
		private String templateName;
		private boolean isAleadyBossRoomGen;

		public SkyCastleTemplate() {}

		public SkyCastleTemplate(TemplateManager manager, BlockPos pos, Rotation rotation, String templateName)
		{
			this(manager, pos, rotation, Mirror.NONE, templateName);
		}

		private SkyCastleTemplate(TemplateManager manager, BlockPos pos, Rotation rotation, Mirror mirror, String templateName)
		{
			super(0);
			this.templatePosition = pos;
			this.rotation = rotation;
			this.mirror = mirror;
			this.templateName = templateName;
			this.loadTemplate(manager);
		}

		private void loadTemplate(TemplateManager manager)
		{
			Template template = manager.getTemplate(null, CaveUtils.getKey("sky_watchtower/" + templateName));
			PlacementSettings placementsettings = (new PlacementSettings()).setIgnoreEntities(true).setRotation(rotation).setMirror(mirror);

			setup(template, templatePosition, placementsettings);
		}

		@Override
		public boolean addComponentParts(World world, Random random, StructureBoundingBox box)
		{
			super.addComponentParts(world, random, box);

			return true;
		}

		@Override
		protected void handleDataMarker(String function, BlockPos pos, World world, Random rand, StructureBoundingBox box)
		{
			if (function.equals("CastleChest"))
			{
				if (box.isVecInside(pos))
				{
					TileEntity tileEntity = world.getTileEntity(pos.down());

					if (tileEntity instanceof TileEntityChest)
					{
						((TileEntityChest) tileEntity).setLootTable(LOOT_CHEST, rand.nextLong());
					}
				}
			}
			else if (function.equals("Guard"))
			{
				EntityCrystalTurret entity = new EntityCrystalTurret(world);

				entity.enablePersistence();
				entity.moveToBlockPosAndAngles(pos, 0.0F, 0.0F);

				world.spawnEntity(entity);
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
			}
			else if (function.equals("Rift"))
			{
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
			}
			else if (function.equals("Boss"))
			{
				EntitySkySeeker entity = new EntitySkySeeker(world);

				entity.enablePersistence();
				entity.moveToBlockPosAndAngles(pos, 0.0F, 0.0F);
				entity.setSleep(true);

				world.spawnEntity(entity);
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
			}
		}

		@Override
		protected void writeStructureToNBT(NBTTagCompound compound)
		{
			super.writeStructureToNBT(compound);

			compound.setBoolean("BossRoom", isAleadyBossRoomGen);
			compound.setString("Template", templateName);
			compound.setString("Rot", placeSettings.getRotation().name());
			compound.setString("Mi", placeSettings.getMirror().name());
		}

		@Override
		protected void readStructureFromNBT(NBTTagCompound compound, TemplateManager manager)
		{
			super.readStructureFromNBT(compound, manager);

			isAleadyBossRoomGen = compound.getBoolean("BossRoom");
			templateName = compound.getString("Template");
			rotation = Rotation.valueOf(compound.getString("Rot"));
			mirror = Mirror.valueOf(compound.getString("Mi"));

			loadTemplate(manager);
		}
	}
}