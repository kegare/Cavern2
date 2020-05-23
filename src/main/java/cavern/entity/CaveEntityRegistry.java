package cavern.entity;

import cavern.core.Cavern;
import cavern.entity.boss.EntitySkySeeker;
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
import cavern.entity.monster.EntityCrystalTurret;
import cavern.entity.monster.EntitySummonCavenicSkeleton;
import cavern.entity.monster.EntitySummonCavenicZombie;
import cavern.entity.monster.EntitySummonSkeleton;
import cavern.entity.monster.EntitySummonZombie;
import cavern.entity.passive.EntityAquaSquid;
import cavern.entity.passive.EntityDurangHog;
import cavern.entity.projectile.EntityBeam;
import cavern.entity.projectile.EntityMagicTorcher;
import cavern.util.CaveUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public final class CaveEntityRegistry
{
	public static final NonNullList<SpawnListEntry> SPAWNS = NonNullList.create();
	public static final NonNullList<SpawnListEntry> CRAZY_SPAWNS = NonNullList.create();
	public static final NonNullList<SpawnListEntry> ANIMAL_SPAWNS = NonNullList.create();

	private static int entityId;

	public static void registerEntity(Class<? extends Entity> entityClass, String key, String name, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates)
	{
		EntityRegistry.registerModEntity(CaveUtils.getKey(key), entityClass, name, entityId++, Cavern.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
	}

	public static void registerEntity(Class<? extends Entity> entityClass, String key, String name, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int primaryColor, int secondaryColor)
	{
		EntityRegistry.registerModEntity(CaveUtils.getKey(key), entityClass, name, entityId++, Cavern.instance, trackingRange, updateFrequency, sendsVelocityUpdates, primaryColor, secondaryColor);
	}

	private static EntityEntry createEntry(Class<? extends Entity> entityClass, String key, String name)
	{
		return new EntityEntry(entityClass, name).setRegistryName(CaveUtils.getKey(key));
	}

	private static EntityEntry createEntry(Class<? extends Entity> entityClass, String key, String name, int primaryColor, int secondaryColor)
	{
		EntityEntry entry = new EntityEntry(entityClass, name);
		ResourceLocation regKey = CaveUtils.getKey(key);

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
		registry.register(createEntry(EntityCrystalTurret.class, "crystal_turret", "CrystalTurret", 0xAAAAAA, 0xd1e6f6));
		registry.register(createEntry(EntitySkySeeker.class, "sky_seeker", "SkySeeker", 0xAAAAAA, 0xd1e6f6));
		registry.register(createEntry(EntityDurangHog.class, "durang_hog", "DurangHog", 0xC69EA0, 0x7D5150));
		registry.register(createEntry(EntitySummonZombie.class, "summon_zombie", "Zombie"));
		registry.register(createEntry(EntitySummonSkeleton.class, "summon_skeleton", "Skeleton"));
		registry.register(createEntry(EntitySummonCavenicZombie.class, "summon_cavenic_zombie", "CavenicZombie"));
		registry.register(createEntry(EntitySummonCavenicSkeleton.class, "summon_cavenic_skeleton", "CavenicSkeleton"));
		registry.register(createEntry(EntityAquaSquid.class, "squid", "Squid"));
		registry.register(createEntry(EntityMagicTorcher.class, "magic_torcher", "MagicTorcher"));

		registerEntity(EntityBeam.class, "beam", "Beam", 100, 1, true);
	}

	public static void regsiterSpawns()
	{
		SPAWNS.add(new SpawnListEntry(EntityCavenicSkeleton.class, 20, 1, 1));
		SPAWNS.add(new SpawnListEntry(EntityCavenicCreeper.class, 30, 1, 1));
		SPAWNS.add(new SpawnListEntry(EntityCavenicZombie.class, 30, 2, 2));
		SPAWNS.add(new SpawnListEntry(EntityCavenicSpider.class, 30, 1, 1));
		SPAWNS.add(new SpawnListEntry(EntityCavenicWitch.class, 15, 1, 1));
		SPAWNS.add(new SpawnListEntry(EntityCavenicBear.class, 30, 1, 1));
		SPAWNS.add(new SpawnListEntry(EntityCaveman.class, 35, 1, 1));

		CRAZY_SPAWNS.add(new SpawnListEntry(EntityCrazySkeleton.class, 1, 1, 1));
		CRAZY_SPAWNS.add(new SpawnListEntry(EntityCrazyCreeper.class, 1, 1, 1));
		CRAZY_SPAWNS.add(new SpawnListEntry(EntityCrazyZombie.class, 1, 1, 1));
		CRAZY_SPAWNS.add(new SpawnListEntry(EntityCrazySpider.class, 1, 1, 1));

		ANIMAL_SPAWNS.add(new SpawnListEntry(EntityDurangHog.class, 5, 1, 2));

		EntitySpawnPlacementRegistry.setPlacementType(EntityAquaSquid.class, SpawnPlacementType.IN_WATER);
	}
}