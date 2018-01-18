package cavern.config.property;

import java.util.Arrays;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;

public class ConfigEntities
{
	private String[] values;

	private final Set<Class<? extends Entity>> entities = Sets.newHashSet();
	private final Set<ResourceLocation> keys = Sets.newHashSet();

	public String[] getValues()
	{
		if (values == null)
		{
			values = new String[0];
		}

		return values;
	}

	public void setValues(String[] entities)
	{
		values = entities;
	}

	public Set<Class<? extends Entity>> getEntities()
	{
		return entities;
	}

	public Set<ResourceLocation> getKeys()
	{
		return keys;
	}

	public boolean isEmpty()
	{
		return entities.isEmpty();
	}

	public void refreshEntities()
	{
		entities.clear();
		keys.clear();

		Arrays.stream(getValues()).filter(value -> !Strings.isNullOrEmpty(value)).forEach(value ->
		{
			value = value.trim();

			if (!value.contains(":"))
			{
				value = "minecraft:" + value;
			}

			ResourceLocation key = new ResourceLocation(value);
			Class<? extends Entity> entityClass = EntityList.getClass(key);

			if (entityClass != null)
			{
				entities.add(entityClass);
				keys.add(key);
			}
		});
	}
}