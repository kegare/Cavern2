package cavern.api;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;

public interface IPortalCache
{
	DimensionType getLastDim(ResourceLocation key);

	DimensionType getLastDim(ResourceLocation key, @Nullable DimensionType nullDefault);

	void setLastDim(ResourceLocation key, DimensionType type);

	@Nullable
	BlockPos getLastPos(ResourceLocation key, DimensionType type);

	BlockPos getLastPos(ResourceLocation key, DimensionType type, @Nullable BlockPos pos);

	boolean hasLastPos(ResourceLocation key, DimensionType type);

	void setLastPos(ResourceLocation key, DimensionType type, @Nullable BlockPos pos);

	void clearLastPos(@Nullable ResourceLocation key, DimensionType type);

	Vec3d getLastPortalVec();

	void setLastPortalVec(Vec3d vec);

	EnumFacing getTeleportDirection();

	void setTeleportDirection(EnumFacing direction);

	void writeToNBT(NBTTagCompound nbt);

	void readFromNBT(NBTTagCompound nbt);
}