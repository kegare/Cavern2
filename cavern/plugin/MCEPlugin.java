package cavern.plugin;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Method;
import shift.mceconomy3.api.MCEconomyAPI;
import shift.mceconomy3.api.shop.IShop;

public class MCEPlugin
{
	public static final String MODID = "mceconomy3";

	public static int PORTAL_SHOP = -1;

	@Method(modid = MODID)
	public static void load()
	{
		MCEPluginWrapper.registerPurchaseItems();
		MCEPluginWrapper.registerShops();
	}

	@Method(modid = MODID)
	public static boolean openShop(@Nullable IShop shop, World world, EntityPlayer player, BlockPos pos)
	{
		if (shop != null)
		{
			MCEconomyAPI.openShopGui(shop, player, world, pos.getX(), pos.getY(), pos.getZ());

			return true;
		}

		return false;
	}

	@Method(modid = MODID)
	@Nullable
	public static IShop getPortalShop()
	{
		return PORTAL_SHOP < 0 ? null : MCEPluginWrapper.PORTAL;
	}
}