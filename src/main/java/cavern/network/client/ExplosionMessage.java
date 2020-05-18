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
	private float x;
	private float y;
	private float z;
	private float strength;
	private List<BlockPos> affectedBlockPositions;

	public ExplosionMessage() {}

	public ExplosionMessage(float x, float y, float z, float strength, List<BlockPos> positions)
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
		x = buf.readFloat();
		y = buf.readFloat();
		z = buf.readFloat();
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
		buf.writeFloat(x);
		buf.writeFloat(y);
		buf.writeFloat(z);
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