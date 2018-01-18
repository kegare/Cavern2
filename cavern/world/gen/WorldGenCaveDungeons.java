package cavern.world.gen;

import java.util.Collection;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import cavern.util.CaveLog;
import cavern.util.CaveUtils;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.storage.loot.LootTableList;

public class WorldGenCaveDungeons extends WorldGenDungeons
{
	private final Collection<ResourceLocation> spawnerMobs;

	public WorldGenCaveDungeons(Collection<ResourceLocation> mobs)
	{
		this.spawnerMobs = mobs;
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos)
	{
		int i1 = rand.nextInt(2) + 2;
		int j1 = -i1 - 1;
		int k1 = i1 + 1;
		int i2 = rand.nextInt(2) + 2;
		int j2 = -i2 - 1;
		int k2 = i2 + 1;
		int count = 0;

		for (int x = j1; x <= k1; ++x)
		{
			for (int y = -1; y <= 4; ++y)
			{
				for (int z = j2; z <= k2; ++z)
				{
					BlockPos blockpos = pos.add(x, y, z);
					Material material = world.getBlockState(blockpos).getMaterial();
					boolean flag = material.isSolid();

					if (y == -1 && !flag)
					{
						return false;
					}

					if (y == 4 && !flag)
					{
						return false;
					}

					if ((x == j1 || x == k1 || z == j2 || z == k2) && y == 0 && world.isAirBlock(blockpos) && world.isAirBlock(blockpos.up()))
					{
						++count;
					}
				}
			}
		}

		if (count >= 1 && count <= 5)
		{
			int type = rand.nextInt(2);
			IBlockState state1;
			IBlockState state2;

			switch (type)
			{
				case 1:
					state1 = Blocks.STONEBRICK.getDefaultState();
					state2 = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY);
				default:
					state1 = Blocks.COBBLESTONE.getDefaultState();
					state2 = Blocks.MOSSY_COBBLESTONE.getDefaultState();
			}

			for (int x = j1; x <= k1; ++x)
			{
				for (int y = 3; y >= -1; --y)
				{
					for (int z = j2; z <= k2; ++z)
					{
						BlockPos blockpos = pos.add(x, y, z);

						if (x != j1 && y != -1 && z != j2 && x != k1 && y != 4 && z != k2)
						{
							if (world.getBlockState(blockpos).getBlock() != Blocks.CHEST)
							{
								world.setBlockToAir(blockpos);
							}
						}
						else if (blockpos.getY() >= 0 && !world.getBlockState(blockpos.down()).getMaterial().isSolid())
						{
							world.setBlockToAir(blockpos);
						}
						else if (world.getBlockState(blockpos).getMaterial().isSolid() && world.getBlockState(blockpos).getBlock() != Blocks.CHEST)
						{
							if (y == -1 && rand.nextInt(4) != 0)
							{
								world.setBlockState(blockpos, state2, 2);
							}
							else
							{
								world.setBlockState(blockpos, state1, 2);
							}
						}
					}
				}
			}

			for (int i = 0; i < 2; ++i)
			{
				for (int j = 0; j < 3; ++j)
				{
					int x = pos.getX() + rand.nextInt(i1 * 2 + 1) - i1;
					int y = pos.getY();
					int z = pos.getZ() + rand.nextInt(i2 * 2 + 1) - i2;
					BlockPos blockpos = new BlockPos(x, y, z);

					if (world.isAirBlock(blockpos))
					{
						count = 0;

						for (EnumFacing face : EnumFacing.Plane.HORIZONTAL)
						{
							if (world.getBlockState(blockpos.offset(face)).getMaterial().isSolid())
							{
								++count;
							}
						}

						if (count == 1)
						{
							world.setBlockState(blockpos, Blocks.CHEST.correctFacing(world, blockpos, Blocks.CHEST.getDefaultState()), 2);

							TileEntity tile = world.getTileEntity(blockpos);

							if (tile != null && tile instanceof TileEntityChest)
							{
								((TileEntityChest)tile).setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, rand.nextLong());
							}

							break;
						}
					}
				}
			}

			world.setBlockState(pos, Blocks.MOB_SPAWNER.getDefaultState(), 2);

			TileEntity tile = world.getTileEntity(pos);

			if (tile != null && tile instanceof TileEntityMobSpawner)
			{
				((TileEntityMobSpawner)tile).getSpawnerBaseLogic().setEntityId(pickMobSpawner(rand));
			}
			else
			{
				CaveLog.warning("Failed to fetch mob spawner entity at (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")");
			}

			return true;
		}

		return false;
	}

	@Nullable
	public ResourceLocation pickMobSpawner(Random random)
	{
		if (!spawnerMobs.isEmpty())
		{
			return CaveUtils.getRandomObject(Lists.newArrayList(spawnerMobs));
		}

		return null;
	}
}