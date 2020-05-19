package cavern.client.gui;

import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ISelectorCallback<T>
{
	default boolean isValidEntry(T entry)
	{
		return entry != null;
	}

	void onSelected(List<T> selected);
}