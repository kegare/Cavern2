package cavern.entity.passive;

import cavern.api.CavernAPI;
import cavern.core.Cavern;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.world.World;

public class EntityAquaSquid extends EntitySquid
{
	public EntityAquaSquid(World world)
	{
		super(world);
	}

	@Override
	public String getName()
	{
		if (hasCustomName())
		{
			return getCustomNameTag();
		}

		return Cavern.proxy.translate("entity.Squid.name");
	}

	@Override
	public boolean getCanSpawnHere()
	{
		return CavernAPI.dimension.isInAquaCavern(this);
	}
}