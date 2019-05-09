package cavern.item;

import cavern.core.Cavern;
import net.minecraft.item.ItemHoe;

public class ItemHoeCave extends ItemHoe
{
	public ItemHoeCave(ToolMaterial material, String name)
	{
		super(material);
		this.setTranslationKey(name);
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}
}