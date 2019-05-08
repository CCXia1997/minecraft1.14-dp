package net.minecraft.client;

import java.util.AbstractList;
import org.apache.logging.log4j.LogManager;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.recipe.Recipe;
import java.util.concurrent.CompletionStage;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.dimension.TheNetherDimension;
import net.minecraft.client.gui.menu.EndCreditsScreen;
import com.mojang.authlib.GameProfile;
import com.google.common.collect.Multimap;
import java.nio.IntBuffer;
import java.nio.ByteOrder;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.item.SkullItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.world.BlockView;
import net.minecraft.client.options.AoOption;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import com.mojang.authlib.AuthenticationService;
import net.minecraft.client.gui.menu.WorkingScreen;
import java.net.SocketAddress;
import com.mojang.authlib.GameProfileRepository;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.server.network.packet.LoginHelloC2SPacket;
import net.minecraft.server.network.packet.HandshakeC2SPacket;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.gui.WorldGenerationProgressScreen;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.QueueingWorldGenerationProgressListener;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.util.UserCache;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.client.gui.ingame.ChatScreen;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.client.gui.menu.AdvancementsScreen;
import net.minecraft.client.gui.ingame.PlayerInventoryScreen;
import net.minecraft.client.gui.ingame.CreativePlayerInventoryScreen;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.Difficulty;
import net.minecraft.client.gui.ingame.SleepingChatScreen;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.client.gui.menu.PauseMenuScreen;
import net.minecraft.client.render.BufferBuilder;
import java.text.DecimalFormatSymbols;
import java.text.DecimalFormat;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ProfilerTiming;
import net.minecraft.client.gui.CloseWorldScreen;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.gui.menu.MultiplayerScreen;
import net.minecraft.text.TextComponent;
import net.minecraft.client.gui.ingame.DeathScreen;
import net.minecraft.client.texture.Sprite;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.gui.ContainerScreenRegistry;
import java.util.Locale;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import java.util.Date;
import java.text.SimpleDateFormat;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.client.recipe.book.RecipeResultCollection;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.util.DefaultedList;
import net.minecraft.client.search.IdentifierSearchableContainer;
import net.minecraft.tag.ItemTags;
import net.minecraft.item.ItemStack;
import net.minecraft.client.search.TextSearchableContainer;
import java.util.stream.Stream;
import net.minecraft.util.registry.Registry;
import net.minecraft.text.TextFormat;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.item.TooltipContext;
import java.util.Iterator;
import java.io.InputStream;
import java.util.function.LongSupplier;
import java.util.concurrent.Executor;
import net.minecraft.client.gui.SplashScreen;
import net.minecraft.client.gui.menu.ServerConnectingScreen;
import net.minecraft.client.gui.MainMenuScreen;
import net.minecraft.world.World;
import net.minecraft.client.texture.TickableTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.resource.FoliageColormapResourceSupplier;
import net.minecraft.client.resource.GrassColormapResourceSupplier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import net.minecraft.resource.ResourcePackContainer;
import java.util.List;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.client.gl.GlDebug;
import java.io.IOException;
import net.minecraft.resource.ResourceType;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.util.crash.CrashException;
import net.minecraft.client.gui.menu.OutOfMemoryScreen;
import net.minecraft.datafixers.Schemas;
import net.minecraft.text.KeybindTextComponent;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.Bootstrap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.util.UUID;
import net.minecraft.resource.FileResourcePackCreator;
import net.minecraft.resource.ResourcePackCreator;
import java.util.function.Supplier;
import net.minecraft.client.resource.RedirectedResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.SharedConstants;
import com.google.common.collect.Queues;
import net.minecraft.util.SystemUtil;
import java.util.Queue;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.PlayerSkinProvider;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.audio.MusicTracker;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.gl.GlFramebuffer;
import net.minecraft.client.render.item.ItemColorMap;
import net.minecraft.client.render.block.BlockColorMap;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.client.resource.ClientResourcePackCreator;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.profiler.DisableableProfiler;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.MetricsData;
import net.minecraft.world.level.storage.LevelStorage;
import java.net.Proxy;
import net.minecraft.client.options.HotbarStorage;
import net.minecraft.client.options.GameOptions;
import net.minecraft.util.hit.HitResult;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.gui.Overlay;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.Session;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.particle.ParticleManager;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.FirstPersonRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.WindowProvider;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import com.mojang.datafixers.DataFixer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.options.ServerEntry;
import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import net.minecraft.util.Void;
import java.util.concurrent.CompletableFuture;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.snooper.SnooperListener;
import net.minecraft.util.NonBlockingThreadExecutor;

@Environment(EnvType.CLIENT)
public class MinecraftClient extends NonBlockingThreadExecutor<Runnable> implements SnooperListener, WindowEventHandler, AutoCloseable
{
    private static final Logger LOGGER;
    public static final boolean IS_SYSTEM_MAC;
    public static final Identifier DEFAULT_TEXT_RENDERER_ID;
    public static final Identifier ALT_TEXT_RENDERER_ID;
    public static CompletableFuture<Void> voidFuture;
    public static byte[] memoryReservedForCrash;
    private static int cachedMaxTextureSize;
    private final File resourcePackDir;
    private final PropertyMap sessionPropertyMap;
    private final WindowSettings windowSettings;
    private ServerEntry currentServerEntry;
    private TextureManager textureManager;
    private static MinecraftClient instance;
    private final DataFixer dataFixer;
    public ClientPlayerInteractionManager interactionManager;
    private WindowProvider windowProvider;
    public Window window;
    private boolean crashed;
    private CrashReport crashReport;
    private boolean connectedToRealms;
    private final RenderTickCounter renderTickCounter;
    private final Snooper snooper;
    public ClientWorld world;
    public WorldRenderer worldRenderer;
    private EntityRenderDispatcher entityRenderManager;
    private ItemRenderer itemRenderer;
    private FirstPersonRenderer firstPersonRenderer;
    public ClientPlayerEntity player;
    @Nullable
    public Entity cameraEntity;
    @Nullable
    public Entity targetedEntity;
    public ParticleManager particleManager;
    private final SearchManager searchManager;
    private final Session session;
    private boolean paused;
    private float pausedTickDelta;
    public TextRenderer textRenderer;
    @Nullable
    public Screen currentScreen;
    @Nullable
    public Overlay overlay;
    public GameRenderer gameRenderer;
    public DebugRenderer debugRenderer;
    protected int attackCooldown;
    @Nullable
    private IntegratedServer server;
    private final AtomicReference<WorldGenerationProgressTracker> worldGenProgressTracker;
    public InGameHud inGameHud;
    public boolean skipGameRender;
    public HitResult hitResult;
    public GameOptions options;
    private HotbarStorage creativeHotbarStorage;
    public Mouse mouse;
    public Keyboard keyboard;
    public final File runDirectory;
    private final File assetDirectory;
    private final String gameVersion;
    private final String versionType;
    private final Proxy netProxy;
    private LevelStorage levelStorage;
    private static int currentFps;
    private int itemUseCooldown;
    private String autoConnectServerIp;
    private int autoConnectServerPort;
    public final MetricsData metricsData;
    private long lastMetricsSampleTime;
    private final boolean is64Bit;
    private final boolean isDemo;
    @Nullable
    private ClientConnection clientConnection;
    private boolean isIntegratedServerRunning;
    private final DisableableProfiler profiler;
    private ReloadableResourceManager resourceManager;
    private final ClientResourcePackCreator resourcePackCreator;
    private final ResourcePackContainerManager<ClientResourcePackContainer> resourcePackContainerManager;
    private LanguageManager languageManager;
    private BlockColorMap blockColorMap;
    private ItemColorMap itemColorMap;
    private GlFramebuffer framebuffer;
    private SpriteAtlasTexture spriteAtlas;
    private SoundManager soundManager;
    private MusicTracker musicTracker;
    private FontManager fontManager;
    private SplashTextResourceSupplier splashTextLoader;
    private final MinecraftSessionService sessionService;
    private PlayerSkinProvider skinProvider;
    private final Thread thread;
    private BakedModelManager bakedModelManager;
    private BlockRenderManager blockRenderManager;
    private PaintingManager paintingManager;
    private StatusEffectSpriteManager statusEffectSpriteManager;
    private final ToastManager toastManager;
    private final MinecraftClientGame game;
    private volatile boolean isRunning;
    public String fpsDebugString;
    public boolean F;
    private long nextDebugInfoUpdateTime;
    private int fpsCounter;
    private final TutorialManager tutorialManager;
    private boolean windowFocused;
    private final Queue<Runnable> renderTaskQueue;
    private CompletableFuture<java.lang.Void> resourceReloadFuture;
    private String openProfilerSection;
    
