package net.minecraft.client.render;

import org.apache.logging.log4j.LogManager;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.item.Item;
import net.minecraft.block.ComposterBlock;
import net.minecraft.world.IWorld;
import net.minecraft.item.BoneMealItem;
import net.minecraft.particle.ItemStackParticleParameters;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.client.options.ParticlesOption;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.block.Block;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.client.render.chunk.ChunkOcclusionGraphBuilder;
import java.util.Queue;
import java.util.Collection;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;
import com.google.common.collect.Queues;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.block.BlockState;
import java.util.Iterator;
import net.minecraft.state.property.Property;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.block.LeavesBlock;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import java.util.Random;
import net.minecraft.client.util.GlAllocationUtils;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.render.model.ModelLoader;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.client.render.chunk.DisplayListChunkRenderer;
import net.minecraft.client.render.chunk.DisplayListChunkRendererList;
import net.minecraft.client.render.chunk.VboChunkRendererList;
import com.mojang.blaze3d.platform.GLX;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.client.render.chunk.ChunkRendererFactory;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.client.render.chunk.ChunkRendererList;
import net.minecraft.client.render.chunk.ChunkBatcher;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.gl.GlFramebuffer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.audio.SoundInstance;
import net.minecraft.util.math.BlockPos;
import java.util.Map;
import net.minecraft.client.gl.GlBuffer;
import net.minecraft.block.entity.BlockEntity;
import java.util.List;
import net.minecraft.client.render.chunk.ChunkRenderer;
import java.util.Set;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Direction;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.SynchronousResourceReloadListener;

@Environment(EnvType.CLIENT)
public class WorldRenderer implements AutoCloseable, SynchronousResourceReloadListener
{
    private static final Logger LOGGER;
    private static final Identifier MOON_PHASES_TEX;
    private static final Identifier SUN_TEX;
    private static final Identifier CLOUDS_TEX;
    private static final Identifier END_SKY_TEX;
    private static final Identifier FORCEFIELD_TEX;
    public static final Direction[] DIRECTIONS;
    private final MinecraftClient client;
    private final TextureManager textureManager;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private ClientWorld world;
    private Set<ChunkRenderer> chunkRenderers;
    private List<ChunkInfo> chunkInfos;
    private final Set<BlockEntity> blockEntities;
    private ChunkRenderDispatcher chunkRenderDispatcher;
    private int starsDisplayList;
    private int q;
    private int r;
    private final VertexFormat s;
    private GlBuffer starsBuffer;
    private GlBuffer u;
    private GlBuffer v;
    private final int w = 28;
    private boolean cloudsDirty;
    private int cloudsDisplayList;
    private GlBuffer cloudsBuffer;
    private int ticks;
    private final Map<Integer, PartiallyBrokenBlockEntry> partiallyBrokenBlocks;
    private final Map<BlockPos, SoundInstance> playingSongs;
    private final Sprite[] destroyStages;
    private GlFramebuffer entityOutlinesFramebuffer;
    private ShaderEffect entityOutlineShader;
    private double lastCameraChunkUpdateX;
    private double lastCameraChunkUpdateY;
    private double lastCameraChunkUpdateZ;
    private int cameraChunkX;
    private int cameraChunkY;
    private int cameraChunkZ;
    private double lastCameraX;
    private double lastCameraY;
    private double lastCameraZ;
    private double lastCameraPitch;
    private double lastCameraYaw;
    private int R;
    private int S;
    private int T;
    private Vec3d U;
    private CloudRenderMode V;
    private ChunkBatcher chunkBatcher;
    private ChunkRendererList chunkRendererList;
    private int renderDistance;
    private int Z;
    private int renderedEntities;
    private int ab;
    private boolean ac;
    private Frustum forcedFrustum;
    private final Vector4f[] ae;
    private final net.minecraft.client.render.Vec3d forcedFrustumPosition;
    private boolean vertexBufferObjectsEnabled;
    private ChunkRendererFactory chunkRendererFactory;
    private double lastTranslucentSortX;
    private double lastTranslucentSortY;
    private double lastTranslucentSortZ;
    private boolean terrainUpdateNecessary;
    private boolean entityOutlinesUpdateNecessary;
    
    public WorldRenderer(final MinecraftClient minecraftClient) {
        this.chunkRenderers = Sets.newLinkedHashSet();
        this.chunkInfos = Lists.newArrayListWithCapacity(69696);
        this.blockEntities = Sets.newHashSet();
        this.starsDisplayList = -1;
        this.q = -1;
        this.r = -1;
        this.cloudsDirty = true;
        this.cloudsDisplayList = -1;
        this.partiallyBrokenBlocks = Maps.newHashMap();
        this.playingSongs = Maps.newHashMap();
        this.destroyStages = new Sprite[10];
        this.lastCameraChunkUpdateX = Double.MIN_VALUE;
        this.lastCameraChunkUpdateY = Double.MIN_VALUE;
        this.lastCameraChunkUpdateZ = Double.MIN_VALUE;
        this.cameraChunkX = Integer.MIN_VALUE;
        this.cameraChunkY = Integer.MIN_VALUE;
        this.cameraChunkZ = Integer.MIN_VALUE;
        this.lastCameraX = Double.MIN_VALUE;
        this.lastCameraY = Double.MIN_VALUE;
        this.lastCameraZ = Double.MIN_VALUE;
        this.lastCameraPitch = Double.MIN_VALUE;
        this.lastCameraYaw = Double.MIN_VALUE;
        this.R = Integer.MIN_VALUE;
        this.S = Integer.MIN_VALUE;
        this.T = Integer.MIN_VALUE;
        this.U = Vec3d.ZERO;
        this.renderDistance = -1;
        this.Z = 2;
        this.ae = new Vector4f[8];
        this.forcedFrustumPosition = new net.minecraft.client.render.Vec3d();
        this.terrainUpdateNecessary = true;
        this.client = minecraftClient;
        this.entityRenderDispatcher = minecraftClient.getEntityRenderManager();
        this.textureManager = minecraftClient.getTextureManager();
        this.vertexBufferObjectsEnabled = GLX.useVbo();
        if (this.vertexBufferObjectsEnabled) {
            this.chunkRendererList = new VboChunkRendererList();
            this.chunkRendererFactory = ChunkRenderer::new;
        }
        else {
            this.chunkRendererList = new DisplayListChunkRendererList();
            this.chunkRendererFactory = DisplayListChunkRenderer::new;
        }
        (this.s = new VertexFormat()).add(new VertexFormatElement(0, VertexFormatElement.Format.FLOAT, VertexFormatElement.Type.POSITION, 3));
        this.setupStarRendering();
        this.q();
        this.p();
    }
    
    @Override
    public void close() {
        if (this.entityOutlineShader != null) {
            this.entityOutlineShader.close();
        }
    }
    
    @Override
    public void apply(final ResourceManager manager) {
        this.textureManager.bindTexture(WorldRenderer.FORCEFIELD_TEX);
        GlStateManager.texParameter(3553, 10242, 10497);
        GlStateManager.texParameter(3553, 10243, 10497);
        GlStateManager.bindTexture(0);
        this.loadDestroyStageTextures();
        this.loadEntityOutlineShader();
    }
    
    private void loadDestroyStageTextures() {
        final SpriteAtlasTexture spriteAtlasTexture1 = this.client.getSpriteAtlas();
        this.destroyStages[0] = spriteAtlasTexture1.getSprite(ModelLoader.DESTROY_STAGE_0);
        this.destroyStages[1] = spriteAtlasTexture1.getSprite(ModelLoader.DESTROY_STAGE_1);
        this.destroyStages[2] = spriteAtlasTexture1.getSprite(ModelLoader.DESTROY_STAGE_2);
        this.destroyStages[3] = spriteAtlasTexture1.getSprite(ModelLoader.DESTROY_STAGE_3);
        this.destroyStages[4] = spriteAtlasTexture1.getSprite(ModelLoader.DESTROY_STAGE_4);
        this.destroyStages[5] = spriteAtlasTexture1.getSprite(ModelLoader.DESTROY_STAGE_5);
        this.destroyStages[6] = spriteAtlasTexture1.getSprite(ModelLoader.DESTROY_STAGE_6);
        this.destroyStages[7] = spriteAtlasTexture1.getSprite(ModelLoader.DESTROY_STAGE_7);
        this.destroyStages[8] = spriteAtlasTexture1.getSprite(ModelLoader.DESTROY_STAGE_8);
        this.destroyStages[9] = spriteAtlasTexture1.getSprite(ModelLoader.DESTROY_STAGE_9);
    }
    
    public void loadEntityOutlineShader() {
        if (GLX.usePostProcess) {
            if (GlProgramManager.getInstance() == null) {
                GlProgramManager.init();
            }
            if (this.entityOutlineShader != null) {
                this.entityOutlineShader.close();
            }
            final Identifier identifier1 = new Identifier("shaders/post/entity_outline.json");
            try {
                (this.entityOutlineShader = new ShaderEffect(this.client.getTextureManager(), this.client.getResourceManager(), this.client.getFramebuffer(), identifier1)).setupDimensions(this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight());
                this.entityOutlinesFramebuffer = this.entityOutlineShader.getSecondaryTarget("final");
            }
            catch (IOException iOException2) {
                WorldRenderer.LOGGER.warn("Failed to load shader: {}", identifier1, iOException2);
                this.entityOutlineShader = null;
                this.entityOutlinesFramebuffer = null;
            }
            catch (JsonSyntaxException jsonSyntaxException2) {
                WorldRenderer.LOGGER.warn("Failed to load shader: {}", identifier1, jsonSyntaxException2);
                this.entityOutlineShader = null;
                this.entityOutlinesFramebuffer = null;
            }
        }
        else {
            this.entityOutlineShader = null;
            this.entityOutlinesFramebuffer = null;
        }
    }
    
    public void drawEntityOutlinesFramebuffer() {
        if (this.canDrawEntityOutlines()) {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            this.entityOutlinesFramebuffer.draw(this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight(), false);
            GlStateManager.disableBlend();
        }
    }
    
    protected boolean canDrawEntityOutlines() {
        return this.entityOutlinesFramebuffer != null && this.entityOutlineShader != null && this.client.player != null;
    }
    
    private void p() {
        final Tessellator tessellator1 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder2 = tessellator1.getBufferBuilder();
        if (this.v != null) {
            this.v.delete();
        }
        if (this.r >= 0) {
            GlAllocationUtils.deleteSingletonList(this.r);
            this.r = -1;
        }
        if (this.vertexBufferObjectsEnabled) {
            this.v = new GlBuffer(this.s);
            this.a(bufferBuilder2, -16.0f, true);
            bufferBuilder2.end();
            bufferBuilder2.clear();
            this.v.set(bufferBuilder2.getByteBuffer());
        }
        else {
            GlStateManager.newList(this.r = GlAllocationUtils.genLists(1), 4864);
            this.a(bufferBuilder2, -16.0f, true);
            tessellator1.draw();
            GlStateManager.endList();
        }
    }
    
    private void q() {
        final Tessellator tessellator1 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder2 = tessellator1.getBufferBuilder();
        if (this.u != null) {
            this.u.delete();
        }
        if (this.q >= 0) {
            GlAllocationUtils.deleteSingletonList(this.q);
            this.q = -1;
        }
        if (this.vertexBufferObjectsEnabled) {
            this.u = new GlBuffer(this.s);
            this.a(bufferBuilder2, 16.0f, false);
            bufferBuilder2.end();
            bufferBuilder2.clear();
            this.u.set(bufferBuilder2.getByteBuffer());
        }
        else {
            GlStateManager.newList(this.q = GlAllocationUtils.genLists(1), 4864);
            this.a(bufferBuilder2, 16.0f, false);
            tessellator1.draw();
            GlStateManager.endList();
        }
    }
    
    private void a(final BufferBuilder bufferBuilder, final float float2, final boolean boolean3) {
        final int integer4 = 64;
        final int integer5 = 6;
        bufferBuilder.begin(7, VertexFormats.POSITION);
        for (int integer6 = -384; integer6 <= 384; integer6 += 64) {
            for (int integer7 = -384; integer7 <= 384; integer7 += 64) {
                float float3 = (float)integer6;
                float float4 = (float)(integer6 + 64);
                if (boolean3) {
                    float4 = (float)integer6;
                    float3 = (float)(integer6 + 64);
                }
                bufferBuilder.vertex(float3, float2, integer7).next();
                bufferBuilder.vertex(float4, float2, integer7).next();
                bufferBuilder.vertex(float4, float2, integer7 + 64).next();
                bufferBuilder.vertex(float3, float2, integer7 + 64).next();
            }
        }
    }
    
