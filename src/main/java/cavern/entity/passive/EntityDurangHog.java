package cavern.entity.passive;

import java.util.Set;

import com.google.common.collect.Sets;

import cavern.block.CaveBlocks;
import cavern.entity.ai.EntityAIEatAcresia;
import cavern.util.CaveUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityDurangHog extends EntityPig
{
	private static final ResourceLocation LOOT_HOG = LootTableList.register(CaveUtils.getKey("entities/durang_hog"));
	private static final Set<Item> TEMPTATION_ITEMS = Sets.newHashSet(Items.CARROT, Items.POTATO, Items.BEETROOT, Item.getItemFromBlock(CaveBlocks.ACRESIA));

	private int eatTimer;
	private EntityAIEatAcresia entityAIEatAcresia;

	public EntityDurangHog(World world)
	{
		super(world);
	}

	@Override
	protected void initEntityAI()
	{
		entityAIEatAcresia = new EntityAIEatAcresia(this);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIPanic(this, 1.25D));
		tasks.addTask(3, new EntityAIMate(this, 1.0D));
		tasks.addTask(4, new EntityAITempt(this, 1.2D, Items.CARROT_ON_A_STICK, false));
		tasks.addTask(4, new EntityAITempt(this, 1.2D, false, TEMPTATION_ITEMS));
		tasks.addTask(5, new EntityAIFollowParent(this, 1.1D));
		tasks.addTask(6, entityAIEatAcresia);
		tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(9, new EntityAILookIdle(this));
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16.0D);
		getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	}

	@Override
	protected ResourceLocation getLootTable()
	{
		return LOOT_HOG;
	}

	@Override
	protected void updateAITasks()
	{
		eatTimer = entityAIEatAcresia.getEatingGrassTimer();

		super.updateAITasks();
	}

	@Override
	public void onLivingUpdate()
	{
		if (world.isRemote)
		{
			eatTimer = Math.max(0, eatTimer - 1);
		}

		super.onLivingUpdate();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id)
	{
		if (id == 10)
		{
			eatTimer = 40;
		}
		else
		{
			super.handleStatusUpdate(id);
		}
	}

	@SideOnly(Side.CLIENT)
	public float getHeadRotationAngleX(float tick)
	{
		if (eatTimer > 4 && eatTimer <= 36)
		{
			float f = (eatTimer - 4 - tick) / 32.0F;

			return ((float)Math.PI / 5F) + ((float)Math.PI * 7F / 100F) * MathHelper.sin(f * 28.7F);
		}
		else
		{
			return eatTimer > 0 ? ((float) Math.PI / 5F) : rotationPitch * 0.017453292F;
		}
	}

	@Override
	public boolean getCanSpawnHere()
	{
		IBlockState state = world.getBlockState((new BlockPos(this)).down());

		int i = MathHelper.floor(posX);
		int j = MathHelper.floor(getEntityBoundingBox().minY);
		int k = MathHelper.floor(posZ);
		BlockPos blockpos = new BlockPos(i, j, k);

		return world.getBlockState(blockpos.down()).getBlock() == spawnableBlock && state.canEntitySpawn(this);
	}

	@Override
	public EntityDurangHog createChild(EntityAgeable ageable)
	{
		return new EntityDurangHog(world);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack)
	{
		return TEMPTATION_ITEMS.contains(stack.getItem());
	}
}
