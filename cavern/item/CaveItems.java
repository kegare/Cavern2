package cavern.item;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.collect.Lists;

import cavern.recipe.RecipeChargeIceEquipment;
import cavern.util.CaveUtils;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

public final class CaveItems
{
	public static final ToolMaterial AQUAMARINE = EnumHelper.addToolMaterial("AQUAMARINE", 2, 200, 8.0F, 1.5F, 15);
	public static final ToolMaterial MAGNITE = EnumHelper.addToolMaterial("MAGNITE", 3, 10, 100.0F, 11.0F, 50);
	public static final ToolMaterial HEXCITE = EnumHelper.addToolMaterial("HEXCITE", 3, 1041, 10.0F, 5.0F, 15);
	public static final ToolMaterial ICE = EnumHelper.addToolMaterial("ICE", 1, 120, 5.0F, 1.0F, 0);
	public static final ToolMaterial CAVENIC = EnumHelper.addToolMaterial("CAVENIC", 2, 278, 7.0F, 2.5F, 30);

	public static final ArmorMaterial HEXCITE_ARMOR = EnumHelper.addArmorMaterial("HEXCITE", "hexcite", 22,
		new int[] {4, 7, 9, 4}, 15, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 1.0F);

	public static final Item CAVE_ITEM = new ItemCave();
	public static final ItemPickaxeAquamarine AQUAMARINE_PICKAXE = new ItemPickaxeAquamarine();
	public static final ItemAxeAquamarine AQUAMARINE_AXE = new ItemAxeAquamarine();
	public static final ItemShovelAquamarine AQUAMARINE_SHOVEL = new ItemShovelAquamarine();
	public static final ItemSwordCave MAGNITE_SWORD = new ItemSwordCave(MAGNITE, "swordMagnite");
	public static final ItemPickaxeCave MAGNITE_PICKAXE = new ItemPickaxeCave(MAGNITE, "pickaxeMagnite");
	public static final ItemAxeCave MAGNITE_AXE = new ItemAxeCave(MAGNITE, 18.0F, -3.0F, "axeMagnite");
	public static final ItemShovelCave MAGNITE_SHOVEL = new ItemShovelCave(MAGNITE, "shovelMagnite");
	public static final ItemSwordCave HEXCITE_SWORD = new ItemSwordCave(HEXCITE, "swordHexcite");
	public static final ItemPickaxeCave HEXCITE_PICKAXE = new ItemPickaxeCave(HEXCITE, "pickaxeHexcite");
	public static final ItemAxeCave HEXCITE_AXE = new ItemAxeCave(HEXCITE, 10.0F, -2.8F, "axeHexcite");
	public static final ItemShovelCave HEXCITE_SHOVEL = new ItemShovelCave(HEXCITE, "shovelHexcite");
	public static final ItemHoeCave HEXCITE_HOE = new ItemHoeCave(HEXCITE, "hoeHexcite");
	public static final ItemArmorCave HEXCITE_HELMET = new ItemArmorCave(HEXCITE_ARMOR, "helmetHexcite", "hexcite", EntityEquipmentSlot.HEAD);
	public static final ItemArmorCave HEXCITE_CHESTPLATE = new ItemArmorCave(HEXCITE_ARMOR, "chestplateHexcite", "hexcite", EntityEquipmentSlot.CHEST);
	public static final ItemArmorCave HEXCITE_LEGGINGS = new ItemArmorCave(HEXCITE_ARMOR, "leggingsHexcite", "hexcite", EntityEquipmentSlot.LEGS);
	public static final ItemArmorCave HEXCITE_BOOTS = new ItemArmorCave(HEXCITE_ARMOR, "bootsHexcite", "hexcite", EntityEquipmentSlot.FEET);
	public static final ItemSwordIce ICE_SWORD = new ItemSwordIce();
	public static final ItemPickaxeIce ICE_PICKAXE = new ItemPickaxeIce();
	public static final ItemAxeIce ICE_AXE = new ItemAxeIce();
	public static final ItemShovelIce ICE_SHOVEL = new ItemShovelIce();
	public static final ItemHoeIce ICE_HOE = new ItemHoeIce();
	public static final ItemBowIce ICE_BOW = new ItemBowIce();
	public static final ItemSwordCavenic CAVENIC_SWORD = new ItemSwordCavenic();
	public static final ItemAxeCavenic CAVENIC_AXE = new ItemAxeCavenic();
	public static final ItemBowCavenic CAVENIC_BOW = new ItemBowCavenic();
	public static final ItemOreCompass ORE_COMPASS = new ItemOreCompass();
	public static final ItemMirageBook MIRAGE_BOOK = new ItemMirageBook();
	public static final ItemMagicBook MAGIC_BOOK = new ItemMagicBook();

