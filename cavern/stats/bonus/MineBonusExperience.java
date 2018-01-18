package cavern.stats.bonus;

import cavern.api.IMineBonus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;

public class MineBonusExperience implements IMineBonus
{
	@Override
	public boolean canMineBonus(int combo, EntityPlayer player)
	{
		return combo % 10 == 0;
	}

	@Override
	public void onMineBonus(boolean isClient, int combo, EntityPlayer player)
	{
		if (!isClient)
		{
			player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
				SoundCategory.PLAYERS, 0.1F, 0.5F * ((RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.7F + 1.8F));

			player.addExperience(combo / 10);
		}
	}
}