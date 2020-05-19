package cavern.entity;

import cavern.entity.passive.EntityDurangHog;
import cavern.util.CaveUtils;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;

public class CaveEntityRegistry
{
	public static final List<SpawnListEntry> SPAWNS = Lists.newArrayList();
	public static final List<SpawnListEntry> CRAZY_SPAWNS = Lists.newArrayList();
	public static final List<SpawnListEntry> AQUA_SPAWNS = Lists.newArrayList();
	public static final List<SpawnListEntry> CAVELAND_ANIMAL_SPAWNS = Lists.newArrayList();

	protected static EntityEntry createEntry(Class<? extends Entity> entityClass, String key, String name)
	{
		return new EntityEntry(entityClass, name).setRegistryName(CaveUtils.getKey(key));
	}

	protected static EntityEntry createEntry(Class<? extends Entity> entityClass, String key, String name, int primaryColor, int secondaryColor)
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
		registry.register(createEntry(EntityDurangHog.class, "durang_hog", "DurangHog", 0xc69ea0, 0x7d5150));
		registry.register(createEntry(EntitySummonZombie.class, "summon_zombie", "Zombie"));
		registry.register(createEntry(EntitySummonSkeleton.class, "summon_skeleton", "Skeleton"));
		registry.register(createEntry(EntitySummonCavenicZombie.class, "summon_cavenic_zombie", "CavenicZombie"));
		registry.register(createEntry(EntitySummonCavenicSkeleton.class, "summon_cavenic_skeleton", "CavenicSkeleton"));
		registry.register(createEntry(EntityAquaSquid.class, "squid", "Squid"));
		registry.register(createEntry(EntityMagicTorcher.class, "magic_torcher", "MagicTorcher"));
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

		CAVELAND_ANIMAL_SPAWNS.add(new SpawnListEntry(EntityDurangHog.class, 5, 1, 2));

		CRAZY_SPAWNS.add(new SpawnListEntry(EntityCrazySkeleton.class, 1, 1, 1));
		CRAZY_SPAWNS.add(new SpawnListEntry(EntityCrazyCreeper.class, 1, 1, 1));
		CRAZY_SPAWNS.add(new SpawnListEntry(EntityCrazyZombie.class, 1, 1, 1));
		CRAZY_SPAWNS.add(new SpawnListEntry(EntityCrazySpider.class, 1, 1, 1));

		EntitySpawnPlacementRegistry.setPlacementType(EntityAquaSquid.class, SpawnPlacementType.IN_WATER);
	}
}