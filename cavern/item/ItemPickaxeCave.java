package cavern.item;

import cavern.core.Cavern;
import net.minecraft.item.ItemPickaxe;

public class ItemPickaxeCave extends ItemPickaxe
{
	public ItemPickaxeCave(ToolMaterial material, String name)
	{
		super(material);
		this.setTranslationKey(name);
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}
}