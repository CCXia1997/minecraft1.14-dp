package net.minecraft.client.render;

import org.apache.logging.log4j.LogManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.property.Property;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.Blocks;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.Heightmap;
import net.minecraft.client.options.ParticlesOption;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.world.BlockView;
import net.minecraft.block.BlockState;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.world.World;
import net.minecraft.util.hit.HitResult;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.util.crash.CrashReportSection;
import java.util.Locale;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.GameMode;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.entity.player.PlayerEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.fluid.FluidState;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.client.gl.GlProgramManager;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.CreeperEntity;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SystemUtil;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.MapRenderer;
import java.util.Random;
import net.minecraft.resource.ResourceManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.SynchronousResourceReloadListener;

@Environment(EnvType.CLIENT)
public class GameRenderer implements AutoCloseable, SynchronousResourceReloadListener
{
    private static final Logger LOGGER;
    private static final Identifier RAIN_LOC;
    private static final Identifier SNOW_LOC;
    private final MinecraftClient client;
    private final ResourceManager resourceContainer;
    private final Random random;
    private float viewDistance;
    public final FirstPersonRenderer firstPersonRenderer;
    private final MapRenderer mapRenderer;
    private int ticks;
    private float movementFovMultiplier;
    private float lastMovementFovMultiplier;
    private float skyDarkness;
    private float lastSkyDarkness;
    private boolean renderHand;
    private boolean blockOutlineEnabled;
    private long lastWorldIconUpdate;
    private long lastWindowFocusedTime;
    private final LightmapTextureManager lightmapTextureManager;
    private int u;
    private final float[] v;
    private final float[] w;
    private final BackgroundRenderer backgroundRenderer;
    private boolean y;
    private double z;
    private double A;
    private double B;
    private ItemStack floatingItem;
    private int floatingItemTimeLeft;
    private float floatingItemWidth;
    private float floatingItemHeight;
    private ShaderEffect shader;
    private static final Identifier[] SHADERS_LOCATIONS;
    public static final int SHADER_COUNT;
    private int forcedShaderIndex;
    private boolean shadersEnabled;
    private int K;
    private final Camera camera;
    
    public GameRenderer(final MinecraftClient client, final ResourceManager resourceManager) {
        this.random = new Random();
        this.renderHand = true;
        this.blockOutlineEnabled = true;
        this.lastWindowFocusedTime = SystemUtil.getMeasuringTimeMs();
        this.v = new float[1024];
        this.w = new float[1024];
        this.z = 1.0;
        this.forcedShaderIndex = GameRenderer.SHADER_COUNT;
        this.camera = new Camera();
        this.client = client;
        this.resourceContainer = resourceManager;
        this.firstPersonRenderer = client.getFirstPersonRenderer();
        this.mapRenderer = new MapRenderer(client.getTextureManager());
        this.lightmapTextureManager = new LightmapTextureManager(this);
        this.backgroundRenderer = new BackgroundRenderer(this);
        this.shader = null;
        for (int integer3 = 0; integer3 < 32; ++integer3) {
            for (int integer4 = 0; integer4 < 32; ++integer4) {
                final float float5 = (float)(integer4 - 16);
                final float float6 = (float)(integer3 - 16);
                final float float7 = MathHelper.sqrt(float5 * float5 + float6 * float6);
                this.v[integer3 << 5 | integer4] = -float6 / float7;
                this.w[integer3 << 5 | integer4] = float5 / float7;
            }
        }
    }
    
    @Override
    public void close() {
        this.lightmapTextureManager.close();
        this.mapRenderer.close();
        this.disableShader();
    }
    
    public boolean isShaderEnabled() {
        return GLX.usePostProcess && this.shader != null;
    }
    
    public void disableShader() {
        if (this.shader != null) {
            this.shader.close();
        }
        this.shader = null;
        this.forcedShaderIndex = GameRenderer.SHADER_COUNT;
    }
    
    public void toggleShadersEnabled() {
        this.shadersEnabled = !this.shadersEnabled;
    }
    
    public void onCameraEntitySet(@Nullable final Entity entity) {
        if (!GLX.usePostProcess) {
            return;
        }
        if (this.shader != null) {
            this.shader.close();
        }
        this.shader = null;
        if (entity instanceof CreeperEntity) {
            this.loadShader(new Identifier("shaders/post/creeper.json"));
        }
        else if (entity instanceof SpiderEntity) {
            this.loadShader(new Identifier("shaders/post/spider.json"));
        }
        else if (entity instanceof EndermanEntity) {
            this.loadShader(new Identifier("shaders/post/invert.json"));
        }
    }
    
    private void loadShader(final Identifier identifier) {
        if (this.shader != null) {
            this.shader.close();
        }
        try {
            (this.shader = new ShaderEffect(this.client.getTextureManager(), this.resourceContainer, this.client.getFramebuffer(), identifier)).setupDimensions(this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight());
            this.shadersEnabled = true;
        }
        catch (IOException iOException2) {
            GameRenderer.LOGGER.warn("Failed to load shader: {}", identifier, iOException2);
            this.forcedShaderIndex = GameRenderer.SHADER_COUNT;
            this.shadersEnabled = false;
        }
        catch (JsonSyntaxException jsonSyntaxException2) {
            GameRenderer.LOGGER.warn("Failed to load shader: {}", identifier, jsonSyntaxException2);
            this.forcedShaderIndex = GameRenderer.SHADER_COUNT;
            this.shadersEnabled = false;
        }
    }
    
    @Override
    public void apply(final ResourceManager manager) {
        if (this.shader != null) {
            this.shader.close();
        }
        this.shader = null;
        if (this.forcedShaderIndex == GameRenderer.SHADER_COUNT) {
            this.onCameraEntitySet(this.client.getCameraEntity());
        }
        else {
            this.loadShader(GameRenderer.SHADERS_LOCATIONS[this.forcedShaderIndex]);
        }
    }
    
