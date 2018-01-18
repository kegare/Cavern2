package cavern.handler.api;

import java.util.Set;

import cavern.api.ICavernAPI;
import cavern.api.IFissureBreakEvent;
import cavern.api.IIceEquipment;
import cavern.api.IMineBonus;
import cavern.block.BlockCave;
import cavern.block.event.FissureBreakEvent;
import cavern.block.event.FissureEventExplosion;
import cavern.block.event.FissureEventPotion;
import cavern.item.CaveItems;
import cavern.item.ItemCave;
import cavern.stats.MinerStats;
import cavern.util.WeightedItemStack;
import cavern.world.mirage.WorldProviderCaveland;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CavernAPIHandler implements ICavernAPI
{
	@SideOnly(Side.CLIENT)
	@Override
	public int getMineCombo()
	{
		return MinerStats.mineCombo;
	}

	@Override
	public Set<IMineBonus> getMineBonus()
	{
		return MinerStats.MINE_BONUS;
	}

	@Override
	public void addMineBonus(IMineBonus bonus)
	{
		MinerStats.MINE_BONUS.add(bonus);
	}

	@Override
	public void addRandomiteItem(ItemStack item, int weight)
	{
		BlockCave.RANDOMITE_ITEMS.add(new WeightedItemStack(item, weight));
	}

	@Override
	public void addRandomiteItem(Item item, int weight)
	{
		addRandomiteItem(new ItemStack(item), weight);
	}

	@Override
	public void addRandomiteItem(Item item, int amount, int weight)
	{
		addRandomiteItem(new ItemStack(item, amount), weight);
	}

	@Override
	public void addRandomiteItem(Block block, int weight)
	{
		addRandomiteItem(new ItemStack(block), weight);
	}

	@Override
	public void addRandomiteItem(Block block, int amount, int weight)
	{
		addRandomiteItem(new ItemStack(block, amount), weight);
	}

	@Override
	public void addHibernateItem(ItemStack item, int weight)
	{
		WorldProviderCaveland.HIBERNATE_ITEMS.add(new WeightedItemStack(item, weight));
	}

	@Override
	public void addHibernateItem(Item item, int weight)
	{
		addHibernateItem(new ItemStack(item), weight);
	}

	@Override
	public void addHibernateItem(Item item, int amount, int weight)
	{
		addHibernateItem(new ItemStack(item, amount), weight);
	}

	@Override
	public void addHibernateItem(Block block, int weight)
	{
		addHibernateItem(new ItemStack(block), weight);
	}

	@Override
	public void addHibernateItem(Block block, int amount, int weight)
	{
		addHibernateItem(new ItemStack(block, amount), weight);
	}

	@Override
	public void addFissureBreakEvent(IFissureBreakEvent event, int weight)
	{
		BlockCave.FISSURE_EVENTS.add(new FissureBreakEvent(event, weight));
	}

	public static void registerItems(ICavernAPI handler)
	{
		handler.addRandomiteItem(Blocks.DIRT, 6, 15);
		handler.addRandomiteItem(Blocks.SAND, 6, 12);
		handler.addRandomiteItem(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.OAK.getMetadata()), 15);
		handler.addRandomiteItem(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.SPRUCE.getMetadata()), 15);
		handler.addRandomiteItem(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.BIRCH.getMetadata()), 15);
		handler.addRandomiteItem(new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.OAK.getMetadata()), 8);
		handler.addRandomiteItem(new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.SPRUCE.getMetadata()), 8);
		handler.addRandomiteItem(new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.BIRCH.getMetadata()), 8);
		handler.addRandomiteItem(Blocks.TORCH, 5, 30);
		handler.addRandomiteItem(Items.COAL, 5, 20);
		handler.addRandomiteItem(Items.IRON_INGOT, 20);
		handler.addRandomiteItem(Items.GOLD_INGOT, 10);
		handler.addRandomiteItem(Items.EMERALD, 10);
		handler.addRandomiteItem(Items.APPLE, 3, 25);
		handler.addRandomiteItem(Items.BAKED_POTATO, 3, 20);
		handler.addRandomiteItem(Items.BREAD, 2, 18);
		handler.addRandomiteItem(Items.COOKED_BEEF, 15);
		handler.addRandomiteItem(Items.COOKED_CHICKEN, 15);
		handler.addRandomiteItem(Items.COOKED_FISH, 15);
		handler.addRandomiteItem(Items.COOKED_MUTTON, 15);
		handler.addRandomiteItem(Items.COOKED_PORKCHOP, 15);
		handler.addRandomiteItem(Items.COOKED_RABBIT, 15);
		handler.addRandomiteItem(Items.BONE, 5, 30);
		handler.addRandomiteItem(Items.IRON_SWORD, 8);
		handler.addRandomiteItem(Items.IRON_PICKAXE, 10);
		handler.addRandomiteItem(Items.IRON_AXE, 10);
		handler.addRandomiteItem(Items.IRON_SHOVEL, 10);
		handler.addRandomiteItem(Items.IRON_HOE, 8);
		handler.addRandomiteItem(Items.DIAMOND, 2);
		handler.addRandomiteItem(Items.DIAMOND_SWORD, 1);
		handler.addRandomiteItem(Items.DIAMOND_PICKAXE, 1);
		handler.addRandomiteItem(Items.DIAMOND_AXE, 1);
		handler.addRandomiteItem(Items.DIAMOND_SHOVEL, 1);
		handler.addRandomiteItem(Items.DIAMOND_HOE, 1);
		handler.addRandomiteItem(ItemCave.EnumType.MINER_ORB.getItemStack(), 1);

		handler.addHibernateItem(ItemCave.EnumType.ICE_STICK.getItemStack(8), 30);
		handler.addHibernateItem(IIceEquipment.getChargedItem(CaveItems.ICE_SWORD, 20), 10);
		handler.addHibernateItem(IIceEquipment.getChargedItem(CaveItems.ICE_PICKAXE, 30), 10);
		handler.addHibernateItem(IIceEquipment.getChargedItem(CaveItems.ICE_AXE, 30), 10);
		handler.addHibernateItem(IIceEquipment.getChargedItem(CaveItems.ICE_SHOVEL, 10), 10);
		handler.addHibernateItem(IIceEquipment.getChargedItem(CaveItems.ICE_HOE, 20), 10);
		handler.addHibernateItem(Items.BONE, 6, 30);
		handler.addHibernateItem(Items.FISH, 4, 30);
		handler.addHibernateItem(Items.BEEF, 2, 15);
		handler.addHibernateItem(Items.CHICKEN, 2, 15);
		handler.addHibernateItem(Items.MUTTON, 2, 15);
		handler.addHibernateItem(Items.PORKCHOP, 2, 15);
		handler.addHibernateItem(Items.RABBIT, 2, 15);
		handler.addHibernateItem(Items.APPLE, 15);
		handler.addHibernateItem(Items.GOLDEN_APPLE, 5);
		handler.addHibernateItem(Items.PUMPKIN_PIE, 15);
		handler.addHibernateItem(Blocks.REEDS, 4, 12);
		handler.addHibernateItem(Items.STICK, 4, 15);
		handler.addHibernateItem(Items.COAL, 4, 15);
		handler.addHibernateItem(ItemCave.EnumType.AQUAMARINE.getItemStack(), 10);
		handler.addHibernateItem(ItemCave.EnumType.HEXCITE.getItemStack(), 5);
	}

	public static void registerEvents(ICavernAPI handler)
	{
		handler.addFissureBreakEvent(new FissureEventPotion(), 100);
		handler.addFissureBreakEvent(new FissureEventExplosion(), 10);
	}
}