    public MinecraftClient(final RunArgs runArgs) {
        super("Client");
        this.renderTickCounter = new RenderTickCounter(20.0f, 0L);
        this.snooper = new Snooper("client", this, SystemUtil.getMeasuringTimeMs());
        this.searchManager = new SearchManager();
        this.worldGenProgressTracker = new AtomicReference<WorldGenerationProgressTracker>();
        this.metricsData = new MetricsData();
        this.lastMetricsSampleTime = SystemUtil.getMeasuringTimeNano();
        this.profiler = new DisableableProfiler(() -> this.renderTickCounter.ticksThisFrame);
        this.thread = Thread.currentThread();
        this.game = new MinecraftClientGame(this);
        this.isRunning = true;
        this.fpsDebugString = "";
        this.F = true;
        this.renderTaskQueue = Queues.newConcurrentLinkedQueue();
        this.openProfilerSection = "root";
        this.windowSettings = runArgs.windowSettings;
        MinecraftClient.instance = this;
        this.runDirectory = runArgs.directories.runDir;
        this.assetDirectory = runArgs.directories.assetDir;
        this.resourcePackDir = runArgs.directories.resourcePackDir;
        this.gameVersion = runArgs.game.version;
        this.versionType = runArgs.game.versionType;
        this.sessionPropertyMap = runArgs.network.profileProperties;
        this.resourcePackCreator = new ClientResourcePackCreator(new File(this.runDirectory, "server-resource-packs"), runArgs.directories.getResourceIndex());
        Object supplier2;
        (this.resourcePackContainerManager = new ResourcePackContainerManager<ClientResourcePackContainer>((string, boolean2, supplier, resourcePack, packResourceMetadata, sortingDirection) -> {
            if (packResourceMetadata.getPackFormat() < SharedConstants.getGameVersion().getPackVersion()) {
                supplier2 = (() -> new RedirectedResourcePack(supplier.get(), RedirectedResourcePack.NEW_TO_OLD_MAP));
            }
            else {
                supplier2 = supplier;
            }
            return new ClientResourcePackContainer(string, boolean2, (Supplier<ResourcePack>)supplier2, resourcePack, packResourceMetadata, sortingDirection);
        })).addCreator(this.resourcePackCreator);
        this.resourcePackContainerManager.addCreator(new FileResourcePackCreator(this.resourcePackDir));
        this.netProxy = ((runArgs.network.netProxy == null) ? Proxy.NO_PROXY : runArgs.network.netProxy);
        this.sessionService = new YggdrasilAuthenticationService(this.netProxy, UUID.randomUUID().toString()).createMinecraftSessionService();
        this.session = runArgs.network.session;
        MinecraftClient.LOGGER.info("Setting user: {}", this.session.getUsername());
        MinecraftClient.LOGGER.debug("(Session ID is {})", this.session.getSessionId());
        this.isDemo = runArgs.game.demo;
        this.is64Bit = checkIs64Bit();
        this.server = null;
        if (runArgs.autoConnect.serverIP != null) {
            this.autoConnectServerIp = runArgs.autoConnect.serverIP;
            this.autoConnectServerPort = runArgs.autoConnect.serverPort;
        }
        Bootstrap.initialize();
        Bootstrap.logMissingTranslations();
        KeybindTextComponent.b = KeyBinding::getLocalizedName;
        this.dataFixer = Schemas.getFixer();
        this.toastManager = new ToastManager(this);
        this.tutorialManager = new TutorialManager(this);
    }
    
    public void start() {
        this.isRunning = true;
        try {
            this.init();
        }
        catch (Throwable throwable1) {
            final CrashReport crashReport2 = CrashReport.create(throwable1, "Initializing game");
            crashReport2.addElement("Initialization");
            this.printCrashReport(this.populateCrashReport(crashReport2));
            return;
        }
        try {
            boolean boolean1 = false;
            while (this.isRunning) {
                if (this.crashed && this.crashReport != null) {
                    this.printCrashReport(this.crashReport);
                    return;
                }
                try {
                    this.render(!boolean1);
                }
                catch (OutOfMemoryError outOfMemoryError2) {
                    if (boolean1) {
                        throw outOfMemoryError2;
                    }
                    this.cleanUpAfterCrash();
                    this.openScreen(new OutOfMemoryScreen());
                    System.gc();
                    MinecraftClient.LOGGER.fatal("Out of memory", (Throwable)outOfMemoryError2);
                    boolean1 = true;
                }
            }
        }
        catch (CrashException crashException1) {
            this.populateCrashReport(crashException1.getReport());
            this.cleanUpAfterCrash();
            MinecraftClient.LOGGER.fatal("Reported exception thrown!", (Throwable)crashException1);
            this.printCrashReport(crashException1.getReport());
        }
        catch (Throwable throwable1) {
            final CrashReport crashReport2 = this.populateCrashReport(new CrashReport("Unexpected error", throwable1));
            MinecraftClient.LOGGER.fatal("Unreported exception thrown!", throwable1);
            this.cleanUpAfterCrash();
            this.printCrashReport(crashReport2);
        }
        finally {
            this.stop();
        }
    }
    
    private void init() {
        this.options = new GameOptions(this, this.runDirectory);
        this.creativeHotbarStorage = new HotbarStorage(this.runDirectory, this.dataFixer);
        this.startTimerHackThread();
        MinecraftClient.LOGGER.info("LWJGL Version: {}", GLX.getLWJGLVersion());
        WindowSettings windowSettings1 = this.windowSettings;
        if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
            windowSettings1 = new WindowSettings(this.options.overrideWidth, this.options.overrideHeight, windowSettings1.fullscreenWidth, windowSettings1.fullscreenHeight, windowSettings1.fullscreen);
        }
        final LongSupplier longSupplier2 = GLX.initGlfw();
        if (longSupplier2 != null) {
            SystemUtil.nanoTimeSupplier = longSupplier2;
        }
        this.windowProvider = new WindowProvider(this);
        this.window = this.windowProvider.createWindow(windowSettings1, this.options.fullscreenResolution, "Minecraft " + SharedConstants.getGameVersion().getName());
        this.onWindowFocusChanged(true);
        try {
            final InputStream inputStream3 = this.getResourcePackDownloader().getPack().open(ResourceType.ASSETS, new Identifier("icons/icon_16x16.png"));
            final InputStream inputStream4 = this.getResourcePackDownloader().getPack().open(ResourceType.ASSETS, new Identifier("icons/icon_32x32.png"));
            this.window.setIcon(inputStream3, inputStream4);
        }
        catch (IOException iOException3) {
            MinecraftClient.LOGGER.error("Couldn't set icon", (Throwable)iOException3);
        }
        this.window.setFramerateLimit(this.options.maxFps);
        (this.mouse = new Mouse(this)).setup(this.window.getHandle());
        (this.keyboard = new Keyboard(this)).setup(this.window.getHandle());
        GLX.init();
        GlDebug.enableDebug(this.options.glDebugVerbosity, false);
        (this.framebuffer = new GlFramebuffer(this.window.getFramebufferWidth(), this.window.getFramebufferHeight(), true, MinecraftClient.IS_SYSTEM_MAC)).setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.resourceManager = new ReloadableResourceManagerImpl(ResourceType.ASSETS, this.thread);
        this.options.addResourcePackContainersToManager(this.resourcePackContainerManager);
        this.resourcePackContainerManager.callCreators();
        final List<ResourcePack> list3 = this.resourcePackContainerManager.getEnabledContainers().stream().map(ResourcePackContainer::createResourcePack).collect(Collectors.toList());
        for (final ResourcePack resourcePack5 : list3) {
            this.resourceManager.addPack(resourcePack5);
        }
        this.languageManager = new LanguageManager(this.options.language);
        this.resourceManager.registerListener(this.languageManager);
        this.languageManager.reloadResources(list3);
        this.textureManager = new TextureManager(this.resourceManager);
        this.resourceManager.registerListener(this.textureManager);
        this.onResolutionChanged();
        this.skinProvider = new PlayerSkinProvider(this.textureManager, new File(this.assetDirectory, "skins"), this.sessionService);
        this.levelStorage = new LevelStorage(this.runDirectory.toPath().resolve("saves"), this.runDirectory.toPath().resolve("backups"), this.dataFixer);
        this.soundManager = new SoundManager(this.resourceManager, this.options);
        this.resourceManager.registerListener(this.soundManager);
        this.splashTextLoader = new SplashTextResourceSupplier(this.session);
        this.resourceManager.registerListener(this.splashTextLoader);
        this.musicTracker = new MusicTracker(this);
        this.fontManager = new FontManager(this.textureManager, this.forcesUnicodeFont());
        this.resourceManager.registerListener(this.fontManager.getResourceReloadListener());
        this.textRenderer = this.fontManager.getTextRenderer(MinecraftClient.DEFAULT_TEXT_RENDERER_ID);
        if (this.options.language != null) {
            this.textRenderer.setRightToLeft(this.languageManager.isRightToLeft());
        }
        this.resourceManager.registerListener(new GrassColormapResourceSupplier());
        this.resourceManager.registerListener(new FoliageColormapResourceSupplier());
        this.window.setPhase("Startup");
        GlStateManager.enableTexture();
        GlStateManager.shadeModel(7425);
        GlStateManager.clearDepth(1.0);
        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(515);
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.cullFace(GlStateManager.FaceSides.b);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        this.window.setPhase("Post startup");
        (this.spriteAtlas = new SpriteAtlasTexture("textures")).setMipLevel(this.options.mipmapLevels);
        this.textureManager.registerTextureUpdateable(SpriteAtlasTexture.BLOCK_ATLAS_TEX, this.spriteAtlas);
        this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        this.spriteAtlas.setFilter(false, this.options.mipmapLevels > 0);
        this.bakedModelManager = new BakedModelManager(this.spriteAtlas);
        this.resourceManager.registerListener(this.bakedModelManager);
        this.blockColorMap = BlockColorMap.create();
        this.itemColorMap = ItemColorMap.create(this.blockColorMap);
        this.itemRenderer = new ItemRenderer(this.textureManager, this.bakedModelManager, this.itemColorMap);
        this.entityRenderManager = new EntityRenderDispatcher(this.textureManager, this.itemRenderer, this.resourceManager);
        this.firstPersonRenderer = new FirstPersonRenderer(this);
        this.resourceManager.registerListener(this.itemRenderer);
        this.gameRenderer = new GameRenderer(this, this.resourceManager);
        this.resourceManager.registerListener(this.gameRenderer);
        this.blockRenderManager = new BlockRenderManager(this.bakedModelManager.getBlockStateMaps(), this.blockColorMap);
        this.resourceManager.registerListener(this.blockRenderManager);
        this.worldRenderer = new WorldRenderer(this);
        this.resourceManager.registerListener(this.worldRenderer);
        this.initializeSearchableContainers();
        this.resourceManager.registerListener(this.searchManager);
        GlStateManager.viewport(0, 0, this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        this.particleManager = new ParticleManager(this.world, this.textureManager);
        this.resourceManager.registerListener(this.particleManager);
        this.paintingManager = new PaintingManager(this.textureManager);
        this.resourceManager.registerListener(this.paintingManager);
        this.statusEffectSpriteManager = new StatusEffectSpriteManager(this.textureManager);
        this.resourceManager.registerListener(this.statusEffectSpriteManager);
        this.inGameHud = new InGameHud(this);
        this.debugRenderer = new DebugRenderer(this);
        GLX.setGlfwErrorCallback(this::handleGlErrorByDisableVsync);
        if (this.options.fullscreen && !this.window.isFullscreen()) {
            this.window.toggleFullscreen();
            this.options.fullscreen = this.window.isFullscreen();
        }
        this.window.setVsync(this.options.enableVsync);
        this.window.logOnGlError();
        if (this.autoConnectServerIp != null) {
            this.openScreen(new ServerConnectingScreen(new MainMenuScreen(), this, this.autoConnectServerIp, this.autoConnectServerPort));
        }
        else {
            this.openScreen(new MainMenuScreen(true));
        }
        SplashScreen.a(this);
        this.setOverlay(new SplashScreen(this, this.resourceManager.beginInitialMonitoredReload(SystemUtil.getServerWorkerExecutor(), this, CompletableFuture.<Void>completedFuture(Void.INSTANCE)), () -> {
            if (SharedConstants.isDevelopment) {
                this.checkGameData();
            }
        }, false));
    }
    
