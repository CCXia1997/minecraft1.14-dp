package net.minecraft.client.options;

import java.lang.reflect.ParameterizedType;
import org.apache.logging.log4j.LogManager;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.resource.ResourcePackContainerManager;
import java.util.Collection;
import com.google.common.collect.ImmutableSet;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.ClientSettingsC2SPacket;
import net.minecraft.client.util.VideoMode;
import net.minecraft.SharedConstants;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;
import net.minecraft.util.TagHelper;
import net.minecraft.datafixers.DataFixTypes;
import java.util.Iterator;
import net.minecraft.util.JsonHelper;
import net.minecraft.nbt.CompoundTag;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import java.io.FileInputStream;
import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.client.util.InputUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import net.minecraft.world.Difficulty;
import java.io.File;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.sound.SoundCategory;
import java.util.Map;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.client.render.entity.PlayerModelPart;
import java.util.Set;
import javax.annotation.Nullable;
import java.util.List;
import com.google.common.base.Splitter;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GameOptions
{
    private static final Logger LOGGER;
    private static final Gson GSON;
    private static final Type STRING_LIST_TYPE;
    public static final Splitter COLON_SPLITTER;
    public double mouseSensitivity;
    public int viewDistance;
    public int maxFps;
    public CloudRenderMode cloudRenderMode;
    public boolean fancyGraphics;
    public AoOption ao;
    public List<String> resourcePacks;
    public List<String> incompatibleResourcePacks;
    public ChatVisibility chatVisibility;
    public double chatOpacity;
    public double textBackgroundOpacity;
    @Nullable
    public String fullscreenResolution;
    public boolean hideServerAddress;
    public boolean advancedItemTooltips;
    public boolean pauseOnLostFocus;
    private final Set<PlayerModelPart> enabledPlayerModelParts;
    public AbsoluteHand mainHand;
    public int overrideWidth;
    public int overrideHeight;
    public boolean heldItemTooltips;
    public double chatScale;
    public double chatWidth;
    public double chatHeightUnfocused;
    public double chatHeightFocused;
    public int mipmapLevels;
    private final Map<SoundCategory, Float> soundVolumeLevels;
    public boolean useNativeTransport;
    public AttackIndicator attackIndicator;
    public TutorialStep tutorialStep;
    public int biomeBlendRadius;
    public double mouseWheelSensitivity;
    public int glDebugVerbosity;
    public boolean autoJump;
    public boolean autoSuggestions;
    public boolean chatColors;
    public boolean chatLinks;
    public boolean chatLinksPrompt;
    public boolean enableVsync;
    public boolean entityShadows;
    public boolean forceUnicodeFont;
    public boolean invertYMouse;
    public boolean discreteMouseScroll;
    public boolean realmsNotifications;
    public boolean reducedDebugInfo;
    public boolean snooperEnabled;
    public boolean showSubtitles;
    public boolean backgroundForChatOnly;
    public boolean touchscreen;
    public boolean fullscreen;
    public boolean bobView;
    public final KeyBinding keyForward;
    public final KeyBinding keyLeft;
    public final KeyBinding keyBack;
    public final KeyBinding keyRight;
    public final KeyBinding keyJump;
    public final KeyBinding keySneak;
    public final KeyBinding keySprint;
    public final KeyBinding keyInventory;
    public final KeyBinding keySwapHands;
    public final KeyBinding keyDrop;
    public final KeyBinding keyUse;
    public final KeyBinding keyAttack;
    public final KeyBinding keyPickItem;
    public final KeyBinding keyChat;
    public final KeyBinding keyPlayerList;
    public final KeyBinding keyCommand;
    public final KeyBinding keyScreenshot;
    public final KeyBinding keyTogglePerspective;
    public final KeyBinding keySmoothCamera;
    public final KeyBinding keyFullscreen;
    public final KeyBinding keySpectatorOutlines;
    public final KeyBinding keyAdvancements;
    public final KeyBinding[] keysHotbar;
    public final KeyBinding keySaveToolbarActivator;
    public final KeyBinding keyLoadToolbarActivator;
    public final KeyBinding[] keysAll;
    protected MinecraftClient client;
    private final File optionsFile;
    public Difficulty difficulty;
    public boolean hudHidden;
    public int perspective;
    public boolean debugEnabled;
    public boolean debugProfilerEnabled;
    public boolean debugTpsEnabled;
    public String lastServer;
    public boolean smoothCameraEnabled;
    public double fov;
    public double gamma;
    public int guiScale;
    public ParticlesOption particles;
    public NarratorOption narrator;
    public String language;
    
    public GameOptions(final MinecraftClient minecraftClient, final File file) {
        this.mouseSensitivity = 0.5;
        this.viewDistance = -1;
        this.maxFps = 120;
        this.cloudRenderMode = CloudRenderMode.c;
        this.fancyGraphics = true;
        this.ao = AoOption.c;
        this.resourcePacks = Lists.newArrayList();
        this.incompatibleResourcePacks = Lists.newArrayList();
        this.chatVisibility = ChatVisibility.FULL;
        this.chatOpacity = 1.0;
        this.textBackgroundOpacity = 0.5;
        this.pauseOnLostFocus = true;
        this.enabledPlayerModelParts = Sets.<PlayerModelPart>newHashSet(PlayerModelPart.values());
        this.mainHand = AbsoluteHand.b;
        this.heldItemTooltips = true;
        this.chatScale = 1.0;
        this.chatWidth = 1.0;
        this.chatHeightUnfocused = 0.44366195797920227;
        this.chatHeightFocused = 1.0;
        this.mipmapLevels = 4;
        this.soundVolumeLevels = Maps.newEnumMap(SoundCategory.class);
        this.useNativeTransport = true;
        this.attackIndicator = AttackIndicator.b;
        this.tutorialStep = TutorialStep.MOVEMENT;
        this.biomeBlendRadius = 2;
        this.mouseWheelSensitivity = 1.0;
        this.glDebugVerbosity = 1;
        this.autoJump = true;
        this.autoSuggestions = true;
        this.chatColors = true;
        this.chatLinks = true;
        this.chatLinksPrompt = true;
        this.enableVsync = true;
        this.entityShadows = true;
        this.realmsNotifications = true;
        this.snooperEnabled = true;
        this.backgroundForChatOnly = true;
        this.bobView = true;
        this.keyForward = new KeyBinding("key.forward", 87, "key.categories.movement");
        this.keyLeft = new KeyBinding("key.left", 65, "key.categories.movement");
        this.keyBack = new KeyBinding("key.back", 83, "key.categories.movement");
        this.keyRight = new KeyBinding("key.right", 68, "key.categories.movement");
        this.keyJump = new KeyBinding("key.jump", 32, "key.categories.movement");
        this.keySneak = new KeyBinding("key.sneak", 340, "key.categories.movement");
        this.keySprint = new KeyBinding("key.sprint", 341, "key.categories.movement");
        this.keyInventory = new KeyBinding("key.inventory", 69, "key.categories.inventory");
        this.keySwapHands = new KeyBinding("key.swapHands", 70, "key.categories.inventory");
        this.keyDrop = new KeyBinding("key.drop", 81, "key.categories.inventory");
        this.keyUse = new KeyBinding("key.use", InputUtil.Type.c, 1, "key.categories.gameplay");
        this.keyAttack = new KeyBinding("key.attack", InputUtil.Type.c, 0, "key.categories.gameplay");
        this.keyPickItem = new KeyBinding("key.pickItem", InputUtil.Type.c, 2, "key.categories.gameplay");
        this.keyChat = new KeyBinding("key.chat", 84, "key.categories.multiplayer");
        this.keyPlayerList = new KeyBinding("key.playerlist", 258, "key.categories.multiplayer");
        this.keyCommand = new KeyBinding("key.command", 47, "key.categories.multiplayer");
        this.keyScreenshot = new KeyBinding("key.screenshot", 291, "key.categories.misc");
        this.keyTogglePerspective = new KeyBinding("key.togglePerspective", 294, "key.categories.misc");
        this.keySmoothCamera = new KeyBinding("key.smoothCamera", InputUtil.UNKNOWN_KEYCODE.getKeyCode(), "key.categories.misc");
        this.keyFullscreen = new KeyBinding("key.fullscreen", 300, "key.categories.misc");
        this.keySpectatorOutlines = new KeyBinding("key.spectatorOutlines", InputUtil.UNKNOWN_KEYCODE.getKeyCode(), "key.categories.misc");
        this.keyAdvancements = new KeyBinding("key.advancements", 76, "key.categories.misc");
        this.keysHotbar = new KeyBinding[] { new KeyBinding("key.hotbar.1", 49, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 50, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 51, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 52, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 53, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 54, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 55, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 56, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 57, "key.categories.inventory") };
        this.keySaveToolbarActivator = new KeyBinding("key.saveToolbarActivator", 67, "key.categories.creative");
        this.keyLoadToolbarActivator = new KeyBinding("key.loadToolbarActivator", 88, "key.categories.creative");
        this.keysAll = (KeyBinding[])ArrayUtils.addAll((Object[])new KeyBinding[] { this.keyAttack, this.keyUse, this.keyForward, this.keyLeft, this.keyBack, this.keyRight, this.keyJump, this.keySneak, this.keySprint, this.keyDrop, this.keyInventory, this.keyChat, this.keyPlayerList, this.keyPickItem, this.keyCommand, this.keyScreenshot, this.keyTogglePerspective, this.keySmoothCamera, this.keyFullscreen, this.keySpectatorOutlines, this.keySwapHands, this.keySaveToolbarActivator, this.keyLoadToolbarActivator, this.keyAdvancements }, (Object[])this.keysHotbar);
        this.difficulty = Difficulty.NORMAL;
        this.lastServer = "";
        this.fov = 70.0;
        this.particles = ParticlesOption.a;
        this.narrator = NarratorOption.a;
        this.language = "en_us";
        this.client = minecraftClient;
        this.optionsFile = new File(file, "options.txt");
        if (minecraftClient.is64Bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
            GameOption.RENDER_DISTANCE.setMax(32.0f);
        }
        else {
            GameOption.RENDER_DISTANCE.setMax(16.0f);
        }
        this.viewDistance = (minecraftClient.is64Bit() ? 12 : 8);
        this.load();
    }
    
    public float getTextBackgroundOpacity(final float fallback) {
        return this.backgroundForChatOnly ? fallback : ((float)this.textBackgroundOpacity);
    }
    
    public int getTextBackgroundColor(final float fallbackOpacity) {
        return (int)(this.getTextBackgroundOpacity(fallbackOpacity) * 255.0f) << 24 & 0xFF000000;
    }
    
    public int getTextBackgroundColor(final int fallbackColor) {
        return this.backgroundForChatOnly ? fallbackColor : ((int)(this.textBackgroundOpacity * 255.0) << 24 & 0xFF000000);
    }
    
    public void setKeyCode(final KeyBinding keyBinding, final InputUtil.KeyCode keyCode) {
        keyBinding.setKeyCode(keyCode);
        this.write();
    }
    
    public void load() {
        try {
            if (!this.optionsFile.exists()) {
                return;
            }
            this.soundVolumeLevels.clear();
            final List<String> list1 = (List<String>)IOUtils.readLines((InputStream)new FileInputStream(this.optionsFile));
            CompoundTag compoundTag2 = new CompoundTag();
            for (final String string4 : list1) {
                try {
                    final Iterator<String> iterator5 = GameOptions.COLON_SPLITTER.omitEmptyStrings().limit(2).split(string4).iterator();
                    compoundTag2.putString(iterator5.next(), iterator5.next());
                }
                catch (Exception exception2) {
                    GameOptions.LOGGER.warn("Skipping bad option: {}", string4);
                }
            }
            compoundTag2 = this.a(compoundTag2);
            for (final String string4 : compoundTag2.getKeys()) {
                final String string5 = compoundTag2.getString(string4);
                try {
                    if ("autoJump".equals(string4)) {
                        GameOption.AUTO_JUMP.set(this, string5);
                    }
                    if ("autoSuggestions".equals(string4)) {
                        GameOption.AUTO_SUGGESTIONS.set(this, string5);
                    }
                    if ("chatColors".equals(string4)) {
                        GameOption.CHAT_COLOR.set(this, string5);
                    }
                    if ("chatLinks".equals(string4)) {
                        GameOption.CHAT_LINKS.set(this, string5);
                    }
                    if ("chatLinksPrompt".equals(string4)) {
                        GameOption.CHAT_LINKS_PROMPT.set(this, string5);
                    }
                    if ("enableVsync".equals(string4)) {
                        GameOption.VSYNC.set(this, string5);
                    }
                    if ("entityShadows".equals(string4)) {
                        GameOption.ENTITY_SHADOWS.set(this, string5);
                    }
                    if ("forceUnicodeFont".equals(string4)) {
                        GameOption.FORCE_UNICODE_FONT.set(this, string5);
                    }
                    if ("discrete_mouse_scroll".equals(string4)) {
                        GameOption.DISCRETE_MOUSE_SCROLL.set(this, string5);
                    }
                    if ("invertYMouse".equals(string4)) {
                        GameOption.INVERT_MOUSE.set(this, string5);
                    }
                    if ("realmsNotifications".equals(string4)) {
                        GameOption.REALMS_NOTIFICATIONS.set(this, string5);
                    }
                    if ("reducedDebugInfo".equals(string4)) {
                        GameOption.REDUCED_DEBUG_INFO.set(this, string5);
                    }
                    if ("showSubtitles".equals(string4)) {
                        GameOption.SUBTITLES.set(this, string5);
                    }
                    if ("snooperEnabled".equals(string4)) {
                        GameOption.SNOOPER.set(this, string5);
                    }
                    if ("touchscreen".equals(string4)) {
                        GameOption.TOUCHSCREEN.set(this, string5);
                    }
                    if ("fullscreen".equals(string4)) {
                        GameOption.FULLSCREEN.set(this, string5);
                    }
                    if ("bobView".equals(string4)) {
                        GameOption.VIEW_BOBBING.set(this, string5);
                    }
                    if ("mouseSensitivity".equals(string4)) {
                        this.mouseSensitivity = parseFloat(string5);
                    }
                    if ("fov".equals(string4)) {
                        this.fov = parseFloat(string5) * 40.0f + 70.0f;
                    }
                    if ("gamma".equals(string4)) {
                        this.gamma = parseFloat(string5);
                    }
                    if ("renderDistance".equals(string4)) {
                        this.viewDistance = Integer.parseInt(string5);
                    }
                    if ("guiScale".equals(string4)) {
                        this.guiScale = Integer.parseInt(string5);
                    }
                    if ("particles".equals(string4)) {
                        this.particles = ParticlesOption.byId(Integer.parseInt(string5));
                    }
                    if ("maxFps".equals(string4)) {
                        this.maxFps = Integer.parseInt(string5);
                        if (this.client.window != null) {
                            this.client.window.setFramerateLimit(this.maxFps);
                        }
                    }
                    if ("difficulty".equals(string4)) {
                        this.difficulty = Difficulty.getDifficulty(Integer.parseInt(string5));
                    }
                    if ("fancyGraphics".equals(string4)) {
                        this.fancyGraphics = "true".equals(string5);
                    }
                    if ("tutorialStep".equals(string4)) {
                        this.tutorialStep = TutorialStep.byName(string5);
                    }
                    if ("ao".equals(string4)) {
                        if ("true".equals(string5)) {
                            this.ao = AoOption.c;
                        }
                        else if ("false".equals(string5)) {
                            this.ao = AoOption.a;
                        }
                        else {
                            this.ao = AoOption.getOption(Integer.parseInt(string5));
                        }
                    }
                    if ("renderClouds".equals(string4)) {
                        if ("true".equals(string5)) {
                            this.cloudRenderMode = CloudRenderMode.c;
                        }
                        else if ("false".equals(string5)) {
                            this.cloudRenderMode = CloudRenderMode.a;
                        }
                        else if ("fast".equals(string5)) {
                            this.cloudRenderMode = CloudRenderMode.b;
                        }
                    }
                    if ("attackIndicator".equals(string4)) {
                        this.attackIndicator = AttackIndicator.byId(Integer.parseInt(string5));
                    }
                    if ("resourcePacks".equals(string4)) {
                        this.resourcePacks = JsonHelper.<List<String>>deserialize(GameOptions.GSON, string5, GameOptions.STRING_LIST_TYPE);
                        if (this.resourcePacks == null) {
                            this.resourcePacks = Lists.newArrayList();
                        }
                    }
                    if ("incompatibleResourcePacks".equals(string4)) {
                        this.incompatibleResourcePacks = JsonHelper.<List<String>>deserialize(GameOptions.GSON, string5, GameOptions.STRING_LIST_TYPE);
                        if (this.incompatibleResourcePacks == null) {
                            this.incompatibleResourcePacks = Lists.newArrayList();
                        }
                    }
                    if ("lastServer".equals(string4)) {
                        this.lastServer = string5;
                    }
                    if ("lang".equals(string4)) {
                        this.language = string5;
                    }
                    if ("chatVisibility".equals(string4)) {
                        this.chatVisibility = ChatVisibility.byId(Integer.parseInt(string5));
                    }
                    if ("chatOpacity".equals(string4)) {
                        this.chatOpacity = parseFloat(string5);
                    }
                    if ("textBackgroundOpacity".equals(string4)) {
                        this.textBackgroundOpacity = parseFloat(string5);
                    }
                    if ("backgroundForChatOnly".equals(string4)) {
                        this.backgroundForChatOnly = "true".equals(string5);
                    }
                    if ("fullscreenResolution".equals(string4)) {
                        this.fullscreenResolution = string5;
                    }
                    if ("hideServerAddress".equals(string4)) {
                        this.hideServerAddress = "true".equals(string5);
                    }
                    if ("advancedItemTooltips".equals(string4)) {
                        this.advancedItemTooltips = "true".equals(string5);
                    }
                    if ("pauseOnLostFocus".equals(string4)) {
                        this.pauseOnLostFocus = "true".equals(string5);
                    }
                    if ("overrideHeight".equals(string4)) {
                        this.overrideHeight = Integer.parseInt(string5);
                    }
                    if ("overrideWidth".equals(string4)) {
                        this.overrideWidth = Integer.parseInt(string5);
                    }
                    if ("heldItemTooltips".equals(string4)) {
                        this.heldItemTooltips = "true".equals(string5);
                    }
                    if ("chatHeightFocused".equals(string4)) {
                        this.chatHeightFocused = parseFloat(string5);
                    }
                    if ("chatHeightUnfocused".equals(string4)) {
                        this.chatHeightUnfocused = parseFloat(string5);
                    }
                    if ("chatScale".equals(string4)) {
                        this.chatScale = parseFloat(string5);
                    }
                    if ("chatWidth".equals(string4)) {
                        this.chatWidth = parseFloat(string5);
                    }
                    if ("mipmapLevels".equals(string4)) {
                        this.mipmapLevels = Integer.parseInt(string5);
                    }
                    if ("useNativeTransport".equals(string4)) {
                        this.useNativeTransport = "true".equals(string5);
                    }
                    if ("mainHand".equals(string4)) {
                        this.mainHand = ("left".equals(string5) ? AbsoluteHand.a : AbsoluteHand.b);
                    }
                    if ("narrator".equals(string4)) {
                        this.narrator = NarratorOption.byId(Integer.parseInt(string5));
                    }
                    if ("biomeBlendRadius".equals(string4)) {
                        this.biomeBlendRadius = Integer.parseInt(string5);
                    }
                    if ("mouseWheelSensitivity".equals(string4)) {
                        this.mouseWheelSensitivity = parseFloat(string5);
                    }
                    if ("glDebugVerbosity".equals(string4)) {
                        this.glDebugVerbosity = Integer.parseInt(string5);
                    }
                    for (final KeyBinding keyBinding9 : this.keysAll) {
                        if (string4.equals("key_" + keyBinding9.getId())) {
                            keyBinding9.setKeyCode(InputUtil.fromName(string5));
                        }
                    }
                    for (final SoundCategory soundCategory9 : SoundCategory.values()) {
                        if (string4.equals("soundCategory_" + soundCategory9.getName())) {
                            this.soundVolumeLevels.put(soundCategory9, parseFloat(string5));
                        }
                    }
                    for (final PlayerModelPart playerModelPart9 : PlayerModelPart.values()) {
                        if (string4.equals("modelPart_" + playerModelPart9.getName())) {
                            this.setPlayerModelPart(playerModelPart9, "true".equals(string5));
                        }
                    }
                }
                catch (Exception exception3) {
                    GameOptions.LOGGER.warn("Skipping bad option: {}:{}", string4, string5);
                }
            }
            KeyBinding.updateKeysByCode();
        }
        catch (Exception exception1) {
            GameOptions.LOGGER.error("Failed to load options", (Throwable)exception1);
        }
    }
    
    private CompoundTag a(final CompoundTag compoundTag) {
        int integer2 = 0;
        try {
            integer2 = Integer.parseInt(compoundTag.getString("version"));
        }
        catch (RuntimeException ex) {}
        return TagHelper.update(this.client.getDataFixer(), DataFixTypes.e, compoundTag, integer2);
    }
    
    private static float parseFloat(final String string) {
        if ("true".equals(string)) {
            return 1.0f;
        }
        if ("false".equals(string)) {
            return 0.0f;
        }
        return Float.parseFloat(string);
    }
    
    public void write() {
        try (final PrintWriter printWriter1 = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8))) {
            printWriter1.println("version:" + SharedConstants.getGameVersion().getWorldVersion());
            printWriter1.println("autoJump:" + GameOption.AUTO_JUMP.get(this));
            printWriter1.println("autoSuggestions:" + GameOption.AUTO_SUGGESTIONS.get(this));
            printWriter1.println("chatColors:" + GameOption.CHAT_COLOR.get(this));
            printWriter1.println("chatLinks:" + GameOption.CHAT_LINKS.get(this));
            printWriter1.println("chatLinksPrompt:" + GameOption.CHAT_LINKS_PROMPT.get(this));
            printWriter1.println("enableVsync:" + GameOption.VSYNC.get(this));
            printWriter1.println("entityShadows:" + GameOption.ENTITY_SHADOWS.get(this));
            printWriter1.println("forceUnicodeFont:" + GameOption.FORCE_UNICODE_FONT.get(this));
            printWriter1.println("discrete_mouse_scroll:" + GameOption.DISCRETE_MOUSE_SCROLL.get(this));
            printWriter1.println("invertYMouse:" + GameOption.INVERT_MOUSE.get(this));
            printWriter1.println("realmsNotifications:" + GameOption.REALMS_NOTIFICATIONS.get(this));
            printWriter1.println("reducedDebugInfo:" + GameOption.REDUCED_DEBUG_INFO.get(this));
            printWriter1.println("snooperEnabled:" + GameOption.SNOOPER.get(this));
            printWriter1.println("showSubtitles:" + GameOption.SUBTITLES.get(this));
            printWriter1.println("touchscreen:" + GameOption.TOUCHSCREEN.get(this));
            printWriter1.println("fullscreen:" + GameOption.FULLSCREEN.get(this));
            printWriter1.println("bobView:" + GameOption.VIEW_BOBBING.get(this));
            printWriter1.println("mouseSensitivity:" + this.mouseSensitivity);
            printWriter1.println("fov:" + (this.fov - 70.0) / 40.0);
            printWriter1.println("gamma:" + this.gamma);
            printWriter1.println("renderDistance:" + this.viewDistance);
            printWriter1.println("guiScale:" + this.guiScale);
            printWriter1.println("particles:" + this.particles.getId());
            printWriter1.println("maxFps:" + this.maxFps);
            printWriter1.println("difficulty:" + this.difficulty.getId());
            printWriter1.println("fancyGraphics:" + this.fancyGraphics);
            printWriter1.println("ao:" + this.ao.getValue());
            printWriter1.println("biomeBlendRadius:" + this.biomeBlendRadius);
            switch (this.cloudRenderMode) {
                case c: {
                    printWriter1.println("renderClouds:true");
                    break;
                }
                case b: {
                    printWriter1.println("renderClouds:fast");
                    break;
                }
                case a: {
                    printWriter1.println("renderClouds:false");
                    break;
                }
            }
            printWriter1.println("resourcePacks:" + GameOptions.GSON.toJson(this.resourcePacks));
            printWriter1.println("incompatibleResourcePacks:" + GameOptions.GSON.toJson(this.incompatibleResourcePacks));
            printWriter1.println("lastServer:" + this.lastServer);
            printWriter1.println("lang:" + this.language);
            printWriter1.println("chatVisibility:" + this.chatVisibility.getId());
            printWriter1.println("chatOpacity:" + this.chatOpacity);
            printWriter1.println("textBackgroundOpacity:" + this.textBackgroundOpacity);
            printWriter1.println("backgroundForChatOnly:" + this.backgroundForChatOnly);
            if (this.client.window.getVideoMode().isPresent()) {
                printWriter1.println("fullscreenResolution:" + this.client.window.getVideoMode().get().asString());
            }
            printWriter1.println("hideServerAddress:" + this.hideServerAddress);
            printWriter1.println("advancedItemTooltips:" + this.advancedItemTooltips);
            printWriter1.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
            printWriter1.println("overrideWidth:" + this.overrideWidth);
            printWriter1.println("overrideHeight:" + this.overrideHeight);
            printWriter1.println("heldItemTooltips:" + this.heldItemTooltips);
            printWriter1.println("chatHeightFocused:" + this.chatHeightFocused);
            printWriter1.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
            printWriter1.println("chatScale:" + this.chatScale);
            printWriter1.println("chatWidth:" + this.chatWidth);
            printWriter1.println("mipmapLevels:" + this.mipmapLevels);
            printWriter1.println("useNativeTransport:" + this.useNativeTransport);
            printWriter1.println("mainHand:" + ((this.mainHand == AbsoluteHand.a) ? "left" : "right"));
            printWriter1.println("attackIndicator:" + this.attackIndicator.getId());
            printWriter1.println("narrator:" + this.narrator.getId());
            printWriter1.println("tutorialStep:" + this.tutorialStep.getName());
            printWriter1.println("mouseWheelSensitivity:" + this.mouseWheelSensitivity);
            printWriter1.println("glDebugVerbosity:" + this.glDebugVerbosity);
            for (final KeyBinding keyBinding6 : this.keysAll) {
                printWriter1.println("key_" + keyBinding6.getId() + ":" + keyBinding6.getName());
            }
            for (final SoundCategory soundCategory6 : SoundCategory.values()) {
                printWriter1.println("soundCategory_" + soundCategory6.getName() + ":" + this.getSoundVolume(soundCategory6));
            }
            for (final PlayerModelPart playerModelPart6 : PlayerModelPart.values()) {
                printWriter1.println("modelPart_" + playerModelPart6.getName() + ":" + this.enabledPlayerModelParts.contains(playerModelPart6));
            }
        }
        catch (Exception exception1) {
            GameOptions.LOGGER.error("Failed to save options", (Throwable)exception1);
        }
        this.onPlayerModelPartChange();
    }
    
    public float getSoundVolume(final SoundCategory soundCategory) {
        if (this.soundVolumeLevels.containsKey(soundCategory)) {
            return this.soundVolumeLevels.get(soundCategory);
        }
        return 1.0f;
    }
    
    public void setSoundVolume(final SoundCategory category, final float float2) {
        this.soundVolumeLevels.put(category, float2);
        this.client.getSoundManager().updateSoundVolume(category, float2);
    }
    
    public void onPlayerModelPartChange() {
        if (this.client.player != null) {
            int integer1 = 0;
            for (final PlayerModelPart playerModelPart3 : this.enabledPlayerModelParts) {
                integer1 |= playerModelPart3.getBitFlag();
            }
            this.client.player.networkHandler.sendPacket(new ClientSettingsC2SPacket(this.language, this.viewDistance, this.chatVisibility, this.chatColors, integer1, this.mainHand));
        }
    }
    
    public Set<PlayerModelPart> getEnabledPlayerModelParts() {
        return ImmutableSet.copyOf(this.enabledPlayerModelParts);
    }
    
    public void setPlayerModelPart(final PlayerModelPart part, final boolean boolean2) {
        if (boolean2) {
            this.enabledPlayerModelParts.add(part);
        }
        else {
            this.enabledPlayerModelParts.remove(part);
        }
        this.onPlayerModelPartChange();
    }
    
    public void togglePlayerModelPart(final PlayerModelPart playerModelPart) {
        if (this.getEnabledPlayerModelParts().contains(playerModelPart)) {
            this.enabledPlayerModelParts.remove(playerModelPart);
        }
        else {
            this.enabledPlayerModelParts.add(playerModelPart);
        }
        this.onPlayerModelPartChange();
    }
    
    public CloudRenderMode getCloudRenderMode() {
        if (this.viewDistance >= 4) {
            return this.cloudRenderMode;
        }
        return CloudRenderMode.a;
    }
    
    public boolean shouldUseNativeTransport() {
        return this.useNativeTransport;
    }
    
    public void addResourcePackContainersToManager(final ResourcePackContainerManager<ClientResourcePackContainer> manager) {
        manager.callCreators();
        final Set<ClientResourcePackContainer> set2 = Sets.newLinkedHashSet();
        final Iterator<String> iterator3 = this.resourcePacks.iterator();
        while (iterator3.hasNext()) {
            final String string4 = iterator3.next();
            ClientResourcePackContainer clientResourcePackContainer5 = manager.getContainer(string4);
            if (clientResourcePackContainer5 == null && !string4.startsWith("file/")) {
                clientResourcePackContainer5 = manager.getContainer("file/" + string4);
            }
            if (clientResourcePackContainer5 == null) {
                GameOptions.LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", string4);
                iterator3.remove();
            }
            else if (!clientResourcePackContainer5.getCompatibility().isCompatible() && !this.incompatibleResourcePacks.contains(string4)) {
                GameOptions.LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", string4);
                iterator3.remove();
            }
            else if (clientResourcePackContainer5.getCompatibility().isCompatible() && this.incompatibleResourcePacks.contains(string4)) {
                GameOptions.LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", string4);
                this.incompatibleResourcePacks.remove(string4);
            }
            else {
                set2.add(clientResourcePackContainer5);
            }
        }
        manager.setEnabled(set2);
    }
    
    static {
        LOGGER = LogManager.getLogger();
        GSON = new Gson();
        STRING_LIST_TYPE = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] { String.class };
            }
            
            @Override
            public Type getRawType() {
                return List.class;
            }
            
            @Override
            public Type getOwnerType() {
                return null;
            }
        };
        COLON_SPLITTER = Splitter.on(':');
    }
}
