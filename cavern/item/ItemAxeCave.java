package cavern.item;

import cavern.core.Cavern;
import net.minecraft.item.ItemAxe;

public class ItemAxeCave extends ItemAxe
{
	public ItemAxeCave(ToolMaterial material, float damage, float speed, String name)
	{
		super(material, damage, speed);
		this.setTranslationKey(name);
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}
}