    private void initializeSearchableContainers() {
        final TextSearchableContainer<ItemStack> textSearchableContainer1 = new TextSearchableContainer<ItemStack>(itemStack -> itemStack.getTooltipText(null, TooltipContext.Default.NORMAL).stream().map(textComponent -> TextFormat.stripFormatting(textComponent.getString()).trim()).filter(string -> !string.isEmpty()), itemStack -> Stream.<Identifier>of(Registry.ITEM.getId(itemStack.getItem())));
        final IdentifierSearchableContainer<ItemStack> identifierSearchableContainer2 = new IdentifierSearchableContainer<ItemStack>(itemStack -> ItemTags.getContainer().getTagsFor(itemStack.getItem()).stream());
        final DefaultedList<ItemStack> defaultedList3 = DefaultedList.<ItemStack>create();
        for (final Item item5 : Registry.ITEM) {
            item5.appendItemsForGroup(ItemGroup.SEARCH, defaultedList3);
        }
        final IdentifierSearchableContainer<ItemStack> identifierSearchableContainer3;
        final IdentifierSearchableContainer<ItemStack> identifierSearchableContainer4;
        defaultedList3.forEach(itemStack -> {
            identifierSearchableContainer3.add(itemStack);
            identifierSearchableContainer4.add(itemStack);
            return;
        });
        final TextSearchableContainer<RecipeResultCollection> textSearchableContainer2 = new TextSearchableContainer<RecipeResultCollection>(recipeResultCollection -> recipeResultCollection.getAllRecipes().stream().flatMap(recipe -> recipe.getOutput().getTooltipText(null, TooltipContext.Default.NORMAL).stream()).map(textComponent -> TextFormat.stripFormatting(textComponent.getString()).trim()).filter(string -> !string.isEmpty()), recipeResultCollection -> recipeResultCollection.getAllRecipes().stream().map(recipe -> Registry.ITEM.getId(recipe.getOutput().getItem())));
        this.searchManager.<ItemStack>put(SearchManager.ITEM_TOOLTIP, textSearchableContainer1);
        this.searchManager.<ItemStack>put(SearchManager.ITEM_TAG, identifierSearchableContainer2);
        this.searchManager.<RecipeResultCollection>put(SearchManager.RECIPE_OUTPUT, textSearchableContainer2);
    }
    
    private void handleGlErrorByDisableVsync(final int error, final long description) {
        this.options.enableVsync = false;
        this.options.write();
    }
    
    private static boolean checkIs64Bit() {
        final String[] array;
        final String[] arr1 = array = new String[] { "sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch" };
        for (final String string5 : array) {
            final String string6 = System.getProperty(string5);
            if (string6 != null && string6.contains("64")) {
                return true;
            }
        }
        return false;
    }
    
    public GlFramebuffer getFramebuffer() {
        return this.framebuffer;
    }
    
    public String getGameVersion() {
        return this.gameVersion;
    }
    
    public String getVersionType() {
        return this.versionType;
    }
    
    private void startTimerHackThread() {
        final Thread thread1 = new Thread("Timer hack thread") {
            @Override
            public void run() {
                while (MinecraftClient.this.isRunning) {
                    try {
                        Thread.sleep(2147483647L);
                    }
                    catch (InterruptedException ex) {}
                }
            }
        };
        thread1.setDaemon(true);
        thread1.setUncaughtExceptionHandler(new UncaughtExceptionLogger(MinecraftClient.LOGGER));
        thread1.start();
    }
    
    public void setCrashReport(final CrashReport crashReport) {
        this.crashed = true;
        this.crashReport = crashReport;
    }
    
