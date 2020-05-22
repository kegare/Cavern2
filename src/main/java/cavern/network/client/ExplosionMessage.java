package cavern.network.client;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.client.ClientExplosion;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ExplosionMessage implements IClientMessage<ExplosionMessage, IMessage>
{
	private double x;
	private double y;
	private double z;
	private float strength;
	private List<BlockPos> affectedBlockPositions;

	public ExplosionMessage() {}

	public ExplosionMessage(double x, double y, double z, float strength, List<BlockPos> positions)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.strength = strength;
		this.affectedBlockPositions = positions;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		strength = buf.readFloat();

		int count = buf.readInt();

		affectedBlockPositions = Lists.newArrayListWithCapacity(count);

		int px = (int)x;
		int py = (int)y;
		int pz = (int)z;

		for (int i = 0; i < count; ++i)
		{
			int posX = buf.readByte() + px;
			int posY = buf.readByte() + py;
			int posZ = buf.readByte() + pz;

			affectedBlockPositions.add(new BlockPos(posX, posY, posZ));
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeFloat(strength);
		buf.writeInt(affectedBlockPositions.size());

		int px = (int)x;
		int py = (int)y;
		int pz = (int)z;

		for (BlockPos pos : affectedBlockPositions)
		{
			buf.writeByte(pos.getX() - px);
			buf.writeByte(pos.getY() - py);
			buf.writeByte(pos.getZ() - pz);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage process(Minecraft mc)
	{
		new ClientExplosion(mc, x, y, z, strength, affectedBlockPositions).doExplosion();

		return null;
	}
}