	public static void registerItems(IForgeRegistry<Item> registry)
	{
		registry.register(CAVE_ITEM.setRegistryName("cave_item"));
		registry.register(AQUAMARINE_PICKAXE.setRegistryName("aquamarine_pickaxe"));
		registry.register(AQUAMARINE_AXE.setRegistryName("aquamarine_axe"));
		registry.register(AQUAMARINE_SHOVEL.setRegistryName("aquamarine_shovel"));
		registry.register(MAGNITE_SWORD.setRegistryName("magnite_sword"));
		registry.register(MAGNITE_PICKAXE.setRegistryName("magnite_pickaxe"));
		registry.register(MAGNITE_AXE.setRegistryName("magnite_axe"));
		registry.register(MAGNITE_SHOVEL.setRegistryName("magnite_shovel"));
		registry.register(HEXCITE_SWORD.setRegistryName("hexcite_sword"));
		registry.register(HEXCITE_PICKAXE.setRegistryName("hexcite_pickaxe"));
		registry.register(HEXCITE_AXE.setRegistryName("hexcite_axe"));
		registry.register(HEXCITE_SHOVEL.setRegistryName("hexcite_shovel"));
		registry.register(HEXCITE_HOE.setRegistryName("hexcite_hoe"));
		registry.register(HEXCITE_HELMET.setRegistryName("hexcite_helmet"));
		registry.register(HEXCITE_CHESTPLATE.setRegistryName("hexcite_chestplate"));
		registry.register(HEXCITE_LEGGINGS.setRegistryName("hexcite_leggings"));
		registry.register(HEXCITE_BOOTS.setRegistryName("hexcite_boots"));
		registry.register(ICE_SWORD.setRegistryName("ice_sword"));
		registry.register(ICE_PICKAXE.setRegistryName("ice_pickaxe"));
		registry.register(ICE_AXE.setRegistryName("ice_axe"));
		registry.register(ICE_SHOVEL.setRegistryName("ice_shovel"));
		registry.register(ICE_HOE.setRegistryName("ice_hoe"));
		registry.register(ICE_BOW.setRegistryName("ice_bow"));
		registry.register(CAVENIC_SWORD.setRegistryName("cavenic_sword"));
		registry.register(CAVENIC_AXE.setRegistryName("cavenic_axe"));
		registry.register(CAVENIC_BOW.setRegistryName("cavenic_bow"));
		registry.register(ORE_COMPASS.setRegistryName("ore_compass"));
		registry.register(MIRAGE_BOOK.setRegistryName("mirage_book"));
		registry.register(MAGIC_BOOK.setRegistryName("magic_book"));
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels()
	{
		registerModels(CAVE_ITEM, "aquamarine", "magnite_ingot", "hexcite", "ice_stick", "miner_orb", "cavenic_orb");
		registerModel(AQUAMARINE_PICKAXE);
		registerModel(AQUAMARINE_AXE);
		registerModel(AQUAMARINE_SHOVEL);
		registerModel(MAGNITE_SWORD);
		registerModel(MAGNITE_PICKAXE);
		registerModel(MAGNITE_AXE);
		registerModel(MAGNITE_SHOVEL);
		registerModel(HEXCITE_SWORD);
		registerModel(HEXCITE_PICKAXE);
		registerModel(HEXCITE_AXE);
		registerModel(HEXCITE_SHOVEL);
		registerModel(HEXCITE_HOE);
		registerModel(HEXCITE_HELMET);
		registerModel(HEXCITE_CHESTPLATE);
		registerModel(HEXCITE_LEGGINGS);
		registerModel(HEXCITE_BOOTS);
		registerModel(ICE_SWORD);
		registerModel(ICE_PICKAXE);
		registerModel(ICE_AXE);
		registerModel(ICE_SHOVEL);
		registerModel(ICE_HOE);
		registerModel(ICE_BOW);
		registerModel(CAVENIC_SWORD);
		registerModel(CAVENIC_AXE);
		registerModel(CAVENIC_BOW);
		registerModel(ORE_COMPASS);
		registerModels("mirage_book_", MIRAGE_BOOK, "caveland", "cavenia", "frost_mountains", "wide_desert", "the_void", "dark_forest", "crown_cliffs", "skyland");
		registerModels("magic_book_", MAGIC_BOOK, "storage", "heal", "warp", "teleport", "torch", "invisible", "summon", "explosion", "thunderbolt", "infinity", "overload", "physical_boost");
	}

	@SideOnly(Side.CLIENT)
	public static void registerModel(Item item)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

	@SideOnly(Side.CLIENT)
	public static void registerModel(Item item, String modelName)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(CaveUtils.getKey(modelName), "inventory"));
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels(Item item, String... modelNames)
	{
		registerModels(null, item, modelNames);
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels(@Nullable String prefix, Item item, String... modelNames)
	{
		List<ModelResourceLocation> models = Lists.newArrayList();

		for (String model : modelNames)
		{
			models.add(new ModelResourceLocation(CaveUtils.getKey(ObjectUtils.defaultIfNull(prefix, "") + model), "inventory"));
		}

		ModelBakery.registerItemVariants(item, models.toArray(new ResourceLocation[models.size()]));

		for (int i = 0, size = models.size(); i < size; ++i)
		{
			ModelLoader.setCustomModelResourceLocation(item, i, models.get(i));
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerVanillaModel(Item item, String modelName)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("minecraft:" + modelName, "inventory"));
	}

	@SideOnly(Side.CLIENT)
	public static void registerVanillaModels(Item item, String... modelNames)
	{
		List<ModelResourceLocation> models = Lists.newArrayList();

		for (String model : modelNames)
		{
			models.add(new ModelResourceLocation("minecraft:" + model, "inventory"));
		}

		ModelBakery.registerItemVariants(item, models.toArray(new ResourceLocation[models.size()]));

		for (int i = 0, size = models.size(); i < size; ++i)
		{
			ModelLoader.setCustomModelResourceLocation(item, i, models.get(i));
		}
	}

	public static void registerOreDicts()
	{
		OreDictionary.registerOre("gemAquamarine", ItemCave.EnumType.AQUAMARINE.getItemStack());
		OreDictionary.registerOre("ingotMagnite", ItemCave.EnumType.MAGNITE_INGOT.getItemStack());
		OreDictionary.registerOre("gemHexcite", ItemCave.EnumType.HEXCITE.getItemStack());
		OreDictionary.registerOre("stickIce", ItemCave.EnumType.ICE_STICK.getItemStack());
		OreDictionary.registerOre("orbMiner", ItemCave.EnumType.MINER_ORB.getItemStack());
		OreDictionary.registerOre("orbCavenic", ItemCave.EnumType.CAVENIC_ORB.getItemStack());
	}

	public static void registerEquipments()
	{
		AQUAMARINE.setRepairItem(ItemCave.EnumType.AQUAMARINE.getItemStack());
		MAGNITE.setRepairItem(ItemCave.EnumType.MAGNITE_INGOT.getItemStack());
		HEXCITE.setRepairItem(ItemCave.EnumType.HEXCITE.getItemStack());
		ICE.setRepairItem(new ItemStack(Blocks.PACKED_ICE));
	}

	public static void registerRecipes(IForgeRegistry<IRecipe> registry)
	{
		registry.register(new RecipeChargeIceEquipment().setRegistryName("charge_ice_equip"));
	}
}