    private void setupStarRendering() {
        final Tessellator tessellator1 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder2 = tessellator1.getBufferBuilder();
        if (this.starsBuffer != null) {
            this.starsBuffer.delete();
        }
        if (this.starsDisplayList >= 0) {
            GlAllocationUtils.deleteSingletonList(this.starsDisplayList);
            this.starsDisplayList = -1;
        }
        if (this.vertexBufferObjectsEnabled) {
            this.starsBuffer = new GlBuffer(this.s);
            this.renderStars(bufferBuilder2);
            bufferBuilder2.end();
            bufferBuilder2.clear();
            this.starsBuffer.set(bufferBuilder2.getByteBuffer());
        }
        else {
            this.starsDisplayList = GlAllocationUtils.genLists(1);
            GlStateManager.pushMatrix();
            GlStateManager.newList(this.starsDisplayList, 4864);
            this.renderStars(bufferBuilder2);
            tessellator1.draw();
            GlStateManager.endList();
            GlStateManager.popMatrix();
        }
    }
    
    private void renderStars(final BufferBuilder bufferBuilder) {
        final Random random2 = new Random(10842L);
        bufferBuilder.begin(7, VertexFormats.POSITION);
        for (int integer3 = 0; integer3 < 1500; ++integer3) {
            double double4 = random2.nextFloat() * 2.0f - 1.0f;
            double double5 = random2.nextFloat() * 2.0f - 1.0f;
            double double6 = random2.nextFloat() * 2.0f - 1.0f;
            final double double7 = 0.15f + random2.nextFloat() * 0.1f;
            double double8 = double4 * double4 + double5 * double5 + double6 * double6;
            if (double8 < 1.0 && double8 > 0.01) {
                double8 = 1.0 / Math.sqrt(double8);
                double4 *= double8;
                double5 *= double8;
                double6 *= double8;
                final double double9 = double4 * 100.0;
                final double double10 = double5 * 100.0;
                final double double11 = double6 * 100.0;
                final double double12 = Math.atan2(double4, double6);
                final double double13 = Math.sin(double12);
                final double double14 = Math.cos(double12);
                final double double15 = Math.atan2(Math.sqrt(double4 * double4 + double6 * double6), double5);
                final double double16 = Math.sin(double15);
                final double double17 = Math.cos(double15);
                final double double18 = random2.nextDouble() * 3.141592653589793 * 2.0;
                final double double19 = Math.sin(double18);
                final double double20 = Math.cos(double18);
                for (int integer4 = 0; integer4 < 4; ++integer4) {
                    final double double21 = 0.0;
                    final double double22 = ((integer4 & 0x2) - 1) * double7;
                    final double double23 = ((integer4 + 1 & 0x2) - 1) * double7;
                    final double double24 = 0.0;
                    final double double25 = double22 * double20 - double23 * double19;
                    final double double27;
                    final double double26 = double27 = double23 * double20 + double22 * double19;
                    final double double28 = double25 * double16 + 0.0 * double17;
                    final double double29 = 0.0 * double16 - double25 * double17;
                    final double double30 = double29 * double13 - double27 * double14;
                    final double double31 = double28;
                    final double double32 = double27 * double13 + double29 * double14;
                    bufferBuilder.vertex(double9 + double30, double10 + double31, double11 + double32).next();
                }
            }
        }
    }
    
    public void setWorld(@Nullable final ClientWorld clientWorld) {
        this.lastCameraChunkUpdateX = Double.MIN_VALUE;
        this.lastCameraChunkUpdateY = Double.MIN_VALUE;
        this.lastCameraChunkUpdateZ = Double.MIN_VALUE;
        this.cameraChunkX = Integer.MIN_VALUE;
        this.cameraChunkY = Integer.MIN_VALUE;
        this.cameraChunkZ = Integer.MIN_VALUE;
        this.entityRenderDispatcher.setWorld(clientWorld);
        this.world = clientWorld;
        if (clientWorld != null) {
            this.reload();
        }
        else {
            this.chunkRenderers.clear();
            this.chunkInfos.clear();
            if (this.chunkRenderDispatcher != null) {
                this.chunkRenderDispatcher.delete();
                this.chunkRenderDispatcher = null;
            }
            if (this.chunkBatcher != null) {
                this.chunkBatcher.h();
            }
            this.chunkBatcher = null;
            this.blockEntities.clear();
        }
    }
    
    public void reload() {
        if (this.world == null) {
            return;
        }
        if (this.chunkBatcher == null) {
            this.chunkBatcher = new ChunkBatcher();
        }
        this.terrainUpdateNecessary = true;
        this.cloudsDirty = true;
        LeavesBlock.setRenderingMode(this.client.options.fancyGraphics);
        this.renderDistance = this.client.options.viewDistance;
        final boolean boolean1 = this.vertexBufferObjectsEnabled;
        this.vertexBufferObjectsEnabled = GLX.useVbo();
        if (boolean1 && !this.vertexBufferObjectsEnabled) {
            this.chunkRendererList = new DisplayListChunkRendererList();
            this.chunkRendererFactory = DisplayListChunkRenderer::new;
        }
        else if (!boolean1 && this.vertexBufferObjectsEnabled) {
            this.chunkRendererList = new VboChunkRendererList();
            this.chunkRendererFactory = ChunkRenderer::new;
        }
        if (boolean1 != this.vertexBufferObjectsEnabled) {
            this.setupStarRendering();
            this.q();
            this.p();
        }
        if (this.chunkRenderDispatcher != null) {
            this.chunkRenderDispatcher.delete();
        }
        this.e();
        synchronized (this.blockEntities) {
            this.blockEntities.clear();
        }
        this.chunkRenderDispatcher = new ChunkRenderDispatcher(this.world, this.client.options.viewDistance, this, this.chunkRendererFactory);
        if (this.world != null) {
            final Entity entity2 = this.client.getCameraEntity();
            if (entity2 != null) {
                this.chunkRenderDispatcher.updateCameraPosition(entity2.x, entity2.z);
            }
        }
        this.Z = 2;
    }
    
    protected void e() {
        this.chunkRenderers.clear();
        this.chunkBatcher.c();
    }
    
    public void onResized(final int integer1, final int integer2) {
        this.scheduleTerrainUpdate();
        if (!GLX.usePostProcess) {
            return;
        }
        if (this.entityOutlineShader != null) {
            this.entityOutlineShader.setupDimensions(integer1, integer2);
        }
    }
    
    public void renderEntities(final Camera camera, final VisibleRegion visibleRegion, final float tickDelta) {
        if (this.Z > 0) {
            --this.Z;
            return;
        }
        final double double4 = camera.getPos().x;
        final double double5 = camera.getPos().y;
        final double double6 = camera.getPos().z;
        this.world.getProfiler().push("prepare");
        BlockEntityRenderDispatcher.INSTANCE.configure(this.world, this.client.getTextureManager(), this.client.textRenderer, camera, this.client.hitResult);
        this.entityRenderDispatcher.configure(this.world, this.client.textRenderer, camera, this.client.targetedEntity, this.client.options);
        this.renderedEntities = 0;
        this.ab = 0;
        final double double7 = camera.getPos().x;
        final double double8 = camera.getPos().y;
        final double double9 = camera.getPos().z;
        BlockEntityRenderDispatcher.renderOffsetX = double7;
        BlockEntityRenderDispatcher.renderOffsetY = double8;
        BlockEntityRenderDispatcher.renderOffsetZ = double9;
        this.entityRenderDispatcher.setRenderPosition(double7, double8, double9);
        this.client.gameRenderer.enableLightmap();
        this.world.getProfiler().swap("entities");
        final List<Entity> list16 = Lists.newArrayList();
        final List<Entity> list17 = Lists.newArrayList();
        for (final Entity entity19 : this.world.getEntities()) {
            if (!this.entityRenderDispatcher.shouldRender(entity19, visibleRegion, double4, double5, double6) && !entity19.y(this.client.player)) {
                continue;
            }
            if (entity19 == camera.getFocusedEntity() && !camera.isThirdPerson()) {
                if (!(camera.getFocusedEntity() instanceof LivingEntity)) {
                    continue;
                }
                if (!((LivingEntity)camera.getFocusedEntity()).isSleeping()) {
                    continue;
                }
            }
            ++this.renderedEntities;
            this.entityRenderDispatcher.render(entity19, tickDelta, false);
            if (entity19.isGlowing() || (entity19 instanceof PlayerEntity && this.client.player.isSpectator() && this.client.options.keySpectatorOutlines.isPressed())) {
                list16.add(entity19);
            }
            if (!this.entityRenderDispatcher.hasSecondPass(entity19)) {
                continue;
            }
            list17.add(entity19);
        }
        if (!list17.isEmpty()) {
            for (final Entity entity19 : list17) {
                this.entityRenderDispatcher.renderSecondPass(entity19, tickDelta);
            }
        }
        if (this.canDrawEntityOutlines() && (!list16.isEmpty() || this.entityOutlinesUpdateNecessary)) {
            this.world.getProfiler().swap("entityOutlines");
            this.entityOutlinesFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
            this.entityOutlinesUpdateNecessary = !list16.isEmpty();
            if (!list16.isEmpty()) {
                GlStateManager.depthFunc(519);
                GlStateManager.disableFog();
                this.entityOutlinesFramebuffer.beginWrite(false);
                GuiLighting.disable();
                this.entityRenderDispatcher.setRenderOutlines(true);
                for (int integer18 = 0; integer18 < list16.size(); ++integer18) {
                    this.entityRenderDispatcher.render(list16.get(integer18), tickDelta, false);
                }
                this.entityRenderDispatcher.setRenderOutlines(false);
                GuiLighting.enable();
                GlStateManager.depthMask(false);
                this.entityOutlineShader.render(tickDelta);
                GlStateManager.enableLighting();
                GlStateManager.depthMask(true);
                GlStateManager.enableFog();
                GlStateManager.enableBlend();
                GlStateManager.enableColorMaterial();
                GlStateManager.depthFunc(515);
                GlStateManager.enableDepthTest();
                GlStateManager.enableAlphaTest();
            }
            this.client.getFramebuffer().beginWrite(false);
        }
        this.world.getProfiler().swap("blockentities");
        GuiLighting.enable();
        for (final ChunkInfo chunkInfo19 : this.chunkInfos) {
            final List<BlockEntity> list18 = chunkInfo19.renderer.getChunkRenderData().getBlockEntities();
            if (list18.isEmpty()) {
                continue;
            }
            for (final BlockEntity blockEntity22 : list18) {
                BlockEntityRenderDispatcher.INSTANCE.render(blockEntity22, tickDelta, -1);
            }
        }
        synchronized (this.blockEntities) {
            for (final BlockEntity blockEntity23 : this.blockEntities) {
                BlockEntityRenderDispatcher.INSTANCE.render(blockEntity23, tickDelta, -1);
            }
        }
        this.enableBlockOverlayRendering();
        for (final PartiallyBrokenBlockEntry partiallyBrokenBlockEntry19 : this.partiallyBrokenBlocks.values()) {
            BlockPos blockPos20 = partiallyBrokenBlockEntry19.getPos();
            final BlockState blockState21 = this.world.getBlockState(blockPos20);
            if (!blockState21.getBlock().hasBlockEntity()) {
                continue;
            }
            BlockEntity blockEntity22 = this.world.getBlockEntity(blockPos20);
            if (blockEntity22 instanceof ChestBlockEntity && blockState21.<ChestType>get(ChestBlock.CHEST_TYPE) == ChestType.b) {
                blockPos20 = blockPos20.offset(blockState21.<Direction>get((Property<Direction>)ChestBlock.FACING).rotateYClockwise());
                blockEntity22 = this.world.getBlockEntity(blockPos20);
            }
            if (blockEntity22 == null || !blockState21.hasBlockEntityBreakingRender()) {
                continue;
            }
            BlockEntityRenderDispatcher.INSTANCE.render(blockEntity22, tickDelta, partiallyBrokenBlockEntry19.getStage());
        }
        this.disableBlockOverlayRendering();
        this.client.gameRenderer.disableLightmap();
        this.client.getProfiler().pop();
    }
    
    public String getChunksDebugString() {
        final int integer1 = this.chunkRenderDispatcher.renderers.length;
        final int integer2 = this.getChunkNumber();
        return String.format("C: %d/%d %sD: %d, %s", integer2, integer1, this.client.F ? "(s) " : "", this.renderDistance, (this.chunkBatcher == null) ? "null" : this.chunkBatcher.getDebugString());
    }
    