    public void printCrashReport(final CrashReport crashReport) {
        final File file2 = new File(getInstance().runDirectory, "crash-reports");
        final File file3 = new File(file2, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-client.txt");
        Bootstrap.println(crashReport.asString());
        if (crashReport.getFile() != null) {
            Bootstrap.println("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReport.getFile());
            System.exit(-1);
        }
        else if (crashReport.writeToFile(file3)) {
            Bootstrap.println("#@!@# Game crashed! Crash report saved to: #@!@# " + file3.getAbsolutePath());
            System.exit(-1);
        }
        else {
            Bootstrap.println("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            System.exit(-2);
        }
    }
    
    public boolean forcesUnicodeFont() {
        return this.options.forceUnicodeFont;
    }
    
    public CompletableFuture<java.lang.Void> reloadResources() {
        if (this.resourceReloadFuture != null) {
            return this.resourceReloadFuture;
        }
        final CompletableFuture<java.lang.Void> completableFuture1 = new CompletableFuture<java.lang.Void>();
        if (this.overlay instanceof SplashScreen) {
            return this.resourceReloadFuture = completableFuture1;
        }
        this.resourcePackContainerManager.callCreators();
        final List<ResourcePack> list2 = this.resourcePackContainerManager.getEnabledContainers().stream().map(ResourcePackContainer::createResourcePack).collect(Collectors.toList());
        final List<ResourcePack> list3;
        final CompletableFuture<Object> completableFuture2;
        this.setOverlay(new SplashScreen(this, this.resourceManager.beginMonitoredReload(SystemUtil.getServerWorkerExecutor(), this, MinecraftClient.voidFuture, list2), () -> {
            this.languageManager.reloadResources(list3);
            if (this.worldRenderer != null) {
                this.worldRenderer.reload();
            }
            completableFuture2.complete(null);
            return;
        }, true));
        return completableFuture1;
    }
    
    private void checkGameData() {
        boolean boolean1 = false;
        final BlockModels blockModels2 = this.getBlockRenderManager().getModels();
        final BakedModel bakedModel3 = blockModels2.getModelManager().getMissingModel();
        for (final Block block5 : Registry.BLOCK) {
            for (final BlockState blockState7 : block5.getStateFactory().getStates()) {
                if (blockState7.getRenderType() == BlockRenderType.c) {
                    final BakedModel bakedModel4 = blockModels2.getModel(blockState7);
                    if (bakedModel4 != bakedModel3) {
                        continue;
                    }
                    MinecraftClient.LOGGER.debug("Missing model for: {}", blockState7);
                    boolean1 = true;
                }
            }
        }
        final Sprite sprite4 = bakedModel3.getSprite();
        for (final Block block6 : Registry.BLOCK) {
            for (final BlockState blockState8 : block6.getStateFactory().getStates()) {
                final Sprite sprite5 = blockModels2.getSprite(blockState8);
                if (!blockState8.isAir() && sprite5 == sprite4) {
                    MinecraftClient.LOGGER.debug("Missing particle icon for: {}", blockState8);
                    boolean1 = true;
                }
            }
        }
        final DefaultedList<ItemStack> defaultedList5 = DefaultedList.<ItemStack>create();
        for (final Item item7 : Registry.ITEM) {
            defaultedList5.clear();
            item7.appendItemsForGroup(ItemGroup.SEARCH, defaultedList5);
            for (final ItemStack itemStack9 : defaultedList5) {
                final String string10 = itemStack9.getTranslationKey();
                final String string11 = new TranslatableTextComponent(string10, new Object[0]).getString();
                if (string11.toLowerCase(Locale.ROOT).equals(item7.getTranslationKey())) {
                    MinecraftClient.LOGGER.debug("Missing translation for: {} {} {}", itemStack9, string10, itemStack9.getItem());
                }
            }
        }
        boolean1 |= ContainerScreenRegistry.checkData();
        if (boolean1) {
            throw new IllegalStateException("Your game data is foobar, fix the errors above!");
        }
    }
    
    public LevelStorage getLevelStorage() {
        return this.levelStorage;
    }
    
    public void openScreen(@Nullable Screen screen) {
        if (this.currentScreen != null) {
            this.currentScreen.removed();
        }
        if (screen == null && this.world == null) {
            screen = new MainMenuScreen();
        }
        else if (screen == null && this.player.getHealth() <= 0.0f) {
            screen = new DeathScreen(null, this.world.getLevelProperties().isHardcore());
        }
        if (screen instanceof MainMenuScreen || screen instanceof MultiplayerScreen) {
            this.options.debugEnabled = false;
            this.inGameHud.getChatHud().clear(true);
        }
        if ((this.currentScreen = screen) != null) {
            this.mouse.unlockCursor();
            KeyBinding.unpressAll();
            screen.init(this, this.window.getScaledWidth(), this.window.getScaledHeight());
            this.skipGameRender = false;
            NarratorManager.INSTANCE.a(screen.getNarrationMessage());
        }
        else {
            this.soundManager.resumeAll();
            this.mouse.lockCursor();
        }
    }
    
    public void setOverlay(@Nullable final Overlay overlay) {
        this.overlay = overlay;
    }
    
    public void stop() {
        try {
            MinecraftClient.LOGGER.info("Stopping!");
            NarratorManager.INSTANCE.c();
            try {
                if (this.world != null) {
                    this.world.disconnect();
                }
                this.disconnect();
            }
            catch (Throwable t) {}
            if (this.currentScreen != null) {
                this.currentScreen.removed();
            }
            this.close();
        }
        finally {
            SystemUtil.nanoTimeSupplier = System::nanoTime;
            if (!this.crashed) {
                System.exit(0);
            }
        }
    }
    
    @Override
    public void close() {
        try {
            this.spriteAtlas.clear();
            this.textRenderer.close();
            this.fontManager.close();
            this.gameRenderer.close();
            this.worldRenderer.close();
            this.soundManager.close();
            this.resourcePackContainerManager.close();
            this.particleManager.clearAtlas();
            this.statusEffectSpriteManager.close();
            this.paintingManager.close();
            SystemUtil.shutdownServerWorkerExecutor();
        }
        finally {
            this.windowProvider.close();
            this.window.close();
        }
    }
    
    private void render(final boolean fullRender) {
        this.window.setPhase("Pre render");
        final long long2 = SystemUtil.getMeasuringTimeNano();
        this.profiler.startTick();
        if (GLX.shouldClose(this.window)) {
            this.scheduleStop();
        }
        if (this.resourceReloadFuture != null && !(this.overlay instanceof SplashScreen)) {
            final CompletableFuture<java.lang.Void> completableFuture4 = this.resourceReloadFuture;
            this.resourceReloadFuture = null;
            this.reloadResources().thenRun(() -> completableFuture4.complete(null));
        }
        Runnable runnable4;
        while ((runnable4 = this.renderTaskQueue.poll()) != null) {
            runnable4.run();
        }
        if (fullRender) {
            this.renderTickCounter.beginRenderTick(SystemUtil.getMeasuringTimeMs());
            this.profiler.push("scheduledExecutables");
            this.executeTaskQueue();
            this.profiler.pop();
        }
        final long long3 = SystemUtil.getMeasuringTimeNano();
        this.profiler.push("tick");
        if (fullRender) {
            for (int integer7 = 0; integer7 < Math.min(10, this.renderTickCounter.ticksThisFrame); ++integer7) {
                this.tick();
            }
        }
        this.mouse.updateMouse();
        this.window.setPhase("Render");
        GLX.pollEvents();
        final long long4 = SystemUtil.getMeasuringTimeNano() - long3;
        this.profiler.swap("sound");
        this.soundManager.updateListenerPosition(this.gameRenderer.getCamera());
        this.profiler.pop();
        this.profiler.push("render");
        GlStateManager.pushMatrix();
        GlStateManager.clear(16640, MinecraftClient.IS_SYSTEM_MAC);
        this.framebuffer.beginWrite(true);
        this.profiler.push("display");
        GlStateManager.enableTexture();
        this.profiler.pop();
        if (!this.skipGameRender) {
            this.profiler.swap("gameRenderer");
            this.gameRenderer.render(this.paused ? this.pausedTickDelta : this.renderTickCounter.tickDelta, long2, fullRender);
            this.profiler.swap("toasts");
            this.toastManager.draw();
            this.profiler.pop();
        }
        this.profiler.endTick();
        if (this.options.debugEnabled && this.options.debugProfilerEnabled && !this.options.hudHidden) {
            this.profiler.getController().enable();
            this.drawProfilerResults();
        }
        else {
            this.profiler.getController().disable();
        }
        this.framebuffer.endWrite();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.framebuffer.draw(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        GlStateManager.popMatrix();
        this.profiler.startTick();
        this.updateDisplay(true);
        Thread.yield();
        this.window.setPhase("Post render");
        ++this.fpsCounter;
        final boolean boolean9 = this.isIntegratedServerRunning() && ((this.currentScreen != null && this.currentScreen.isPauseScreen()) || (this.overlay != null && this.overlay.pausesGame())) && !this.server.isRemote();
        if (this.paused != boolean9) {
            if (this.paused) {
                this.pausedTickDelta = this.renderTickCounter.tickDelta;
            }
            else {
                this.renderTickCounter.tickDelta = this.pausedTickDelta;
            }
            this.paused = boolean9;
        }
        final long long5 = SystemUtil.getMeasuringTimeNano();
        this.metricsData.pushSample(long5 - this.lastMetricsSampleTime);
        this.lastMetricsSampleTime = long5;
        while (SystemUtil.getMeasuringTimeMs() >= this.nextDebugInfoUpdateTime + 1000L) {
            MinecraftClient.currentFps = this.fpsCounter;
            this.fpsDebugString = String.format("%d fps (%d chunk update%s) T: %s%s%s%s%s", MinecraftClient.currentFps, ChunkRenderer.chunkUpdateCount, (ChunkRenderer.chunkUpdateCount == 1) ? "" : "s", (this.options.maxFps == GameOption.FRAMERATE_LIMIT.getMax()) ? "inf" : Integer.valueOf(this.options.maxFps), this.options.enableVsync ? " vsync" : "", this.options.fancyGraphics ? "" : " fast", (this.options.cloudRenderMode == CloudRenderMode.a) ? "" : ((this.options.cloudRenderMode == CloudRenderMode.b) ? " fast-clouds" : " fancy-clouds"), GLX.useVbo() ? " vbo" : "");
            ChunkRenderer.chunkUpdateCount = 0;
            this.nextDebugInfoUpdateTime += 1000L;
            this.fpsCounter = 0;
            this.snooper.update();
            if (!this.snooper.isActive()) {
                this.snooper.a();
            }
        }
        this.profiler.endTick();
    }
    
    @Override
    public void updateDisplay(final boolean respectFramerateLimit) {
        this.profiler.push("display_update");
        this.window.setFullscreen(this.options.fullscreen);
        this.profiler.pop();
        if (respectFramerateLimit && this.isFramerateLimited()) {
            this.profiler.push("fpslimit_wait");
            this.window.waitForFramerateLimit();
            this.profiler.pop();
        }
    }
    
    @Override
    public void onResolutionChanged() {
        final int integer1 = this.window.calculateScaleFactor(this.options.guiScale, this.forcesUnicodeFont());
        this.window.setScaleFactor(integer1);
        if (this.currentScreen != null) {
            this.currentScreen.resize(this, this.window.getScaledWidth(), this.window.getScaledHeight());
        }
        final GlFramebuffer glFramebuffer2 = this.getFramebuffer();
        if (glFramebuffer2 != null) {
            glFramebuffer2.resize(this.window.getFramebufferWidth(), this.window.getFramebufferHeight(), MinecraftClient.IS_SYSTEM_MAC);
        }
        if (this.gameRenderer != null) {
            this.gameRenderer.onResized(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
        }
        if (this.mouse != null) {
            this.mouse.onResolutionChanged();
        }
    }
    
    private int getFramerateLimit() {
        if (this.world == null && (this.currentScreen != null || this.overlay != null)) {
            return 60;
        }
        return this.window.getFramerateLimit();
    }
    
    private boolean isFramerateLimited() {
        return this.getFramerateLimit() < GameOption.FRAMERATE_LIMIT.getMax();
    }
    
    public void cleanUpAfterCrash() {
        try {
            MinecraftClient.memoryReservedForCrash = new byte[0];
            this.worldRenderer.l();
        }
        catch (Throwable t) {}
        try {
            System.gc();
            if (this.isIntegratedServerRunning()) {
                this.server.stop(true);
            }
            this.disconnect(new CloseWorldScreen(new TranslatableTextComponent("menu.savingLevel", new Object[0])));
        }
        catch (Throwable t2) {}
        System.gc();
    }
    
    void handleProfilerKeyPress(int digit) {
        final ProfileResult profileResult2 = this.profiler.getController().getResults();
        final List<ProfilerTiming> list3 = profileResult2.getTimings(this.openProfilerSection);
        if (list3.isEmpty()) {
            return;
        }
        final ProfilerTiming profilerTiming4 = list3.remove(0);
        if (digit == 0) {
            if (!profilerTiming4.name.isEmpty()) {
                final int integer5 = this.openProfilerSection.lastIndexOf(46);
                if (integer5 >= 0) {
                    this.openProfilerSection = this.openProfilerSection.substring(0, integer5);
                }
            }
        }
        else if (--digit < list3.size() && !"unspecified".equals(list3.get(digit).name)) {
            if (!this.openProfilerSection.isEmpty()) {
                this.openProfilerSection += ".";
            }
            this.openProfilerSection += list3.get(digit).name;
        }
    }
    
    private void drawProfilerResults() {
        if (!this.profiler.getController().isEnabled()) {
            return;
        }
        final ProfileResult profileResult1 = this.profiler.getController().getResults();
        final List<ProfilerTiming> list2 = profileResult1.getTimings(this.openProfilerSection);
        final ProfilerTiming profilerTiming3 = list2.remove(0);
        GlStateManager.clear(256, MinecraftClient.IS_SYSTEM_MAC);
        GlStateManager.matrixMode(5889);
        GlStateManager.enableColorMaterial();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, this.window.getFramebufferWidth(), this.window.getFramebufferHeight(), 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translatef(0.0f, 0.0f, -2000.0f);
        GlStateManager.lineWidth(1.0f);
        GlStateManager.disableTexture();
        final Tessellator tessellator4 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder5 = tessellator4.getBufferBuilder();
        final int integer6 = 160;
        final int integer7 = this.window.getFramebufferWidth() - 160 - 10;
        final int integer8 = this.window.getFramebufferHeight() - 320;
        GlStateManager.enableBlend();
        bufferBuilder5.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder5.vertex(integer7 - 176.0f, integer8 - 96.0f - 16.0f, 0.0).color(200, 0, 0, 0).next();
        bufferBuilder5.vertex(integer7 - 176.0f, integer8 + 320, 0.0).color(200, 0, 0, 0).next();
        bufferBuilder5.vertex(integer7 + 176.0f, integer8 + 320, 0.0).color(200, 0, 0, 0).next();
        bufferBuilder5.vertex(integer7 + 176.0f, integer8 - 96.0f - 16.0f, 0.0).color(200, 0, 0, 0).next();
        tessellator4.draw();
        GlStateManager.disableBlend();
        double double9 = 0.0;
        for (int integer9 = 0; integer9 < list2.size(); ++integer9) {
            final ProfilerTiming profilerTiming4 = list2.get(integer9);
            final int integer10 = MathHelper.floor(profilerTiming4.parentSectionUsagePercentage / 4.0) + 1;
            bufferBuilder5.begin(6, VertexFormats.POSITION_COLOR);
            final int integer11 = profilerTiming4.getColor();
            final int integer12 = integer11 >> 16 & 0xFF;
            final int integer13 = integer11 >> 8 & 0xFF;
            final int integer14 = integer11 & 0xFF;
            bufferBuilder5.vertex(integer7, integer8, 0.0).color(integer12, integer13, integer14, 255).next();
            for (int integer15 = integer10; integer15 >= 0; --integer15) {
                final float float19 = (float)((double9 + profilerTiming4.parentSectionUsagePercentage * integer15 / integer10) * 6.2831854820251465 / 100.0);
                final float float20 = MathHelper.sin(float19) * 160.0f;
                final float float21 = MathHelper.cos(float19) * 160.0f * 0.5f;
                bufferBuilder5.vertex(integer7 + float20, integer8 - float21, 0.0).color(integer12, integer13, integer14, 255).next();
            }
            tessellator4.draw();
            bufferBuilder5.begin(5, VertexFormats.POSITION_COLOR);
            for (int integer15 = integer10; integer15 >= 0; --integer15) {
                final float float19 = (float)((double9 + profilerTiming4.parentSectionUsagePercentage * integer15 / integer10) * 6.2831854820251465 / 100.0);
                final float float20 = MathHelper.sin(float19) * 160.0f;
                final float float21 = MathHelper.cos(float19) * 160.0f * 0.5f;
                bufferBuilder5.vertex(integer7 + float20, integer8 - float21, 0.0).color(integer12 >> 1, integer13 >> 1, integer14 >> 1, 255).next();
                bufferBuilder5.vertex(integer7 + float20, integer8 - float21 + 10.0f, 0.0).color(integer12 >> 1, integer13 >> 1, integer14 >> 1, 255).next();
            }
            tessellator4.draw();
            double9 += profilerTiming4.parentSectionUsagePercentage;
        }
        final DecimalFormat decimalFormat11 = new DecimalFormat("##0.00");
        decimalFormat11.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
        GlStateManager.enableTexture();
        String string12 = "";
        if (!"unspecified".equals(profilerTiming3.name)) {
            string12 += "[0] ";
        }
        if (profilerTiming3.name.isEmpty()) {
            string12 += "ROOT ";
        }
        else {
            string12 = string12 + profilerTiming3.name + ' ';
        }
        final int integer10 = 16777215;
        this.textRenderer.drawWithShadow(string12, (float)(integer7 - 160), (float)(integer8 - 80 - 16), 16777215);
        string12 = decimalFormat11.format(profilerTiming3.totalUsagePercentage) + "%";
        this.textRenderer.drawWithShadow(string12, (float)(integer7 + 160 - this.textRenderer.getStringWidth(string12)), (float)(integer8 - 80 - 16), 16777215);
        for (int integer16 = 0; integer16 < list2.size(); ++integer16) {
            final ProfilerTiming profilerTiming5 = list2.get(integer16);
            final StringBuilder stringBuilder14 = new StringBuilder();
            if ("unspecified".equals(profilerTiming5.name)) {
                stringBuilder14.append("[?] ");
            }
            else {
                stringBuilder14.append("[").append(integer16 + 1).append("] ");
            }
            String string13 = stringBuilder14.append(profilerTiming5.name).toString();
            this.textRenderer.drawWithShadow(string13, (float)(integer7 - 160), (float)(integer8 + 80 + integer16 * 8 + 20), profilerTiming5.getColor());
            string13 = decimalFormat11.format(profilerTiming5.parentSectionUsagePercentage) + "%";
            this.textRenderer.drawWithShadow(string13, (float)(integer7 + 160 - 50 - this.textRenderer.getStringWidth(string13)), (float)(integer8 + 80 + integer16 * 8 + 20), profilerTiming5.getColor());
            string13 = decimalFormat11.format(profilerTiming5.totalUsagePercentage) + "%";
            this.textRenderer.drawWithShadow(string13, (float)(integer7 + 160 - this.textRenderer.getStringWidth(string13)), (float)(integer8 + 80 + integer16 * 8 + 20), profilerTiming5.getColor());
        }
    }
    
    public void scheduleStop() {
        this.isRunning = false;
    }
    
    public void openPauseMenu() {
        if (this.currentScreen != null) {
            return;
        }
        this.openScreen(new PauseMenuScreen());
        if (this.isIntegratedServerRunning() && !this.server.isRemote()) {
            this.soundManager.pauseAll();
        }
    }
    
    private void e(final boolean boolean1) {
        if (!boolean1) {
            this.attackCooldown = 0;
        }
        if (this.attackCooldown > 0 || this.player.isUsingItem()) {
            return;
        }
        if (boolean1 && this.hitResult != null && this.hitResult.getType() == HitResult.Type.BLOCK) {
            final BlockHitResult blockHitResult2 = (BlockHitResult)this.hitResult;
            final BlockPos blockPos3 = blockHitResult2.getBlockPos();
            if (!this.world.getBlockState(blockPos3).isAir()) {
                final Direction direction4 = blockHitResult2.getSide();
                if (this.interactionManager.b(blockPos3, direction4)) {
                    this.particleManager.addBlockBreakingParticles(blockPos3, direction4);
                    this.player.swingHand(Hand.a);
                }
            }
            return;
        }
        this.interactionManager.cancelBlockBreaking();
    }
    
    private void doAttack() {
        if (this.attackCooldown > 0) {
            return;
        }
        if (this.hitResult == null) {
            MinecraftClient.LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.interactionManager.hasLimitedAttackSpeed()) {
                this.attackCooldown = 10;
            }
            return;
        }
        if (this.player.isRiding()) {
            return;
        }
        switch (this.hitResult.getType()) {
            case ENTITY: {
                this.interactionManager.attackEntity(this.player, ((EntityHitResult)this.hitResult).getEntity());
                break;
            }
            case BLOCK: {
                final BlockHitResult blockHitResult1 = (BlockHitResult)this.hitResult;
                final BlockPos blockPos2 = blockHitResult1.getBlockPos();
                if (!this.world.getBlockState(blockPos2).isAir()) {
                    this.interactionManager.attackBlock(blockPos2, blockHitResult1.getSide());
                    break;
                }
            }
            case NONE: {
                if (this.interactionManager.hasLimitedAttackSpeed()) {
                    this.attackCooldown = 10;
                }
                this.player.dZ();
                break;
            }
        }
        this.player.swingHand(Hand.a);
    }
    
    private void doItemUse() {
        if (this.interactionManager.isBreakingBlock()) {
            return;
        }
        this.itemUseCooldown = 4;
        if (this.player.isRiding()) {
            return;
        }
        if (this.hitResult == null) {
            MinecraftClient.LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
        }
        for (final Hand hand4 : Hand.values()) {
            final ItemStack itemStack5 = this.player.getStackInHand(hand4);
            if (this.hitResult != null) {
                switch (this.hitResult.getType()) {
                    case ENTITY: {
                        final EntityHitResult entityHitResult6 = (EntityHitResult)this.hitResult;
                        final Entity entity7 = entityHitResult6.getEntity();
                        if (this.interactionManager.interactEntityAtLocation(this.player, entity7, entityHitResult6, hand4) == ActionResult.a) {
                            return;
                        }
                        if (this.interactionManager.interactEntity(this.player, entity7, hand4) == ActionResult.a) {
                            return;
                        }
                        break;
                    }
                    case BLOCK: {
                        final BlockHitResult blockHitResult8 = (BlockHitResult)this.hitResult;
                        final int integer9 = itemStack5.getAmount();
                        final ActionResult actionResult10 = this.interactionManager.interactBlock(this.player, this.world, hand4, blockHitResult8);
                        if (actionResult10 == ActionResult.a) {
                            this.player.swingHand(hand4);
                            if (!itemStack5.isEmpty() && (itemStack5.getAmount() != integer9 || this.interactionManager.hasCreativeInventory())) {
                                this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand4);
                            }
                            return;
                        }
                        if (actionResult10 == ActionResult.c) {
                            return;
                        }
                        break;
                    }
                }
            }
            if (!itemStack5.isEmpty() && this.interactionManager.interactItem(this.player, this.world, hand4) == ActionResult.a) {
                this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand4);
                return;
            }
        }
    }
    
    public MusicTracker getMusicTracker() {
        return this.musicTracker;
    }
    
    public void tick() {
        if (this.itemUseCooldown > 0) {
            --this.itemUseCooldown;
        }
        this.profiler.push("gui");
        if (!this.paused) {
            this.inGameHud.tick();
        }
        this.profiler.pop();
        this.gameRenderer.updateTargetedEntity(1.0f);
        this.tutorialManager.tick(this.world, this.hitResult);
        this.profiler.push("gameMode");
        if (!this.paused && this.world != null) {
            this.interactionManager.tick();
        }
        this.profiler.swap("textures");
        if (this.world != null) {
            this.textureManager.tick();
        }
        if (this.currentScreen == null && this.player != null) {
            if (this.player.getHealth() <= 0.0f && !(this.currentScreen instanceof DeathScreen)) {
                this.openScreen(null);
            }
            else if (this.player.isSleeping() && this.world != null) {
                this.openScreen(new SleepingChatScreen());
            }
        }
        else if (this.currentScreen != null && this.currentScreen instanceof SleepingChatScreen && !this.player.isSleeping()) {
            this.openScreen(null);
        }
        if (this.currentScreen != null) {
            this.attackCooldown = 10000;
        }
        if (this.currentScreen != null) {
            Screen.wrapScreenError(() -> this.currentScreen.tick(), "Ticking screen", this.currentScreen.getClass().getCanonicalName());
        }
        if (!this.options.debugEnabled) {
            this.inGameHud.resetDebugHudChunk();
        }
        if (this.overlay == null && (this.currentScreen == null || this.currentScreen.passEvents)) {
            this.profiler.swap("GLFW events");
            GLX.pollEvents();
            this.handleInputEvents();
            if (this.attackCooldown > 0) {
                --this.attackCooldown;
            }
        }
        if (this.world != null) {
            this.profiler.swap("gameRenderer");
            if (!this.paused) {
                this.gameRenderer.tick();
            }
            this.profiler.swap("levelRenderer");
            if (!this.paused) {
                this.worldRenderer.tick();
            }
            this.profiler.swap("level");
            if (!this.paused) {
                if (this.world.getTicksSinceLightning() > 0) {
                    this.world.setTicksSinceLightning(this.world.getTicksSinceLightning() - 1);
                }
                this.world.tickEntities();
            }
        }
        else if (this.gameRenderer.isShaderEnabled()) {
            this.gameRenderer.disableShader();
        }
        if (!this.paused) {
            this.musicTracker.tick();
        }
        this.soundManager.tick(this.paused);
        if (this.world != null) {
            if (!this.paused) {
                this.world.setMobSpawnOptions(this.world.getDifficulty() != Difficulty.PEACEFUL, true);
                this.tutorialManager.tick();
                try {
                    this.world.tick(() -> true);
                }
                catch (Throwable throwable1) {
                    final CrashReport crashReport2 = CrashReport.create(throwable1, "Exception in world tick");
                    if (this.world == null) {
                        final CrashReportSection crashReportSection3 = crashReport2.addElement("Affected level");
                        crashReportSection3.add("Problem", "Level is null!");
                    }
                    else {
                        this.world.addDetailsToCrashReport(crashReport2);
                    }
                    throw new CrashException(crashReport2);
                }
            }
            this.profiler.swap("animateTick");
            if (!this.paused && this.world != null) {
                this.world.doRandomBlockDisplayTicks(MathHelper.floor(this.player.x), MathHelper.floor(this.player.y), MathHelper.floor(this.player.z));
            }
            this.profiler.swap("particles");
            if (!this.paused) {
                this.particleManager.tick();
            }
        }
        else if (this.clientConnection != null) {
            this.profiler.swap("pendingConnection");
            this.clientConnection.tick();
        }
        this.profiler.swap("keyboard");
        this.keyboard.pollDebugCrash();
        this.profiler.pop();
    }
    
    private void handleInputEvents() {
        while (this.options.keyTogglePerspective.wasPressed()) {
            final GameOptions options = this.options;
            ++options.perspective;
            if (this.options.perspective > 2) {
                this.options.perspective = 0;
            }
            if (this.options.perspective == 0) {
                this.gameRenderer.onCameraEntitySet(this.getCameraEntity());
            }
            else if (this.options.perspective == 1) {
                this.gameRenderer.onCameraEntitySet(null);
            }
            this.worldRenderer.scheduleTerrainUpdate();
        }
        while (this.options.keySmoothCamera.wasPressed()) {
            this.options.smoothCameraEnabled = !this.options.smoothCameraEnabled;
        }
        for (int integer1 = 0; integer1 < 9; ++integer1) {
            final boolean boolean2 = this.options.keySaveToolbarActivator.isPressed();
            final boolean boolean3 = this.options.keyLoadToolbarActivator.isPressed();
            if (this.options.keysHotbar[integer1].wasPressed()) {
                if (this.player.isSpectator()) {
                    this.inGameHud.getSpectatorWidget().onHotbarKeyPress(integer1);
                }
                else if (this.player.isCreative() && this.currentScreen == null && (boolean3 || boolean2)) {
                    CreativePlayerInventoryScreen.onHotbarKeyPress(this, integer1, boolean3, boolean2);
                }
                else {
                    this.player.inventory.selectedSlot = integer1;
                }
            }
        }
        while (this.options.keyInventory.wasPressed()) {
            if (this.interactionManager.hasRidingInventory()) {
                this.player.openRidingInventory();
            }
            else {
                this.tutorialManager.onInventoryOpened();
                this.openScreen(new PlayerInventoryScreen(this.player));
            }
        }
        while (this.options.keyAdvancements.wasPressed()) {
            this.openScreen(new AdvancementsScreen(this.player.networkHandler.getAdvancementHandler()));
        }
        while (this.options.keySwapHands.wasPressed()) {
            if (!this.player.isSpectator()) {
                this.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.g, BlockPos.ORIGIN, Direction.DOWN));
            }
        }
        while (this.options.keyDrop.wasPressed()) {
            if (!this.player.isSpectator()) {
                this.player.dropSelectedItem(Screen.hasControlDown());
            }
        }
        final boolean boolean4 = this.options.chatVisibility != ChatVisibility.HIDDEN;
        if (boolean4) {
            while (this.options.keyChat.wasPressed()) {
                this.openScreen(new ChatScreen(""));
            }
            if (this.currentScreen == null && this.overlay == null && this.options.keyCommand.wasPressed()) {
                this.openScreen(new ChatScreen("/"));
            }
        }
        if (this.player.isUsingItem()) {
            if (!this.options.keyUse.isPressed()) {
                this.interactionManager.stopUsingItem(this.player);
            }
            while (this.options.keyAttack.wasPressed()) {}
            while (this.options.keyUse.wasPressed()) {}
            while (this.options.keyPickItem.wasPressed()) {}
        }
        else {
            while (this.options.keyAttack.wasPressed()) {
                this.doAttack();
            }
            while (this.options.keyUse.wasPressed()) {
                this.doItemUse();
            }
            while (this.options.keyPickItem.wasPressed()) {
                this.doItemPick();
            }
        }
        if (this.options.keyUse.isPressed() && this.itemUseCooldown == 0 && !this.player.isUsingItem()) {
            this.doItemUse();
        }
        this.e(this.currentScreen == null && this.options.keyAttack.isPressed() && this.mouse.isCursorLocked());
    }
    
