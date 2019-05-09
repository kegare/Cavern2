package cavern.item;

import com.google.common.collect.Multimap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;

public class ItemSwordCavenic extends ItemSwordCave
{
	public ItemSwordCavenic()
	{
		super(CaveItems.CAVENIC, "swordCavenic");
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
	{
		if (target.hurtResistantTime > 0)
		{
			target.hurtResistantTime = 0;

			target.world.playSound(null, target.posX, target.posY + 0.85D, target.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
				SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F) + 0.25F);
		}

		stack.damageItem(1, attacker);

		return true;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
	{
		Multimap<String, AttributeModifier> map = super.getAttributeModifiers(slot, stack);

		if (slot == EntityEquipmentSlot.MAINHAND)
		{
			map.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
			map.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -1.0000000953674316D, 0));
		}

		return map;
	}
}