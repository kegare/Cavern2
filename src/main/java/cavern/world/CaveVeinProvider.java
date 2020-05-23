package cavern.world;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cavern.block.BlockCave;
import cavern.block.CaveBlocks;
import cavern.config.manager.CaveVein;
import cavern.config.manager.CaveVeinManager;
import cavern.util.BlockMeta;
import cavern.util.CaveLog;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public class CaveVeinProvider
{
	private static final Map<BlockMeta, EnumRarity> RARITY_MAP = Maps.newHashMap();

	static
	{
		setRarity(Blocks.COAL_ORE.getDefaultState(), EnumRarity.COMMON);
		setRarity(Blocks.IRON_ORE.getDefaultState(), EnumRarity.COMMON);
		setRarity(Blocks.GOLD_ORE.getDefaultState(), EnumRarity.RARE);
		setRarity(Blocks.REDSTONE_ORE.getDefaultState(), EnumRarity.UNCOMMON);
		setRarity(Blocks.LAPIS_ORE.getDefaultState(), EnumRarity.RARE);
		setRarity(Blocks.EMERALD_ORE.getDefaultState(), EnumRarity.RARE);
		setRarity(Blocks.DIAMOND_ORE.getDefaultState(), EnumRarity.EPIC);
		setRarity(CaveBlocks.CAVE_BLOCK.getStateFromMeta(BlockCave.EnumType.AQUAMARINE_ORE.getMetadata()), EnumRarity.UNCOMMON);
		setRarity(CaveBlocks.CAVE_BLOCK.getStateFromMeta(BlockCave.EnumType.MAGNITE_ORE.getMetadata()), EnumRarity.COMMON);
		setRarity(CaveBlocks.CAVE_BLOCK.getStateFromMeta(BlockCave.EnumType.RANDOMITE_ORE.getMetadata()), EnumRarity.UNCOMMON);
		setRarity(CaveBlocks.CAVE_BLOCK.getStateFromMeta(BlockCave.EnumType.HEXCITE_ORE.getMetadata()), EnumRarity.RARE);
		setRarity(CaveBlocks.CAVE_BLOCK.getStateFromMeta(BlockCave.EnumType.FISSURED_STONE.getMetadata()), EnumRarity.UNCOMMON);
	}

	public static void setRarity(IBlockState state, EnumRarity rarity)
	{
		RARITY_MAP.put(new BlockMeta(state), rarity);
	}

	private final World world;
	private final Random rand;
	private final Supplier<CaveVeinManager> veinManager;

	private Pair<Long, List<CaveVein>> cachedVeins;

	public CaveVeinProvider(World world, Supplier<CaveVeinManager> manager)
	{
		this.world = world;
		this.rand = new Random();
		this.veinManager = manager;
	}

	@SuppressWarnings("deprecation")
	public List<CaveVein> getVeins()
	{
		CaveVeinManager manager = veinManager.get();

		if (manager != null)
		{
			return manager.getCaveVeins();
		}

		List<CaveVein> list;
		long time = world.getWorldInfo().getWorldTotalTime();

		if (cachedVeins == null || cachedVeins.getLeft().longValue() + 1200L < time)
		{
			list = Lists.newArrayList();
		}
		else
		{
			return cachedVeins.getRight();
		}

		int worldHeight = world.getActualHeight() - 1;

		Set<String> oreNames = Arrays.stream(OreDictionary.getOreNames())
			.filter(name -> name.startsWith("ore") && name.length() > 3 && Character.isUpperCase(name.charAt(3)))
			.sorted().collect(Collectors.toSet());

		for (String name : oreNames)
		{
			for (ItemStack stack : OreDictionary.getOres(name, false))
			{
				try
				{
					if (stack.isEmpty() || stack.getItem() == Items.AIR || !(stack.getItem() instanceof ItemBlock))
					{
						continue;
					}

					Block block = ((ItemBlock)stack.getItem()).getBlock();

					if (block == null || block instanceof BlockAir)
					{
						continue;
					}

					IBlockState state = block.getStateFromMeta(stack.getItemDamage());
					MapColor color = state.getMapColor(world, BlockPos.ORIGIN);

					if (color == MapColor.NETHERRACK)
					{
						continue;
					}

					BlockMeta blockMeta = new BlockMeta(state);
					EnumRarity rarity = RARITY_MAP.get(blockMeta);

					if (rarity == null)
					{
						String variant = name.substring(3).toLowerCase();

						int harvestLevel = block.getHarvestLevel(state);
						int level = harvestLevel;

						Item pickaxe = ForgeRegistries.ITEMS.getValue(new ResourceLocation(block.getRegistryName().getResourceDomain(), variant + "_pickaxe"));
						double toolRarity = 1.0D;

						if (pickaxe != null)
						{
							ItemStack dummy = new ItemStack(pickaxe);
							int max = pickaxe.getMaxDamage(dummy);
							float destroy = pickaxe.getDestroySpeed(dummy, Blocks.IRON_ORE.getDefaultState());
							int enchant = pickaxe.getItemEnchantability(dummy);
							int harvest = pickaxe.getHarvestLevel(dummy, "pickaxe", null, null);

							toolRarity = max * 0.01D + destroy * 0.001D + enchant * 0.01D + harvest * 1.0D;
						}
						else if (harvestLevel > 0)
						{
							++level;
						}

						if (toolRarity >= 5.0D)
						{
							level += MathHelper.ceil(toolRarity * 0.25D) - 3;
						}

						if (level > 3)
						{
							rarity = EnumRarity.EPIC;
						}
						else if (level > 2)
						{
							rarity = EnumRarity.RARE;
						}
						else if (level > 1)
						{
							rarity = EnumRarity.UNCOMMON;
						}

						rarity = EnumRarity.COMMON;

						RARITY_MAP.put(blockMeta, rarity);
					}

					int weight = 30;
					int size = 10;
					int min = 1;
					int max = worldHeight;

					switch (rarity)
					{
						case COMMON:
							weight = MathHelper.getInt(rand, 25, 35);
							size = MathHelper.getInt(rand, 10, 20);
							break;
						case UNCOMMON:
							weight = MathHelper.getInt(rand, 15, 25);
							size = MathHelper.getInt(rand, 7, 12);
							break;
						case RARE:
							weight = MathHelper.getInt(rand, 10, 20);
							size = MathHelper.getInt(rand, 5, 10);
							break;
						case EPIC:
							weight = MathHelper.getInt(rand, 1, 5);
							size = MathHelper.getInt(rand, 2, 7);
							max = 30;
							break;
					}

					list.add(new CaveVein(blockMeta, weight, size, min, max));
				}
				catch (Exception e)
				{
					CaveLog.log(Level.ERROR, "An error occurred while setup. Skip: {%s} %s", name, stack.toString());
				}
			}
		}

		oreNames = Arrays.stream(OreDictionary.getOreNames())
			.filter(name -> name.startsWith("stone") && name.length() > 5 && Character.isUpperCase(name.charAt(5)) && !name.endsWith("Polished"))
			.sorted().collect(Collectors.toSet());

		oreNames.add("dirt");
		oreNames.add("gravel");

		for (String name : oreNames)
		{
			for (ItemStack stack : OreDictionary.getOres(name, false))
			{
				try
				{
					if (stack.isEmpty() || stack.getItem() == Items.AIR || !(stack.getItem() instanceof ItemBlock))
					{
						continue;
					}

					Block block = ((ItemBlock)stack.getItem()).getBlock();

					if (block == null || block instanceof BlockAir)
					{
						continue;
					}

					IBlockState state = block.getStateFromMeta(stack.getItemDamage());
					int weight = MathHelper.getInt(rand, 25, 40);
					int size = MathHelper.getInt(rand, 10, 30);

					list.add(new CaveVein(new BlockMeta(state), weight, size, 1, worldHeight));
				}
				catch (Exception e)
				{
					CaveLog.log(Level.ERROR, "An error occurred while setup. Skip: {%s} %s", name, stack.toString());
				}
			}
		}

		cachedVeins = Pair.of(time, list);

		return list;
	}
}