    public void startIntegratedServer(final String name, final String displayName, @Nullable LevelInfo levelInfo) {
        this.disconnect();
        final WorldSaveHandler worldSaveHandler4 = this.levelStorage.createSaveHandler(name, null);
        LevelProperties levelProperties5 = worldSaveHandler4.readProperties();
        if (levelProperties5 == null && levelInfo != null) {
            levelProperties5 = new LevelProperties(levelInfo, name);
            worldSaveHandler4.saveWorld(levelProperties5);
        }
        if (levelInfo == null) {
            levelInfo = new LevelInfo(levelProperties5);
        }
        this.worldGenProgressTracker.set(null);
        try {
            final YggdrasilAuthenticationService yggdrasilAuthenticationService6 = new YggdrasilAuthenticationService(this.netProxy, UUID.randomUUID().toString());
            final MinecraftSessionService minecraftSessionService7 = yggdrasilAuthenticationService6.createMinecraftSessionService();
            final GameProfileRepository gameProfileRepository8 = yggdrasilAuthenticationService6.createProfileRepository();
            final UserCache userCache9 = new UserCache(gameProfileRepository8, new File(this.runDirectory, MinecraftServer.USER_CACHE_FILE.getName()));
            SkullBlockEntity.setUserCache(userCache9);
            SkullBlockEntity.setSessionService(minecraftSessionService7);
            UserCache.setUseRemote(false);
            final WorldGenerationProgressTracker worldGenerationProgressTracker2;
            (this.server = new IntegratedServer(this, name, displayName, levelInfo, yggdrasilAuthenticationService6, minecraftSessionService7, gameProfileRepository8, userCache9, integer -> {
                worldGenerationProgressTracker2 = new WorldGenerationProgressTracker(integer + 0);
                worldGenerationProgressTracker2.start();
                this.worldGenProgressTracker.set(worldGenerationProgressTracker2);
                return new QueueingWorldGenerationProgressListener(worldGenerationProgressTracker2, this.renderTaskQueue::add);
            })).start();
            this.isIntegratedServerRunning = true;
        }
        catch (Throwable throwable6) {
            final CrashReport crashReport7 = CrashReport.create(throwable6, "Starting integrated server");
            final CrashReportSection crashReportSection8 = crashReport7.addElement("Starting integrated server");
            crashReportSection8.add("Level ID", name);
            crashReportSection8.add("Level Name", displayName);
            throw new CrashException(crashReport7);
        }
        while (this.worldGenProgressTracker.get() == null) {
            Thread.yield();
        }
        final WorldGenerationProgressScreen worldGenerationProgressScreen6 = new WorldGenerationProgressScreen(this.worldGenProgressTracker.get());
        this.openScreen(worldGenerationProgressScreen6);
        while (!this.server.isLoading()) {
            worldGenerationProgressScreen6.tick();
            this.render(false);
            try {
                Thread.sleep(16L);
            }
            catch (InterruptedException ex) {}
            if (this.crashed && this.crashReport != null) {
                this.printCrashReport(this.crashReport);
                return;
            }
        }
        final SocketAddress socketAddress7 = this.server.getNetworkIo().bindLocal();
        final ClientConnection clientConnection8 = ClientConnection.connect(socketAddress7);
        clientConnection8.setPacketListener(new ClientLoginNetworkHandler(clientConnection8, this, null, textComponent -> {}));
        clientConnection8.send(new HandshakeC2SPacket(socketAddress7.toString(), 0, NetworkState.LOGIN));
        clientConnection8.send(new LoginHelloC2SPacket(this.getSession().getProfile()));
        this.clientConnection = clientConnection8;
    }
    
