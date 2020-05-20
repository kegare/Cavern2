package cavern.world.gen;

import cavern.core.Cavern;
import cavern.util.CaveUtils;
import com.google.common.collect.Lists;
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

import java.util.List;
import java.util.Random;

public class SkyCastlePiece
{
	private static final ResourceLocation LOOT_CHEST = LootTableList.register(CaveUtils.getKey("chests/sky_castle"));

	public static void registerSkyCastlePiece()
	{
		MapGenStructureIO.registerStructureComponent(SkyCastlePiece.SkyCastleTemplate.class, "SCT");
	}

	public static void generateCore(TemplateManager templateManager, BlockPos pos, Rotation rotation, List<SkyCastleTemplate> list, Random random)
	{
		generatesub(templateManager, pos, rotation, list, random);
		generateMain(templateManager, pos, rotation, list, random);
	}

	private static void generateMain(TemplateManager templateManager, BlockPos pos, Rotation rotation, List<SkyCastleTemplate> list, Random random)
	{
		BlockPos pos1 = new BlockPos(pos.up(15));

		list.add(new SkyCastlePiece.SkyCastleTemplate(templateManager, pos, rotation, "sky_castle_main"));
		list.add(new SkyCastlePiece.SkyCastleTemplate(templateManager, pos1, rotation, "sky_castle_main2"));
	}

	private static void generateUnderSub(TemplateManager templateManager, BlockPos pos, Rotation rotation, List<SkyCastleTemplate> list, Random random)
	{

	}

	private static void generatesub(TemplateManager templateManager, BlockPos pos, Rotation rotation, List<SkyCastleTemplate> list, Random random)
	{
		//generate sub

		List<EnumFacing> listFacing = Lists.<EnumFacing>newArrayList();

		EnumFacing enumfacing1 = listFacing.get(random.nextInt(listFacing.size()));

		addSubCastle(templateManager, list, pos, rotation, enumfacing1, random);
	}

	private static void addSubCastle(TemplateManager templateManager, List<SkyCastleTemplate> p_191129_1_, BlockPos p_191129_2_, Rotation p_191129_3_, EnumFacing p_191129_4_, Random random)
	{
		Rotation rotation = Rotation.NONE;
		String s = get1x1(random);

		if (p_191129_4_ != EnumFacing.EAST)
		{
			if (p_191129_4_ == EnumFacing.NORTH)
			{
				rotation = rotation.add(Rotation.COUNTERCLOCKWISE_90);
			}
			else if (p_191129_4_ == EnumFacing.WEST)
			{
				rotation = rotation.add(Rotation.CLOCKWISE_180);
			}
			else if (p_191129_4_ == EnumFacing.SOUTH)
			{
				rotation = rotation.add(Rotation.CLOCKWISE_90);
			}
		}

		BlockPos blockpos = Template.getZeroPositionWithTransform(new BlockPos(2, 0, 0), Mirror.NONE, rotation, 19, 19);
		rotation = rotation.add(p_191129_3_);
		blockpos = blockpos.rotate(p_191129_3_);
		BlockPos blockpos1 = p_191129_2_.add(blockpos.getX(), 0, blockpos.getZ());
		p_191129_1_.add(new SkyCastleTemplate(templateManager, blockpos1, rotation, s));
	}

	private static String get1x1(Random random)
	{
		return "sky_castle_sub_1x1" + 1;
	}

	public static class SkyCastleTemplate extends StructureComponentTemplate
	{
		private Rotation rotation;
		private Mirror mirror;
		private String templateName;
		private boolean isAleadyBossRoomGen;

		public SkyCastleTemplate()
		{ //Needs empty constructor
		}

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
			Template template = manager.getTemplate(null, new ResourceLocation(Cavern.MODID, "sky_castle/" + this.templateName));
			PlacementSettings placementsettings = (new PlacementSettings()).setIgnoreEntities(true).setRotation(this.rotation).setMirror(this.mirror);
			this.setup(template, this.templatePosition, placementsettings);
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
				/*EntityCrystalTurret entityturret = new EntityCrystalTurret(world);
				entityturret.enablePersistence();
				entityturret.moveToBlockPosAndAngles(pos, 0.0F, 0.0F);
				world.spawnEntity(entityturret);*/
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
			}
			else if (function.equals("Rift"))
			{
				/*EntityCrystalTurret entityturret = new EntityCrystalTurret(world);
				entityturret.enablePersistence();
				entityturret.moveToBlockPosAndAngles(pos, 0.0F, 0.0F);
				world.spawnEntity(entityturret);*/
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
			}
			else if (function.equals("Boss"))
			{
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
			}
		}

		@Override
		protected void writeStructureToNBT(NBTTagCompound compound)
		{
			super.writeStructureToNBT(compound);
			compound.setBoolean("BossRoom", this.isAleadyBossRoomGen);
			compound.setString("Template", this.templateName);
			compound.setString("Rot", this.placeSettings.getRotation().name());
			compound.setString("Mi", this.placeSettings.getMirror().name());
		}

		@Override
		protected void readStructureFromNBT(NBTTagCompound compound, TemplateManager manager)
		{
			super.readStructureFromNBT(compound, manager);
			this.isAleadyBossRoomGen = compound.getBoolean("BossRoom");
			this.templateName = compound.getString("Template");
			this.rotation = Rotation.valueOf(compound.getString("Rot"));
			this.mirror = Mirror.valueOf(compound.getString("Mi"));
			this.loadTemplate(manager);
		}
	}
}