    public void tick() {
        if (GLX.usePostProcess && GlProgramManager.getInstance() == null) {
            GlProgramManager.init();
        }
        this.updateMovementFovMultiplier();
        this.lightmapTextureManager.tick();
        if (this.client.getCameraEntity() == null) {
            this.client.setCameraEntity(this.client.player);
        }
        this.camera.updateEyeHeight();
        ++this.ticks;
        this.firstPersonRenderer.updateHeldItems();
        this.renderRain();
        this.lastSkyDarkness = this.skyDarkness;
        if (this.client.inGameHud.getBossBarHud().shouldDarkenSky()) {
            this.skyDarkness += 0.05f;
            if (this.skyDarkness > 1.0f) {
                this.skyDarkness = 1.0f;
            }
        }
        else if (this.skyDarkness > 0.0f) {
            this.skyDarkness -= 0.0125f;
        }
        if (this.floatingItemTimeLeft > 0) {
            --this.floatingItemTimeLeft;
            if (this.floatingItemTimeLeft == 0) {
                this.floatingItem = null;
            }
        }
    }
    
    public ShaderEffect getShader() {
        return this.shader;
    }
    
    public void onResized(final int integer1, final int integer2) {
        if (!GLX.usePostProcess) {
            return;
        }
        if (this.shader != null) {
            this.shader.setupDimensions(integer1, integer2);
        }
        this.client.worldRenderer.onResized(integer1, integer2);
    }
    
    public void updateTargetedEntity(final float tickDelta) {
        final Entity entity2 = this.client.getCameraEntity();
        if (entity2 == null) {
            return;
        }
        if (this.client.world == null) {
            return;
        }
        this.client.getProfiler().push("pick");
        this.client.targetedEntity = null;
        double double3 = this.client.interactionManager.getReachDistance();
        this.client.hitResult = entity2.rayTrace(double3, tickDelta, false);
        final Vec3d vec3d5 = entity2.getCameraPosVec(tickDelta);
        boolean boolean6 = false;
        final int integer7 = 3;
        double double4 = double3;
        if (this.client.interactionManager.hasExtendedReach()) {
            double4 = (double3 = 6.0);
        }
        else {
            if (double4 > 3.0) {
                boolean6 = true;
            }
            double3 = double4;
        }
        double4 *= double4;
        if (this.client.hitResult != null) {
            double4 = this.client.hitResult.getPos().squaredDistanceTo(vec3d5);
        }
        final Vec3d vec3d6 = entity2.getRotationVec(1.0f);
        final Vec3d vec3d7 = vec3d5.add(vec3d6.x * double3, vec3d6.y * double3, vec3d6.z * double3);
        final float float12 = 1.0f;
        final BoundingBox boundingBox13 = entity2.getBoundingBox().stretch(vec3d6.multiply(double3)).expand(1.0, 1.0, 1.0);
        final EntityHitResult entityHitResult14 = ProjectileUtil.rayTrace(entity2, vec3d5, vec3d7, boundingBox13, entity -> !entity.isSpectator() && entity.collides(), double4);
        if (entityHitResult14 != null) {
            final Entity entity3 = entityHitResult14.getEntity();
            final Vec3d vec3d8 = entityHitResult14.getPos();
            final double double5 = vec3d5.squaredDistanceTo(vec3d8);
            if (boolean6 && double5 > 9.0) {
                this.client.hitResult = BlockHitResult.createMissed(vec3d8, Direction.getFacing(vec3d6.x, vec3d6.y, vec3d6.z), new BlockPos(vec3d8));
            }
            else if (double5 < double4 || this.client.hitResult == null) {
                this.client.hitResult = entityHitResult14;
                if (entity3 instanceof LivingEntity || entity3 instanceof ItemFrameEntity) {
                    this.client.targetedEntity = entity3;
                }
            }
        }
        this.client.getProfiler().pop();
    }
    
    private void updateMovementFovMultiplier() {
        float float1 = 1.0f;
        if (this.client.getCameraEntity() instanceof AbstractClientPlayerEntity) {
            final AbstractClientPlayerEntity abstractClientPlayerEntity2 = (AbstractClientPlayerEntity)this.client.getCameraEntity();
            float1 = abstractClientPlayerEntity2.getSpeed();
        }
        this.lastMovementFovMultiplier = this.movementFovMultiplier;
        this.movementFovMultiplier += (float1 - this.movementFovMultiplier) * 0.5f;
        if (this.movementFovMultiplier > 1.5f) {
            this.movementFovMultiplier = 1.5f;
        }
        if (this.movementFovMultiplier < 0.1f) {
            this.movementFovMultiplier = 0.1f;
        }
    }
    