    public void joinWorld(final ClientWorld clientWorld) {
        final WorkingScreen workingScreen2 = new WorkingScreen();
        workingScreen2.a(new TranslatableTextComponent("connect.joining", new Object[0]));
        this.reset(workingScreen2);
        this.setWorld(this.world = clientWorld);
        if (!this.isIntegratedServerRunning) {
            final AuthenticationService authenticationService3 = (AuthenticationService)new YggdrasilAuthenticationService(this.netProxy, UUID.randomUUID().toString());
            final MinecraftSessionService minecraftSessionService4 = authenticationService3.createMinecraftSessionService();
            final GameProfileRepository gameProfileRepository5 = authenticationService3.createProfileRepository();
            final UserCache userCache6 = new UserCache(gameProfileRepository5, new File(this.runDirectory, MinecraftServer.USER_CACHE_FILE.getName()));
            SkullBlockEntity.setUserCache(userCache6);
            SkullBlockEntity.setSessionService(minecraftSessionService4);
            UserCache.setUseRemote(false);
        }
    }
    
    public void disconnect() {
        this.disconnect(new WorkingScreen());
    }
    
    public void disconnect(final Screen screen) {
        final ClientPlayNetworkHandler clientPlayNetworkHandler2 = this.getNetworkHandler();
        if (clientPlayNetworkHandler2 != null) {
            this.clear();
            clientPlayNetworkHandler2.clearWorld();
        }
        final IntegratedServer integratedServer3 = this.server;
        this.server = null;
        this.gameRenderer.reset();
        this.interactionManager = null;
        NarratorManager.INSTANCE.clear();
        this.reset(screen);
        if (this.world != null) {
            if (integratedServer3 != null) {
                while (!integratedServer3.isServerThreadAlive()) {
                    this.render(false);
                }
            }
            this.resourcePackCreator.clear();
            this.inGameHud.clear();
            this.setCurrentServerEntry(null);
            this.isIntegratedServerRunning = false;
            this.game.onLeaveGameSession();
        }
        this.setWorld(this.world = null);
        this.player = null;
    }
    
