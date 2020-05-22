package cavern.client.gui;

import java.util.Random;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ObjectUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import com.google.common.base.Strings;

import cavern.client.CaveRenderingRegistry;
import cavern.util.BlockMeta;
import cavern.util.CaveUtils;
import cavern.util.PanoramaPaths;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@SideOnly(Side.CLIENT)
public abstract class GuiListSlot extends GuiSlot
{
	public static final NonNullList<PanoramaPaths> PANORAMA_PATHS = NonNullList.create();

	private static final Random RANDOM = new Random();

	private static int panoramaTimer;

	static
	{
		for (int i = 0; i <= 2; ++i)
		{
			ResourceLocation[] paths = new ResourceLocation[6];

			for (int j = 0; j < paths.length; ++j)
			{
				paths[j] = CaveUtils.getKey(String.format("textures/gui/panorama/%d/%d.png", i, j));
			}

			PANORAMA_PATHS.add(new PanoramaPaths(paths[0], paths[1], paths[2], paths[3], paths[4], paths[5]));
		}
	}

	protected final Minecraft mc;

	private final DynamicTexture viewportTexture;
	private final ResourceLocation panoramaBackground;
	private float panoramaTicks;

	public PanoramaPaths currentPanoramaPaths;

	public GuiListSlot(Minecraft mc, int width, int height, int top, int bottom, int slotHeight)
	{
		super(mc, width, height, top, bottom, slotHeight);
		this.mc = mc;
		this.viewportTexture = new DynamicTexture(256, 256);
		this.panoramaBackground = mc.getTextureManager().getDynamicTextureLocation("background", viewportTexture);
	}

	@Nullable
	public PanoramaPaths getPanoramaPaths()
	{
		if (PANORAMA_PATHS.isEmpty())
		{
			currentPanoramaPaths = null;
		}
		else if (currentPanoramaPaths == null)
		{
			currentPanoramaPaths = PANORAMA_PATHS.get(RANDOM.nextInt(PANORAMA_PATHS.size()));
		}

		return currentPanoramaPaths;
	}

	private void drawPanorama(float ticks)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		byte b0 = 8;

