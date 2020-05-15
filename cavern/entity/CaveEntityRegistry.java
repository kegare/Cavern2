package cavern.entity;

import java.util.Collection;

import cavern.core.Cavern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class CaveEntityRegistry
{
	protected static EntityEntry createEntry(Class<? extends Entity> entityClass, String key, String name)
	{
		return new EntityEntry(entityClass, name).setRegistryName(new ResourceLocation(Cavern.MODID, key));
	}

	protected static EntityEntry createEntry(Class<? extends Entity> entityClass, String key, String name, int primaryColor, int secondaryColor)
	{
		EntityEntry entry = new EntityEntry(entityClass, name);
		ResourceLocation regKey = new ResourceLocation(Cavern.MODID, key);

		entry.setRegistryName(regKey);
		entry.setEgg(new EntityEggInfo(regKey, primaryColor, secondaryColor));

		return entry;
	}

	public static void registerEntities(IForgeRegistry<EntityEntry> registry)
	{
		registry.register(createEntry(EntityCavenicSkeleton.class, "cavenic_skeleton", "CavenicSkeleton", 0xAAAAAA, 0xDDDDDD));
		registry.register(createEntry(EntityCavenicCreeper.class, "cavenic_creeper", "CavenicCreeper", 0xAAAAAA, 0x2E8B57));
		registry.register(createEntry(EntityCavenicZombie.class, "cavenic_zombie", "CavenicZombie", 0xAAAAAA, 0x00A0A0));
		registry.register(createEntry(EntityCavenicSpider.class, "cavenic_spider", "CavenicSpider", 0xAAAAAA, 0x811F1F));
		registry.register(createEntry(EntityCavenicWitch.class, "cavenic_witch", "CavenicWitch", 0xAAAAAA, 0x4A5348));
		registry.register(createEntry(EntityCavenicBear.class, "cavenic_bear", "CavenicBear", 0xAAAAAA, 0xFFFFFF));
		registry.register(createEntry(EntityCrazySkeleton.class, "crazy_skeleton", "CrazySkeleton", 0x909090, 0xDDDDDD));
		registry.register(createEntry(EntityCrazyCreeper.class, "crazy_creeper", "CrazyCreeper", 0x909090, 0x2E8B57));
		registry.register(createEntry(EntityCrazyZombie.class, "crazy_zombie", "CrazyZombie", 0x909090, 0x00A0A0));
		registry.register(createEntry(EntityCrazySpider.class, "crazy_spider", "CrazySpider", 0x909090, 0x811F1F));
		registry.register(createEntry(EntityCaveman.class, "caveman", "Caveman", 0xAAAAAA, 0xCCCCCC));
		registry.register(createEntry(EntitySummonZombie.class, "summon_zombie", "Zombie"));
		registry.register(createEntry(EntitySummonSkeleton.class, "summon_skeleton", "Skeleton"));
		registry.register(createEntry(EntitySummonCavenicZombie.class, "summon_cavenic_zombie", "CavenicZombie"));
		registry.register(createEntry(EntitySummonCavenicSkeleton.class, "summon_cavenic_skeleton", "CavenicSkeleton"));
		registry.register(createEntry(EntityAquaSquid.class, "squid", "Squid"));
		registry.register(createEntry(EntityMagicTorcher.class, "magic_torcher", "MagicTorcher"));
	}

	public static void addSpawns()
	{
		Collection<Biome> biomes = ForgeRegistries.BIOMES.getValuesCollection();
		Biome[] biomeArray = biomes.toArray(new Biome[biomes.size()]);

		EntityRegistry.addSpawn(EntityCavenicSkeleton.class, 20, 1, 1, EnumCreatureType.MONSTER, biomeArray);
		EntityRegistry.addSpawn(EntityCavenicCreeper.class, 30, 1, 1, EnumCreatureType.MONSTER, biomeArray);
		EntityRegistry.addSpawn(EntityCavenicZombie.class, 30, 2, 2, EnumCreatureType.MONSTER, biomeArray);
		EntityRegistry.addSpawn(EntityCavenicSpider.class, 30, 1, 1, EnumCreatureType.MONSTER, biomeArray);
		EntityRegistry.addSpawn(EntityCavenicWitch.class, 15, 1, 1, EnumCreatureType.MONSTER, biomeArray);
		EntityRegistry.addSpawn(EntityCavenicBear.class, 30, 1, 1, EnumCreatureType.MONSTER, biomeArray);
		EntityRegistry.addSpawn(EntityCrazySkeleton.class, 1, 1, 1, EnumCreatureType.MONSTER, biomeArray);
		EntityRegistry.addSpawn(EntityCrazyCreeper.class, 1, 1, 1, EnumCreatureType.MONSTER, biomeArray);
		EntityRegistry.addSpawn(EntityCrazyZombie.class, 1, 1, 1, EnumCreatureType.MONSTER, biomeArray);
		EntityRegistry.addSpawn(EntityCrazySpider.class, 1, 1, 1, EnumCreatureType.MONSTER, biomeArray);
		EntityRegistry.addSpawn(EntityCaveman.class, 30, 1, 1, EnumCreatureType.MONSTER, biomeArray);
		EntityRegistry.addSpawn(EntityAquaSquid.class, 100, 4, 4, EnumCreatureType.WATER_CREATURE, biomeArray);

		EntitySpawnPlacementRegistry.setPlacementType(EntityAquaSquid.class, SpawnPlacementType.IN_WATER);
	}
}