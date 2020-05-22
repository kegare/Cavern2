package cavern.world;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public class CustomHeightData
{
	private Integer height;

	public CustomHeightData(@Nullable NBTTagCompound nbt)
	{
		if (nbt != null && nbt.hasKey("Height", NBT.TAG_ANY_NUMERIC))
		{
			height = nbt.getInteger("Height");
		}
	}

	public NBTTagCompound getCompound(@Nullable NBTTagCompound nbt)
	{
		if (height == null)
		{
			return nbt;
		}

		if (nbt == null)
		{
			nbt = new NBTTagCompound();
		}

		nbt.setLong("Height", height.intValue());

		return nbt;
	}

	public int getHeight(int customHeight)
	{
		if (height == null)
		{
			setHeight(customHeight);
		}

		return height;
	}

	public void setHeight(int newHeight)
	{
		height = newHeight;
	}
}