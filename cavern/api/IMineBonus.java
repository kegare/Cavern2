package cavern.api;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;

public interface IMineBonus
{
	static final Random RANDOM = new Random();

	boolean canMineBonus(int combo, EntityPlayer player);

	void onMineBonus(boolean isClient, int combo, EntityPlayer player);
}