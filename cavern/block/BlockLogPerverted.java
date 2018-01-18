package cavern.block;

import cavern.core.Cavern;
import net.minecraft.block.BlockOldLog;

public class BlockLogPerverted extends BlockOldLog
{
	public BlockLogPerverted()
	{
		super();
		this.setUnlocalizedName("pervertedLog");
		this.setHardness(1.2F);
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}
}