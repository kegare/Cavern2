package cavern.item;

import cavern.core.Cavern;
import net.minecraft.item.ItemSword;

public class ItemSwordCave extends ItemSword
{
	protected final ToolMaterial toolMaterial;

	public ItemSwordCave(ToolMaterial material, String name)
	{
		super(material);
		this.toolMaterial = material;
		this.setUnlocalizedName(name);
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}

	public ToolMaterial getToolMaterial()
	{
		return toolMaterial;
	}
}