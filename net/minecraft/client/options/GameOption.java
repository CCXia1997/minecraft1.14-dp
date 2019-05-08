package net.minecraft.client.options;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.util.NarratorManager;
import java.util.concurrent.Executor;
import net.minecraft.util.SystemUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class GameOption
{
    public static final DoubleGameOption BIOME_BLEND_RADIUS;
    public static final DoubleGameOption CHAT_HEIGHT_FOCUSED;
    public static final DoubleGameOption SATURATION;
    public static final DoubleGameOption CHAT_OPACITY;
    public static final DoubleGameOption CHAT_SCALE;
    public static final DoubleGameOption CHAT_WIDTH;
    public static final DoubleGameOption FOV;
    public static final DoubleGameOption FRAMERATE_LIMIT;
    public static final DoubleGameOption FULLSCREEN_RESOLUTION;
    public static final DoubleGameOption GAMMA;
    public static final DoubleGameOption MIPMAP_LEVELS;
    public static final DoubleGameOption MOUSE_WHEEL_SENSITIVITY;
    public static final DoubleGameOption RENDER_DISTANCE;
    public static final DoubleGameOption SENSITIVITY;
    public static final DoubleGameOption TEXT_BACKGROUND_OPACITY;
    public static final StringGameOption AO;
    public static final StringGameOption ATTACK_INDICATOR;
    public static final StringGameOption VISIBILITY;
    public static final StringGameOption GRAPHICS;
    public static final StringGameOption GUI_SCALE;
    public static final StringGameOption MAIN_HAND;
    public static final StringGameOption NARRATOR;
    public static final StringGameOption PARTICLES;
    public static final StringGameOption CLOUDS;
    public static final StringGameOption TEXT_BACKGROUND;
    public static final BooleanGameOption AUTO_JUMP;
    public static final BooleanGameOption AUTO_SUGGESTIONS;
    public static final BooleanGameOption CHAT_COLOR;
    public static final BooleanGameOption CHAT_LINKS;
    public static final BooleanGameOption CHAT_LINKS_PROMPT;
    public static final BooleanGameOption DISCRETE_MOUSE_SCROLL;
    public static final BooleanGameOption VSYNC;
    public static final BooleanGameOption ENTITY_SHADOWS;
    public static final BooleanGameOption FORCE_UNICODE_FONT;
    public static final BooleanGameOption INVERT_MOUSE;
    public static final BooleanGameOption REALMS_NOTIFICATIONS;
    public static final BooleanGameOption REDUCED_DEBUG_INFO;
    public static final BooleanGameOption SUBTITLES;
    public static final BooleanGameOption SNOOPER;
    public static final BooleanGameOption TOUCHSCREEN;
    public static final BooleanGameOption FULLSCREEN;
    public static final BooleanGameOption VIEW_BOBBING;
    private final String key;
    
    public GameOption(final String key) {
        this.key = key;
    }
    
    public abstract AbstractButtonWidget createOptionButton(final GameOptions arg1, final int arg2, final int arg3, final int arg4);
    
    public String getDisplayPrefix() {
        return I18n.translate(this.key) + ": ";
    }
    
    static {
        final double double3;
        final String string5;
        int integer2;
        BIOME_BLEND_RADIUS = new DoubleGameOption("options.biomeBlendRadius", 0.0, 7.0, 1.0f, gameOptions -> Double.valueOf(gameOptions.biomeBlendRadius), (gameOptions, double2) -> {
            gameOptions.biomeBlendRadius = MathHelper.clamp((int)(double)double2, 0, 7);
            MinecraftClient.getInstance().worldRenderer.reload();
            return;
        }, (gameOptions, doubleGameOption) -> {
            double3 = doubleGameOption.get(gameOptions);
            string5 = doubleGameOption.getDisplayPrefix();
            if (double3 == 0.0) {
                return string5 + I18n.translate("options.off");
            }
            else {
                integer2 = (int)double3 * 2 + 1;
                return string5 + integer2 + "x" + integer2;
            }
        });
        final double double4;
        CHAT_HEIGHT_FOCUSED = new DoubleGameOption("options.chat.height.focused", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.chatHeightFocused, (gameOptions, double2) -> {
            gameOptions.chatHeightFocused = double2;
            MinecraftClient.getInstance().inGameHud.getChatHud().reset();
            return;
        }, (gameOptions, doubleGameOption) -> {
            double4 = doubleGameOption.a(doubleGameOption.get(gameOptions));
            return doubleGameOption.getDisplayPrefix() + ChatHud.getHeight(double4) + "px";
        });
        final double double5;
        SATURATION = new DoubleGameOption("options.chat.height.unfocused", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.chatHeightUnfocused, (gameOptions, double2) -> {
            gameOptions.chatHeightUnfocused = double2;
            MinecraftClient.getInstance().inGameHud.getChatHud().reset();
            return;
        }, (gameOptions, doubleGameOption) -> {
            double5 = doubleGameOption.a(doubleGameOption.get(gameOptions));
            return doubleGameOption.getDisplayPrefix() + ChatHud.getHeight(double5) + "px";
        });
        final double double6;
        CHAT_OPACITY = new DoubleGameOption("options.chat.opacity", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.chatOpacity, (gameOptions, double2) -> {
            gameOptions.chatOpacity = double2;
            MinecraftClient.getInstance().inGameHud.getChatHud().reset();
            return;
        }, (gameOptions, doubleGameOption) -> {
            double6 = doubleGameOption.a(doubleGameOption.get(gameOptions));
            return doubleGameOption.getDisplayPrefix() + (int)(double6 * 90.0 + 10.0) + "%";
        });
        final double double7;
        final String string6;
        CHAT_SCALE = new DoubleGameOption("options.chat.scale", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.chatScale, (gameOptions, double2) -> {
            gameOptions.chatScale = double2;
            MinecraftClient.getInstance().inGameHud.getChatHud().reset();
            return;
        }, (gameOptions, doubleGameOption) -> {
            double7 = doubleGameOption.a(doubleGameOption.get(gameOptions));
            string6 = doubleGameOption.getDisplayPrefix();
            if (double7 == 0.0) {
                return string6 + I18n.translate("options.off");
            }
            else {
                return string6 + (int)(double7 * 100.0) + "%";
            }
        });
        final double double8;
        CHAT_WIDTH = new DoubleGameOption("options.chat.width", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.chatWidth, (gameOptions, double2) -> {
            gameOptions.chatWidth = double2;
            MinecraftClient.getInstance().inGameHud.getChatHud().reset();
            return;
        }, (gameOptions, doubleGameOption) -> {
            double8 = doubleGameOption.a(doubleGameOption.get(gameOptions));
            return doubleGameOption.getDisplayPrefix() + ChatHud.getWidth(double8) + "px";
        });
        final double double9;
        final String string7;
        FOV = new DoubleGameOption("options.fov", 30.0, 110.0, 1.0f, gameOptions -> gameOptions.fov, (gameOptions, double2) -> gameOptions.fov = double2, (gameOptions, doubleGameOption) -> {
            double9 = doubleGameOption.get(gameOptions);
            string7 = doubleGameOption.getDisplayPrefix();
            if (double9 == 70.0) {
                return string7 + I18n.translate("options.fov.min");
            }
            else if (double9 == doubleGameOption.getMax()) {
                return string7 + I18n.translate("options.fov.max");
            }
            else {
                return string7 + (int)double9;
            }
        });
        final double double10;
        final String string8;
        FRAMERATE_LIMIT = new DoubleGameOption("options.framerateLimit", 10.0, 260.0, 10.0f, gameOptions -> Double.valueOf(gameOptions.maxFps), (gameOptions, double2) -> {
            gameOptions.maxFps = (int)(double)double2;
            MinecraftClient.getInstance().window.setFramerateLimit(gameOptions.maxFps);
            return;
        }, (gameOptions, doubleGameOption) -> {
            double10 = doubleGameOption.get(gameOptions);
            string8 = doubleGameOption.getDisplayPrefix();
            if (double10 == doubleGameOption.getMax()) {
                return string8 + I18n.translate("options.framerateLimit.max");
            }
            else {
                return string8 + I18n.translate("options.framerate", (int)double10);
            }
        });
        final double double11;
        final String string9;
        FULLSCREEN_RESOLUTION = new DoubleGameOption("options.fullscreen.resolution", 0.0, 0.0, 1.0f, gameOptions -> Double.valueOf(MinecraftClient.getInstance().window.e()), (gameOptions, double2) -> MinecraftClient.getInstance().window.c((int)(double)double2), (gameOptions, doubleGameOption) -> {
            double11 = doubleGameOption.get(gameOptions);
            string9 = doubleGameOption.getDisplayPrefix();
            if (double11 == 0.0) {
                return string9 + I18n.translate("options.fullscreen.current");
            }
            else {
                return string9 + MinecraftClient.getInstance().window.b((int)double11 - 1);
            }
        });
        final double double12;
        final String string10;
        GAMMA = new DoubleGameOption("options.gamma", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.gamma, (gameOptions, double2) -> gameOptions.gamma = double2, (gameOptions, doubleGameOption) -> {
            double12 = doubleGameOption.a(doubleGameOption.get(gameOptions));
            string10 = doubleGameOption.getDisplayPrefix();
            if (double12 == 0.0) {
                return string10 + I18n.translate("options.gamma.min");
            }
            else if (double12 == 1.0) {
                return string10 + I18n.translate("options.gamma.max");
            }
            else {
                return string10 + "+" + (int)(double12 * 100.0) + "%";
            }
        });
        final double double13;
        final String string11;
        MIPMAP_LEVELS = new DoubleGameOption("options.mipmapLevels", 0.0, 4.0, 1.0f, gameOptions -> Double.valueOf(gameOptions.mipmapLevels), (gameOptions, double2) -> gameOptions.mipmapLevels = (int)(double)double2, (gameOptions, doubleGameOption) -> {
            double13 = doubleGameOption.get(gameOptions);
            string11 = doubleGameOption.getDisplayPrefix();
            if (double13 == 0.0) {
                return string11 + I18n.translate("options.off");
            }
            else {
                return string11 + (int)double13;
            }
        });
        final double double14;
        MOUSE_WHEEL_SENSITIVITY = new LogarithmicGameOption("options.mouseWheelSensitivity", 0.01, 10.0, 0.01f, gameOptions -> gameOptions.mouseWheelSensitivity, (gameOptions, double2) -> gameOptions.mouseWheelSensitivity = double2, (gameOptions, doubleGameOption) -> {
            double14 = doubleGameOption.a(doubleGameOption.get(gameOptions));
            return doubleGameOption.getDisplayPrefix() + String.format("%.2f", doubleGameOption.b(double14));
        });
        final double double15;
        RENDER_DISTANCE = new DoubleGameOption("options.renderDistance", 2.0, 16.0, 1.0f, gameOptions -> Double.valueOf(gameOptions.viewDistance), (gameOptions, double2) -> {
            gameOptions.viewDistance = (int)(double)double2;
            MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate();
            return;
        }, (gameOptions, doubleGameOption) -> {
            double15 = doubleGameOption.get(gameOptions);
            return doubleGameOption.getDisplayPrefix() + I18n.translate("options.chunks", (int)double15);
        });
        final double double16;
        final String string12;
        SENSITIVITY = new DoubleGameOption("options.sensitivity", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.mouseSensitivity, (gameOptions, double2) -> gameOptions.mouseSensitivity = double2, (gameOptions, doubleGameOption) -> {
            double16 = doubleGameOption.a(doubleGameOption.get(gameOptions));
            string12 = doubleGameOption.getDisplayPrefix();
            if (double16 == 0.0) {
                return string12 + I18n.translate("options.sensitivity.min");
            }
            else if (double16 == 1.0) {
                return string12 + I18n.translate("options.sensitivity.max");
            }
            else {
                return string12 + (int)(double16 * 200.0) + "%";
            }
        });
        TEXT_BACKGROUND_OPACITY = new DoubleGameOption("options.accessibility.text_background_opacity", 0.0, 1.0, 0.0f, gameOptions -> gameOptions.textBackgroundOpacity, (gameOptions, double2) -> {
            gameOptions.textBackgroundOpacity = double2;
            MinecraftClient.getInstance().inGameHud.getChatHud().reset();
            return;
        }, (gameOptions, doubleGameOption) -> doubleGameOption.getDisplayPrefix() + (int)(doubleGameOption.a(doubleGameOption.get(gameOptions)) * 100.0) + "%");
        AO = new StringGameOption("options.ao", (gameOptions, integer) -> {
            gameOptions.ao = AoOption.getOption(gameOptions.ao.getValue() + integer);
            MinecraftClient.getInstance().worldRenderer.reload();
            return;
        }, (gameOptions, stringGameOption) -> stringGameOption.getDisplayPrefix() + I18n.translate(gameOptions.ao.getTranslationKey()));
        ATTACK_INDICATOR = new StringGameOption("options.attackIndicator", (gameOptions, integer) -> gameOptions.attackIndicator = AttackIndicator.byId(gameOptions.attackIndicator.getId() + integer), (gameOptions, stringGameOption) -> stringGameOption.getDisplayPrefix() + I18n.translate(gameOptions.attackIndicator.getTranslationKey()));
        VISIBILITY = new StringGameOption("options.chat.visibility", (gameOptions, integer) -> gameOptions.chatVisibility = ChatVisibility.byId((gameOptions.chatVisibility.getId() + integer) % 3), (gameOptions, stringGameOption) -> stringGameOption.getDisplayPrefix() + I18n.translate(gameOptions.chatVisibility.getTranslationKey()));
        GRAPHICS = new StringGameOption("options.graphics", (gameOptions, integer) -> {
            gameOptions.fancyGraphics = !gameOptions.fancyGraphics;
            MinecraftClient.getInstance().worldRenderer.reload();
            return;
        }, (gameOptions, stringGameOption) -> {
            if (gameOptions.fancyGraphics) {
                return stringGameOption.getDisplayPrefix() + I18n.translate("options.graphics.fancy");
            }
            else {
                return stringGameOption.getDisplayPrefix() + I18n.translate("options.graphics.fast");
            }
        });
        GUI_SCALE = new StringGameOption("options.guiScale", (gameOptions, integer) -> gameOptions.guiScale = Integer.remainderUnsigned(gameOptions.guiScale + integer, MinecraftClient.getInstance().window.calculateScaleFactor(0, MinecraftClient.getInstance().forcesUnicodeFont()) + 1), (gameOptions, stringGameOption) -> stringGameOption.getDisplayPrefix() + ((gameOptions.guiScale == 0) ? I18n.translate("options.guiScale.auto") : Integer.valueOf(gameOptions.guiScale)));
        MAIN_HAND = new StringGameOption("options.mainHand", (gameOptions, integer) -> gameOptions.mainHand = gameOptions.mainHand.getOpposite(), (gameOptions, stringGameOption) -> stringGameOption.getDisplayPrefix() + gameOptions.mainHand);
        NARRATOR = new StringGameOption("options.narrator", (gameOptions, integer) -> {
            if (NarratorManager.INSTANCE.isActive()) {
                gameOptions.narrator = NarratorOption.byId(gameOptions.narrator.getId() + integer);
            }
            else {
                gameOptions.narrator = NarratorOption.a;
            }
            NarratorManager.INSTANCE.addToast(gameOptions.narrator);
            return;
        }, (gameOptions, stringGameOption) -> {
            if (NarratorManager.INSTANCE.isActive()) {
                return stringGameOption.getDisplayPrefix() + I18n.translate(gameOptions.narrator.getTranslationKey());
            }
            else {
                return stringGameOption.getDisplayPrefix() + I18n.translate("options.narrator.notavailable");
            }
        });
        PARTICLES = new StringGameOption("options.particles", (gameOptions, integer) -> gameOptions.particles = ParticlesOption.byId(gameOptions.particles.getId() + integer), (gameOptions, stringGameOption) -> stringGameOption.getDisplayPrefix() + I18n.translate(gameOptions.particles.getTranslationKey()));
        CLOUDS = new StringGameOption("options.renderClouds", (gameOptions, integer) -> gameOptions.cloudRenderMode = CloudRenderMode.getOption(gameOptions.cloudRenderMode.getValue() + integer), (gameOptions, stringGameOption) -> stringGameOption.getDisplayPrefix() + I18n.translate(gameOptions.cloudRenderMode.getTranslationKey()));
        TEXT_BACKGROUND = new StringGameOption("options.accessibility.text_background", (gameOptions, integer) -> gameOptions.backgroundForChatOnly = !gameOptions.backgroundForChatOnly, (gameOptions, stringGameOption) -> stringGameOption.getDisplayPrefix() + I18n.translate(gameOptions.backgroundForChatOnly ? "options.accessibility.text_background.chat" : "options.accessibility.text_background.everywhere"));
        AUTO_JUMP = new BooleanGameOption("options.autoJump", gameOptions -> gameOptions.autoJump, (gameOptions, boolean2) -> gameOptions.autoJump = boolean2);
        AUTO_SUGGESTIONS = new BooleanGameOption("options.autoSuggestCommands", gameOptions -> gameOptions.autoSuggestions, (gameOptions, boolean2) -> gameOptions.autoSuggestions = boolean2);
        CHAT_COLOR = new BooleanGameOption("options.chat.color", gameOptions -> gameOptions.chatColors, (gameOptions, boolean2) -> gameOptions.chatColors = boolean2);
        CHAT_LINKS = new BooleanGameOption("options.chat.links", gameOptions -> gameOptions.chatLinks, (gameOptions, boolean2) -> gameOptions.chatLinks = boolean2);
        CHAT_LINKS_PROMPT = new BooleanGameOption("options.chat.links.prompt", gameOptions -> gameOptions.chatLinksPrompt, (gameOptions, boolean2) -> gameOptions.chatLinksPrompt = boolean2);
        DISCRETE_MOUSE_SCROLL = new BooleanGameOption("options.discrete_mouse_scroll", gameOptions -> gameOptions.discreteMouseScroll, (gameOptions, boolean2) -> gameOptions.discreteMouseScroll = boolean2);
        VSYNC = new BooleanGameOption("options.vsync", gameOptions -> gameOptions.enableVsync, (gameOptions, boolean2) -> {
            gameOptions.enableVsync = boolean2;
            if (MinecraftClient.getInstance().window != null) {
                MinecraftClient.getInstance().window.setVsync(gameOptions.enableVsync);
            }
            return;
        });
        ENTITY_SHADOWS = new BooleanGameOption("options.entityShadows", gameOptions -> gameOptions.entityShadows, (gameOptions, boolean2) -> gameOptions.entityShadows = boolean2);
        final MinecraftClient minecraftClient3;
        FORCE_UNICODE_FONT = new BooleanGameOption("options.forceUnicodeFont", gameOptions -> gameOptions.forceUnicodeFont, (gameOptions, boolean2) -> {
            gameOptions.forceUnicodeFont = boolean2;
            minecraftClient3 = MinecraftClient.getInstance();
            if (minecraftClient3.getFontManager() != null) {
                minecraftClient3.getFontManager().setForceUnicodeFont(gameOptions.forceUnicodeFont, SystemUtil.getServerWorkerExecutor(), minecraftClient3);
            }
            return;
        });
        INVERT_MOUSE = new BooleanGameOption("options.invertMouse", gameOptions -> gameOptions.invertYMouse, (gameOptions, boolean2) -> gameOptions.invertYMouse = boolean2);
        REALMS_NOTIFICATIONS = new BooleanGameOption("options.realmsNotifications", gameOptions -> gameOptions.realmsNotifications, (gameOptions, boolean2) -> gameOptions.realmsNotifications = boolean2);
        REDUCED_DEBUG_INFO = new BooleanGameOption("options.reducedDebugInfo", gameOptions -> gameOptions.reducedDebugInfo, (gameOptions, boolean2) -> gameOptions.reducedDebugInfo = boolean2);
        SUBTITLES = new BooleanGameOption("options.showSubtitles", gameOptions -> gameOptions.showSubtitles, (gameOptions, boolean2) -> gameOptions.showSubtitles = boolean2);
        SNOOPER = new BooleanGameOption("options.snooper", gameOptions -> {
            if (gameOptions.snooperEnabled) {}
            return false;
        }, (gameOptions, boolean2) -> gameOptions.snooperEnabled = boolean2);
        TOUCHSCREEN = new BooleanGameOption("options.touchscreen", gameOptions -> gameOptions.touchscreen, (gameOptions, boolean2) -> gameOptions.touchscreen = boolean2);
        final MinecraftClient minecraftClient4;
        FULLSCREEN = new BooleanGameOption("options.fullscreen", gameOptions -> gameOptions.fullscreen, (gameOptions, boolean2) -> {
            gameOptions.fullscreen = boolean2;
            minecraftClient4 = MinecraftClient.getInstance();
            if (minecraftClient4.window != null && minecraftClient4.window.isFullscreen() != gameOptions.fullscreen) {
                minecraftClient4.window.toggleFullscreen();
                gameOptions.fullscreen = minecraftClient4.window.isFullscreen();
            }
            return;
        });
        VIEW_BOBBING = new BooleanGameOption("options.viewBobbing", gameOptions -> gameOptions.bobView, (gameOptions, boolean2) -> gameOptions.bobView = boolean2);
    }
}
