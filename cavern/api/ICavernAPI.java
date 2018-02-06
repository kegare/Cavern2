package cavern.api;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICavernAPI
{
	@SideOnly(Side.CLIENT)
	int getMineCombo();

	Set<IMineBonus> getMineBonus();

	void addMineBonus(IMineBonus bonus);

	void addHibernateItem(ItemStack item, int weight);

	void addHibernateItem(Item item, int weight);

	void addHibernateItem(Item item, int amount, int weight);

	void addHibernateItem(Block block, int weight);

	void addHibernateItem(Block block, int amount, int weight);
}