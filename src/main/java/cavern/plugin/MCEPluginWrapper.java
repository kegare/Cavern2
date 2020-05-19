package cavern.plugin;

import cavern.block.BlockCave;
import cavern.block.CaveBlocks;
import cavern.data.MinerRank;
import cavern.entity.monster.EntityCaveman;
import cavern.entity.monster.EntityCavenicBear;
import cavern.entity.monster.EntityCavenicCreeper;
import cavern.entity.monster.EntityCavenicSkeleton;
import cavern.entity.monster.EntityCavenicSpider;
import cavern.entity.monster.EntityCavenicWitch;
import cavern.entity.monster.EntityCavenicZombie;
import cavern.entity.monster.EntityCrazyCreeper;
import cavern.entity.monster.EntityCrazySkeleton;
import cavern.entity.monster.EntityCrazySpider;
import cavern.entity.monster.EntityCrazyZombie;
import cavern.item.CaveItems;
import cavern.item.ItemAcresia;
import cavern.item.ItemCave;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import shift.mceconomy3.api.MCEconomyAPI;

public class MCEPluginWrapper
{
	public static final MCEPortalShop PORTAL = new MCEPortalShop();

	public static void registerShops()
	{
		PORTAL.addProduct(new ItemStack(Blocks.TORCH, 16), 20);
		PORTAL.addProduct(new ItemStack(Items.BREAD, 3), 30, MinerRank.STONE_MINER);
		PORTAL.addProduct(new ItemStack(Items.BONE, 2), 10);

		for (int i = 0; i < 3; ++i)
		{
			PORTAL.addProduct(new ItemStack(Blocks.LOG, 1, i), 30);
		}

		MCEPlugin.PORTAL_SHOP = MCEconomyAPI.registerShop(PORTAL);
	}

	public static void registerPurchaseItems()
	{
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.CAVERN_PORTAL), -1);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.HUGE_CAVERN_PORTAL), -1);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.AQUA_CAVERN_PORTAL), -1);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.MIRAGE_PORTAL), -1);
		MCEconomyAPI.addPurchaseItem(BlockCave.EnumType.AQUAMARINE_ORE.getItemStack(), 60);
		MCEconomyAPI.addPurchaseItem(BlockCave.EnumType.AQUAMARINE_BLOCK.getItemStack(), 315);
		MCEconomyAPI.addPurchaseItem(BlockCave.EnumType.MAGNITE_ORE.getItemStack(), 50);
		MCEconomyAPI.addPurchaseItem(BlockCave.EnumType.MAGNITE_BLOCK.getItemStack(), 900);
		MCEconomyAPI.addPurchaseItem(BlockCave.EnumType.RANDOMITE_ORE.getItemStack(), 75);
		MCEconomyAPI.addPurchaseItem(BlockCave.EnumType.HEXCITE_ORE.getItemStack(), 2200);
		MCEconomyAPI.addPurchaseItem(BlockCave.EnumType.HEXCITE_BLOCK.getItemStack(), 9900);
		MCEconomyAPI.addPurchaseItem(BlockCave.EnumType.FISSURED_STONE.getItemStack(), 75);
		MCEconomyAPI.addPurchaseItem(ItemAcresia.EnumType.SEEDS.getItemStack(), 0);
		MCEconomyAPI.addPurchaseItem(ItemAcresia.EnumType.FRUITS.getItemStack(), 1);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.PERVERTED_LOG, 1, OreDictionary.WILDCARD_VALUE), 1);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.PERVERTED_LEAVES, 1, OreDictionary.WILDCARD_VALUE), 0);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.PERVERTED_SAPLING, 1, OreDictionary.WILDCARD_VALUE), 0);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.SLIPPERY_ICE), 110);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.MIRAGE_BOOKSHELF), -1);

		MCEconomyAPI.addPurchaseItem(ItemCave.EnumType.AQUAMARINE.getItemStack(), 35);
		MCEconomyAPI.addPurchaseItem(ItemCave.EnumType.MAGNITE_INGOT.getItemStack(), 100);
		MCEconomyAPI.addPurchaseItem(ItemCave.EnumType.HEXCITE.getItemStack(), 1100);
		MCEconomyAPI.addPurchaseItem(ItemCave.EnumType.ICE_STICK.getItemStack(), 2);
		MCEconomyAPI.addPurchaseItem(ItemCave.EnumType.MINER_ORB.getItemStack(), 3000);
		MCEconomyAPI.addPurchaseItem(ItemCave.EnumType.CAVENIC_ORB.getItemStack(), 500);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.AQUAMARINE_PICKAXE), 110);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.AQUAMARINE_AXE), 110);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.AQUAMARINE_SHOVEL), 40);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.MAGNITE_SWORD), 205);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.MAGNITE_PICKAXE), 305);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.MAGNITE_AXE), 305);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.MAGNITE_SHOVEL), 105);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.HEXCITE_SWORD), 2205);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.HEXCITE_PICKAXE), 3305);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.HEXCITE_AXE), 3305);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.HEXCITE_SHOVEL), 1105);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.HEXCITE_HOE), 2205);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.HEXCITE_HELMET), 5505);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.HEXCITE_CHESTPLATE), 8805);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.HEXCITE_LEGGINGS), 7705);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.HEXCITE_BOOTS), 4405);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.ICE_SWORD), 135);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.ICE_PICKAXE), 200);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.ICE_AXE), 200);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.ICE_SHOVEL), 70);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.ICE_HOE), 135);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.ICE_BOW), 30);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.CAVENIC_SWORD), 1005);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.CAVENIC_AXE), 1505);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.CAVENIC_BOW), 2000);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.ORE_COMPASS), 3000);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.MIRAGE_BOOK, 1, OreDictionary.WILDCARD_VALUE), 3000);

		MCEconomyAPI.SHOP_MANAGER.addPurchaseEntity(EntityCavenicSkeleton.class, 40);
		MCEconomyAPI.SHOP_MANAGER.addPurchaseEntity(EntityCavenicCreeper.class, 35);
		MCEconomyAPI.SHOP_MANAGER.addPurchaseEntity(EntityCavenicZombie.class, 35);
		MCEconomyAPI.SHOP_MANAGER.addPurchaseEntity(EntityCavenicSpider.class, 30);
		MCEconomyAPI.SHOP_MANAGER.addPurchaseEntity(EntityCavenicWitch.class, 35);
		MCEconomyAPI.SHOP_MANAGER.addPurchaseEntity(EntityCavenicBear.class, 35);
		MCEconomyAPI.SHOP_MANAGER.addPurchaseEntity(EntityCrazySkeleton.class, 800);
		MCEconomyAPI.SHOP_MANAGER.addPurchaseEntity(EntityCrazyCreeper.class, 700);
		MCEconomyAPI.SHOP_MANAGER.addPurchaseEntity(EntityCrazyZombie.class, 700);
		MCEconomyAPI.SHOP_MANAGER.addPurchaseEntity(EntityCrazySpider.class, 600);
		MCEconomyAPI.SHOP_MANAGER.addPurchaseEntity(EntityCaveman.class, 15);
	}
}