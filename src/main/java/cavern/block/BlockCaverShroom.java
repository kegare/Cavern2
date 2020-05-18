package cavern.block;

import cavern.core.Cavern;
import cavern.util.CaveLog;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class BlockCaverShroom extends BlockBush {
    private final Map<ResourceLocation, ResourceLocation> transformMap = new HashMap<>();
    protected static final AxisAlignedBB MUSHROOM_AABB = new AxisAlignedBB(0.30000001192092896D, 0.0D, 0.30000001192092896D, 0.699999988079071D, 0.4000000059604645D, 0.699999988079071D);

    public BlockCaverShroom() {
        super();
        this.setUnlocalizedName("caver_shroom");
        this.setTickRandomly(true);
        this.setSoundType(SoundType.PLANT);
        this.setCreativeTab(Cavern.TAB_CAVERN);

        this.addCavernicTransformation(new ResourceLocation("minecraft", "zombie"), new ResourceLocation("cavern", "cavenic_zombie"));
        this.addCavernicTransformation(new ResourceLocation("minecraft", "skeleton"), new ResourceLocation("cavern", "cavenic_skeleton"));
        this.addCavernicTransformation(new ResourceLocation("minecraft", "spider"), new ResourceLocation("cavern", "cavenic_spider"));
        this.addCavernicTransformation(new ResourceLocation("minecraft", "creeper"), new ResourceLocation("cavern", "cavenic_creeper"));
        this.addCavernicTransformation(new ResourceLocation("minecraft", "witch"), new ResourceLocation("cavern", "cavenic_witch"));
        this.addCavernicTransformation(new ResourceLocation("minecraft", "polar_bear"), new ResourceLocation("cavern", "cavenic_bear"));
        this.addCavernicTransformation(new ResourceLocation("tofucraft", "tofunian"), new ResourceLocation("cavern", "caveman"));
    }

    private void addCavernicTransformation(ResourceLocation from, ResourceLocation to) {
        transformMap.put(from, to);
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return MUSHROOM_AABB;
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (rand.nextInt(25) == 0) {
            int i = 5;
            int j = 4;

            for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-4, -1, -4), pos.add(4, 1, 4))) {
                if (worldIn.getBlockState(blockpos).getBlock() == this) {
                    --i;

                    if (i <= 0) {
                        return;
                    }
                }
            }

            BlockPos blockpos1 = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);

            for (int k = 0; k < 4; ++k) {
                if (worldIn.isAirBlock(blockpos1) && this.canBlockStay(worldIn, blockpos1, this.getDefaultState())) {
                    pos = blockpos1;
                }

                blockpos1 = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
            }

            if (worldIn.isAirBlock(blockpos1) && this.canBlockStay(worldIn, blockpos1, this.getDefaultState())) {
                worldIn.setBlockState(blockpos1, this.getDefaultState(), 2);
            }
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);

        if (!worldIn.isRemote && entityIn instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) entityIn;

            entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 40));


            if (worldIn.rand.nextFloat() < 0.005F && !entityIn.isDead) {
                ResourceLocation location = transformMap.get(EntityList.getKey(entityIn));

                if (location != null) {

                    Entity newEntity = EntityList.createEntityByIDFromName(location, entityIn.world);


                    if (newEntity != null) {
                        newEntity.setLocationAndAngles(entityIn.posX, entityIn.posY, entityIn.posZ, entityIn.rotationYaw, entityIn.rotationPitch);

                        if (newEntity instanceof EntityMob) {
                            ((EntityMob) newEntity).onInitialSpawn(entityIn.world.getDifficultyForLocation(new BlockPos(entityIn)), null);
                        }


                        try { // try copying what can be copied
                            UUID uuid = newEntity.getUniqueID();

                            newEntity.readFromNBT(entityIn.writeToNBT(newEntity.writeToNBT(new NBTTagCompound())));

                            newEntity.setUniqueId(uuid);
                        } catch (Exception e) {
                            CaveLog.LOG.getLogger().warn("Couldn't transform entity NBT data: {}", e);
                        }

                        entityIn.world.spawnEntity(newEntity);
                        entityIn.setDead();

                        entityIn.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F + worldIn.rand.nextFloat(), worldIn.rand.nextFloat() * 0.7F + 0.3F);


                    }
                }
            }

        }
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos, this.getDefaultState());
    }

    protected boolean canSustainBush(IBlockState state) {
        return state.isFullBlock();
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        if (pos.getY() >= 0 && pos.getY() < 256) {
            IBlockState iblockstate = worldIn.getBlockState(pos.down());

            if (iblockstate.getBlock() == Blocks.MYCELIUM) {
                return true;
            } else if (iblockstate.getBlock() == Blocks.DIRT && iblockstate.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.PODZOL) {
                return true;
            } else if (iblockstate.getBlock() == Blocks.STONE) {
                return true;
            } else {
                return worldIn.getLight(pos) < 13 && iblockstate.getBlock().canSustainPlant(iblockstate, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this);
            }
        } else {
            return false;
        }
    }

}