    private void reset(final Screen screen) {
        this.musicTracker.stop();
        this.soundManager.stopAll();
        this.cameraEntity = null;
        this.clientConnection = null;
        this.openScreen(screen);
        this.render(false);
    }
    
    private void setWorld(@Nullable final ClientWorld clientWorld) {
        if (this.worldRenderer != null) {
            this.worldRenderer.setWorld(clientWorld);
        }
        if (this.particleManager != null) {
            this.particleManager.setWorld(clientWorld);
        }
        BlockEntityRenderDispatcher.INSTANCE.setWorld(clientWorld);
    }
    
    public final boolean isDemo() {
        return this.isDemo;
    }
    
    @Nullable
    public ClientPlayNetworkHandler getNetworkHandler() {
        return (this.player == null) ? null : this.player.networkHandler;
    }
    
    public static boolean isHudEnabled() {
        return MinecraftClient.instance == null || !MinecraftClient.instance.options.hudHidden;
    }
    
    public static boolean isFancyGraphicsEnabled() {
        return MinecraftClient.instance != null && MinecraftClient.instance.options.fancyGraphics;
    }
    
    public static boolean isAmbientOcclusionEnabled() {
        return MinecraftClient.instance != null && MinecraftClient.instance.options.ao != AoOption.a;
    }
    
    private void doItemPick() {
        if (this.hitResult == null || this.hitResult.getType() == HitResult.Type.NONE) {
            return;
        }
        final boolean boolean1 = this.player.abilities.creativeMode;
        BlockEntity blockEntity2 = null;
        final HitResult.Type type4 = this.hitResult.getType();
        ItemStack itemStack3;
        if (type4 == HitResult.Type.BLOCK) {
            final BlockPos blockPos5 = ((BlockHitResult)this.hitResult).getBlockPos();
            final BlockState blockState6 = this.world.getBlockState(blockPos5);
            final Block block7 = blockState6.getBlock();
            if (blockState6.isAir()) {
                return;
            }
            itemStack3 = block7.getPickStack(this.world, blockPos5, blockState6);
            if (itemStack3.isEmpty()) {
                return;
            }
            if (boolean1 && Screen.hasControlDown() && block7.hasBlockEntity()) {
                blockEntity2 = this.world.getBlockEntity(blockPos5);
            }
        }
        else {
            if (type4 != HitResult.Type.ENTITY || !boolean1) {
                return;
            }
            final Entity entity5 = ((EntityHitResult)this.hitResult).getEntity();
            if (entity5 instanceof PaintingEntity) {
                itemStack3 = new ItemStack(Items.ko);
            }
            else if (entity5 instanceof LeadKnotEntity) {
                itemStack3 = new ItemStack(Items.oq);
            }
            else if (entity5 instanceof ItemFrameEntity) {
                final ItemFrameEntity itemFrameEntity6 = (ItemFrameEntity)entity5;
                final ItemStack itemStack4 = itemFrameEntity6.getHeldItemStack();
                if (itemStack4.isEmpty()) {
                    itemStack3 = new ItemStack(Items.nG);
                }
                else {
                    itemStack3 = itemStack4.copy();
                }
            }
            else if (entity5 instanceof AbstractMinecartEntity) {
                final AbstractMinecartEntity abstractMinecartEntity6 = (AbstractMinecartEntity)entity5;
                Item item7 = null;
                switch (abstractMinecartEntity6.getMinecartType()) {
                    case c: {
                        item7 = Items.kV;
                        break;
                    }
                    case b: {
                        item7 = Items.kU;
                        break;
                    }
                    case d: {
                        item7 = Items.oc;
                        break;
                    }
                    case f: {
                        item7 = Items.od;
                        break;
                    }
                    case g: {
                        item7 = Items.os;
                        break;
                    }
                    default: {
                        item7 = Items.kA;
                        break;
                    }
                }
                itemStack3 = new ItemStack(item7);
            }
            else if (entity5 instanceof BoatEntity) {
                itemStack3 = new ItemStack(((BoatEntity)entity5).asItem());
            }
            else if (entity5 instanceof ArmorStandEntity) {
                itemStack3 = new ItemStack(Items.ol);
            }
            else if (entity5 instanceof EnderCrystalEntity) {
                itemStack3 = new ItemStack(Items.oL);
            }
            else {
                final SpawnEggItem spawnEggItem6 = SpawnEggItem.forEntity(entity5.getType());
                if (spawnEggItem6 == null) {
                    return;
                }
                itemStack3 = new ItemStack(spawnEggItem6);
            }
        }
        if (itemStack3.isEmpty()) {
            String string5 = "";
            if (type4 == HitResult.Type.BLOCK) {
                string5 = Registry.BLOCK.getId(this.world.getBlockState(((BlockHitResult)this.hitResult).getBlockPos()).getBlock()).toString();
            }
            else if (type4 == HitResult.Type.ENTITY) {
                string5 = Registry.ENTITY_TYPE.getId(((EntityHitResult)this.hitResult).getEntity().getType()).toString();
            }
            MinecraftClient.LOGGER.warn("Picking on: [{}] {} gave null item", type4, string5);
            return;
        }
        final PlayerInventory playerInventory5 = this.player.inventory;
        if (blockEntity2 != null) {
            this.addBlockEntityNbt(itemStack3, blockEntity2);
        }
        final int integer6 = playerInventory5.getSlotWithStack(itemStack3);
        if (boolean1) {
            playerInventory5.addPickBlock(itemStack3);
            this.interactionManager.clickCreativeStack(this.player.getStackInHand(Hand.a), 36 + playerInventory5.selectedSlot);
        }
        else if (integer6 != -1) {
            if (PlayerInventory.isValidHotbarIndex(integer6)) {
                playerInventory5.selectedSlot = integer6;
            }
            else {
                this.interactionManager.pickFromInventory(integer6);
            }
        }
    }
    
    private ItemStack addBlockEntityNbt(final ItemStack stack, final BlockEntity blockEntity) {
        final CompoundTag compoundTag3 = blockEntity.toTag(new CompoundTag());
        if (stack.getItem() instanceof SkullItem && compoundTag3.containsKey("Owner")) {
            final CompoundTag compoundTag4 = compoundTag3.getCompound("Owner");
            stack.getOrCreateTag().put("SkullOwner", compoundTag4);
            return stack;
        }
        stack.setChildTag("BlockEntityTag", compoundTag3);
        final CompoundTag compoundTag4 = new CompoundTag();
        final ListTag listTag5 = new ListTag();
        ((AbstractList<StringTag>)listTag5).add(new StringTag("\"(+NBT)\""));
        compoundTag4.put("Lore", listTag5);
        stack.setChildTag("display", compoundTag4);
        return stack;
    }
    
    public CrashReport populateCrashReport(final CrashReport report) {
        final CrashReportSection crashReportSection2 = report.getSystemDetailsSection();
        crashReportSection2.add("Launched Version", () -> this.gameVersion);
        crashReportSection2.add("LWJGL", GLX::getLWJGLVersion);
        crashReportSection2.add("OpenGL", GLX::getOpenGLVersionString);
        crashReportSection2.add("GL Caps", GLX::getCapsString);
        crashReportSection2.add("Using VBOs", () -> "Yes");
        final String string1;
        crashReportSection2.add("Is Modded", () -> {
            string1 = ClientBrandRetriever.getClientModName();
            if (!"vanilla".equals(string1)) {
                return "Definitely; Client brand changed to '" + string1 + "'";
            }
            else if (MinecraftClient.class.getSigners() == null) {
                return "Very likely; Jar signature invalidated";
            }
            else {
                return "Probably not. Jar signature remains and client brand is untouched.";
            }
        });
        crashReportSection2.add("Type", "Client (map_client.txt)");
        final StringBuilder stringBuilder1;
        final Iterator<String> iterator;
        String string2;
        crashReportSection2.add("Resource Packs", () -> {
            stringBuilder1 = new StringBuilder();
            this.options.resourcePacks.iterator();
            while (iterator.hasNext()) {
                string2 = iterator.next();
                if (stringBuilder1.length() > 0) {
                    stringBuilder1.append(", ");
                }
                stringBuilder1.append(string2);
                if (this.options.incompatibleResourcePacks.contains(string2)) {
                    stringBuilder1.append(" (incompatible)");
                }
            }
            return stringBuilder1.toString();
        });
        crashReportSection2.add("Current Language", () -> this.languageManager.getLanguage().toString());
        crashReportSection2.add("CPU", GLX::getCpuInfo);
        if (this.world != null) {
            this.world.addDetailsToCrashReport(report);
        }
        return report;
    }
    
