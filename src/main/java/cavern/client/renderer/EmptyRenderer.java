package cavern.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EmptyRenderer extends IRenderHandler
{
	public static final EmptyRenderer INSTANCE = new EmptyRenderer();

    @SideOnly(Side.CLIENT)
	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {}
}