		for (int k = 0; k < b0 * b0; ++k)
		{
			GlStateManager.pushMatrix();
			float f1 = ((float)(k % b0) / (float)b0 - 0.5F) / 64.0F;
			float f2 = ((float)(k / b0) / (float)b0 - 0.5F) / 64.0F;
			float f3 = 0.0F;
			GlStateManager.translate(f1, f2, f3);
			GlStateManager.rotate(MathHelper.sin((panoramaTimer + ticks) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-(panoramaTimer + ticks) * 0.1F, 0.0F, 1.0F, 0.0F);

			for (int l = 0; l < 6; ++l)
			{
				PanoramaPaths paths = getPanoramaPaths();

				if (paths == null)
				{
					break;
				}

				GlStateManager.pushMatrix();

				switch (l)
				{
					case 1:
						GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
						break;
					case 2:
						GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
						break;
					case 3:
						GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
						break;
					case 4:
						GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
						break;
					case 5:
						GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
						break;
				}

				mc.getTextureManager().bindTexture(paths.getPath(l));
				buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				int i = 255 / (k + 1);
				buffer.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, i).endVertex();
				buffer.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, i).endVertex();
				buffer.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, i).endVertex();
				buffer.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, i).endVertex();
				tessellator.draw();
				GlStateManager.popMatrix();
			}

			GlStateManager.popMatrix();
			GlStateManager.colorMask(true, true, true, false);
		}

		buffer.setTranslation(0.0D, 0.0D, 0.0D);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.enableDepth();
	}

	private void rotateAndBlurSkybox(float ticks)
	{
		mc.getTextureManager().bindTexture(panoramaBackground);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GlStateManager.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.colorMask(true, true, true, false);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		GlStateManager.disableAlpha();
		byte b0 = 3;

		for (int i = 0; i < b0; ++i)
		{
			float f = 1.0F / (i + 1);
			int j = width;
			int k = height;
			float f1 = (i - b0 / 2) / 256.0F;
			buffer.pos(j, k, 0.0D).tex(0.0F + f1, 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
			buffer.pos(j, 0.0D, 0.0D).tex(1.0F + f1, 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
			buffer.pos(0.0D, 0.0D, 0.0D).tex(1.0F + f1, 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
			buffer.pos(0.0D, k, 0.0D).tex(0.0F + f1, 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
		}

		tessellator.draw();
		GlStateManager.enableAlpha();
		GlStateManager.colorMask(true, true, true, true);
	}

	private void renderSkybox(float ticks)
	{
		mc.getFramebuffer().unbindFramebuffer();
		GlStateManager.viewport(0, 0, 256, 256);
		drawPanorama(ticks);
		rotateAndBlurSkybox(ticks);
		rotateAndBlurSkybox(ticks);
		rotateAndBlurSkybox(ticks);
		rotateAndBlurSkybox(ticks);
		rotateAndBlurSkybox(ticks);
		rotateAndBlurSkybox(ticks);
		rotateAndBlurSkybox(ticks);
		mc.getFramebuffer().bindFramebuffer(true);
		GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);
		float f1 = width > height ? 120.0F / width : 120.0F / height;
		float f2 = height * f1 / 256.0F;
		float f3 = width * f1 / 256.0F;
		int k = width;
		int l = height;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		buffer.pos(0.0D, l, 0.0D).tex(0.5F - f2, 0.5F + f3).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		buffer.pos(k, l, 0.0D).tex(0.5F - f2, 0.5F - f3).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		buffer.pos(k, 0.0D, 0.0D).tex(0.5F + f2, 0.5F - f3).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		buffer.pos(0.0D, 0.0D, 0.0D).tex(0.5F + f2, 0.5F + f3).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		tessellator.draw();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		panoramaTicks = partialTicks;

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawContainerBackground(Tessellator tessellator)
	{
		if (mc.world != null)
		{
			Gui.drawRect(left, top, right, bottom, 0x101010);
		}
		else if (getPanoramaPaths() != null)
		{
			++panoramaTimer;

			GlStateManager.disableAlpha();
			renderSkybox(panoramaTicks);
			GlStateManager.enableAlpha();
		}
		else super.drawContainerBackground(tessellator);
	}

	public void scrollUp()
	{
		int i = getAmountScrolled() % getSlotHeight();

		if (i == 0)
		{
			scrollBy(-getSlotHeight());
		}
		else
		{
			scrollBy(-i);
		}
	}

	public void scrollDown()
	{
		scrollBy(getSlotHeight() - getAmountScrolled() % getSlotHeight());
	}

	public void scrollToTop()
	{
		scrollBy(-getAmountScrolled());
	}

	public void scrollToEnd()
	{
		scrollBy(getSlotHeight() * getSize());
	}

	public abstract void scrollToSelected();

	public void scrollToPrev()
	{
		scrollBy(-(getAmountScrolled() % getSlotHeight() + (bottom - top) / getSlotHeight() * getSlotHeight()));
	}

	public void scrollToNext()
	{
		scrollBy(getAmountScrolled() % getSlotHeight() + (bottom - top) / getSlotHeight() * getSlotHeight());
	}

	public void drawItemStack(RenderItem renderer, ItemStack stack, int x, int y)
	{
		drawItemStack(renderer, stack, x, y, null, null);
	}

	public void drawItemStack(RenderItem renderer, ItemStack stack, int x, int y, FontRenderer fontRenderer, @Nullable String overlay)
	{
		if (stack.isEmpty())
		{
			return;
		}

		if (stack.getMetadata() == OreDictionary.WILDCARD_VALUE)
		{
			NBTTagCompound nbt = stack.getTagCompound();

			stack = new ItemStack(stack.getItem(), stack.getCount());
			stack.setTagCompound(nbt);
		}

		GlStateManager.enableRescaleNormal();
		RenderHelper.enableGUIStandardItemLighting();

		renderer.renderItemIntoGUI(stack, x, y);

		if (!Strings.isNullOrEmpty(overlay))
		{
			renderer.renderItemOverlayIntoGUI(ObjectUtils.defaultIfNull(stack.getItem().getFontRenderer(stack), fontRenderer), stack, x, y, overlay);
		}

		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
	}

	public void drawItemStack(RenderItem renderer, @Nullable IBlockState state, int x, int y)
	{
		drawItemStack(renderer, state, x, y, null, null);
	}

	public void drawItemStack(RenderItem renderer, @Nullable IBlockState state, int x, int y, FontRenderer fontRenderer, @Nullable String overlay)
	{
		if (state == null)
		{
			return;
		}

		Item item = Item.getItemFromBlock(CaveRenderingRegistry.getRenderBlock(state.getBlock()));

		if (item == Items.AIR)
		{
			return;
		}

		int meta = state.getBlock().getMetaFromState(state);

		drawItemStack(renderer, new ItemStack(item, 1, meta), x, y, fontRenderer, overlay);
	}

	public void drawItemStack(RenderItem renderer, @Nullable BlockMeta blockMeta, int x, int y)
	{
		drawItemStack(renderer, blockMeta, x, y, null, null);
	}

	public void drawItemStack(RenderItem renderer, @Nullable BlockMeta blockMeta, int x, int y, FontRenderer fontRenderer, @Nullable String overlay)
	{
		if (blockMeta == null)
		{
			return;
		}

		Item item = Item.getItemFromBlock(CaveRenderingRegistry.getRenderBlock(blockMeta.getBlock()));

		if (item == Items.AIR)
		{
			return;
		}

		drawItemStack(renderer, new ItemStack(item, 1, blockMeta.getMeta()), x, y, fontRenderer, overlay);
	}
}