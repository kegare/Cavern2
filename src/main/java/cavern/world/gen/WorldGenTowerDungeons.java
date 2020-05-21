package cavern.world.gen;

import java.util.Collection;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import cavern.util.CaveLog;
import cavern.util.CaveUtils;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.loot.LootTableList;

public class WorldGenTowerDungeons extends WorldGenerator
{
	public static final ResourceLocation CHESTS_TOWER_DUNGEON = LootTableList.register(CaveUtils.getKey("chests/tower_dungeon"));

	private int maxFloor;
	private int roomSize;
	private int roomHeight;

	private final Collection<ResourceLocation> spawnerMobs;

	public WorldGenTowerDungeons(Collection<ResourceLocation> mobs)
	{
		this.spawnerMobs = mobs;
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos)
	{
		setRandomStructures(world, rand, pos);

		while (pos.getY() > 1 && world.isAirBlock(pos))
		{
			pos = pos.down();
		}

		generateAirs(world, rand, pos);
		generateWalls(world, rand, pos);
		generateFloors(world, rand, pos);
		generatePillars(world, rand, pos);
		generateCeiling(world, rand, pos);
		generateLadders(world, rand, pos);
		generateChests(world, rand, pos);
		generateSpawners(world, rand, pos);

		return true;
	}

	protected void setRandomStructures(World world, Random rand, BlockPos pos)
	{
		maxFloor = rand.nextInt(4) + 5;
		roomSize = rand.nextInt(4) + 4;
		roomHeight = rand.nextInt(4) + 5;
	}