    private double getFov(final Camera camera, final float tickDelta, final boolean boolean3) {
        if (this.y) {
            return 90.0;
        }
        double double4 = 70.0;
        if (boolean3) {
            double4 = this.client.options.fov;
            double4 *= MathHelper.lerp(tickDelta, this.lastMovementFovMultiplier, this.movementFovMultiplier);
        }
        if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).getHealth() <= 0.0f) {
            final float float6 = ((LivingEntity)camera.getFocusedEntity()).deathTime + tickDelta;
            double4 /= (1.0f - 500.0f / (float6 + 500.0f)) * 2.0f + 1.0f;
        }
        final FluidState fluidState6 = camera.getSubmergedFluidState();
        if (!fluidState6.isEmpty()) {
            double4 = double4 * 60.0 / 70.0;
        }
        return double4;
    }
    
    private void bobViewWhenHurt(final float tickDelta) {
        if (this.client.getCameraEntity() instanceof LivingEntity) {
            final LivingEntity livingEntity2 = (LivingEntity)this.client.getCameraEntity();
            float float3 = livingEntity2.hurtTime - tickDelta;
            if (livingEntity2.getHealth() <= 0.0f) {
                final float float4 = livingEntity2.deathTime + tickDelta;
                GlStateManager.rotatef(40.0f - 8000.0f / (float4 + 200.0f), 0.0f, 0.0f, 1.0f);
            }
            if (float3 < 0.0f) {
                return;
            }
            float3 /= livingEntity2.ay;
            float3 = MathHelper.sin(float3 * float3 * float3 * float3 * 3.1415927f);
            final float float4 = livingEntity2.az;
            GlStateManager.rotatef(-float4, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotatef(-float3 * 14.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.rotatef(float4, 0.0f, 1.0f, 0.0f);
        }
    }
    
    private void bobView(final float tickDelta) {
        if (!(this.client.getCameraEntity() instanceof PlayerEntity)) {
            return;
        }
        final PlayerEntity playerEntity2 = (PlayerEntity)this.client.getCameraEntity();
        final float float3 = playerEntity2.E - playerEntity2.D;
        final float float4 = -(playerEntity2.E + float3 * tickDelta);
        final float float5 = MathHelper.lerp(tickDelta, playerEntity2.bD, playerEntity2.bE);
        GlStateManager.translatef(MathHelper.sin(float4 * 3.1415927f) * float5 * 0.5f, -Math.abs(MathHelper.cos(float4 * 3.1415927f) * float5), 0.0f);
        GlStateManager.rotatef(MathHelper.sin(float4 * 3.1415927f) * float5 * 3.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotatef(Math.abs(MathHelper.cos(float4 * 3.1415927f - 0.2f) * float5) * 5.0f, 1.0f, 0.0f, 0.0f);
    }
    
    private void applyCameraTransformations(final float tickDelta) {
        this.viewDistance = (float)(this.client.options.viewDistance * 16);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        if (this.z != 1.0) {
            GlStateManager.translatef((float)this.A, (float)(-this.B), 0.0f);
            GlStateManager.scaled(this.z, this.z, 1.0);
        }
        GlStateManager.multMatrix(Matrix4f.a(this.getFov(this.camera, tickDelta, true), this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(), 0.05f, this.viewDistance * MathHelper.SQUARE_ROOT_OF_TWO));
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        this.bobViewWhenHurt(tickDelta);
        if (this.client.options.bobView) {
            this.bobView(tickDelta);
        }
        final float float2 = MathHelper.lerp(tickDelta, this.client.player.lastNauseaStrength, this.client.player.nextNauseaStrength);
        if (float2 > 0.0f) {
            int integer3 = 20;
            if (this.client.player.hasStatusEffect(StatusEffects.i)) {
                integer3 = 7;
            }
            float float3 = 5.0f / (float2 * float2 + 5.0f) - float2 * 0.04f;
            float3 *= float3;
            GlStateManager.rotatef((this.ticks + tickDelta) * integer3, 0.0f, 1.0f, 1.0f);
            GlStateManager.scalef(1.0f / float3, 1.0f, 1.0f);
            GlStateManager.rotatef(-(this.ticks + tickDelta) * integer3, 0.0f, 1.0f, 1.0f);
        }
    }
    
    private void renderHand(final Camera camera, final float tickDelta) {
        if (this.y) {
            return;
        }
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.multMatrix(Matrix4f.a(this.getFov(camera, tickDelta, false), this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(), 0.05f, this.viewDistance * 2.0f));
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.pushMatrix();
        this.bobViewWhenHurt(tickDelta);
        if (this.client.options.bobView) {
            this.bobView(tickDelta);
        }
        final boolean boolean3 = this.client.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.client.getCameraEntity()).isSleeping();
        if (this.client.options.perspective == 0 && !boolean3 && !this.client.options.hudHidden && this.client.interactionManager.getCurrentGameMode() != GameMode.e) {
            this.enableLightmap();
            this.firstPersonRenderer.renderFirstPersonItem(tickDelta);
            this.disableLightmap();
        }
        GlStateManager.popMatrix();
        if (this.client.options.perspective == 0 && !boolean3) {
            this.firstPersonRenderer.renderOverlays(tickDelta);
            this.bobViewWhenHurt(tickDelta);
        }
        if (this.client.options.bobView) {
            this.bobView(tickDelta);
        }
    }
    
    public void disableLightmap() {
        this.lightmapTextureManager.disable();
    }
    
    public void enableLightmap() {
        this.lightmapTextureManager.enable();
    }
    
    public float getNightVisionStrength(final LivingEntity entity, final float tickDelta) {
        final int integer3 = entity.getStatusEffect(StatusEffects.p).getDuration();
        if (integer3 > 200) {
            return 1.0f;
        }
        return 0.7f + MathHelper.sin((integer3 - tickDelta) * 3.1415927f * 0.2f) * 0.3f;
    }
    
    public void render(final float tickDelta, final long startTime, final boolean fullRender) {
        if (this.client.isWindowFocused() || !this.client.options.pauseOnLostFocus || (this.client.options.touchscreen && this.client.mouse.wasRightButtonClicked())) {
            this.lastWindowFocusedTime = SystemUtil.getMeasuringTimeMs();
        }
        else if (SystemUtil.getMeasuringTimeMs() - this.lastWindowFocusedTime > 500L) {
            this.client.openPauseMenu();
        }
        if (this.client.skipGameRender) {
            return;
        }
        final int integer5 = (int)(this.client.mouse.getX() * this.client.window.getScaledWidth() / this.client.window.getWidth());
        final int integer6 = (int)(this.client.mouse.getY() * this.client.window.getScaledHeight() / this.client.window.getHeight());
        final int integer7 = this.client.options.maxFps;
        if (fullRender && this.client.world != null) {
            this.client.getProfiler().push("level");
            int integer8 = Math.min(MinecraftClient.getCurrentFps(), integer7);
            integer8 = Math.max(integer8, 60);
            final long long9 = SystemUtil.getMeasuringTimeNano() - startTime;
            final long long10 = Math.max(1000000000 / integer8 / 4 - long9, 0L);
            this.renderWorld(tickDelta, SystemUtil.getMeasuringTimeNano() + long10);
            if (this.client.isIntegratedServerRunning() && this.lastWorldIconUpdate < SystemUtil.getMeasuringTimeMs() - 1000L) {
                this.lastWorldIconUpdate = SystemUtil.getMeasuringTimeMs();
                if (!this.client.getServer().hasIconFile()) {
                    this.updateWorldIcon();
                }
            }
            if (GLX.usePostProcess) {
                this.client.worldRenderer.drawEntityOutlinesFramebuffer();
                if (this.shader != null && this.shadersEnabled) {
                    GlStateManager.matrixMode(5890);
                    GlStateManager.pushMatrix();
                    GlStateManager.loadIdentity();
                    this.shader.render(tickDelta);
                    GlStateManager.popMatrix();
                }
                this.client.getFramebuffer().beginWrite(true);
            }
            this.client.getProfiler().swap("gui");
            if (!this.client.options.hudHidden || this.client.currentScreen != null) {
                GlStateManager.alphaFunc(516, 0.1f);
                this.client.window.a(MinecraftClient.IS_SYSTEM_MAC);
                this.renderFloatingItem(this.client.window.getScaledWidth(), this.client.window.getScaledHeight(), tickDelta);
                this.client.inGameHud.draw(tickDelta);
            }
            this.client.getProfiler().pop();
        }
        else {
            GlStateManager.viewport(0, 0, this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight());
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            this.client.window.a(MinecraftClient.IS_SYSTEM_MAC);
        }
        if (this.client.overlay != null) {
            GlStateManager.clear(256, MinecraftClient.IS_SYSTEM_MAC);
            try {
                this.client.overlay.render(integer5, integer6, this.client.getLastFrameDuration());
                return;
            }
            catch (Throwable throwable8) {
                final CrashReport crashReport9 = CrashReport.create(throwable8, "Rendering overlay");
                final CrashReportSection crashReportSection10 = crashReport9.addElement("Overlay render details");
                crashReportSection10.add("Overlay name", () -> this.client.overlay.getClass().getCanonicalName());
                throw new CrashException(crashReport9);
            }
        }
        if (this.client.currentScreen != null) {
            GlStateManager.clear(256, MinecraftClient.IS_SYSTEM_MAC);
            try {
                this.client.currentScreen.render(integer5, integer6, this.client.getLastFrameDuration());
            }
            catch (Throwable throwable8) {
                final CrashReport crashReport9 = CrashReport.create(throwable8, "Rendering screen");
                final CrashReportSection crashReportSection10 = crashReport9.addElement("Screen render details");
                crashReportSection10.add("Screen name", () -> this.client.currentScreen.getClass().getCanonicalName());
                crashReportSection10.add("Mouse location", () -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", integer5, integer6, this.client.mouse.getX(), this.client.mouse.getY()));
                crashReportSection10.add("Screen size", () -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", this.client.window.getScaledWidth(), this.client.window.getScaledHeight(), this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight(), this.client.window.getScaleFactor()));
                throw new CrashException(crashReport9);
            }
        }
    }
    
    private void updateWorldIcon() {
        if (this.client.worldRenderer.getChunkNumber() > 10 && this.client.worldRenderer.isTerrainRenderComplete() && !this.client.getServer().hasIconFile()) {
            final NativeImage nativeImage1 = ScreenshotUtils.a(this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight(), this.client.getFramebuffer());
            final NativeImage nativeImage3;
            int integer2;
            int integer3;
            int integer4;
            int integer5;
            NativeImage nativeImage2;
            final Throwable t2;
            ResourceImpl.RESOURCE_IO_EXECUTOR.execute(() -> {
                integer2 = nativeImage3.getWidth();
                integer3 = nativeImage3.getHeight();
                integer4 = 0;
                integer5 = 0;
                if (integer2 > integer3) {
                    integer4 = (integer2 - integer3) / 2;
                    integer2 = integer3;
                }
                else {
                    integer5 = (integer3 - integer2) / 2;
                    integer3 = integer2;
                }
                try {
                    nativeImage2 = new NativeImage(64, 64, false);
                    try {
                        nativeImage3.resizeSubRectTo(integer4, integer5, integer2, integer3, nativeImage2);
                        nativeImage2.writeFile(this.client.getServer().getIconFile());
                    }
                    catch (Throwable t) {
                        throw t;
                    }
                    finally {
                        if (nativeImage2 != null) {
                            if (t2 != null) {
                                try {
                                    nativeImage2.close();
                                }
                                catch (Throwable t3) {
                                    t2.addSuppressed(t3);
                                }
                            }
                            else {
                                nativeImage2.close();
                            }
                        }
                    }
                }
                catch (IOException iOException6) {
                    GameRenderer.LOGGER.warn("Couldn't save auto screenshot", (Throwable)iOException6);
                }
                finally {
                    nativeImage3.close();
                }
            });
        }
    }
    
    private boolean shouldRenderBlockOutline() {
        if (!this.blockOutlineEnabled) {
            return false;
        }
        final Entity entity1 = this.client.getCameraEntity();
        boolean boolean2 = entity1 instanceof PlayerEntity && !this.client.options.hudHidden;
        if (boolean2 && !((PlayerEntity)entity1).abilities.allowModifyWorld) {
            final ItemStack itemStack3 = ((LivingEntity)entity1).getMainHandStack();
            final HitResult hitResult4 = this.client.hitResult;
            if (hitResult4 != null && hitResult4.getType() == HitResult.Type.BLOCK) {
                final BlockPos blockPos5 = ((BlockHitResult)hitResult4).getBlockPos();
                final BlockState blockState6 = this.client.world.getBlockState(blockPos5);
                if (this.client.interactionManager.getCurrentGameMode() == GameMode.e) {
                    boolean2 = (blockState6.createContainerProvider(this.client.world, blockPos5) != null);
                }
                else {
                    final CachedBlockPosition cachedBlockPosition7 = new CachedBlockPosition(this.client.world, blockPos5, false);
                    boolean2 = (!itemStack3.isEmpty() && (itemStack3.getCustomCanHarvest(this.client.world.getTagManager(), cachedBlockPosition7) || itemStack3.getCustomCanPlace(this.client.world.getTagManager(), cachedBlockPosition7)));
                }
            }
        }
        return boolean2;
    }
    
    public void renderWorld(final float tickDelta, final long long2) {
        this.lightmapTextureManager.update(tickDelta);
        if (this.client.getCameraEntity() == null) {
            this.client.setCameraEntity(this.client.player);
        }
        this.updateTargetedEntity(tickDelta);
        GlStateManager.enableDepthTest();
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(516, 0.5f);
        this.client.getProfiler().push("center");
        this.renderCenter(tickDelta, long2);
        this.client.getProfiler().pop();
    }
    
    private void renderCenter(final float tickDelta, final long long2) {
        final WorldRenderer worldRenderer4 = this.client.worldRenderer;
        final ParticleManager particleManager5 = this.client.particleManager;
        final boolean boolean6 = this.shouldRenderBlockOutline();
        GlStateManager.enableCull();
        this.client.getProfiler().swap("camera");
        this.applyCameraTransformations(tickDelta);
        final Camera camera7 = this.camera;
        camera7.update(this.client.world, (this.client.getCameraEntity() == null) ? this.client.player : this.client.getCameraEntity(), this.client.options.perspective > 0, this.client.options.perspective == 2, tickDelta);
        final Frustum frustum8 = GlMatrixFrustum.get();
        this.client.getProfiler().swap("clear");
        GlStateManager.viewport(0, 0, this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight());
        this.backgroundRenderer.renderBackground(camera7, tickDelta);
        GlStateManager.clear(16640, MinecraftClient.IS_SYSTEM_MAC);
        this.client.getProfiler().swap("culling");
        final VisibleRegion visibleRegion9 = new FrustumWithOrigin(frustum8);
        final double double10 = camera7.getPos().x;
        final double double11 = camera7.getPos().y;
        final double double12 = camera7.getPos().z;
        visibleRegion9.setOrigin(double10, double11, double12);
        if (this.client.options.viewDistance >= 4) {
            this.backgroundRenderer.applyFog(camera7, -1);
            this.client.getProfiler().swap("sky");
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.multMatrix(Matrix4f.a(this.getFov(camera7, tickDelta, true), this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(), 0.05f, this.viewDistance * 2.0f));
            GlStateManager.matrixMode(5888);
            worldRenderer4.renderSky(tickDelta);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.multMatrix(Matrix4f.a(this.getFov(camera7, tickDelta, true), this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(), 0.05f, this.viewDistance * MathHelper.SQUARE_ROOT_OF_TWO));
            GlStateManager.matrixMode(5888);
        }
        this.backgroundRenderer.applyFog(camera7, 0);
        GlStateManager.shadeModel(7425);
        if (camera7.getPos().y < 128.0) {
            this.renderAboveClouds(camera7, worldRenderer4, tickDelta, double10, double11, double12);
        }
        this.client.getProfiler().swap("prepareterrain");
        this.backgroundRenderer.applyFog(camera7, 0);
        this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        GuiLighting.disable();
        this.client.getProfiler().swap("terrain_setup");
        this.client.world.getChunkManager().getLightingProvider().doLightUpdates(Integer.MAX_VALUE, true, true);
        worldRenderer4.setUpTerrain(camera7, visibleRegion9, this.K++, this.client.player.isSpectator());
        this.client.getProfiler().swap("updatechunks");
        this.client.worldRenderer.updateChunks(long2);
        this.client.getProfiler().swap("terrain");
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.disableAlphaTest();
        worldRenderer4.renderLayer(BlockRenderLayer.SOLID, camera7);
        GlStateManager.enableAlphaTest();
        worldRenderer4.renderLayer(BlockRenderLayer.MIPPED_CUTOUT, camera7);
        this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
        worldRenderer4.renderLayer(BlockRenderLayer.CUTOUT, camera7);
        this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();
        GlStateManager.shadeModel(7424);
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GuiLighting.enable();
        this.client.getProfiler().swap("entities");
        worldRenderer4.renderEntities(camera7, visibleRegion9, tickDelta);
        GuiLighting.disable();
        this.disableLightmap();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        if (boolean6 && this.client.hitResult != null) {
            GlStateManager.disableAlphaTest();
            this.client.getProfiler().swap("outline");
            worldRenderer4.drawHighlightedBlockOutline(camera7, this.client.hitResult, 0);
            GlStateManager.enableAlphaTest();
        }
        if (this.client.debugRenderer.shouldRender()) {
            this.client.debugRenderer.renderDebuggers(long2);
        }
        this.client.getProfiler().swap("destroyProgress");
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
        worldRenderer4.renderPartiallyBrokenBlocks(Tessellator.getInstance(), Tessellator.getInstance().getBufferBuilder(), camera7);
        this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();
        GlStateManager.disableBlend();
        this.enableLightmap();
        this.backgroundRenderer.applyFog(camera7, 0);
        this.client.getProfiler().swap("particles");
        particleManager5.renderParticles(camera7, tickDelta);
        this.disableLightmap();
        GlStateManager.depthMask(false);
        GlStateManager.enableCull();
        this.client.getProfiler().swap("weather");
        this.renderWeather(tickDelta);
        GlStateManager.depthMask(true);
        worldRenderer4.renderWorldBorder(camera7, tickDelta);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc(516, 0.1f);
        this.backgroundRenderer.applyFog(camera7, 0);
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        GlStateManager.shadeModel(7425);
        this.client.getProfiler().swap("translucent");
        worldRenderer4.renderLayer(BlockRenderLayer.TRANSLUCENT, camera7);
        GlStateManager.shadeModel(7424);
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.disableFog();
        if (camera7.getPos().y >= 128.0) {
            this.client.getProfiler().swap("aboveClouds");
            this.renderAboveClouds(camera7, worldRenderer4, tickDelta, double10, double11, double12);
        }
        this.client.getProfiler().swap("hand");
        if (this.renderHand) {
            GlStateManager.clear(256, MinecraftClient.IS_SYSTEM_MAC);
            this.renderHand(camera7, tickDelta);
        }
    }
    
    private void renderAboveClouds(final Camera camera, final WorldRenderer worldRenderer, final float tickDelta, final double cameraX, final double cameraY, final double cameraZ) {
        if (this.client.options.getCloudRenderMode() != CloudRenderMode.a) {
            this.client.getProfiler().swap("clouds");
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.multMatrix(Matrix4f.a(this.getFov(camera, tickDelta, true), this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(), 0.05f, this.viewDistance * 4.0f));
            GlStateManager.matrixMode(5888);
            GlStateManager.pushMatrix();
            this.backgroundRenderer.applyFog(camera, 0);
            worldRenderer.renderClouds(tickDelta, cameraX, cameraY, cameraZ);
            GlStateManager.disableFog();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.multMatrix(Matrix4f.a(this.getFov(camera, tickDelta, true), this.client.window.getFramebufferWidth() / (float)this.client.window.getFramebufferHeight(), 0.05f, this.viewDistance * MathHelper.SQUARE_ROOT_OF_TWO));
            GlStateManager.matrixMode(5888);
        }
    }
    
    private void renderRain() {
        float float1 = this.client.world.getRainGradient(1.0f);
        if (!this.client.options.fancyGraphics) {
            float1 /= 2.0f;
        }
        if (float1 == 0.0f) {
            return;
        }
        this.random.setSeed(this.ticks * 312987231L);
        final ViewableWorld viewableWorld2 = this.client.world;
        final BlockPos blockPos3 = new BlockPos(this.camera.getPos());
        final int integer4 = 10;
        double double5 = 0.0;
        double double6 = 0.0;
        double double7 = 0.0;
        int integer5 = 0;
        int integer6 = (int)(100.0f * float1 * float1);
        if (this.client.options.particles == ParticlesOption.b) {
            integer6 >>= 1;
        }
        else if (this.client.options.particles == ParticlesOption.c) {
            integer6 = 0;
        }
        for (int integer7 = 0; integer7 < integer6; ++integer7) {
            final BlockPos blockPos4 = viewableWorld2.getTopPosition(Heightmap.Type.e, blockPos3.add(this.random.nextInt(10) - this.random.nextInt(10), 0, this.random.nextInt(10) - this.random.nextInt(10)));
            final Biome biome15 = viewableWorld2.getBiome(blockPos4);
            final BlockPos blockPos5 = blockPos4.down();
            if (blockPos4.getY() <= blockPos3.getY() + 10 && blockPos4.getY() >= blockPos3.getY() - 10 && biome15.getPrecipitation() == Biome.Precipitation.RAIN && biome15.getTemperature(blockPos4) >= 0.15f) {
                final double double8 = this.random.nextDouble();
                final double double9 = this.random.nextDouble();
                final BlockState blockState21 = viewableWorld2.getBlockState(blockPos5);
                final FluidState fluidState22 = viewableWorld2.getFluidState(blockPos4);
                final VoxelShape voxelShape23 = blockState21.getCollisionShape(viewableWorld2, blockPos5);
                final double double10 = voxelShape23.b(Direction.Axis.Y, double8, double9);
                final double double11 = fluidState22.getHeight(viewableWorld2, blockPos4);
                double double12;
                double double13;
                if (double10 >= double11) {
                    double12 = double10;
                    double13 = voxelShape23.a(Direction.Axis.Y, double8, double9);
                }
                else {
                    double12 = 0.0;
                    double13 = 0.0;
                }
                if (double12 > -1.7976931348623157E308) {
                    if (fluidState22.matches(FluidTags.b) || blockState21.getBlock() == Blocks.iB || (blockState21.getBlock() == Blocks.lV && blockState21.<Boolean>get((Property<Boolean>)CampfireBlock.LIT))) {
                        this.client.world.addParticle(ParticleTypes.Q, blockPos4.getX() + double8, blockPos4.getY() + 0.1f - double13, blockPos4.getZ() + double9, 0.0, 0.0, 0.0);
                    }
                    else {
                        if (this.random.nextInt(++integer5) == 0) {
                            double5 = blockPos5.getX() + double8;
                            double6 = blockPos5.getY() + 0.1f + double12 - 1.0;
                            double7 = blockPos5.getZ() + double9;
                        }
                        this.client.world.addParticle(ParticleTypes.P, blockPos5.getX() + double8, blockPos5.getY() + 0.1f + double12, blockPos5.getZ() + double9, 0.0, 0.0, 0.0);
                    }
                }
            }
        }
        if (integer5 > 0 && this.random.nextInt(3) < this.u++) {
            this.u = 0;
            if (double6 > blockPos3.getY() + 1 && viewableWorld2.getTopPosition(Heightmap.Type.e, blockPos3).getY() > MathHelper.floor((float)blockPos3.getY())) {
                this.client.world.playSound(double5, double6, double7, SoundEvents.nf, SoundCategory.d, 0.1f, 0.5f, false);
            }
            else {
                this.client.world.playSound(double5, double6, double7, SoundEvents.ne, SoundCategory.d, 0.2f, 1.0f, false);
            }
        }
    }
    
    protected void renderWeather(final float float1) {
        final float float2 = this.client.world.getRainGradient(float1);
        if (float2 <= 0.0f) {
            return;
        }
        this.enableLightmap();
        final World world3 = this.client.world;
        final int integer4 = MathHelper.floor(this.camera.getPos().x);
        final int integer5 = MathHelper.floor(this.camera.getPos().y);
        final int integer6 = MathHelper.floor(this.camera.getPos().z);
        final Tessellator tessellator7 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder8 = tessellator7.getBufferBuilder();
        GlStateManager.disableCull();
        GlStateManager.normal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc(516, 0.1f);
        final double double9 = this.camera.getPos().x;
        final double double10 = this.camera.getPos().y;
        final double double11 = this.camera.getPos().z;
        final int integer7 = MathHelper.floor(double10);
        int integer8 = 5;
        if (this.client.options.fancyGraphics) {
            integer8 = 10;
        }
        int integer9 = -1;
        final float float3 = this.ticks + float1;
        bufferBuilder8.setOffset(-double9, -double10, -double11);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final BlockPos.Mutable mutable19 = new BlockPos.Mutable();
        for (int integer10 = integer6 - integer8; integer10 <= integer6 + integer8; ++integer10) {
            for (int integer11 = integer4 - integer8; integer11 <= integer4 + integer8; ++integer11) {
                final int integer12 = (integer10 - integer6 + 16) * 32 + integer11 - integer4 + 16;
                final double double12 = this.v[integer12] * 0.5;
                final double double13 = this.w[integer12] * 0.5;
                mutable19.set(integer11, 0, integer10);
                final Biome biome27 = world3.getBiome(mutable19);
                if (biome27.getPrecipitation() != Biome.Precipitation.NONE) {
                    final int integer13 = world3.getTopPosition(Heightmap.Type.e, mutable19).getY();
                    int integer14 = integer5 - integer8;
                    int integer15 = integer5 + integer8;
                    if (integer14 < integer13) {
                        integer14 = integer13;
                    }
                    if (integer15 < integer13) {
                        integer15 = integer13;
                    }
                    int integer16 = integer13;
                    if (integer16 < integer7) {
                        integer16 = integer7;
                    }
                    if (integer14 != integer15) {
                        this.random.setSeed(integer11 * integer11 * 3121 + integer11 * 45238971 ^ integer10 * integer10 * 418711 + integer10 * 13761);
                        mutable19.set(integer11, integer14, integer10);
                        final float float4 = biome27.getTemperature(mutable19);
                        if (float4 >= 0.15f) {
                            if (integer9 != 0) {
                                if (integer9 >= 0) {
                                    tessellator7.draw();
                                }
                                integer9 = 0;
                                this.client.getTextureManager().bindTexture(GameRenderer.RAIN_LOC);
                                bufferBuilder8.begin(7, VertexFormats.POSITION_UV_COLOR_LMAP);
                            }
                            final double double14 = -((this.ticks + integer11 * integer11 * 3121 + integer11 * 45238971 + integer10 * integer10 * 418711 + integer10 * 13761 & 0x1F) + (double)float1) / 32.0 * (3.0 + this.random.nextDouble());
                            final double double15 = integer11 + 0.5f - this.camera.getPos().x;
                            final double double16 = integer10 + 0.5f - this.camera.getPos().z;
                            final float float5 = MathHelper.sqrt(double15 * double15 + double16 * double16) / integer8;
                            final float float6 = ((1.0f - float5 * float5) * 0.5f + 0.5f) * float2;
                            mutable19.set(integer11, integer16, integer10);
                            final int integer17 = world3.getLightmapIndex(mutable19, 0);
                            final int integer18 = integer17 >> 16 & 0xFFFF;
                            final int integer19 = integer17 & 0xFFFF;
                            bufferBuilder8.vertex(integer11 - double12 + 0.5, integer15, integer10 - double13 + 0.5).texture(0.0, integer14 * 0.25 + double14).color(1.0f, 1.0f, 1.0f, float6).texture(integer18, integer19).next();
                            bufferBuilder8.vertex(integer11 + double12 + 0.5, integer15, integer10 + double13 + 0.5).texture(1.0, integer14 * 0.25 + double14).color(1.0f, 1.0f, 1.0f, float6).texture(integer18, integer19).next();
                            bufferBuilder8.vertex(integer11 + double12 + 0.5, integer14, integer10 + double13 + 0.5).texture(1.0, integer15 * 0.25 + double14).color(1.0f, 1.0f, 1.0f, float6).texture(integer18, integer19).next();
                            bufferBuilder8.vertex(integer11 - double12 + 0.5, integer14, integer10 - double13 + 0.5).texture(0.0, integer15 * 0.25 + double14).color(1.0f, 1.0f, 1.0f, float6).texture(integer18, integer19).next();
                        }
                        else {
                            if (integer9 != 1) {
                                if (integer9 >= 0) {
                                    tessellator7.draw();
                                }
                                integer9 = 1;
                                this.client.getTextureManager().bindTexture(GameRenderer.SNOW_LOC);
                                bufferBuilder8.begin(7, VertexFormats.POSITION_UV_COLOR_LMAP);
                            }
                            final double double14 = -((this.ticks & 0x1FF) + float1) / 512.0f;
                            final double double15 = this.random.nextDouble() + float3 * 0.01 * (float)this.random.nextGaussian();
                            final double double16 = this.random.nextDouble() + float3 * (float)this.random.nextGaussian() * 0.001;
                            final double double17 = integer11 + 0.5f - this.camera.getPos().x;
                            final double double18 = integer10 + 0.5f - this.camera.getPos().z;
                            final float float7 = MathHelper.sqrt(double17 * double17 + double18 * double18) / integer8;
                            final float float8 = ((1.0f - float7 * float7) * 0.3f + 0.5f) * float2;
                            mutable19.set(integer11, integer16, integer10);
                            final int integer20 = (world3.getLightmapIndex(mutable19, 0) * 3 + 15728880) / 4;
                            final int integer21 = integer20 >> 16 & 0xFFFF;
                            final int integer22 = integer20 & 0xFFFF;
                            bufferBuilder8.vertex(integer11 - double12 + 0.5, integer15, integer10 - double13 + 0.5).texture(0.0 + double15, integer14 * 0.25 + double14 + double16).color(1.0f, 1.0f, 1.0f, float8).texture(integer21, integer22).next();
                            bufferBuilder8.vertex(integer11 + double12 + 0.5, integer15, integer10 + double13 + 0.5).texture(1.0 + double15, integer14 * 0.25 + double14 + double16).color(1.0f, 1.0f, 1.0f, float8).texture(integer21, integer22).next();
                            bufferBuilder8.vertex(integer11 + double12 + 0.5, integer14, integer10 + double13 + 0.5).texture(1.0 + double15, integer15 * 0.25 + double14 + double16).color(1.0f, 1.0f, 1.0f, float8).texture(integer21, integer22).next();
                            bufferBuilder8.vertex(integer11 - double12 + 0.5, integer14, integer10 - double13 + 0.5).texture(0.0 + double15, integer15 * 0.25 + double14 + double16).color(1.0f, 1.0f, 1.0f, float8).texture(integer21, integer22).next();
                        }
                    }
                }
            }
        }
        if (integer9 >= 0) {
            tessellator7.draw();
        }
        bufferBuilder8.setOffset(0.0, 0.0, 0.0);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1f);
        this.disableLightmap();
    }
    
    public void setFogBlack(final boolean fogBlack) {
        this.backgroundRenderer.setFogBlack(fogBlack);
    }
    
    public void reset() {
        this.floatingItem = null;
        this.mapRenderer.clearStateTextures();
        this.camera.reset();
    }
    
    public MapRenderer getMapRenderer() {
        return this.mapRenderer;
    }
    
    public static void renderFloatingText(final TextRenderer textRenderer, final String text, final float x, final float y, final float z, final int verticalOffset, final float yaw, final float pitch, final boolean translucent) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef(x, y, z);
        GlStateManager.normal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(-yaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(pitch, 1.0f, 0.0f, 0.0f);
        GlStateManager.scalef(-0.025f, -0.025f, 0.025f);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        if (!translucent) {
            GlStateManager.disableDepthTest();
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        final int integer10 = textRenderer.getStringWidth(text) / 2;
        GlStateManager.disableTexture();
        final Tessellator tessellator11 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder12 = tessellator11.getBufferBuilder();
        bufferBuilder12.begin(7, VertexFormats.POSITION_COLOR);
        final float float13 = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f);
        bufferBuilder12.vertex(-integer10 - 1, -1 + verticalOffset, 0.0).color(0.0f, 0.0f, 0.0f, float13).next();
        bufferBuilder12.vertex(-integer10 - 1, 8 + verticalOffset, 0.0).color(0.0f, 0.0f, 0.0f, float13).next();
        bufferBuilder12.vertex(integer10 + 1, 8 + verticalOffset, 0.0).color(0.0f, 0.0f, 0.0f, float13).next();
        bufferBuilder12.vertex(integer10 + 1, -1 + verticalOffset, 0.0).color(0.0f, 0.0f, 0.0f, float13).next();
        tessellator11.draw();
        GlStateManager.enableTexture();
        if (!translucent) {
            textRenderer.draw(text, (float)(-textRenderer.getStringWidth(text) / 2), (float)verticalOffset, 553648127);
            GlStateManager.enableDepthTest();
        }
        GlStateManager.depthMask(true);
        textRenderer.draw(text, (float)(-textRenderer.getStringWidth(text) / 2), (float)verticalOffset, translucent ? 553648127 : -1);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }
    
    public void showFloatingItem(final ItemStack floatingItem) {
        this.floatingItem = floatingItem;
        this.floatingItemTimeLeft = 40;
        this.floatingItemWidth = this.random.nextFloat() * 2.0f - 1.0f;
        this.floatingItemHeight = this.random.nextFloat() * 2.0f - 1.0f;
    }
    
    private void renderFloatingItem(final int scaledWidth, final int scaledHeight, final float tickDelta) {
        if (this.floatingItem == null || this.floatingItemTimeLeft <= 0) {
            return;
        }
        final int integer4 = 40 - this.floatingItemTimeLeft;
        final float float5 = (integer4 + tickDelta) / 40.0f;
        final float float6 = float5 * float5;
        final float float7 = float5 * float6;
        final float float8 = 10.25f * float7 * float6 - 24.95f * float6 * float6 + 25.5f * float7 - 13.8f * float6 + 4.0f * float5;
        final float float9 = float8 * 3.1415927f;
        final float float10 = this.floatingItemWidth * (scaledWidth / 4);
        final float float11 = this.floatingItemHeight * (scaledHeight / 4);
        GlStateManager.enableAlphaTest();
        GlStateManager.pushMatrix();
        GlStateManager.pushLightingAttributes();
        GlStateManager.enableDepthTest();
        GlStateManager.disableCull();
        GuiLighting.enable();
        GlStateManager.translatef(scaledWidth / 2 + float10 * MathHelper.abs(MathHelper.sin(float9 * 2.0f)), scaledHeight / 2 + float11 * MathHelper.abs(MathHelper.sin(float9 * 2.0f)), -50.0f);
        final float float12 = 50.0f + 175.0f * MathHelper.sin(float9);
        GlStateManager.scalef(float12, -float12, float12);
        GlStateManager.rotatef(900.0f * MathHelper.abs(MathHelper.sin(float9)), 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(6.0f * MathHelper.cos(float5 * 8.0f), 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(6.0f * MathHelper.cos(float5 * 8.0f), 0.0f, 0.0f, 1.0f);
        this.client.getItemRenderer().renderItem(this.floatingItem, ModelTransformation.Type.FIXED);
        GlStateManager.popAttributes();
        GlStateManager.popMatrix();
        GuiLighting.disable();
        GlStateManager.enableCull();
        GlStateManager.disableDepthTest();
    }
    
    public MinecraftClient getClient() {
        return this.client;
    }
    
    public float getSkyDarkness(final float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastSkyDarkness, this.skyDarkness);
    }
    
    public float getViewDistance() {
        return this.viewDistance;
    }
    
    public Camera getCamera() {
        return this.camera;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        RAIN_LOC = new Identifier("textures/environment/rain.png");
        SNOW_LOC = new Identifier("textures/environment/snow.png");
        SHADERS_LOCATIONS = new Identifier[] { new Identifier("shaders/post/notch.json"), new Identifier("shaders/post/fxaa.json"), new Identifier("shaders/post/art.json"), new Identifier("shaders/post/bumpy.json"), new Identifier("shaders/post/blobs2.json"), new Identifier("shaders/post/pencil.json"), new Identifier("shaders/post/color_convolve.json"), new Identifier("shaders/post/deconverge.json"), new Identifier("shaders/post/flip.json"), new Identifier("shaders/post/invert.json"), new Identifier("shaders/post/ntsc.json"), new Identifier("shaders/post/outline.json"), new Identifier("shaders/post/phosphor.json"), new Identifier("shaders/post/scan_pincushion.json"), new Identifier("shaders/post/sobel.json"), new Identifier("shaders/post/bits.json"), new Identifier("shaders/post/desaturate.json"), new Identifier("shaders/post/green.json"), new Identifier("shaders/post/blur.json"), new Identifier("shaders/post/wobble.json"), new Identifier("shaders/post/blobs.json"), new Identifier("shaders/post/antialias.json"), new Identifier("shaders/post/creeper.json"), new Identifier("shaders/post/spider.json") };
        SHADER_COUNT = GameRenderer.SHADERS_LOCATIONS.length;
    }
}
