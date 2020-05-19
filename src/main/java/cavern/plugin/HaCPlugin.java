package cavern.plugin;

import cavern.block.CaveBlocks;
import cavern.entity.monster.EntityCaveman;
import cavern.entity.monster.EntityCavenicBear;
import cavern.entity.monster.EntityCavenicCreeper;
import cavern.entity.monster.EntityCavenicSkeleton;
import cavern.entity.monster.EntityCavenicSpider;
import cavern.entity.monster.EntityCavenicWitch;
import cavern.entity.monster.EntityCavenicZombie;
import cavern.item.CaveItems;
import cavern.world.CaveDimensions;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.damage.DamageAPI;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.Optional.Method;

public class HaCPlugin
{
	public static final String LIB_MODID = "dcs_climate|lib";

	@Method(modid = LIB_MODID)
	public static void load()
	{
		RecipeAPI.registerSmelting.addRecipe(new ItemStack(CaveBlocks.SLIPPERY_ICE), DCHeatTier.FROSTBITE, DCHumidity.WET, DCAirflow.FLOW, false, new ItemStack(Blocks.PACKED_ICE));

		DamageAPI.armorRegister.registerMaterial(CaveItems.HEXCITE_ARMOR, 0.5F, 0.5F);

		DamageAPI.resistantData.registerEntityResistant(EntityCavenicSkeleton.class, 7.0F, 4.0F);
		DamageAPI.resistantData.registerEntityResistant(EntityCavenicCreeper.class, 2.0F, 5.0F);
		DamageAPI.resistantData.registerEntityResistant(EntityCavenicZombie.class, 8.0F, 5.0F);
		DamageAPI.resistantData.registerEntityResistant(EntityCavenicSpider.class, 6.0F, 4.0F);
		DamageAPI.resistantData.registerEntityResistant(EntityCavenicWitch.class, 6.0F, 6.0F);
		DamageAPI.resistantData.registerEntityResistant(EntityCavenicBear.class, 10.0F, 15.0F);
		DamageAPI.resistantData.registerEntityResistant(EntityCaveman.class, 5.0F, 3.0F);

		if (CaveDimensions.FROST_MOUNTAINS != null)
		{
			int dim = CaveDimensions.FROST_MOUNTAINS.getId();

			for (Biome biome : BiomeDictionary.getBiomes(BiomeDictionary.Type.COLD))
			{
				ClimateAPI.register.addBiomeClimate(biome, dim, DCHeatTier.FROSTBITE, DCHumidity.WET, DCAirflow.NORMAL);
			}
		}

		if (CaveDimensions.WIDE_DESERT != null)
		{
			int dim = CaveDimensions.WIDE_DESERT.getId();

			for (Biome biome : BiomeDictionary.getBiomes(BiomeDictionary.Type.SANDY))
			{
				ClimateAPI.register.addBiomeClimate(biome, dim, DCHeatTier.HOT, DCHumidity.DRY, DCAirflow.NORMAL);
			}
		}
	}
}