	protected void generateAirs(World world, Random rand, BlockPos pos)
	{
		int y = roomHeight * maxFloor;
		int ceilSize = roomSize - 1;
		BlockPos from = pos.add(ceilSize, 0, ceilSize);
		BlockPos to = pos.add(-ceilSize, y, -ceilSize);

		BlockPos.getAllInBoxMutable(from, to).forEach(blockPos -> world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2));
	}

	protected void generateWalls(World world, Random rand, BlockPos pos)
	{
		for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL)
		{
			BlockPos center = pos.offset(facing, roomSize);
			BlockPos from = center.offset(facing.rotateY(), roomSize);
			BlockPos to = center.offset(facing.rotateYCCW(), roomSize).up(roomHeight * maxFloor);

			BlockPos.getAllInBoxMutable(from, to).forEach(blockPos -> world.setBlockState(blockPos, getWallBlock(world, rand, blockPos), 2));
		}
	}

	protected IBlockState getWallBlock(World world, Random rand, BlockPos pos)
	{
		IBlockState state = Blocks.STONEBRICK.getDefaultState();

		if (rand.nextDouble() < 0.7D)
		{
			return state.withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY);
		}

		return state;
	}

	protected void generateFloors(World world, Random rand, BlockPos pos)
	{
		int floorSize = roomSize - 2;

		for (int i = 0; i < maxFloor; ++i)
		{
			int y = roomHeight * i;
			BlockPos from = pos.add(floorSize, y, floorSize);
			BlockPos to = pos.add(-floorSize, y, -floorSize);

			for (BlockPos blockPos : BlockPos.getAllInBoxMutable(from, to))
			{
				world.setBlockState(blockPos, getFloorBlock(world, rand, i + 1, blockPos), 2);
			}
		}

		int ceilSize = roomSize - 1;

		for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL)
		{
			BlockPos center = pos.offset(facing, ceilSize);
			BlockPos from = center.offset(facing.rotateY(), ceilSize);
			BlockPos to = center.offset(facing.rotateYCCW(), ceilSize);

			BlockPos.getAllInBoxMutable(from, to).forEach(blockPos -> world.setBlockState(blockPos, getFirstFloorLiquid(world, rand, blockPos), 2));

			world.setBlockState(center, getFootholdBlock(world, rand, center), 2);
		}
	}

	protected IBlockState getFloorBlock(World world, Random rand, int floor, BlockPos pos)
	{
		if (floor <= 1)
		{
			return getWallBlock(world, rand, pos);
		}

		IBlockState state = Blocks.STONEBRICK.getDefaultState();

		if (rand.nextDouble() < 0.7D)
		{
			return state.withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED);
		}

		return state;
	}

	protected IBlockState getFirstFloorLiquid(World world, Random rand, BlockPos pos)
	{
		return Blocks.LAVA.getDefaultState();
	}

	protected IBlockState getFootholdBlock(World world, Random rand, BlockPos pos)
	{
		return Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED);
	}

	protected void generatePillars(World world, Random rand, BlockPos pos)
	{
		int floorSize = roomSize - 2;

		for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL)
		{
			BlockPos center = pos.offset(facing, floorSize);
			BlockPos from = center.offset(facing.rotateY(), floorSize);
			BlockPos to = from.up(roomHeight * maxFloor - 1);

			BlockPos.getAllInBoxMutable(from, to).forEach(blockPos -> world.setBlockState(blockPos, getPillarBlock(world, rand, blockPos), 2));
		}
	}

	protected IBlockState getPillarBlock(World world, Random rand, BlockPos pos)
	{
		return Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED);
	}

	protected void generateCeiling(World world, Random rand, BlockPos pos)
	{
		int y = roomHeight * maxFloor;
		int ceilSize = roomSize - 1;
		BlockPos from = pos.add(ceilSize, y, ceilSize);
		BlockPos to = pos.add(-ceilSize, y, -ceilSize);

		BlockPos.getAllInBoxMutable(from, to).forEach(blockPos -> world.setBlockState(blockPos, getCeilingBlock(world, rand, blockPos), 2));
	}

	protected IBlockState getCeilingBlock(World world, Random rand, BlockPos pos)
	{
		return Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED);
	}

	protected void generateLadders(World world, Random rand, BlockPos pos)
	{
		int ceilSize = roomSize - 1;
		int ladderHeight = roomHeight * (maxFloor - 1);
		int wallHeight = roomHeight * maxFloor - 1;

		for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL)
		{
			BlockPos center = pos.offset(facing, ceilSize);
			BlockPos from = center.up();
			BlockPos to = center.up(ladderHeight);

			BlockPos.getAllInBoxMutable(from, to).forEach(blockPos -> world.setBlockState(blockPos, Blocks.LADDER.getDefaultState().withProperty(BlockLadder.FACING, facing.getOpposite()), 2));

			from = center.offset(facing.rotateY());
			to = from.up(wallHeight);

			BlockPos.getAllInBoxMutable(from, to).forEach(blockPos -> world.setBlockState(blockPos, getLadderCoverBlock(world, rand, blockPos), 2));

			from = center.offset(facing.rotateYCCW());
			to = from.up(wallHeight);

			BlockPos.getAllInBoxMutable(from, to).forEach(blockPos -> world.setBlockState(blockPos, getLadderCoverBlock(world, rand, blockPos), 2));
		}
	}

	protected IBlockState getLadderCoverBlock(World world, Random rand, BlockPos pos)
	{
		return Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED);
	}

	protected void generateChests(World world, Random rand, BlockPos pos)
	{
		int floorSize = roomSize - 2;

		for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL)
		{
			BlockPos center = pos.offset(facing, floorSize);
			BlockPos chestPos = center.offset(facing.rotateYCCW(), floorSize - 2);

			for (int i = 1; i < maxFloor; ++i)
			{
				if (rand.nextDouble() > 0.7D)
				{
					continue;
				}

				int y = roomHeight * i + 1;
				BlockPos blockPos = chestPos.up(y);

				world.setBlockState(blockPos, Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, facing.getOpposite()), 2);

				TileEntity tile = world.getTileEntity(blockPos);

				if (tile != null && tile instanceof TileEntityChest)
				{
					((TileEntityChest)tile).setLootTable(CHESTS_TOWER_DUNGEON, rand.nextLong());
				}
			}
		}
	}

	protected void generateSpawners(World world, Random rand, BlockPos pos)
	{
		for (int i = 0; i < maxFloor; ++i)
		{
			int y = roomHeight * i + 1;
			BlockPos blockPos = pos.up(y);

			world.setBlockState(blockPos, Blocks.MOB_SPAWNER.getDefaultState(), 2);

			TileEntity tile = world.getTileEntity(blockPos);

			if (tile != null && tile instanceof TileEntityMobSpawner)
			{
				((TileEntityMobSpawner)tile).getSpawnerBaseLogic().setEntityId(pickMobSpawner(rand, i + 1));
			}
			else
			{
				CaveLog.warning("Failed to fetch mob spawner entity at (" + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ() + ")");
			}
		}
	}

	@Nullable
	public ResourceLocation pickMobSpawner(Random rand, int floor)
	{
		if (!spawnerMobs.isEmpty())
		{
			return CaveUtils.getRandomObject(Lists.newArrayList(spawnerMobs));
		}

		return null;
	}
}