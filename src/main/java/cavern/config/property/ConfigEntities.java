package cavern.config.property;

import java.util.Arrays;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

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

		Arrays.stream(getValues()).map(String::trim).filter(StringUtils::isNotEmpty).map(ResourceLocation::new).forEach(value ->
		{
			Class<? extends Entity> entityClass = EntityList.getClass(value);

			if (entityClass != null)
			{
				entities.add(entityClass);
				keys.add(value);
			}
		});
	}
}