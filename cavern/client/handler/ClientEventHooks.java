package cavern.client.handler;

import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import cavern.api.CavernAPI;
import cavern.api.IIceEquipment;
import cavern.client.CaveKeyBindings;
import cavern.client.gui.GuiDownloadCaveTerrain;
import cavern.client.gui.GuiLoadCaveTerrain;
import cavern.client.gui.GuiMiningRecords;
import cavern.client.gui.toasts.DelayedToast;
import cavern.config.AquaCavernConfig;
import cavern.config.CavelandConfig;
import cavern.config.CaveniaConfig;
import cavern.config.CavernConfig;
import cavern.config.Config;
import cavern.config.DisplayConfig;
import cavern.config.GeneralConfig;
import cavern.config.HugeCavernConfig;
import cavern.config.MiningAssistConfig;
import cavern.config.MirageWorldsConfig;
import cavern.core.Cavern;
import cavern.item.ItemBowCavenic;
import cavern.item.ItemBowIce;
import cavern.miningassist.MiningAssist;
import cavern.stats.MinerRank;
import cavern.stats.MinerStats;
import cavern.util.CaveUtils;
import cavern.util.Version;
import cavern.world.CaveDimensions;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventHooks
{
	public static final List<DelayedToast> DELAYED_TOAST = Lists.newArrayList();

	@SubscribeEvent
	public void onTick(ClientTickEvent event)
	{
		if (event.phase != TickEvent.Phase.END)
		{
			return;
		}

		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc.currentScreen != null && mc.currentScreen instanceof GuiMainMenu && Config.configChecker.isUpdated() && !Config.configChecker.isNotified())
		{
			String line1 = I18n.format("cavern.config.message.update");
			String line2 = I18n.format("cavern.config.message.open");
			GuiScreen parentScreen = mc.currentScreen;

			mc.displayGuiScreen(new GuiYesNo((result, id) ->
			{
				if (result)
				{
					IModGuiFactory guiFactory = FMLClientHandler.instance().getGuiFactoryFor(CaveUtils.getModContainer());
					GuiScreen guiConfig = guiFactory.createConfigGui(parentScreen);

					mc.displayGuiScreen(guiConfig);
				}
				else
				{
					mc.displayGuiScreen(parentScreen);
				}
			},
			line1, line2, -1));

			Config.configChecker.setNotified(true);
		}

		if (!DELAYED_TOAST.isEmpty())
		{
			Iterator<DelayedToast> iterator = DELAYED_TOAST.iterator();

			while (iterator.hasNext())
			{
				if (!iterator.next().onUpdate())
				{
					iterator.remove();
				}
			}
		}
	}

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		String mod = event.getModID();
		String type = event.getConfigID();

		if (mod.equals(Cavern.MODID))
		{
			if (Strings.isNullOrEmpty(type))
			{
				GeneralConfig.syncConfig();
				DisplayConfig.syncConfig();
				MiningAssistConfig.syncConfig();
				CavernConfig.syncConfig();
				HugeCavernConfig.syncConfig();
				AquaCavernConfig.syncConfig();
				CavelandConfig.syncConfig();
				CaveniaConfig.syncConfig();
				MirageWorldsConfig.syncConfig();
			}
			else switch (type)
			{
				case Configuration.CATEGORY_GENERAL:
					GeneralConfig.syncConfig();

					if (event.isWorldRunning())
					{
						GeneralConfig.refreshMiningPointItems();
						GeneralConfig.refreshMiningPoints();
						GeneralConfig.refreshCavebornBonusItems();
					}

					break;
				case "display":
					DisplayConfig.syncConfig();
					break;
				case "miningassist":
					MiningAssistConfig.syncConfig();

					if (event.isWorldRunning())
					{
						MiningAssistConfig.refreshEffectiveItems();
						MiningAssistConfig.refreshTargetBlocks();
					}

					break;
				case "dimension.cavern":
					CavernConfig.syncConfig();

					if (event.isWorldRunning())
					{
						CavernConfig.refreshTriggerItems();
						CavernConfig.refreshDungeonMobs();
					}

					break;
				case "dimension.hugeCavern":
					HugeCavernConfig.syncConfig();

					if (event.isWorldRunning())
					{
						HugeCavernConfig.refreshTriggerItems();
					}

					break;
				case "dimension.aquaCavern":
					AquaCavernConfig.syncConfig();

					if (event.isWorldRunning())
					{
						AquaCavernConfig.refreshTriggerItems();
						AquaCavernConfig.refreshDungeonMobs();
					}

					break;
				case "dimension.caveland":
					CavelandConfig.syncConfig();
					break;
				case "dimension.cavenia":
					CaveniaConfig.syncConfig();
					break;
				case "dimension.mirageWorlds":
					MirageWorldsConfig.syncConfig();
					break;
			}
		}
	}

	@SubscribeEvent
	public void onRenderGameTextOverlay(RenderGameOverlayEvent.Text event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		EntityPlayer player = mc.player;

		if (mc.gameSettings.showDebugInfo)
		{
			String name = CaveDimensions.getLocalizedName(mc.world.provider.getDimensionType());

			if (CavernAPI.dimension.isInMirageWorlds(player))
			{
				String dim = "Dim: " + I18n.format("dimension.mirageWorlds.name");

				if (Strings.isNullOrEmpty(name))
				{
					event.getLeft().add(dim);
				}
				else
				{
					event.getLeft().add(dim + " / " + name);
				}
			}
			else if (!Strings.isNullOrEmpty(name))
			{
				event.getLeft().add("Dim: " + name);
			}
		}
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event)
	{
		if (!Keyboard.getEventKeyState())
		{
			return;
		}

		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc.player == null)
		{
			return;
		}

		int key = Keyboard.getEventKey();

		if (CaveKeyBindings.KEY_MINING_RECORDS.isActiveAndMatches(key))
		{
			mc.displayGuiScreen(new GuiMiningRecords());
		}
	}

	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		GuiScreen gui = event.getGui();

		if (gui != null && GuiModList.class == gui.getClass())
		{
			String desc = I18n.format("cavern.description");

			if (!Strings.isNullOrEmpty(desc))
			{
				Cavern.metadata.description = desc;
			}
		}
		else if (DisplayConfig.customLoadingScreen && CavernAPI.dimension.isInCaveDimensions(mc.player) && (mc.currentScreen == null || !(mc.currentScreen instanceof GuiWorldSelection)))
		{
			if (gui == null)
			{
				if (mc.currentScreen != null && GuiDownloadCaveTerrain.class == mc.currentScreen.getClass())
				{
					event.setGui(new GuiLoadCaveTerrain());
				}
			}
			else if (GuiDownloadTerrain.class == gui.getClass())
			{
				event.setGui(new GuiDownloadCaveTerrain());
			}
		}
	}

	@SubscribeEvent
	public void onPlaySound(PlaySoundEvent event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		ISound sound = event.getSound();

		if (sound != null && sound.getCategory() == SoundCategory.MUSIC && CavernAPI.dimension.isInCaves(mc.player))
		{
			event.setResultSound(null);
		}
	}

	@SubscribeEvent
	public void onConnected(ClientConnectedToServerEvent event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (GeneralConfig.versionNotify)
		{
			ITextComponent message;
			ITextComponent name = new TextComponentString(Cavern.metadata.name);
			name.getStyle().setColor(TextFormatting.AQUA);

			if (Version.isOutdated())
			{
				ITextComponent latest = new TextComponentString(Version.getLatest().toString());
				latest.getStyle().setColor(TextFormatting.YELLOW);

				message = new TextComponentTranslation("cavern.version.message", name);
				message.appendText(" : ").appendSibling(latest);
				message.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Cavern.metadata.url));

				mc.ingameGUI.getChatGUI().printChatMessage(message);
			}

			message = null;

			if (Version.DEV_DEBUG)
			{
				message = new TextComponentTranslation("cavern.version.message.dev", name);
			}
			else if (Version.isBeta())
			{
				message = new TextComponentTranslation("cavern.version.message.beta", name);
			}
			else if (Version.isAlpha())
			{
				message = new TextComponentTranslation("cavern.version.message.alpha", name);
			}

			if (message != null)
			{
				mc.ingameGUI.getChatGUI().printChatMessage(message);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)
	{
		EntityPlayer player = event.player;

		if (MiningAssistConfig.miningAssistNotify)
		{
			MiningAssist assist = MiningAssist.byPlayer(player);

			if (assist != MiningAssist.DISABLED)
			{
				ITextComponent message = new TextComponentTranslation(assist.getUnlocalizedName());
				message.getStyle().setColor(TextFormatting.GRAY).setItalic(Boolean.valueOf(true));
				message = new TextComponentTranslation("cavern.miningassist.notify.message", message);

				player.sendMessage(message);
			}
		}
	}

	@SubscribeEvent
	public void onFogDensity(FogDensity event)
	{
		Entity entity = event.getEntity();
		IBlockState state = event.getState();

		if (CavernAPI.dimension.isInCaveDimensions(entity))
		{
			if (state.getMaterial() == Material.WATER)
			{
				if (entity instanceof EntityPlayer)
				{
					EntityPlayer player = (EntityPlayer)entity;

					if (MinerStats.get(player).getRank() >= MinerRank.AQUA_MINER.getRank())
					{
						GlStateManager.setFog(GlStateManager.FogMode.EXP);

						if (player.isPotionActive(MobEffects.WATER_BREATHING))
						{
							event.setDensity(0.005F);
						}
						else
						{
							event.setDensity(0.01F - EnchantmentHelper.getRespirationModifier((EntityLivingBase)entity) * 0.003F);
						}

						event.setCanceled(true);
					}
				}
			}
			else if (CavernAPI.dimension.isInCaveland(entity))
			{
				GlStateManager.setFog(GlStateManager.FogMode.EXP);

				event.setDensity((float)Math.abs(Math.pow((Math.min(entity.posY, 20) - 63) / (255 - 63), 4)));
				event.setCanceled(true);
			}
			else if (CavernAPI.dimension.isInHugeCavern(entity) || CavernAPI.dimension.isInCavenia(entity) || CavernAPI.dimension.isInDarkForest(entity))
			{
				GlStateManager.setFog(GlStateManager.FogMode.EXP);

				event.setDensity(0.005F);
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onFogColors(FogColors event)
	{
		Entity entity = event.getEntity();
		float var1 = 0.0F;

		if (CavernAPI.dimension.isInHugeCavern(entity))
		{
			var1 = 0.8F;
		}
		else if (CavernAPI.dimension.isInCaveland(entity))
		{
			var1 = 0.7F;
		}
		else if (CavernAPI.dimension.isInCavenia(entity))
		{
			var1 = 0.95F;
		}
		else if (CavernAPI.dimension.isInDarkForest(entity))
		{
			var1 = 0.75F;
		}

		if (var1 > 0.0F)
		{
			float red = event.getRed();
			float green = event.getGreen();
			float blue = event.getBlue();
			float var2 = 1.0F / red;

			if (var2 > 1.0F / green)
			{
				var2 = 1.0F / green;
			}

			if (var2 > 1.0F / blue)
			{
				var2 = 1.0F / blue;
			}

			event.setRed(red * (1.0F - var1) + red * var2 * var1);
			event.setGreen(green * (1.0F - var1) + green * var2 * var1);
			event.setBlue(blue * (1.0F - var1) + blue * var2 * var1);
		}
	}

	@SubscribeEvent
	public void onFOVUpdate(FOVUpdateEvent event)
	{
		EntityPlayer player = event.getEntity();

		if (!player.isHandActive())
		{
			return;
		}

		ItemStack using = player.getActiveItemStack();

		if (using.isEmpty())
		{
			return;
		}

		if (using.getItem() instanceof ItemBowIce)
		{
			float f = player.getItemInUseMaxCount() / 8.0F;

			if (f > 1.0F)
			{
				f = 1.0F;
			}
			else
			{
				f *= f;
			}

			event.setNewfov(event.getFov() * (1.0F - f * 0.15F));
		}

		if (using.getItem() instanceof ItemBowCavenic)
		{
			ItemBowCavenic.BowMode mode = ItemBowCavenic.BowMode.byItemStack(using);
			float zoom = mode.getZoomScale();

			if (zoom <= 0.0F)
			{
				return;
			}

			float f = player.getItemInUseMaxCount() / mode.getPullingSpeed();

			if (f > 1.0F)
			{
				f = 1.0F;
			}
			else
			{
				f *= f;
			}

			event.setNewfov(event.getFov() * (1.0F - f * zoom));
		}
	}

	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event)
	{
		ItemStack stack = event.getItemStack();

		if (stack.getItem() instanceof IIceEquipment)
		{
			IIceEquipment equip = (IIceEquipment)stack.getItem();

			if (!equip.isHiddenTooltip())
			{
				event.getToolTip().add(Cavern.proxy.translateFormat("tooltip.iceEquipment.charge", equip.getCharge(stack)));
			}
		}
	}
}