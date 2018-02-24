package cavern.entity;

import java.util.Collection;

import cavern.core.Cavern;
import cavern.util.CaveUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class CaveEntityRegistry
{
	private static int entityId;

	public static void registerEntity(Class<? extends Entity> entityClass, String key, String name, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates)
	{
		EntityRegistry.registerModEntity(CaveUtils.getKey(key), entityClass, name, entityId++, Cavern.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
	}

	public static void registerEntity(Class<? extends Entity> entityClass, String key, String name, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int primaryColor, int secondaryColor)
	{
		EntityRegistry.registerModEntity(CaveUtils.getKey(key), entityClass, name, entityId++, Cavern.instance, trackingRange, updateFrequency, sendsVelocityUpdates, primaryColor, secondaryColor);
	}

	public static void registerMob(Class<? extends Entity> entityClass, String key, String name)
	{
		registerEntity(entityClass, key, name, 128, 3, true);
	}

	public static void registerMob(Class<? extends Entity> entityClass, String key, String name, int primaryColor, int secondaryColor)
	{
		registerEntity(entityClass, key, name, 128, 3, true, primaryColor, secondaryColor);

		EntitySpawnPlacementRegistry.setPlacementType(entityClass, SpawnPlacementType.ON_GROUND);
	}

	public static void registerEntities()
	{
		registerMob(EntityCavenicSkeleton.class, "cavenic_skeleton", "CavenicSkeleton", 0xAAAAAA, 0xDDDDDD);
		registerMob(EntityCavenicCreeper.class, "cavenic_creeper", "CavenicCreeper", 0xAAAAAA, 0x2E8B57);
		registerMob(EntityCavenicZombie.class, "cavenic_zombie", "CavenicZombie", 0xAAAAAA, 0x00A0A0);
		registerMob(EntityCavenicSpider.class, "cavenic_spider", "CavenicSpider", 0xAAAAAA, 0x811F1F);
		registerMob(EntityCavenicWitch.class, "cavenic_witch", "CavenicWitch", 0xAAAAAA, 0x4A5348);
		registerMob(EntityCavenicBear.class, "cavenic_bear", "CavenicBear", 0xAAAAAA, 0xFFFFFF);
		registerMob(EntityCrazySkeleton.class, "crazy_skeleton", "CrazySkeleton", 0x909090, 0xDDDDDD);
		registerMob(EntityCrazyCreeper.class, "crazy_creeper", "CrazyCreeper", 0x909090, 0x2E8B57);
		registerMob(EntityCrazyZombie.class, "crazy_zombie", "CrazyZombie", 0x909090, 0x00A0A0);
		registerMob(EntityCrazySpider.class, "crazy_spider", "CrazySpider", 0x909090, 0x811F1F);
		registerMob(EntityCaveman.class, "caveman", "Caveman", 0xAAAAAA, 0xCCCCCC);

		registerMob(EntitySummonZombie.class, "summon_zombie", "Zombie");
		registerMob(EntitySummonSkeleton.class, "summon_skeleton", "Skeleton");
		registerMob(EntitySummonCavenicZombie.class, "summon_cavenic_zombie", "CavenicZombie");
		registerMob(EntitySummonCavenicSkeleton.class, "summon_cavenic_skeleton", "CavenicSkeleton");

		registerEntity(EntityAquaSquid.class, "squid", "Squid", 64, 3, true);

		registerEntity(EntityMagicTorcher.class, "magic_torcher", "MagicTorcher", 16, 3, false);

		EntitySpawnPlacementRegistry.setPlacementType(EntityAquaSquid.class, SpawnPlacementType.IN_WATER);
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
	}
}