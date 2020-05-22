package cavern.entity.monster;

import cavern.api.CavernAPI;
import cavern.api.entity.ICavenicMob;
import cavern.item.ItemMagicBook;
import cavern.item.ItemMirageBook;
import cavern.util.CaveUtils;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class EntityCaveman extends EntityMob implements ICavenicMob
{
	private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(EntityCaveman.class, DataSerializers.BOOLEAN);

	private final InventoryBasic backpackInventory;
	private final EntityAIAttackMelee aiAttackOnCollide = new EntityAIAttackMelee(this, 1.15F, false);

	private int restTime;

	public EntityCaveman(World world)
	{
		super(world);
		this.experienceValue = 5;
		this.backpackInventory = new InventoryBasic("entity.Caveman.name", false, 9 * 3);
		this.setSize(0.48F, 1.85F);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();

		dataManager.register(SITTING, false);
	}

	@Override
	protected void initEntityAI()
	{
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIRestrictSun(this));
		tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(8, new EntityAILookIdle(this));
		applyEntityAI();
	}

	protected void applyEntityAI()
	{
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2875D);
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key)
	{
		if (SITTING.equals(key))
		{
			setSizeForSitting(isSitting());
		}

		super.notifyDataManagerChange(key);
	}

	public InventoryBasic getBackpackInventory()
	{
		return backpackInventory;
	}

	public boolean isSitting()
	{
		return dataManager.get(SITTING);
	}

	public void setSitting(boolean sit)
	{
		dataManager.set(SITTING, sit);
	}

	public int getRestTime()
	{
		return restTime;
	}

	protected void setSizeForSitting(boolean sit)
	{
		if (sit)
		{
			setSize(0.48F, 1.35F);
		}
		else
		{
			setSize(0.48F, 1.85F);
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);

		compound.setInteger("RestTime", restTime);

		NBTTagList list = new NBTTagList();

		for (int i = 0; i < backpackInventory.getSizeInventory(); ++i)
		{
			ItemStack stack = backpackInventory.getStackInSlot(i);

			if (!stack.isEmpty())
			{
				list.appendTag(stack.writeToNBT(new NBTTagCompound()));
			}
		}

		compound.setTag("Inventory", list);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);

		restTime = compound.getInteger("RestTime");

		NBTTagList list = compound.getTagList("Inventory", NBT.TAG_COMPOUND);

		for (int i = 0; i < list.tagCount(); ++i)
		{
			ItemStack stack = new ItemStack(list.getCompoundTagAt(i));

			if (!stack.isEmpty())
			{
				backpackInventory.addItem(stack);
			}
		}
	}

	@Override
	public float getEyeHeight()
	{
		if (isSitting())
		{
			return 1.125F;
		}

		return 1.65F;
	}

	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();

		if (!world.isRemote)
		{
			if (onGround && !CaveUtils.isMoving(this))
			{
				++restTime;

				if (restTime > 500)
				{
					setSitting(true);
				}
			}
			else
			{
				if (restTime > 0)
				{
					setSitting(false);
				}

				restTime = 0;
			}

			pickupItem();
		}
	}

	protected void pickupItem()
	{
		if (ticksExisted % 10 != 0)
		{
			return;
		}

		for (EntityItem entityItem : world.getEntitiesWithinAABB(EntityItem.class, getEntityBoundingBox().grow(0.65D)))
		{
			if (entityItem.isEntityAlive() && entityItem.onGround)
			{
				ItemStack stack = entityItem.getItem();

				if (!stack.isEmpty())
				{
					stack = onItemStackPickup(stack);

					if (stack.isEmpty())
					{
						entityItem.setDead();

						playPickupSound();
					}
					else
					{
						entityItem.setItem(stack);
					}
				}

				break;
			}
		}
	}

	public ItemStack onItemStackPickup(ItemStack stack)
	{
		return backpackInventory.addItem(stack);
	}

	protected void playPickupSound()
	{
		playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.25F, 0.85F);
	}

	public boolean canCombatItem(ItemStack stack)
	{
		if (stack.isEmpty())
		{
			return false;
		}

		if (stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemTool)
		{
			return true;
		}

		return false;
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand)
	{
		ItemStack held = player.getHeldItem(hand);

		if (held.isEmpty() || getDistanceSq(player) > 3.0D)
		{
			return false;
		}

		player.swingArm(hand);

		if (!world.isRemote)
		{
			player.displayGUIChest(backpackInventory);

			playPickupSound();
		}

		return true;
	}

	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slot, ItemStack stack)
	{
		super.setItemStackToSlot(slot, stack);

		if (world != null && !world.isRemote && slot == EntityEquipmentSlot.MAINHAND)
		{
			tasks.removeTask(aiAttackOnCollide);

			if (canCombatItem(stack))
			{
				tasks.addTask(4, aiAttackOnCollide);
			}
		}
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
		livingdata = super.onInitialSpawn(difficulty, livingdata);

		setHeldItem(EnumHand.MAIN_HAND, getInitialHeldItem());

		for (ItemStack stack : getInitialInventoryItems())
		{
			backpackInventory.addItem(stack);
		}

		return livingdata;
	}

	protected ItemStack getInitialHeldItem()
	{
		if (rand.nextDouble() < 0.35D)
		{
			return new ItemStack(Items.IRON_SWORD);
		}

		if (rand.nextDouble() < 0.35D)
		{
			return new ItemStack(Items.IRON_PICKAXE);
		}

		if (rand.nextDouble() < 0.15D)
		{
			return new ItemStack(Items.IRON_AXE);
		}

		if (rand.nextDouble() < 0.15D)
		{
			return new ItemStack(Items.IRON_SHOVEL);
		}

		return ItemStack.EMPTY;
	}

	protected NonNullList<ItemStack> getInitialInventoryItems()
	{
		NonNullList<ItemStack> list = NonNullList.create();

		for (int i = 0; i < 2; ++i)
		{
			ItemStack stack = getInitialHeldItem();

			if (!stack.isEmpty())
			{
				list.add(stack);
			}
		}

		list.add(new ItemStack(Blocks.TORCH, MathHelper.getInt(rand, 3, 10)));
		list.add(new ItemStack(Items.BREAD, MathHelper.getInt(rand, 1, 3)));

		if (rand.nextDouble() < 0.05D)
		{
			ItemStack stack = ItemMirageBook.getRandomBook();

			if (!stack.isEmpty())
			{
				list.add(stack);
			}
		}

		if (rand.nextDouble() < 0.05D)
		{
			list.add(ItemMagicBook.getRandomBook());
		}

		return list;
	}

	@Override
	public void onDeath(DamageSource source)
	{
		super.onDeath(source);

		if (!world.isRemote)
		{
			InventoryHelper.dropInventoryItems(world, this, backpackInventory);
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage)
	{
		if (source == DamageSource.FALL)
		{
			damage *= 0.35F;
		}

		return !source.isFireDamage() && super.attackEntityFrom(source, damage);
	}

	@Override
	public int getMaxSpawnedInChunk()
	{
		return CavernAPI.dimension.isInCavenia(this) ? 4 : 1;
	}
}