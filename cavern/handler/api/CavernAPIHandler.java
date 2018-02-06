package cavern.handler.api;

import java.util.Set;

import cavern.api.ICavernAPI;
import cavern.api.IIceEquipment;
import cavern.api.IMineBonus;
import cavern.item.CaveItems;
import cavern.item.ItemCave;
import cavern.stats.MinerStats;
import cavern.util.WeightedItemStack;
import cavern.world.mirage.WorldProviderCaveland;
import net.minecraft.block.Block;
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

	public static void registerItems(ICavernAPI handler)
	{
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
}