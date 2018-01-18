package cavern.stats.bonus;

import cavern.api.IMineBonus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class MineBonusResistance implements IMineBonus
{
	@Override
	public boolean canMineBonus(int combo, EntityPlayer player)
	{
		return combo % 5 == 0;
	}

	@Override
	public void onMineBonus(boolean isClient, int combo, EntityPlayer player)
	{
		if (!isClient)
		{
			player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 5 * 20, combo >= 10 ? 1 : 0, false, true));
		}
	}
}