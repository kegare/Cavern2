package cavern.item;

import cavern.core.Cavern;
import net.minecraft.item.ItemSpade;

public class ItemShovelCave extends ItemSpade
{
	public ItemShovelCave(ToolMaterial material, String name)
	{
		super(material);
		this.setUnlocalizedName(name);
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}
}