    public static MinecraftClient getInstance() {
        return MinecraftClient.instance;
    }
    
    public CompletableFuture<java.lang.Void> reloadResourcesConcurrently() {
        return this.<CompletableFuture>executeFuture(this::reloadResources).<java.lang.Void>thenCompose(completableFuture -> completableFuture);
    }
    
    @Override
    public void addSnooperInfo(final Snooper snooper) {
        snooper.addInfo("fps", MinecraftClient.currentFps);
        snooper.addInfo("vsync_enabled", this.options.enableVsync);
        final int integer2 = GLX.getRefreshRate(this.window);
        snooper.addInfo("display_frequency", integer2);
        snooper.addInfo("display_type", this.window.isFullscreen() ? "fullscreen" : "windowed");
        snooper.addInfo("run_time", (SystemUtil.getMeasuringTimeMs() - snooper.getStartTime()) / 60L * 1000L);
        snooper.addInfo("current_action", this.getCurrentAction());
        snooper.addInfo("language", (this.options.language == null) ? "en_us" : this.options.language);
        final String string3 = (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) ? "little" : "big";
        snooper.addInfo("endianness", string3);
        snooper.addInfo("subtitles", this.options.showSubtitles);
        snooper.addInfo("touch", this.options.touchscreen ? "touch" : "mouse");
        int integer3 = 0;
        for (final ClientResourcePackContainer clientResourcePackContainer6 : this.resourcePackContainerManager.getEnabledContainers()) {
            if (!clientResourcePackContainer6.canBeSorted() && !clientResourcePackContainer6.sortsTillEnd()) {
                snooper.addInfo("resource_pack[" + integer3++ + "]", clientResourcePackContainer6.getName());
            }
        }
        snooper.addInfo("resource_packs", integer3);
        if (this.server != null && this.server.getSnooper() != null) {
            snooper.addInfo("snooper_partner", this.server.getSnooper().getToken());
        }
    }
    
    private String getCurrentAction() {
        if (this.server != null) {
            if (this.server.isRemote()) {
                return "hosting_lan";
            }
            return "singleplayer";
        }
        else {
            if (this.currentServerEntry == null) {
                return "out_of_game";
            }
            if (this.currentServerEntry.isLocal()) {
                return "playing_lan";
            }
            return "multiplayer";
        }
    }
    
    public static int getMaxTextureSize() {
        if (MinecraftClient.cachedMaxTextureSize == -1) {
            for (int integer1 = 16384; integer1 > 0; integer1 >>= 1) {
                GlStateManager.texImage2D(32868, 0, 6408, integer1, integer1, 0, 6408, 5121, null);
                final int integer2 = GlStateManager.getTexLevelParameter(32868, 0, 4096);
                if (integer2 != 0) {
                    return MinecraftClient.cachedMaxTextureSize = integer1;
                }
            }
        }
        return MinecraftClient.cachedMaxTextureSize;
    }
    
    public void setCurrentServerEntry(final ServerEntry serverEntry) {
        this.currentServerEntry = serverEntry;
    }
    
    @Nullable
    public ServerEntry getCurrentServerEntry() {
        return this.currentServerEntry;
    }
    
    public boolean isInSingleplayer() {
        return this.isIntegratedServerRunning;
    }
    
    public boolean isIntegratedServerRunning() {
        return this.isIntegratedServerRunning && this.server != null;
    }
    
    @Nullable
    public IntegratedServer getServer() {
        return this.server;
    }
    
    public Snooper getSnooper() {
        return this.snooper;
    }
    
    public Session getSession() {
        return this.session;
    }
    
    public PropertyMap getSessionProperties() {
        if (this.sessionPropertyMap.isEmpty()) {
            final GameProfile gameProfile1 = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
            this.sessionPropertyMap.putAll((Multimap)gameProfile1.getProperties());
        }
        return this.sessionPropertyMap;
    }
    
    public Proxy getNetworkProxy() {
        return this.netProxy;
    }
    
    public TextureManager getTextureManager() {
        return this.textureManager;
    }
    
    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }
    
    public ResourcePackContainerManager<ClientResourcePackContainer> I() {
        return this.resourcePackContainerManager;
    }
    
    public ClientResourcePackCreator getResourcePackDownloader() {
        return this.resourcePackCreator;
    }
    
    public File getResourcePackDir() {
        return this.resourcePackDir;
    }
    
    public LanguageManager getLanguageManager() {
        return this.languageManager;
    }
    
    public SpriteAtlasTexture getSpriteAtlas() {
        return this.spriteAtlas;
    }
    
    public boolean is64Bit() {
        return this.is64Bit;
    }
    
    public boolean isPaused() {
        return this.paused;
    }
    
    public SoundManager getSoundManager() {
        return this.soundManager;
    }
    
    public MusicTracker.MusicType getMusicType() {
        if (this.currentScreen instanceof EndCreditsScreen) {
            return MusicTracker.MusicType.d;
        }
        if (this.player == null) {
            return MusicTracker.MusicType.a;
        }
        if (this.player.world.dimension instanceof TheNetherDimension) {
            return MusicTracker.MusicType.e;
        }
        if (this.player.world.dimension instanceof TheEndDimension) {
            if (this.inGameHud.getBossBarHud().shouldPlayDragonMusic()) {
                return MusicTracker.MusicType.f;
            }
            return MusicTracker.MusicType.g;
        }
        else {
            final Biome.Category category1 = this.player.world.getBiome(new BlockPos(this.player.x, this.player.y, this.player.z)).getCategory();
            if (this.musicTracker.isPlayingType(MusicTracker.MusicType.h) || (this.player.isInWater() && !this.musicTracker.isPlayingType(MusicTracker.MusicType.b) && (category1 == Biome.Category.OCEAN || category1 == Biome.Category.RIVER))) {
                return MusicTracker.MusicType.h;
            }
            if (this.player.abilities.creativeMode && this.player.abilities.allowFlying) {
                return MusicTracker.MusicType.c;
            }
            return MusicTracker.MusicType.b;
        }
    }
    
    public MinecraftSessionService getSessionService() {
        return this.sessionService;
    }
    
    public PlayerSkinProvider getSkinProvider() {
        return this.skinProvider;
    }
    
    @Nullable
    public Entity getCameraEntity() {
        return this.cameraEntity;
    }
    
    public void setCameraEntity(final Entity entity) {
        this.cameraEntity = entity;
        this.gameRenderer.onCameraEntitySet(entity);
    }
    
    @Override
    protected Thread getThread() {
        return this.thread;
    }
    
    @Override
    protected Runnable prepareRunnable(final Runnable runnable) {
        return runnable;
    }
    
    @Override
    protected boolean canRun(final Runnable runnable) {
        return true;
    }
    
    public BlockRenderManager getBlockRenderManager() {
        return this.blockRenderManager;
    }
    
    public EntityRenderDispatcher getEntityRenderManager() {
        return this.entityRenderManager;
    }
    
    public ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }
    
    public FirstPersonRenderer getFirstPersonRenderer() {
        return this.firstPersonRenderer;
    }
    
    public <T> SearchableContainer<T> getSearchableContainer(final SearchManager.Key<T> key) {
        return this.searchManager.<T>get(key);
    }
    
    public static int getCurrentFps() {
        return MinecraftClient.currentFps;
    }
    
    public MetricsData getMetricsData() {
        return this.metricsData;
    }
    
    public boolean isConnectedToRealms() {
        return this.connectedToRealms;
    }
    
    public void setConnectedToRealms(final boolean connectedToRealms) {
        this.connectedToRealms = connectedToRealms;
    }
    
    public DataFixer getDataFixer() {
        return this.dataFixer;
    }
    
    public float getTickDelta() {
        return this.renderTickCounter.tickDelta;
    }
    
    public float getLastFrameDuration() {
        return this.renderTickCounter.lastFrameDuration;
    }
    
    public BlockColorMap getBlockColorMap() {
        return this.blockColorMap;
    }
    
    public boolean hasReducedDebugInfo() {
        return (this.player != null && this.player.getReducedDebugInfo()) || this.options.reducedDebugInfo;
    }
    
    public ToastManager getToastManager() {
        return this.toastManager;
    }
    
    public TutorialManager getTutorialManager() {
        return this.tutorialManager;
    }
    
    public boolean isWindowFocused() {
        return this.windowFocused;
    }
    
    public HotbarStorage getCreativeHotbarStorage() {
        return this.creativeHotbarStorage;
    }
    
    public BakedModelManager getBakedModelManager() {
        return this.bakedModelManager;
    }
    
    public FontManager getFontManager() {
        return this.fontManager;
    }
    
    public PaintingManager getPaintingManager() {
        return this.paintingManager;
    }
    
    public StatusEffectSpriteManager getStatusEffectSpriteManager() {
        return this.statusEffectSpriteManager;
    }
    
    @Override
    public void onWindowFocusChanged(final boolean focused) {
        this.windowFocused = focused;
    }
    
    public Profiler getProfiler() {
        return this.profiler;
    }
    
    public MinecraftClientGame getGame() {
        return this.game;
    }
    
    public SplashTextResourceSupplier getSplashTextLoader() {
        return this.splashTextLoader;
    }
    
    @Nullable
    public Overlay getOverlay() {
        return this.overlay;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        IS_SYSTEM_MAC = (SystemUtil.getOperatingSystem() == SystemUtil.OperatingSystem.MAC);
        DEFAULT_TEXT_RENDERER_ID = new Identifier("default");
        ALT_TEXT_RENDERER_ID = new Identifier("alt");
        MinecraftClient.voidFuture = CompletableFuture.<Void>completedFuture(Void.INSTANCE);
        MinecraftClient.memoryReservedForCrash = new byte[10485760];
        MinecraftClient.cachedMaxTextureSize = -1;
    }
}