    protected int getChunkNumber() {
        int integer1 = 0;
        for (final ChunkInfo chunkInfo3 : this.chunkInfos) {
            final ChunkRenderData chunkRenderData4 = chunkInfo3.renderer.chunkRenderData;
            if (chunkRenderData4 != ChunkRenderData.EMPTY && !chunkRenderData4.isEmpty()) {
                ++integer1;
            }
        }
        return integer1;
    }
    
    public String getEntitiesDebugString() {
        return "E: " + this.renderedEntities + "/" + this.world.getRegularEntityCount() + ", B: " + this.ab;
    }
    
    public void setUpTerrain(final Camera camera, VisibleRegion visibleRegion, final int integer, final boolean boolean4) {
        if (this.client.options.viewDistance != this.renderDistance) {
            this.reload();
        }
        this.world.getProfiler().push("camera");
        final double double5 = this.client.player.x - this.lastCameraChunkUpdateX;
        final double double6 = this.client.player.y - this.lastCameraChunkUpdateY;
        final double double7 = this.client.player.z - this.lastCameraChunkUpdateZ;
        if (this.cameraChunkX != this.client.player.chunkX || this.cameraChunkY != this.client.player.chunkY || this.cameraChunkZ != this.client.player.chunkZ || double5 * double5 + double6 * double6 + double7 * double7 > 16.0) {
            this.lastCameraChunkUpdateX = this.client.player.x;
            this.lastCameraChunkUpdateY = this.client.player.y;
            this.lastCameraChunkUpdateZ = this.client.player.z;
            this.cameraChunkX = this.client.player.chunkX;
            this.cameraChunkY = this.client.player.chunkY;
            this.cameraChunkZ = this.client.player.chunkZ;
            this.chunkRenderDispatcher.updateCameraPosition(this.client.player.x, this.client.player.z);
        }
        this.world.getProfiler().swap("renderlistcamera");
        this.chunkRendererList.setCameraPosition(camera.getPos().x, camera.getPos().y, camera.getPos().z);
        this.chunkBatcher.a(camera.getPos());
        this.world.getProfiler().swap("cull");
        if (this.forcedFrustum != null) {
            final FrustumWithOrigin frustumWithOrigin11 = new FrustumWithOrigin(this.forcedFrustum);
            frustumWithOrigin11.setOrigin(this.forcedFrustumPosition.x, this.forcedFrustumPosition.y, this.forcedFrustumPosition.z);
            visibleRegion = frustumWithOrigin11;
        }
        this.client.getProfiler().swap("culling");
        final BlockPos blockPos11 = camera.getBlockPos();
        final ChunkRenderer chunkRenderer12 = this.chunkRenderDispatcher.getChunkRenderer(blockPos11);
        final BlockPos blockPos12 = new BlockPos(MathHelper.floor(camera.getPos().x / 16.0) * 16, MathHelper.floor(camera.getPos().y / 16.0) * 16, MathHelper.floor(camera.getPos().z / 16.0) * 16);
        final float float14 = camera.getPitch();
        final float float15 = camera.getYaw();
        this.terrainUpdateNecessary = (this.terrainUpdateNecessary || !this.chunkRenderers.isEmpty() || camera.getPos().x != this.lastCameraX || camera.getPos().y != this.lastCameraY || camera.getPos().z != this.lastCameraZ || float14 != this.lastCameraPitch || float15 != this.lastCameraYaw);
        this.lastCameraX = camera.getPos().x;
        this.lastCameraY = camera.getPos().y;
        this.lastCameraZ = camera.getPos().z;
        this.lastCameraPitch = float14;
        this.lastCameraYaw = float15;
        final boolean boolean5 = this.forcedFrustum != null;
        this.client.getProfiler().swap("update");
        if (!boolean5 && this.terrainUpdateNecessary) {
            this.terrainUpdateNecessary = false;
            this.chunkInfos = Lists.newArrayList();
            final Queue<ChunkInfo> queue17 = Queues.newArrayDeque();
            Entity.setRenderDistanceMultiplier(MathHelper.clamp(this.client.options.viewDistance / 8.0, 1.0, 2.5));
            boolean boolean6 = this.client.F;
            if (chunkRenderer12 == null) {
                final int integer2 = (blockPos11.getY() > 0) ? 248 : 8;
                for (int integer3 = -this.renderDistance; integer3 <= this.renderDistance; ++integer3) {
                    for (int integer4 = -this.renderDistance; integer4 <= this.renderDistance; ++integer4) {
                        final ChunkRenderer chunkRenderer13 = this.chunkRenderDispatcher.getChunkRenderer(new BlockPos((integer3 << 4) + 8, integer2, (integer4 << 4) + 8));
                        if (chunkRenderer13 != null && visibleRegion.intersects(chunkRenderer13.boundingBox)) {
                            chunkRenderer13.a(integer);
                            queue17.add(new ChunkInfo(chunkRenderer13, (Direction)null, 0));
                        }
                    }
                }
            }
            else {
                boolean boolean7 = false;
                final ChunkInfo chunkInfo20 = new ChunkInfo(chunkRenderer12, (Direction)null, 0);
                final Set<Direction> set21 = this.getOpenChunkFaces(blockPos11);
                if (set21.size() == 1) {
                    final Vec3d vec3d22 = camera.l();
                    final Direction direction23 = Direction.getFacing(vec3d22.x, vec3d22.y, vec3d22.z).getOpposite();
                    set21.remove(direction23);
                }
                if (set21.isEmpty()) {
                    boolean7 = true;
                }
                if (!boolean7 || boolean4) {
                    if (boolean4 && this.world.getBlockState(blockPos11).isFullOpaque(this.world, blockPos11)) {
                        boolean6 = false;
                    }
                    chunkRenderer12.a(integer);
                    queue17.add(chunkInfo20);
                }
                else {
                    this.chunkInfos.add(chunkInfo20);
                }
            }
            this.client.getProfiler().push("iteration");
            while (!queue17.isEmpty()) {
                final ChunkInfo chunkInfo21 = queue17.poll();
                final ChunkRenderer chunkRenderer14 = chunkInfo21.renderer;
                final Direction direction24 = chunkInfo21.c;
                this.chunkInfos.add(chunkInfo21);
                for (final Direction direction25 : WorldRenderer.DIRECTIONS) {
                    final ChunkRenderer chunkRenderer15 = this.getChunkRenderer(blockPos12, chunkRenderer14, direction25);
                    if (!boolean6 || !chunkInfo21.a(direction25.getOpposite())) {
                        if (!boolean6 || direction24 == null || chunkRenderer14.getChunkRenderData().a(direction24.getOpposite(), direction25)) {
                            if (chunkRenderer15 != null) {
                                if (chunkRenderer15.b()) {
                                    if (chunkRenderer15.a(integer)) {
                                        if (visibleRegion.intersects(chunkRenderer15.boundingBox)) {
                                            final ChunkInfo chunkInfo22 = new ChunkInfo(chunkRenderer15, direction25, chunkInfo21.e + 1);
                                            chunkInfo22.a(chunkInfo21.d, direction25);
                                            queue17.add(chunkInfo22);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            this.client.getProfiler().pop();
        }
        this.client.getProfiler().swap("captureFrustum");
        if (this.ac) {
            this.a(camera.getPos().x, camera.getPos().y, camera.getPos().z);
            this.ac = false;
        }
        this.client.getProfiler().swap("rebuildNear");
        final Set<ChunkRenderer> set22 = this.chunkRenderers;
        this.chunkRenderers = Sets.newLinkedHashSet();
        final Iterator<ChunkInfo> iterator = this.chunkInfos.iterator();
        while (iterator.hasNext()) {
            final ChunkInfo chunkInfo21 = iterator.next();
            final ChunkRenderer chunkRenderer14 = chunkInfo21.renderer;
            if (chunkRenderer14.m() || set22.contains(chunkRenderer14)) {
                this.terrainUpdateNecessary = true;
                final BlockPos blockPos13 = chunkRenderer14.getOrigin().add(8, 8, 8);
                final boolean boolean8 = blockPos13.getSquaredDistance(blockPos11) < 768.0;
                if (chunkRenderer14.n() || boolean8) {
                    this.client.getProfiler().push("build near");
                    this.chunkBatcher.b(chunkRenderer14);
                    chunkRenderer14.l();
                    this.client.getProfiler().pop();
                }
                else {
                    this.chunkRenderers.add(chunkRenderer14);
                }
            }
        }
        this.chunkRenderers.addAll(set22);
        this.client.getProfiler().pop();
    }
    
    private Set<Direction> getOpenChunkFaces(final BlockPos pos) {
        final ChunkOcclusionGraphBuilder chunkOcclusionGraphBuilder2 = new ChunkOcclusionGraphBuilder();
        final BlockPos blockPos3 = new BlockPos(pos.getX() >> 4 << 4, pos.getY() >> 4 << 4, pos.getZ() >> 4 << 4);
        final WorldChunk worldChunk4 = this.world.getWorldChunk(blockPos3);
        for (final BlockPos blockPos4 : BlockPos.iterate(blockPos3, blockPos3.add(15, 15, 15))) {
            if (worldChunk4.getBlockState(blockPos4).isFullOpaque(this.world, blockPos4)) {
                chunkOcclusionGraphBuilder2.markClosed(blockPos4);
            }
        }
        return chunkOcclusionGraphBuilder2.getOpenFaces(pos);
    }
    
    @Nullable
    private ChunkRenderer getChunkRenderer(final BlockPos pos, final ChunkRenderer chunkRenderer, final Direction direction) {
        final BlockPos blockPos4 = chunkRenderer.a(direction);
        if (MathHelper.abs(pos.getX() - blockPos4.getX()) > this.renderDistance * 16) {
            return null;
        }
        if (blockPos4.getY() < 0 || blockPos4.getY() >= 256) {
            return null;
        }
        if (MathHelper.abs(pos.getZ() - blockPos4.getZ()) > this.renderDistance * 16) {
            return null;
        }
        return this.chunkRenderDispatcher.getChunkRenderer(blockPos4);
    }
    
    private void a(final double double1, final double double3, final double double5) {
    }
    
    public int renderLayer(final BlockRenderLayer layer, final Camera camera) {
        GuiLighting.disable();
        if (layer == BlockRenderLayer.TRANSLUCENT) {
            this.client.getProfiler().push("translucent_sort");
            final double double3 = camera.getPos().x - this.lastTranslucentSortX;
            final double double4 = camera.getPos().y - this.lastTranslucentSortY;
            final double double5 = camera.getPos().z - this.lastTranslucentSortZ;
            if (double3 * double3 + double4 * double4 + double5 * double5 > 1.0) {
                this.lastTranslucentSortX = camera.getPos().x;
                this.lastTranslucentSortY = camera.getPos().y;
                this.lastTranslucentSortZ = camera.getPos().z;
                int integer9 = 0;
                for (final ChunkInfo chunkInfo11 : this.chunkInfos) {
                    if (chunkInfo11.renderer.chunkRenderData.isBufferInitialized(layer) && integer9++ < 15) {
                        this.chunkBatcher.c(chunkInfo11.renderer);
                    }
                }
            }
            this.client.getProfiler().pop();
        }
        this.client.getProfiler().push("filterempty");
        int integer10 = 0;
        final boolean boolean4 = layer == BlockRenderLayer.TRANSLUCENT;
        final int integer11 = boolean4 ? (this.chunkInfos.size() - 1) : 0;
        for (int integer12 = boolean4 ? -1 : this.chunkInfos.size(), integer13 = boolean4 ? -1 : 1, integer14 = integer11; integer14 != integer12; integer14 += integer13) {
            final ChunkRenderer chunkRenderer9 = this.chunkInfos.get(integer14).renderer;
            if (!chunkRenderer9.getChunkRenderData().b(layer)) {
                ++integer10;
                this.chunkRendererList.add(chunkRenderer9, layer);
            }
        }
        this.client.getProfiler().swap(() -> "render_" + layer);
        this.renderLayer(layer);
        this.client.getProfiler().pop();
        return integer10;
    }
    
    private void renderLayer(final BlockRenderLayer layer) {
        this.client.gameRenderer.enableLightmap();
        if (GLX.useVbo()) {
            GlStateManager.enableClientState(32884);
            GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
            GlStateManager.enableClientState(32888);
            GLX.glClientActiveTexture(GLX.GL_TEXTURE1);
            GlStateManager.enableClientState(32888);
            GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
            GlStateManager.enableClientState(32886);
        }
        this.chunkRendererList.render(layer);
        if (GLX.useVbo()) {
            final List<VertexFormatElement> list2 = VertexFormats.POSITION_COLOR_UV_LMAP.getElements();
            for (final VertexFormatElement vertexFormatElement4 : list2) {
                final VertexFormatElement.Type type5 = vertexFormatElement4.getType();
                final int integer6 = vertexFormatElement4.getIndex();
                switch (type5) {
                    case POSITION: {
                        GlStateManager.disableClientState(32884);
                        continue;
                    }
                    case UV: {
                        GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + integer6);
                        GlStateManager.disableClientState(32888);
                        GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
                        continue;
                    }
                    case COLOR: {
                        GlStateManager.disableClientState(32886);
                        GlStateManager.clearCurrentColor();
                        continue;
                    }
                }
            }
        }
        this.client.gameRenderer.disableLightmap();
    }
    
    private void removeOutdatedPartiallyBrokenBlocks(final Iterator<PartiallyBrokenBlockEntry> partiallyBrokenBlocks) {
        while (partiallyBrokenBlocks.hasNext()) {
            final PartiallyBrokenBlockEntry partiallyBrokenBlockEntry2 = partiallyBrokenBlocks.next();
            final int integer3 = partiallyBrokenBlockEntry2.getLastUpdateTicks();
            if (this.ticks - integer3 > 400) {
                partiallyBrokenBlocks.remove();
            }
        }
    }
    
    public void tick() {
        ++this.ticks;
        if (this.ticks % 20 == 0) {
            this.removeOutdatedPartiallyBrokenBlocks(this.partiallyBrokenBlocks.values().iterator());
        }
    }
    
    private void renderEndSky() {
        GlStateManager.disableFog();
        GlStateManager.disableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GuiLighting.disable();
        GlStateManager.depthMask(false);
        this.textureManager.bindTexture(WorldRenderer.END_SKY_TEX);
        final Tessellator tessellator1 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder2 = tessellator1.getBufferBuilder();
        for (int integer3 = 0; integer3 < 6; ++integer3) {
            GlStateManager.pushMatrix();
            if (integer3 == 1) {
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
            }
            if (integer3 == 2) {
                GlStateManager.rotatef(-90.0f, 1.0f, 0.0f, 0.0f);
            }
            if (integer3 == 3) {
                GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
            }
            if (integer3 == 4) {
                GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
            }
            if (integer3 == 5) {
                GlStateManager.rotatef(-90.0f, 0.0f, 0.0f, 1.0f);
            }
            bufferBuilder2.begin(7, VertexFormats.POSITION_UV_COLOR);
            bufferBuilder2.vertex(-100.0, -100.0, -100.0).texture(0.0, 0.0).color(40, 40, 40, 255).next();
            bufferBuilder2.vertex(-100.0, -100.0, 100.0).texture(0.0, 16.0).color(40, 40, 40, 255).next();
            bufferBuilder2.vertex(100.0, -100.0, 100.0).texture(16.0, 16.0).color(40, 40, 40, 255).next();
            bufferBuilder2.vertex(100.0, -100.0, -100.0).texture(16.0, 0.0).color(40, 40, 40, 255).next();
            tessellator1.draw();
            GlStateManager.popMatrix();
        }
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
    }
    
    public void renderSky(final float tickDelta) {
        if (this.client.world.dimension.getType() == DimensionType.c) {
            this.renderEndSky();
            return;
        }
        if (!this.client.world.dimension.hasVisibleSky()) {
            return;
        }
        GlStateManager.disableTexture();
        final Vec3d vec3d2 = this.world.getSkyColor(this.client.gameRenderer.getCamera().getBlockPos(), tickDelta);
        final float float3 = (float)vec3d2.x;
        final float float4 = (float)vec3d2.y;
        final float float5 = (float)vec3d2.z;
        GlStateManager.color3f(float3, float4, float5);
        final Tessellator tessellator6 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder7 = tessellator6.getBufferBuilder();
        GlStateManager.depthMask(false);
        GlStateManager.enableFog();
        GlStateManager.color3f(float3, float4, float5);
        if (this.vertexBufferObjectsEnabled) {
            this.u.bind();
            GlStateManager.enableClientState(32884);
            GlStateManager.vertexPointer(3, 5126, 12, 0);
            this.u.draw(7);
            GlBuffer.unbind();
            GlStateManager.disableClientState(32884);
        }
        else {
            GlStateManager.callList(this.q);
        }
        GlStateManager.disableFog();
        GlStateManager.disableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GuiLighting.disable();
        final float[] arr8 = this.world.dimension.getBackgroundColor(this.world.getSkyAngle(tickDelta), tickDelta);
        if (arr8 != null) {
            GlStateManager.disableTexture();
            GlStateManager.shadeModel(7425);
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef((MathHelper.sin(this.world.b(tickDelta)) < 0.0f) ? 180.0f : 0.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
            final float float6 = arr8[0];
            final float float7 = arr8[1];
            final float float8 = arr8[2];
            bufferBuilder7.begin(6, VertexFormats.POSITION_COLOR);
            bufferBuilder7.vertex(0.0, 100.0, 0.0).color(float6, float7, float8, arr8[3]).next();
            final int integer12 = 16;
            for (int integer13 = 0; integer13 <= 16; ++integer13) {
                final float float9 = integer13 * 6.2831855f / 16.0f;
                final float float10 = MathHelper.sin(float9);
                final float float11 = MathHelper.cos(float9);
                bufferBuilder7.vertex(float10 * 120.0f, float11 * 120.0f, -float11 * 40.0f * arr8[3]).color(arr8[0], arr8[1], arr8[2], 0.0f).next();
            }
            tessellator6.draw();
            GlStateManager.popMatrix();
            GlStateManager.shadeModel(7424);
        }
        GlStateManager.enableTexture();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        final float float6 = 1.0f - this.world.getRainGradient(tickDelta);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, float6);
        GlStateManager.rotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(this.world.getSkyAngle(tickDelta) * 360.0f, 1.0f, 0.0f, 0.0f);
        float float7 = 30.0f;
        this.textureManager.bindTexture(WorldRenderer.SUN_TEX);
        bufferBuilder7.begin(7, VertexFormats.POSITION_UV);
        bufferBuilder7.vertex(-float7, 100.0, -float7).texture(0.0, 0.0).next();
        bufferBuilder7.vertex(float7, 100.0, -float7).texture(1.0, 0.0).next();
        bufferBuilder7.vertex(float7, 100.0, float7).texture(1.0, 1.0).next();
        bufferBuilder7.vertex(-float7, 100.0, float7).texture(0.0, 1.0).next();
        tessellator6.draw();
        float7 = 20.0f;
        this.textureManager.bindTexture(WorldRenderer.MOON_PHASES_TEX);
        final int integer14 = this.world.getMoonPhase();
        final int integer12 = integer14 % 4;
        int integer13 = integer14 / 4 % 2;
        final float float9 = (integer12 + 0) / 4.0f;
        final float float10 = (integer13 + 0) / 2.0f;
        final float float11 = (integer12 + 1) / 4.0f;
        final float float12 = (integer13 + 1) / 2.0f;
        bufferBuilder7.begin(7, VertexFormats.POSITION_UV);
        bufferBuilder7.vertex(-float7, -100.0, float7).texture(float11, float12).next();
        bufferBuilder7.vertex(float7, -100.0, float7).texture(float9, float12).next();
        bufferBuilder7.vertex(float7, -100.0, -float7).texture(float9, float10).next();
        bufferBuilder7.vertex(-float7, -100.0, -float7).texture(float11, float10).next();
        tessellator6.draw();
        GlStateManager.disableTexture();
        final float float13 = this.world.getStarsBrightness(tickDelta) * float6;
        if (float13 > 0.0f) {
            GlStateManager.color4f(float13, float13, float13, float13);
            if (this.vertexBufferObjectsEnabled) {
                this.starsBuffer.bind();
                GlStateManager.enableClientState(32884);
                GlStateManager.vertexPointer(3, 5126, 12, 0);
                this.starsBuffer.draw(7);
                GlBuffer.unbind();
                GlStateManager.disableClientState(32884);
            }
            else {
                GlStateManager.callList(this.starsDisplayList);
            }
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
        GlStateManager.disableTexture();
        GlStateManager.color3f(0.0f, 0.0f, 0.0f);
        final double double9 = this.client.player.getCameraPosVec(tickDelta).y - this.world.getHorizonHeight();
        if (double9 < 0.0) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, 12.0f, 0.0f);
            if (this.vertexBufferObjectsEnabled) {
                this.v.bind();
                GlStateManager.enableClientState(32884);
                GlStateManager.vertexPointer(3, 5126, 12, 0);
                this.v.draw(7);
                GlBuffer.unbind();
                GlStateManager.disableClientState(32884);
            }
            else {
                GlStateManager.callList(this.r);
            }
            GlStateManager.popMatrix();
        }
        if (this.world.dimension.c()) {
            GlStateManager.color3f(float3 * 0.2f + 0.04f, float4 * 0.2f + 0.04f, float5 * 0.6f + 0.1f);
        }
        else {
            GlStateManager.color3f(float3, float4, float5);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, -(float)(double9 - 16.0), 0.0f);
        GlStateManager.callList(this.r);
        GlStateManager.popMatrix();
        GlStateManager.enableTexture();
        GlStateManager.depthMask(true);
    }
    
    public void renderClouds(final float tickDelta, final double double2, final double double4, final double double6) {
        if (!this.client.world.dimension.hasVisibleSky()) {
            return;
        }
        final float float8 = 12.0f;
        final float float9 = 4.0f;
        final double double7 = 2.0E-4;
        final double double8 = (this.ticks + tickDelta) * 0.03f;
        double double9 = (double2 + double8) / 12.0;
        final double double10 = this.world.dimension.getCloudHeight() - (float)double4 + 0.33f;
        double double11 = double6 / 12.0 + 0.33000001311302185;
        double9 -= MathHelper.floor(double9 / 2048.0) * 2048;
        double11 -= MathHelper.floor(double11 / 2048.0) * 2048;
        final float float10 = (float)(double9 - MathHelper.floor(double9));
        final float float11 = (float)(double10 / 4.0 - MathHelper.floor(double10 / 4.0)) * 4.0f;
        final float float12 = (float)(double11 - MathHelper.floor(double11));
        final Vec3d vec3d23 = this.world.getCloudColor(tickDelta);
        final int integer24 = (int)Math.floor(double9);
        final int integer25 = (int)Math.floor(double10 / 4.0);
        final int integer26 = (int)Math.floor(double11);
        if (integer24 != this.R || integer25 != this.S || integer26 != this.T || this.client.options.getCloudRenderMode() != this.V || this.U.squaredDistanceTo(vec3d23) > 2.0E-4) {
            this.R = integer24;
            this.S = integer25;
            this.T = integer26;
            this.U = vec3d23;
            this.V = this.client.options.getCloudRenderMode();
            this.cloudsDirty = true;
        }
        if (this.cloudsDirty) {
            this.cloudsDirty = false;
            final Tessellator tessellator27 = Tessellator.getInstance();
            final BufferBuilder bufferBuilder28 = tessellator27.getBufferBuilder();
            if (this.cloudsBuffer != null) {
                this.cloudsBuffer.delete();
            }
            if (this.cloudsDisplayList >= 0) {
                GlAllocationUtils.deleteSingletonList(this.cloudsDisplayList);
                this.cloudsDisplayList = -1;
            }
            if (this.vertexBufferObjectsEnabled) {
                this.cloudsBuffer = new GlBuffer(VertexFormats.POSITION_UV_COLOR_NORMAL);
                this.renderClouds(bufferBuilder28, double9, double10, double11, vec3d23);
                bufferBuilder28.end();
                bufferBuilder28.clear();
                this.cloudsBuffer.set(bufferBuilder28.getByteBuffer());
            }
            else {
                GlStateManager.newList(this.cloudsDisplayList = GlAllocationUtils.genLists(1), 4864);
                this.renderClouds(bufferBuilder28, double9, double10, double11, vec3d23);
                tessellator27.draw();
                GlStateManager.endList();
            }
        }
        GlStateManager.disableCull();
        this.textureManager.bindTexture(WorldRenderer.CLOUDS_TEX);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        GlStateManager.scalef(12.0f, 1.0f, 12.0f);
        GlStateManager.translatef(-float10, float11, -float12);
        if (this.vertexBufferObjectsEnabled && this.cloudsBuffer != null) {
            this.cloudsBuffer.bind();
            GlStateManager.enableClientState(32884);
            GlStateManager.enableClientState(32888);
            GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
            GlStateManager.enableClientState(32886);
            GlStateManager.enableClientState(32885);
            GlStateManager.vertexPointer(3, 5126, 28, 0);
            GlStateManager.texCoordPointer(2, 5126, 28, 12);
            GlStateManager.colorPointer(4, 5121, 28, 20);
            GlStateManager.normalPointer(5120, 28, 24);
            int integer28;
            for (int integer27 = integer28 = ((this.V != CloudRenderMode.c) ? 1 : 0); integer28 < 2; ++integer28) {
                if (integer28 == 0) {
                    GlStateManager.colorMask(false, false, false, false);
                }
                else {
                    GlStateManager.colorMask(true, true, true, true);
                }
                this.cloudsBuffer.draw(7);
            }
            GlBuffer.unbind();
            GlStateManager.disableClientState(32884);
            GlStateManager.disableClientState(32888);
            GlStateManager.disableClientState(32886);
            GlStateManager.disableClientState(32885);
        }
        else if (this.cloudsDisplayList >= 0) {
            int integer28;
            for (int integer27 = integer28 = ((this.V != CloudRenderMode.c) ? 1 : 0); integer28 < 2; ++integer28) {
                if (integer28 == 0) {
                    GlStateManager.colorMask(false, false, false, false);
                }
                else {
                    GlStateManager.colorMask(true, true, true, true);
                }
                GlStateManager.callList(this.cloudsDisplayList);
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.clearCurrentColor();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }
    
    private void renderClouds(final BufferBuilder bufferBuilder, final double double2, final double double4, final double double6, final Vec3d color) {
        final float float9 = 4.0f;
        final float float10 = 0.00390625f;
        final int integer11 = 8;
        final int integer12 = 4;
        final float float11 = 9.765625E-4f;
        final float float12 = MathHelper.floor(double2) * 0.00390625f;
        final float float13 = MathHelper.floor(double6) * 0.00390625f;
        final float float14 = (float)color.x;
        final float float15 = (float)color.y;
        final float float16 = (float)color.z;
        final float float17 = float14 * 0.9f;
        final float float18 = float15 * 0.9f;
        final float float19 = float16 * 0.9f;
        final float float20 = float14 * 0.7f;
        final float float21 = float15 * 0.7f;
        final float float22 = float16 * 0.7f;
        final float float23 = float14 * 0.8f;
        final float float24 = float15 * 0.8f;
        final float float25 = float16 * 0.8f;
        bufferBuilder.begin(7, VertexFormats.POSITION_UV_COLOR_NORMAL);
        final float float26 = (float)Math.floor(double4 / 4.0) * 4.0f;
        if (this.V == CloudRenderMode.c) {
            for (int integer13 = -3; integer13 <= 4; ++integer13) {
                for (int integer14 = -3; integer14 <= 4; ++integer14) {
                    final float float27 = (float)(integer13 * 8);
                    final float float28 = (float)(integer14 * 8);
                    if (float26 > -5.0f) {
                        bufferBuilder.vertex(float27 + 0.0f, float26 + 0.0f, float28 + 8.0f).texture((float27 + 0.0f) * 0.00390625f + float12, (float28 + 8.0f) * 0.00390625f + float13).color(float20, float21, float22, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                        bufferBuilder.vertex(float27 + 8.0f, float26 + 0.0f, float28 + 8.0f).texture((float27 + 8.0f) * 0.00390625f + float12, (float28 + 8.0f) * 0.00390625f + float13).color(float20, float21, float22, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                        bufferBuilder.vertex(float27 + 8.0f, float26 + 0.0f, float28 + 0.0f).texture((float27 + 8.0f) * 0.00390625f + float12, (float28 + 0.0f) * 0.00390625f + float13).color(float20, float21, float22, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                        bufferBuilder.vertex(float27 + 0.0f, float26 + 0.0f, float28 + 0.0f).texture((float27 + 0.0f) * 0.00390625f + float12, (float28 + 0.0f) * 0.00390625f + float13).color(float20, float21, float22, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    }
                    if (float26 <= 5.0f) {
                        bufferBuilder.vertex(float27 + 0.0f, float26 + 4.0f - 9.765625E-4f, float28 + 8.0f).texture((float27 + 0.0f) * 0.00390625f + float12, (float28 + 8.0f) * 0.00390625f + float13).color(float14, float15, float16, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                        bufferBuilder.vertex(float27 + 8.0f, float26 + 4.0f - 9.765625E-4f, float28 + 8.0f).texture((float27 + 8.0f) * 0.00390625f + float12, (float28 + 8.0f) * 0.00390625f + float13).color(float14, float15, float16, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                        bufferBuilder.vertex(float27 + 8.0f, float26 + 4.0f - 9.765625E-4f, float28 + 0.0f).texture((float27 + 8.0f) * 0.00390625f + float12, (float28 + 0.0f) * 0.00390625f + float13).color(float14, float15, float16, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                        bufferBuilder.vertex(float27 + 0.0f, float26 + 4.0f - 9.765625E-4f, float28 + 0.0f).texture((float27 + 0.0f) * 0.00390625f + float12, (float28 + 0.0f) * 0.00390625f + float13).color(float14, float15, float16, 0.8f).normal(0.0f, 1.0f, 0.0f).next();
                    }
                    if (integer13 > -1) {
                        for (int integer15 = 0; integer15 < 8; ++integer15) {
                            bufferBuilder.vertex(float27 + integer15 + 0.0f, float26 + 0.0f, float28 + 8.0f).texture((float27 + integer15 + 0.5f) * 0.00390625f + float12, (float28 + 8.0f) * 0.00390625f + float13).color(float17, float18, float19, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                            bufferBuilder.vertex(float27 + integer15 + 0.0f, float26 + 4.0f, float28 + 8.0f).texture((float27 + integer15 + 0.5f) * 0.00390625f + float12, (float28 + 8.0f) * 0.00390625f + float13).color(float17, float18, float19, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                            bufferBuilder.vertex(float27 + integer15 + 0.0f, float26 + 4.0f, float28 + 0.0f).texture((float27 + integer15 + 0.5f) * 0.00390625f + float12, (float28 + 0.0f) * 0.00390625f + float13).color(float17, float18, float19, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                            bufferBuilder.vertex(float27 + integer15 + 0.0f, float26 + 0.0f, float28 + 0.0f).texture((float27 + integer15 + 0.5f) * 0.00390625f + float12, (float28 + 0.0f) * 0.00390625f + float13).color(float17, float18, float19, 0.8f).normal(-1.0f, 0.0f, 0.0f).next();
                        }
                    }
                    if (integer13 <= 1) {
                        for (int integer15 = 0; integer15 < 8; ++integer15) {
                            bufferBuilder.vertex(float27 + integer15 + 1.0f - 9.765625E-4f, float26 + 0.0f, float28 + 8.0f).texture((float27 + integer15 + 0.5f) * 0.00390625f + float12, (float28 + 8.0f) * 0.00390625f + float13).color(float17, float18, float19, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                            bufferBuilder.vertex(float27 + integer15 + 1.0f - 9.765625E-4f, float26 + 4.0f, float28 + 8.0f).texture((float27 + integer15 + 0.5f) * 0.00390625f + float12, (float28 + 8.0f) * 0.00390625f + float13).color(float17, float18, float19, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                            bufferBuilder.vertex(float27 + integer15 + 1.0f - 9.765625E-4f, float26 + 4.0f, float28 + 0.0f).texture((float27 + integer15 + 0.5f) * 0.00390625f + float12, (float28 + 0.0f) * 0.00390625f + float13).color(float17, float18, float19, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                            bufferBuilder.vertex(float27 + integer15 + 1.0f - 9.765625E-4f, float26 + 0.0f, float28 + 0.0f).texture((float27 + integer15 + 0.5f) * 0.00390625f + float12, (float28 + 0.0f) * 0.00390625f + float13).color(float17, float18, float19, 0.8f).normal(1.0f, 0.0f, 0.0f).next();
                        }
                    }
                    if (integer14 > -1) {
                        for (int integer15 = 0; integer15 < 8; ++integer15) {
                            bufferBuilder.vertex(float27 + 0.0f, float26 + 4.0f, float28 + integer15 + 0.0f).texture((float27 + 0.0f) * 0.00390625f + float12, (float28 + integer15 + 0.5f) * 0.00390625f + float13).color(float23, float24, float25, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                            bufferBuilder.vertex(float27 + 8.0f, float26 + 4.0f, float28 + integer15 + 0.0f).texture((float27 + 8.0f) * 0.00390625f + float12, (float28 + integer15 + 0.5f) * 0.00390625f + float13).color(float23, float24, float25, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                            bufferBuilder.vertex(float27 + 8.0f, float26 + 0.0f, float28 + integer15 + 0.0f).texture((float27 + 8.0f) * 0.00390625f + float12, (float28 + integer15 + 0.5f) * 0.00390625f + float13).color(float23, float24, float25, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                            bufferBuilder.vertex(float27 + 0.0f, float26 + 0.0f, float28 + integer15 + 0.0f).texture((float27 + 0.0f) * 0.00390625f + float12, (float28 + integer15 + 0.5f) * 0.00390625f + float13).color(float23, float24, float25, 0.8f).normal(0.0f, 0.0f, -1.0f).next();
                        }
                    }
                    if (integer14 <= 1) {
                        for (int integer15 = 0; integer15 < 8; ++integer15) {
                            bufferBuilder.vertex(float27 + 0.0f, float26 + 4.0f, float28 + integer15 + 1.0f - 9.765625E-4f).texture((float27 + 0.0f) * 0.00390625f + float12, (float28 + integer15 + 0.5f) * 0.00390625f + float13).color(float23, float24, float25, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                            bufferBuilder.vertex(float27 + 8.0f, float26 + 4.0f, float28 + integer15 + 1.0f - 9.765625E-4f).texture((float27 + 8.0f) * 0.00390625f + float12, (float28 + integer15 + 0.5f) * 0.00390625f + float13).color(float23, float24, float25, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                            bufferBuilder.vertex(float27 + 8.0f, float26 + 0.0f, float28 + integer15 + 1.0f - 9.765625E-4f).texture((float27 + 8.0f) * 0.00390625f + float12, (float28 + integer15 + 0.5f) * 0.00390625f + float13).color(float23, float24, float25, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                            bufferBuilder.vertex(float27 + 0.0f, float26 + 0.0f, float28 + integer15 + 1.0f - 9.765625E-4f).texture((float27 + 0.0f) * 0.00390625f + float12, (float28 + integer15 + 0.5f) * 0.00390625f + float13).color(float23, float24, float25, 0.8f).normal(0.0f, 0.0f, 1.0f).next();
                        }
                    }
                }
            }
        }
        else {
            final int integer13 = 1;
            final int integer14 = 32;
            for (int integer16 = -32; integer16 < 32; integer16 += 32) {
                for (int integer17 = -32; integer17 < 32; integer17 += 32) {
                    bufferBuilder.vertex(integer16 + 0, float26, integer17 + 32).texture((integer16 + 0) * 0.00390625f + float12, (integer17 + 32) * 0.00390625f + float13).color(float14, float15, float16, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    bufferBuilder.vertex(integer16 + 32, float26, integer17 + 32).texture((integer16 + 32) * 0.00390625f + float12, (integer17 + 32) * 0.00390625f + float13).color(float14, float15, float16, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    bufferBuilder.vertex(integer16 + 32, float26, integer17 + 0).texture((integer16 + 32) * 0.00390625f + float12, (integer17 + 0) * 0.00390625f + float13).color(float14, float15, float16, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                    bufferBuilder.vertex(integer16 + 0, float26, integer17 + 0).texture((integer16 + 0) * 0.00390625f + float12, (integer17 + 0) * 0.00390625f + float13).color(float14, float15, float16, 0.8f).normal(0.0f, -1.0f, 0.0f).next();
                }
            }
        }
    }
    
    public void updateChunks(final long long1) {
        this.terrainUpdateNecessary |= this.chunkBatcher.a(long1);
        if (!this.chunkRenderers.isEmpty()) {
            final Iterator<ChunkRenderer> iterator3 = this.chunkRenderers.iterator();
            while (iterator3.hasNext()) {
                final ChunkRenderer chunkRenderer4 = iterator3.next();
                boolean boolean5;
                if (chunkRenderer4.n()) {
                    boolean5 = this.chunkBatcher.b(chunkRenderer4);
                }
                else {
                    boolean5 = this.chunkBatcher.a(chunkRenderer4);
                }
                if (!boolean5) {
                    break;
                }
                chunkRenderer4.l();
                iterator3.remove();
                final long long2 = long1 - SystemUtil.getMeasuringTimeNano();
                if (long2 < 0L) {
                    break;
                }
            }
        }
    }
    
    public void renderWorldBorder(final Camera camera, final float delta) {
        final Tessellator tessellator3 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder4 = tessellator3.getBufferBuilder();
        final WorldBorder worldBorder5 = this.world.getWorldBorder();
        final double double6 = this.client.options.viewDistance * 16;
        if (camera.getPos().x < worldBorder5.getBoundEast() - double6 && camera.getPos().x > worldBorder5.getBoundWest() + double6 && camera.getPos().z < worldBorder5.getBoundSouth() - double6 && camera.getPos().z > worldBorder5.getBoundNorth() + double6) {
            return;
        }
        double double7 = 1.0 - worldBorder5.contains(camera.getPos().x, camera.getPos().z) / double6;
        double7 = Math.pow(double7, 4.0);
        final double double8 = camera.getPos().x;
        final double double9 = camera.getPos().y;
        final double double10 = camera.getPos().z;
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        this.textureManager.bindTexture(WorldRenderer.FORCEFIELD_TEX);
        GlStateManager.depthMask(false);
        GlStateManager.pushMatrix();
        final int integer16 = worldBorder5.getStage().getColor();
        final float float17 = (integer16 >> 16 & 0xFF) / 255.0f;
        final float float18 = (integer16 >> 8 & 0xFF) / 255.0f;
        final float float19 = (integer16 & 0xFF) / 255.0f;
        GlStateManager.color4f(float17, float18, float19, (float)double7);
        GlStateManager.polygonOffset(-3.0f, -3.0f);
        GlStateManager.enablePolygonOffset();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableAlphaTest();
        GlStateManager.disableCull();
        final float float20 = SystemUtil.getMeasuringTimeMs() % 3000L / 3000.0f;
        final float float21 = 0.0f;
        final float float22 = 0.0f;
        final float float23 = 128.0f;
        bufferBuilder4.begin(7, VertexFormats.POSITION_UV);
        bufferBuilder4.setOffset(-double8, -double9, -double10);
        double double11 = Math.max(MathHelper.floor(double10 - double6), worldBorder5.getBoundNorth());
        double double12 = Math.min(MathHelper.ceil(double10 + double6), worldBorder5.getBoundSouth());
        if (double8 > worldBorder5.getBoundEast() - double6) {
            float float24 = 0.0f;
            for (double double13 = double11; double13 < double12; ++double13, float24 += 0.5f) {
                final double double14 = Math.min(1.0, double12 - double13);
                final float float25 = (float)double14 * 0.5f;
                bufferBuilder4.vertex(worldBorder5.getBoundEast(), 256.0, double13).texture(float20 + float24, float20 + 0.0f).next();
                bufferBuilder4.vertex(worldBorder5.getBoundEast(), 256.0, double13 + double14).texture(float20 + float25 + float24, float20 + 0.0f).next();
                bufferBuilder4.vertex(worldBorder5.getBoundEast(), 0.0, double13 + double14).texture(float20 + float25 + float24, float20 + 128.0f).next();
                bufferBuilder4.vertex(worldBorder5.getBoundEast(), 0.0, double13).texture(float20 + float24, float20 + 128.0f).next();
            }
        }
        if (double8 < worldBorder5.getBoundWest() + double6) {
            float float24 = 0.0f;
            for (double double13 = double11; double13 < double12; ++double13, float24 += 0.5f) {
                final double double14 = Math.min(1.0, double12 - double13);
                final float float25 = (float)double14 * 0.5f;
                bufferBuilder4.vertex(worldBorder5.getBoundWest(), 256.0, double13).texture(float20 + float24, float20 + 0.0f).next();
                bufferBuilder4.vertex(worldBorder5.getBoundWest(), 256.0, double13 + double14).texture(float20 + float25 + float24, float20 + 0.0f).next();
                bufferBuilder4.vertex(worldBorder5.getBoundWest(), 0.0, double13 + double14).texture(float20 + float25 + float24, float20 + 128.0f).next();
                bufferBuilder4.vertex(worldBorder5.getBoundWest(), 0.0, double13).texture(float20 + float24, float20 + 128.0f).next();
            }
        }
        double11 = Math.max(MathHelper.floor(double8 - double6), worldBorder5.getBoundWest());
        double12 = Math.min(MathHelper.ceil(double8 + double6), worldBorder5.getBoundEast());
        if (double10 > worldBorder5.getBoundSouth() - double6) {
            float float24 = 0.0f;
            for (double double13 = double11; double13 < double12; ++double13, float24 += 0.5f) {
                final double double14 = Math.min(1.0, double12 - double13);
                final float float25 = (float)double14 * 0.5f;
                bufferBuilder4.vertex(double13, 256.0, worldBorder5.getBoundSouth()).texture(float20 + float24, float20 + 0.0f).next();
                bufferBuilder4.vertex(double13 + double14, 256.0, worldBorder5.getBoundSouth()).texture(float20 + float25 + float24, float20 + 0.0f).next();
                bufferBuilder4.vertex(double13 + double14, 0.0, worldBorder5.getBoundSouth()).texture(float20 + float25 + float24, float20 + 128.0f).next();
                bufferBuilder4.vertex(double13, 0.0, worldBorder5.getBoundSouth()).texture(float20 + float24, float20 + 128.0f).next();
            }
        }
        if (double10 < worldBorder5.getBoundNorth() + double6) {
            float float24 = 0.0f;
            for (double double13 = double11; double13 < double12; ++double13, float24 += 0.5f) {
                final double double14 = Math.min(1.0, double12 - double13);
                final float float25 = (float)double14 * 0.5f;
                bufferBuilder4.vertex(double13, 256.0, worldBorder5.getBoundNorth()).texture(float20 + float24, float20 + 0.0f).next();
                bufferBuilder4.vertex(double13 + double14, 256.0, worldBorder5.getBoundNorth()).texture(float20 + float25 + float24, float20 + 0.0f).next();
                bufferBuilder4.vertex(double13 + double14, 0.0, worldBorder5.getBoundNorth()).texture(float20 + float25 + float24, float20 + 128.0f).next();
                bufferBuilder4.vertex(double13, 0.0, worldBorder5.getBoundNorth()).texture(float20 + float24, float20 + 128.0f).next();
            }
        }
        tessellator3.draw();
        bufferBuilder4.setOffset(0.0, 0.0, 0.0);
        GlStateManager.enableCull();
        GlStateManager.disableAlphaTest();
        GlStateManager.polygonOffset(0.0f, 0.0f);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableAlphaTest();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
    }
    
    private void enableBlockOverlayRendering() {
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableBlend();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 0.5f);
        GlStateManager.polygonOffset(-1.0f, -10.0f);
        GlStateManager.enablePolygonOffset();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableAlphaTest();
        GlStateManager.pushMatrix();
    }
    
    private void disableBlockOverlayRendering() {
        GlStateManager.disableAlphaTest();
        GlStateManager.polygonOffset(0.0f, 0.0f);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableAlphaTest();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }
    
    public void renderPartiallyBrokenBlocks(final Tessellator tesselator, final BufferBuilder builder, final Camera camera) {
        final double double4 = camera.getPos().x;
        final double double5 = camera.getPos().y;
        final double double6 = camera.getPos().z;
        if (!this.partiallyBrokenBlocks.isEmpty()) {
            this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            this.enableBlockOverlayRendering();
            builder.begin(7, VertexFormats.POSITION_COLOR_UV_LMAP);
            builder.setOffset(-double4, -double5, -double6);
            builder.disableColor();
            final Iterator<PartiallyBrokenBlockEntry> iterator10 = this.partiallyBrokenBlocks.values().iterator();
            while (iterator10.hasNext()) {
                final PartiallyBrokenBlockEntry partiallyBrokenBlockEntry11 = iterator10.next();
                final BlockPos blockPos12 = partiallyBrokenBlockEntry11.getPos();
                final Block block13 = this.world.getBlockState(blockPos12).getBlock();
                if (!(block13 instanceof ChestBlock) && !(block13 instanceof EnderChestBlock) && !(block13 instanceof AbstractSignBlock)) {
                    if (block13 instanceof AbstractSkullBlock) {
                        continue;
                    }
                    final double double7 = blockPos12.getX() - double4;
                    final double double8 = blockPos12.getY() - double5;
                    final double double9 = blockPos12.getZ() - double6;
                    if (double7 * double7 + double8 * double8 + double9 * double9 > 1024.0) {
                        iterator10.remove();
                    }
                    else {
                        final BlockState blockState20 = this.world.getBlockState(blockPos12);
                        if (blockState20.isAir()) {
                            continue;
                        }
                        final int integer21 = partiallyBrokenBlockEntry11.getStage();
                        final Sprite sprite22 = this.destroyStages[integer21];
                        final BlockRenderManager blockRenderManager23 = this.client.getBlockRenderManager();
                        blockRenderManager23.tesselateDamage(blockState20, blockPos12, sprite22, this.world);
                    }
                }
            }
            tesselator.draw();
            builder.setOffset(0.0, 0.0, 0.0);
            this.disableBlockOverlayRendering();
        }
    }
    
    public void drawHighlightedBlockOutline(final Camera camera, final HitResult hit, final int renderPass) {
        if (renderPass == 0 && hit.getType() == HitResult.Type.BLOCK) {
            final BlockPos blockPos4 = ((BlockHitResult)hit).getBlockPos();
            final BlockState blockState5 = this.world.getBlockState(blockPos4);
            if (!blockState5.isAir() && this.world.getWorldBorder().contains(blockPos4)) {
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.lineWidth(Math.max(2.5f, this.client.window.getFramebufferWidth() / 1920.0f * 2.5f));
                GlStateManager.disableTexture();
                GlStateManager.depthMask(false);
                GlStateManager.matrixMode(5889);
                GlStateManager.pushMatrix();
                GlStateManager.scalef(1.0f, 1.0f, 0.999f);
                final double double6 = camera.getPos().x;
                final double double7 = camera.getPos().y;
                final double double8 = camera.getPos().z;
                drawShapeOutline(blockState5.getOutlineShape(this.world, blockPos4, VerticalEntityPosition.fromEntity(camera.getFocusedEntity())), blockPos4.getX() - double6, blockPos4.getY() - double7, blockPos4.getZ() - double8, 0.0f, 0.0f, 0.0f, 0.4f);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(5888);
                GlStateManager.depthMask(true);
                GlStateManager.enableTexture();
                GlStateManager.disableBlend();
            }
        }
    }
    
    public static void drawDebugShapeOutline(final VoxelShape shape, final double x, final double y, final double z, final float red, final float green, final float blue, final float alpha) {
        final List<BoundingBox> list12 = shape.getBoundingBoxes();
        final int integer13 = MathHelper.ceil(list12.size() / 3.0);
        for (int integer14 = 0; integer14 < list12.size(); ++integer14) {
            final BoundingBox boundingBox15 = list12.get(integer14);
            final float float16 = (integer14 % (float)integer13 + 1.0f) / integer13;
            final float float17 = (float)(integer14 / integer13);
            final float float18 = float16 * ((float17 == 0.0f) ? 1 : 0);
            final float float19 = float16 * ((float17 == 1.0f) ? 1 : 0);
            final float float20 = float16 * ((float17 == 2.0f) ? 1 : 0);
            drawShapeOutline(VoxelShapes.cuboid(boundingBox15.offset(0.0, 0.0, 0.0)), x, y, z, float18, float19, float20, 1.0f);
        }
    }
    
    public static void drawShapeOutline(final VoxelShape shape, final double x, final double y, final double z, final float red, final float green, final float blue, final float alpha) {
        final Tessellator tessellator12 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder13 = tessellator12.getBufferBuilder();
        bufferBuilder13.begin(1, VertexFormats.POSITION_COLOR);
        final BufferBuilder bufferBuilder14;
        shape.forEachEdge((double12, double14, double16, double18, double20, double22) -> {
            bufferBuilder14.vertex(double12 + x, double14 + y, double16 + z).color(red, green, blue, alpha).next();
            bufferBuilder14.vertex(double18 + x, double20 + y, double22 + z).color(red, green, blue, alpha).next();
            return;
        });
        tessellator12.draw();
    }
    
    public static void drawBoxOutline(final BoundingBox box, final float red, final float green, final float blue, final float alpha) {
        drawBoxOutline(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);
    }
    
    public static void drawBoxOutline(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ, final float red, final float green, final float blue, final float alpha) {
        final Tessellator tessellator17 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder18 = tessellator17.getBufferBuilder();
        bufferBuilder18.begin(3, VertexFormats.POSITION_COLOR);
        buildBoxOutline(bufferBuilder18, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tessellator17.draw();
    }
    
    public static void buildBoxOutline(final BufferBuilder buffer, final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ, final float red, final float green, final float blue, final float alpha) {
        buffer.vertex(minX, minY, minZ).color(red, green, blue, 0.0f).next();
        buffer.vertex(minX, minY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(maxX, minY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(maxX, minY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(minX, minY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(minX, minY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(minX, maxY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(maxX, maxY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(maxX, maxY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(minX, maxY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(minX, maxY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(minX, maxY, maxZ).color(red, green, blue, 0.0f).next();
        buffer.vertex(minX, minY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(maxX, maxY, maxZ).color(red, green, blue, 0.0f).next();
        buffer.vertex(maxX, minY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(maxX, maxY, minZ).color(red, green, blue, 0.0f).next();
        buffer.vertex(maxX, minY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(maxX, minY, minZ).color(red, green, blue, 0.0f).next();
    }
    
    public static void buildBox(final BufferBuilder builder, final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ, final float red, final float green, final float blue, final float alpha) {
        builder.vertex(minX, minY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, minY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, minY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, minY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, maxY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, maxY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, maxY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, minY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, maxY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, minY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, minY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, minY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, maxY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, maxY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, maxY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, minY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, maxY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, minY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, minY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, minY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, minY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, minY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, minY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, maxY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, maxY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(minX, maxY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, maxY, minZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, maxY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, maxY, maxZ).color(red, green, blue, alpha).next();
        builder.vertex(maxX, maxY, maxZ).color(red, green, blue, alpha).next();
    }
    
    public void updateBlock(final BlockView blockView, final BlockPos pos, final BlockState blockState3, final BlockState blockState4, final int integer) {
        this.scheduleSectionRender(pos, (integer & 0x8) != 0x0);
    }
    
    private void scheduleSectionRender(final BlockPos pos, final boolean boolean2) {
        for (int integer3 = pos.getZ() - 1; integer3 <= pos.getZ() + 1; ++integer3) {
            for (int integer4 = pos.getX() - 1; integer4 <= pos.getX() + 1; ++integer4) {
                for (int integer5 = pos.getY() - 1; integer5 <= pos.getY() + 1; ++integer5) {
                    this.scheduleChunkRender(integer4 >> 4, integer5 >> 4, integer3 >> 4, boolean2);
                }
            }
        }
    }
    
    public void scheduleBlockRenders(final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ) {
        for (int integer7 = minZ - 1; integer7 <= maxZ + 1; ++integer7) {
            for (int integer8 = minX - 1; integer8 <= maxX + 1; ++integer8) {
                for (int integer9 = minY - 1; integer9 <= maxY + 1; ++integer9) {
                    this.scheduleBlockRender(integer8 >> 4, integer9 >> 4, integer7 >> 4);
                }
            }
        }
    }
    
    public void scheduleBlockRenders(final int x, final int y, final int z) {
        for (int integer4 = z - 1; integer4 <= z + 1; ++integer4) {
            for (int integer5 = x - 1; integer5 <= x + 1; ++integer5) {
                for (int integer6 = y - 1; integer6 <= y + 1; ++integer6) {
                    this.scheduleBlockRender(integer5, integer6, integer4);
                }
            }
        }
    }
    
    public void scheduleBlockRender(final int x, final int y, final int z) {
        this.scheduleChunkRender(x, y, z, false);
    }
    
    private void scheduleChunkRender(final int x, final int y, final int z, final boolean boolean4) {
        this.chunkRenderDispatcher.scheduleChunkRender(x, y, z, boolean4);
    }
    
    public void playSong(@Nullable final SoundEvent song, final BlockPos pos) {
        SoundInstance soundInstance3 = this.playingSongs.get(pos);
        if (soundInstance3 != null) {
            this.client.getSoundManager().stop(soundInstance3);
            this.playingSongs.remove(pos);
        }
        if (song != null) {
            final MusicDiscItem musicDiscItem4 = MusicDiscItem.bySound(song);
            if (musicDiscItem4 != null) {
                this.client.inGameHud.setRecordPlayingOverlay(musicDiscItem4.getDescription().getFormattedText());
            }
            soundInstance3 = PositionedSoundInstance.record(song, (float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
            this.playingSongs.put(pos, soundInstance3);
            this.client.getSoundManager().play(soundInstance3);
        }
        this.updateEntitiesForSong(this.world, pos, song != null);
    }
    
    private void updateEntitiesForSong(final World world, final BlockPos pos, final boolean playing) {
        final List<LivingEntity> list4 = world.<LivingEntity>getEntities(LivingEntity.class, new BoundingBox(pos).expand(3.0));
        for (final LivingEntity livingEntity6 : list4) {
            livingEntity6.setNearbySongPlaying(pos, playing);
        }
    }
    
    public void addParticle(final ParticleParameters parameters, final boolean shouldAlwaysSpawn, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
        this.addParticle(parameters, shouldAlwaysSpawn, false, x, y, z, velocityX, velocityY, velocityZ);
    }
    
    public void addParticle(final ParticleParameters parameters, final boolean shouldAlwaysSpawn, final boolean isImportant, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityY) {
        try {
            this.spawnParticle(parameters, shouldAlwaysSpawn, isImportant, x, y, z, velocityX, velocityY, velocityY);
        }
        catch (Throwable throwable16) {
            final CrashReport crashReport17 = CrashReport.create(throwable16, "Exception while adding particle");
            final CrashReportSection crashReportSection18 = crashReport17.addElement("Particle being added");
            crashReportSection18.add("ID", Registry.PARTICLE_TYPE.getId(parameters.getType()));
            crashReportSection18.add("Parameters", parameters.asString());
            crashReportSection18.add("Position", () -> CrashReportSection.createPositionString(x, y, z));
            throw new CrashException(crashReport17);
        }
    }
    
    private <T extends ParticleParameters> void addParticle(final T parameters, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
        this.addParticle(parameters, parameters.getType().shouldAlwaysSpawn(), x, y, z, velocityX, velocityY, velocityZ);
    }
    
    @Nullable
    private Particle spawnParticle(final ParticleParameters parameters, final boolean alwaysSpawn, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
        return this.spawnParticle(parameters, alwaysSpawn, false, x, y, z, velocityX, velocityY, velocityZ);
    }
    
    @Nullable
    private Particle spawnParticle(final ParticleParameters parameters, final boolean alwaysSpawn, final boolean canSpawnOnMinimal, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
        final Camera camera16 = this.client.gameRenderer.getCamera();
        if (this.client == null || !camera16.isReady() || this.client.particleManager == null) {
            return null;
        }
        final ParticlesOption particlesOption17 = this.getRandomParticleSpawnChance(canSpawnOnMinimal);
        if (alwaysSpawn) {
            return this.client.particleManager.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
        }
        if (camera16.getPos().squaredDistanceTo(x, y, z) > 1024.0) {
            return null;
        }
        if (particlesOption17 == ParticlesOption.c) {
            return null;
        }
        return this.client.particleManager.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
    }
    
    private ParticlesOption getRandomParticleSpawnChance(final boolean canSpawnOnMinimal) {
        ParticlesOption particlesOption2 = this.client.options.particles;
        if (canSpawnOnMinimal && particlesOption2 == ParticlesOption.c && this.world.random.nextInt(10) == 0) {
            particlesOption2 = ParticlesOption.b;
        }
        if (particlesOption2 == ParticlesOption.b && this.world.random.nextInt(3) == 0) {
            particlesOption2 = ParticlesOption.c;
        }
        return particlesOption2;
    }
    
    public void l() {
    }
    
    public void playGlobalEvent(final int integer1, final BlockPos blockPos, final int integer3) {
        switch (integer1) {
            case 1023:
            case 1028:
            case 1038: {
                final Camera camera4 = this.client.gameRenderer.getCamera();
                if (!camera4.isReady()) {
                    break;
                }
                final double double5 = blockPos.getX() - camera4.getPos().x;
                final double double6 = blockPos.getY() - camera4.getPos().y;
                final double double7 = blockPos.getZ() - camera4.getPos().z;
                final double double8 = Math.sqrt(double5 * double5 + double6 * double6 + double7 * double7);
                double double9 = camera4.getPos().x;
                double double10 = camera4.getPos().y;
                double double11 = camera4.getPos().z;
                if (double8 > 0.0) {
                    double9 += double5 / double8 * 2.0;
                    double10 += double6 / double8 * 2.0;
                    double11 += double7 / double8 * 2.0;
                }
                if (integer1 == 1023) {
                    this.world.playSound(double9, double10, double11, SoundEvents.nv, SoundCategory.f, 1.0f, 1.0f, false);
                    break;
                }
                if (integer1 == 1038) {
                    this.world.playSound(double9, double10, double11, SoundEvents.cS, SoundCategory.f, 1.0f, 1.0f, false);
                    break;
                }
                this.world.playSound(double9, double10, double11, SoundEvents.cx, SoundCategory.f, 5.0f, 1.0f, false);
                break;
            }
        }
    }
    
    public void playLevelEvent(final PlayerEntity source, final int type, final BlockPos pos, final int data) {
        final Random random5 = this.world.random;
        switch (type) {
            case 1035: {
                this.world.playSound(pos, SoundEvents.aq, SoundCategory.e, 1.0f, 1.0f, false);
                break;
            }
            case 1033: {
                this.world.playSound(pos, SoundEvents.aV, SoundCategory.e, 1.0f, 1.0f, false);
                break;
            }
            case 1034: {
                this.world.playSound(pos, SoundEvents.aU, SoundCategory.e, 1.0f, 1.0f, false);
                break;
            }
            case 1032: {
                this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.iY, random5.nextFloat() * 0.4f + 0.8f));
                break;
            }
            case 1001: {
                this.world.playSound(pos, SoundEvents.bJ, SoundCategory.e, 1.0f, 1.2f, false);
                break;
            }
            case 1000: {
                this.world.playSound(pos, SoundEvents.bI, SoundCategory.e, 1.0f, 1.0f, false);
                break;
            }
            case 1003: {
                this.world.playSound(pos, SoundEvents.cE, SoundCategory.g, 1.0f, 1.2f, false);
                break;
            }
            case 1004: {
                this.world.playSound(pos, SoundEvents.dm, SoundCategory.g, 1.0f, 1.2f, false);
                break;
            }
            case 1002: {
                this.world.playSound(pos, SoundEvents.bK, SoundCategory.e, 1.0f, 1.2f, false);
                break;
            }
            case 2000: {
                final Direction direction6 = Direction.byId(data);
                final int integer7 = direction6.getOffsetX();
                final int integer8 = direction6.getOffsetY();
                final int integer9 = direction6.getOffsetZ();
                final double double10 = pos.getX() + integer7 * 0.6 + 0.5;
                final double double11 = pos.getY() + integer8 * 0.6 + 0.5;
                final double double12 = pos.getZ() + integer9 * 0.6 + 0.5;
                for (int integer10 = 0; integer10 < 10; ++integer10) {
                    final double double13 = random5.nextDouble() * 0.2 + 0.01;
                    final double double14 = double10 + integer7 * 0.01 + (random5.nextDouble() - 0.5) * integer9 * 0.5;
                    final double double15 = double11 + integer8 * 0.01 + (random5.nextDouble() - 0.5) * integer8 * 0.5;
                    final double double16 = double12 + integer9 * 0.01 + (random5.nextDouble() - 0.5) * integer7 * 0.5;
                    final double double17 = integer7 * double13 + random5.nextGaussian() * 0.01;
                    final double double18 = integer8 * double13 + random5.nextGaussian() * 0.01;
                    final double double19 = integer9 * double13 + random5.nextGaussian() * 0.01;
                    this.<DefaultParticleType>addParticle(ParticleTypes.Q, double14, double15, double16, double17, double18, double19);
                }
                break;
            }
            case 2003: {
                final double double20 = pos.getX() + 0.5;
                final double double21 = pos.getY();
                final double double10 = pos.getZ() + 0.5;
                for (int integer11 = 0; integer11 < 8; ++integer11) {
                    this.<ItemStackParticleParameters>addParticle(new ItemStackParticleParameters(ParticleTypes.G, new ItemStack(Items.mt)), double20, double21, double10, random5.nextGaussian() * 0.15, random5.nextDouble() * 0.2, random5.nextGaussian() * 0.15);
                }
                for (double double11 = 0.0; double11 < 6.283185307179586; double11 += 0.15707963267948966) {
                    this.<DefaultParticleType>addParticle(ParticleTypes.O, double20 + Math.cos(double11) * 5.0, double21 - 0.4, double10 + Math.sin(double11) * 5.0, Math.cos(double11) * -5.0, 0.0, Math.sin(double11) * -5.0);
                    this.<DefaultParticleType>addParticle(ParticleTypes.O, double20 + Math.cos(double11) * 5.0, double21 - 0.4, double10 + Math.sin(double11) * 5.0, Math.cos(double11) * -7.0, 0.0, Math.sin(double11) * -7.0);
                }
                break;
            }
            case 2002:
            case 2007: {
                final double double20 = pos.getX();
                final double double21 = pos.getY();
                final double double10 = pos.getZ();
                for (int integer11 = 0; integer11 < 8; ++integer11) {
                    this.<ItemStackParticleParameters>addParticle(new ItemStackParticleParameters(ParticleTypes.G, new ItemStack(Items.oS)), double20, double21, double10, random5.nextGaussian() * 0.15, random5.nextDouble() * 0.2, random5.nextGaussian() * 0.15);
                }
                final float float12 = (data >> 16 & 0xFF) / 255.0f;
                final float float13 = (data >> 8 & 0xFF) / 255.0f;
                final float float14 = (data >> 0 & 0xFF) / 255.0f;
                final ParticleParameters particleParameters15 = (type == 2007) ? ParticleTypes.F : ParticleTypes.p;
                for (int integer10 = 0; integer10 < 100; ++integer10) {
                    final double double13 = random5.nextDouble() * 4.0;
                    final double double14 = random5.nextDouble() * 3.141592653589793 * 2.0;
                    final double double15 = Math.cos(double14) * double13;
                    final double double16 = 0.01 + random5.nextDouble() * 0.5;
                    final double double17 = Math.sin(double14) * double13;
                    final Particle particle27 = this.spawnParticle(particleParameters15, particleParameters15.getType().shouldAlwaysSpawn(), double20 + double15 * 0.1, double21 + 0.3, double10 + double17 * 0.1, double15, double16, double17);
                    if (particle27 != null) {
                        final float float15 = 0.75f + random5.nextFloat() * 0.25f;
                        particle27.setColor(float12 * float15, float13 * float15, float14 * float15);
                        particle27.move((float)double13);
                    }
                }
                this.world.playSound(pos, SoundEvents.lg, SoundCategory.g, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2001: {
                final BlockState blockState6 = Block.getStateFromRawId(data);
                if (!blockState6.isAir()) {
                    final BlockSoundGroup blockSoundGroup7 = blockState6.getSoundGroup();
                    this.world.playSound(pos, blockSoundGroup7.getBreakSound(), SoundCategory.e, (blockSoundGroup7.getVolume() + 1.0f) / 2.0f, blockSoundGroup7.getPitch() * 0.8f, false);
                }
                this.client.particleManager.addBlockBreakParticles(pos, blockState6);
                break;
            }
            case 2004: {
                for (int integer7 = 0; integer7 < 20; ++integer7) {
                    final double double21 = pos.getX() + 0.5 + (this.world.random.nextFloat() - 0.5) * 2.0;
                    final double double10 = pos.getY() + 0.5 + (this.world.random.nextFloat() - 0.5) * 2.0;
                    final double double11 = pos.getZ() + 0.5 + (this.world.random.nextFloat() - 0.5) * 2.0;
                    this.world.addParticle(ParticleTypes.Q, double21, double10, double11, 0.0, 0.0, 0.0);
                    this.world.addParticle(ParticleTypes.A, double21, double10, double11, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 2005: {
                BoneMealItem.playEffects(this.world, pos, data);
                break;
            }
            case 2008: {
                this.world.addParticle(ParticleTypes.w, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
                break;
            }
            case 1500: {
                ComposterBlock.playEffects(this.world, pos, data > 0);
                break;
            }
            case 1501: {
                this.world.playSound(pos, SoundEvents.fR, SoundCategory.e, 0.5f, 2.6f + (this.world.getRandom().nextFloat() - this.world.getRandom().nextFloat()) * 0.8f, false);
                for (int integer7 = 0; integer7 < 8; ++integer7) {
                    this.world.addParticle(ParticleTypes.J, pos.getX() + Math.random(), pos.getY() + 1.2, pos.getZ() + Math.random(), 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1502: {
                this.world.playSound(pos, SoundEvents.jA, SoundCategory.e, 0.5f, 2.6f + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.8f, false);
                for (int integer7 = 0; integer7 < 5; ++integer7) {
                    final double double21 = pos.getX() + random5.nextDouble() * 0.6 + 0.2;
                    final double double10 = pos.getY() + random5.nextDouble() * 0.6 + 0.2;
                    final double double11 = pos.getZ() + random5.nextDouble() * 0.6 + 0.2;
                    this.world.addParticle(ParticleTypes.Q, double21, double10, double11, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1503: {
                this.world.playSound(pos, SoundEvents.cR, SoundCategory.e, 1.0f, 1.0f, false);
                for (int integer7 = 0; integer7 < 16; ++integer7) {
                    final double double21 = pos.getX() + (5.0f + random5.nextFloat() * 6.0f) / 16.0f;
                    final double double10 = pos.getY() + 0.8125f;
                    final double double11 = pos.getZ() + (5.0f + random5.nextFloat() * 6.0f) / 16.0f;
                    final double double12 = 0.0;
                    final double double22 = 0.0;
                    final double double23 = 0.0;
                    this.world.addParticle(ParticleTypes.Q, double21, double10, double11, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 2006: {
                for (int integer7 = 0; integer7 < 200; ++integer7) {
                    final float float16 = random5.nextFloat() * 4.0f;
                    final float float17 = random5.nextFloat() * 6.2831855f;
                    final double double10 = MathHelper.cos(float17) * float16;
                    final double double11 = 0.01 + random5.nextDouble() * 0.5;
                    final double double12 = MathHelper.sin(float17) * float16;
                    final Particle particle28 = this.spawnParticle(ParticleTypes.i, false, pos.getX() + double10 * 0.1, pos.getY() + 0.3, pos.getZ() + double12 * 0.1, double10, double11, double12);
                    if (particle28 != null) {
                        particle28.move(float16);
                    }
                }
                this.world.playSound(pos, SoundEvents.cy, SoundCategory.f, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1012: {
                this.world.playSound(pos, SoundEvents.nF, SoundCategory.e, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1036: {
                this.world.playSound(pos, SoundEvents.fx, SoundCategory.e, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1013: {
                this.world.playSound(pos, SoundEvents.nH, SoundCategory.e, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1014: {
                this.world.playSound(pos, SoundEvents.de, SoundCategory.e, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1011: {
                this.world.playSound(pos, SoundEvents.fr, SoundCategory.e, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1006: {
                this.world.playSound(pos, SoundEvents.nG, SoundCategory.e, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1007: {
                this.world.playSound(pos, SoundEvents.nI, SoundCategory.e, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1037: {
                this.world.playSound(pos, SoundEvents.fy, SoundCategory.e, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1008: {
                this.world.playSound(pos, SoundEvents.df, SoundCategory.e, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1005: {
                this.world.playSound(pos, SoundEvents.fs, SoundCategory.e, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1009: {
                this.world.playSound(pos, SoundEvents.dq, SoundCategory.e, 0.5f, 2.6f + (random5.nextFloat() - random5.nextFloat()) * 0.8f, false);
                break;
            }
            case 1029: {
                this.world.playSound(pos, SoundEvents.i, SoundCategory.e, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1030: {
                this.world.playSound(pos, SoundEvents.o, SoundCategory.e, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1031: {
                this.world.playSound(pos, SoundEvents.l, SoundCategory.e, 0.3f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1039: {
                this.world.playSound(pos, SoundEvents.ii, SoundCategory.f, 0.3f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1010: {
                if (Item.byRawId(data) instanceof MusicDiscItem) {
                    this.playSong(((MusicDiscItem)Item.byRawId(data)).getSound(), pos);
                    break;
                }
                this.playSong(null, pos);
                break;
            }
            case 1015: {
                this.world.playSound(pos, SoundEvents.dU, SoundCategory.f, 10.0f, (random5.nextFloat() - random5.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1017: {
                this.world.playSound(pos, SoundEvents.cC, SoundCategory.f, 10.0f, (random5.nextFloat() - random5.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1016: {
                this.world.playSound(pos, SoundEvents.dT, SoundCategory.f, 10.0f, (random5.nextFloat() - random5.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1019: {
                this.world.playSound(pos, SoundEvents.nT, SoundCategory.f, 2.0f, (random5.nextFloat() - random5.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1022: {
                this.world.playSound(pos, SoundEvents.nn, SoundCategory.f, 2.0f, (random5.nextFloat() - random5.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1021: {
                this.world.playSound(pos, SoundEvents.nV, SoundCategory.f, 2.0f, (random5.nextFloat() - random5.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1020: {
                this.world.playSound(pos, SoundEvents.nU, SoundCategory.f, 2.0f, (random5.nextFloat() - random5.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1018: {
                this.world.playSound(pos, SoundEvents.ae, SoundCategory.f, 2.0f, (random5.nextFloat() - random5.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1024: {
                this.world.playSound(pos, SoundEvents.nq, SoundCategory.f, 2.0f, (random5.nextFloat() - random5.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1026: {
                this.world.playSound(pos, SoundEvents.od, SoundCategory.f, 2.0f, (random5.nextFloat() - random5.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1027: {
                this.world.playSound(pos, SoundEvents.ok, SoundCategory.g, 2.0f, (random5.nextFloat() - random5.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1040: {
                this.world.playSound(pos, SoundEvents.nW, SoundCategory.g, 2.0f, (random5.nextFloat() - random5.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1041: {
                this.world.playSound(pos, SoundEvents.eY, SoundCategory.g, 2.0f, (random5.nextFloat() - random5.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1025: {
                this.world.playSound(pos, SoundEvents.T, SoundCategory.g, 0.05f, (random5.nextFloat() - random5.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1042: {
                this.world.playSound(pos, SoundEvents.eu, SoundCategory.e, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1043: {
                this.world.playSound(pos, SoundEvents.ah, SoundCategory.e, 1.0f, this.world.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 3000: {
                this.world.addParticle(ParticleTypes.v, true, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
                this.world.playSound(pos, SoundEvents.cQ, SoundCategory.e, 10.0f, (1.0f + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2f) * 0.7f, false);
                break;
            }
            case 3001: {
                this.world.playSound(pos, SoundEvents.cA, SoundCategory.f, 64.0f, 0.8f + this.world.random.nextFloat() * 0.3f, false);
                break;
            }
        }
    }
    
    public void setBlockBreakingProgress(final int integer1, final BlockPos blockPos, final int integer3) {
        if (integer3 < 0 || integer3 >= 10) {
            this.partiallyBrokenBlocks.remove(integer1);
        }
        else {
            PartiallyBrokenBlockEntry partiallyBrokenBlockEntry4 = this.partiallyBrokenBlocks.get(integer1);
            if (partiallyBrokenBlockEntry4 == null || partiallyBrokenBlockEntry4.getPos().getX() != blockPos.getX() || partiallyBrokenBlockEntry4.getPos().getY() != blockPos.getY() || partiallyBrokenBlockEntry4.getPos().getZ() != blockPos.getZ()) {
                partiallyBrokenBlockEntry4 = new PartiallyBrokenBlockEntry(integer1, blockPos);
                this.partiallyBrokenBlocks.put(integer1, partiallyBrokenBlockEntry4);
            }
            partiallyBrokenBlockEntry4.setStage(integer3);
            partiallyBrokenBlockEntry4.setLastUpdateTicks(this.ticks);
        }
    }
    
    public boolean isTerrainRenderComplete() {
        return this.chunkRenderers.isEmpty() && this.chunkBatcher.isEmpty();
    }
    
    public void scheduleTerrainUpdate() {
        this.terrainUpdateNecessary = true;
        this.cloudsDirty = true;
    }
    
    public void updateBlockEntities(final Collection<BlockEntity> removed, final Collection<BlockEntity> added) {
        synchronized (this.blockEntities) {
            this.blockEntities.removeAll(removed);
            this.blockEntities.addAll(added);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        MOON_PHASES_TEX = new Identifier("textures/environment/moon_phases.png");
        SUN_TEX = new Identifier("textures/environment/sun.png");
        CLOUDS_TEX = new Identifier("textures/environment/clouds.png");
        END_SKY_TEX = new Identifier("textures/environment/end_sky.png");
        FORCEFIELD_TEX = new Identifier("textures/misc/forcefield.png");
        DIRECTIONS = Direction.values();
    }
    
    @Environment(EnvType.CLIENT)
    class ChunkInfo
    {
        private final ChunkRenderer renderer;
        private final Direction c;
        private byte d;
        private final int e;
        
        private ChunkInfo(final ChunkRenderer chunkRenderer, @Nullable final Direction direction, final int integer) {
            this.renderer = chunkRenderer;
            this.c = direction;
            this.e = integer;
        }
        
        public void a(final byte byte1, final Direction direction) {
            this.d |= (byte)(byte1 | 1 << direction.ordinal());
        }
        
        public boolean a(final Direction direction) {
            return (this.d & 1 << direction.ordinal()) > 0;
        }
    }
}
