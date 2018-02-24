package cavern.api;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;

public interface ISummonMob extends IEntityOwnable
{
	static final Predicate<? super Entity> CAN_SUMMON_MOB_TARGET = entity -> entity instanceof IMob && !(entity instanceof ISummonMob);

	int getLifeTime();

	@Nullable
	EntityPlayer getSummoner();

	default boolean isSummonerEqual(@Nullable EntityPlayer player)
	{
		if (player == null || getSummoner() == null)
		{
			return false;
		}

		return player.getCachedUniqueIdString().equals(getSummoner().getCachedUniqueIdString());
	}

	@Override
	default UUID getOwnerId()
	{
		if (getSummoner() != null)
		{
			return EntityPlayer.getUUID(getSummoner().getGameProfile());
		}

		return null;
	}

	@Override
	default Entity getOwner()
	{
		return getSummoner();
	}
}