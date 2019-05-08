package net.minecraft.client.render.debug;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.font.TextRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.EntityHitResult;
import java.util.function.Predicate;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ProjectileUtil;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DebugRenderer
{
    public final PathfindingDebugRenderer pathfindingDebugRenderer;
    public final Renderer waterDebugRenderer;
    public final Renderer chunkBorderDebugRenderer;
    public final Renderer heightmapDebugRenderer;
    public final Renderer voxelDebugRenderer;
    public final Renderer neighborUpdateDebugRenderer;
    public final CaveDebugRenderer caveDebugRenderer;
    public final StructureDebugRenderer structureDebugRenderer;
    public final Renderer skyLightDebugRenderer;
    public final Renderer worldGenAttemptDebugRenderer;
    public final Renderer blockOutlineDebugRenderer;
    public final Renderer chunkLoadingDebugRenderer;
    public final PointOfInterestDebugRenderer pointsOfInterestDebugRenderer;
    public final GoalSelectorDebugRenderer goalSelectorDebugRenderer;
    private boolean showChunkBorder;
    private boolean showPathfinding;
    private boolean showWater;
    private boolean showHeightmap;
    private boolean showVoxels;
    private boolean showNeighborUpdates;
    private boolean showCaves;
    private boolean showStructures;
    private boolean showSkyLight;
    private boolean showWorldGenAttempts;
    private boolean showBlockOutlines;
    private boolean z;
    private boolean showGoalSelectors;
    
    public DebugRenderer(final MinecraftClient minecraftClient) {
        this.pathfindingDebugRenderer = new PathfindingDebugRenderer(minecraftClient);
        this.waterDebugRenderer = new WaterDebugRenderer(minecraftClient);
        this.chunkBorderDebugRenderer = new ChunkBorderDebugRenderer(minecraftClient);
        this.heightmapDebugRenderer = new HeightmapDebugRenderer(minecraftClient);
        this.voxelDebugRenderer = new VoxelDebugRenderer(minecraftClient);
        this.neighborUpdateDebugRenderer = new NeighborUpdateDebugRenderer(minecraftClient);
        this.caveDebugRenderer = new CaveDebugRenderer(minecraftClient);
        this.structureDebugRenderer = new StructureDebugRenderer(minecraftClient);
        this.skyLightDebugRenderer = new SkyLightDebugRenderer(minecraftClient);
        this.worldGenAttemptDebugRenderer = new WorldGenAttemptDebugRenderer(minecraftClient);
        this.blockOutlineDebugRenderer = new BlockOutlineDebugRenderer(minecraftClient);
        this.chunkLoadingDebugRenderer = new ChunkLoadingDebugRenderer(minecraftClient);
        this.pointsOfInterestDebugRenderer = new PointOfInterestDebugRenderer(minecraftClient);
        this.goalSelectorDebugRenderer = new GoalSelectorDebugRenderer(minecraftClient);
    }
    
    public void a() {
        this.pathfindingDebugRenderer.a();
        this.waterDebugRenderer.a();
        this.chunkBorderDebugRenderer.a();
        this.heightmapDebugRenderer.a();
        this.voxelDebugRenderer.a();
        this.neighborUpdateDebugRenderer.a();
        this.caveDebugRenderer.a();
        this.structureDebugRenderer.a();
        this.skyLightDebugRenderer.a();
        this.worldGenAttemptDebugRenderer.a();
        this.blockOutlineDebugRenderer.a();
        this.chunkLoadingDebugRenderer.a();
        this.pointsOfInterestDebugRenderer.a();
        this.goalSelectorDebugRenderer.a();
    }
    
    public boolean shouldRender() {
        return this.showChunkBorder || this.showPathfinding || this.showWater || this.showHeightmap || this.showVoxels || this.showNeighborUpdates || this.showSkyLight || this.showWorldGenAttempts || this.showBlockOutlines || this.z || this.showGoalSelectors;
    }
    
    public boolean toggleShowChunkBorder() {
        return this.showChunkBorder = !this.showChunkBorder;
    }
    
    public void renderDebuggers(final long long1) {
        if (this.showPathfinding) {
            this.pathfindingDebugRenderer.render(long1);
        }
        if (this.showChunkBorder && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
            this.chunkBorderDebugRenderer.render(long1);
        }
        if (this.showWater) {
            this.waterDebugRenderer.render(long1);
        }
        if (this.showHeightmap) {
            this.heightmapDebugRenderer.render(long1);
        }
        if (this.showVoxels) {
            this.voxelDebugRenderer.render(long1);
        }
        if (this.showNeighborUpdates) {
            this.neighborUpdateDebugRenderer.render(long1);
        }
        if (this.showCaves) {
            this.caveDebugRenderer.render(long1);
        }
        if (this.showStructures) {
            this.structureDebugRenderer.render(long1);
        }
        if (this.showSkyLight) {
            this.skyLightDebugRenderer.render(long1);
        }
        if (this.showWorldGenAttempts) {
            this.worldGenAttemptDebugRenderer.render(long1);
        }
        if (this.showBlockOutlines) {
            this.blockOutlineDebugRenderer.render(long1);
        }
        if (this.showGoalSelectors) {
            this.goalSelectorDebugRenderer.render(long1);
        }
    }
    
    public static Optional<Entity> a(@Nullable final Entity entity, final int integer) {
        if (entity == null) {
            return Optional.<Entity>empty();
        }
        final Vec3d vec3d3 = entity.getCameraPosVec(1.0f);
        final Vec3d vec3d4 = entity.getRotationVec(1.0f).multiply(integer);
        final Vec3d vec3d5 = vec3d3.add(vec3d4);
        final BoundingBox boundingBox6 = entity.getBoundingBox().stretch(vec3d4).expand(1.0);
        final int integer2 = integer * integer;
        final Predicate<Entity> predicate8 = entity -> !entity.isSpectator() && entity.collides();
        final EntityHitResult entityHitResult9 = ProjectileUtil.rayTrace(entity, vec3d3, vec3d5, boundingBox6, predicate8, integer2);
        if (entityHitResult9 == null) {
            return Optional.<Entity>empty();
        }
        if (vec3d3.squaredDistanceTo(entityHitResult9.getPos()) > integer2) {
            return Optional.<Entity>empty();
        }
        return Optional.<Entity>of(entityHitResult9.getEntity());
    }
    
    public static void a(final BlockPos blockPos1, final BlockPos blockPos2, final float float3, final float float4, final float float5, final float float6) {
        final Camera camera7 = MinecraftClient.getInstance().gameRenderer.getCamera();
        if (!camera7.isReady()) {
            return;
        }
        final Vec3d vec3d8 = camera7.getPos().negate();
        final BoundingBox boundingBox9 = new BoundingBox(blockPos1, blockPos2).offset(vec3d8);
        a(boundingBox9, float3, float4, float5, float6);
    }
    
    public static void a(final BlockPos blockPos, final float float2, final float float3, final float float4, final float float5, final float float6) {
        final Camera camera7 = MinecraftClient.getInstance().gameRenderer.getCamera();
        if (!camera7.isReady()) {
            return;
        }
        final Vec3d vec3d8 = camera7.getPos().negate();
        final BoundingBox boundingBox9 = new BoundingBox(blockPos).offset(vec3d8).expand(float2);
        a(boundingBox9, float3, float4, float5, float6);
    }
    
    public static void a(final BoundingBox boundingBox, final float float2, final float float3, final float float4, final float float5) {
        a(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ, float2, float3, float4, float5);
    }
    
    public static void a(final double double1, final double double3, final double double5, final double double7, final double double9, final double double11, final float float13, final float float14, final float float15, final float float16) {
        final Tessellator tessellator17 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder18 = tessellator17.getBufferBuilder();
        bufferBuilder18.begin(5, VertexFormats.POSITION_COLOR);
        WorldRenderer.buildBox(bufferBuilder18, double1, double3, double5, double7, double9, double11, float13, float14, float15, float16);
        tessellator17.draw();
    }
    
    public static void a(final String string, final int integer2, final int integer3, final int integer4, final int integer5) {
        a(string, integer2 + 0.5, integer3 + 0.5, integer4 + 0.5, integer5);
    }
    
    public static void a(final String string, final double double2, final double double4, final double double6, final int integer8) {
        a(string, double2, double4, double6, integer8, 0.02f);
    }
    
    public static void a(final String string, final double double2, final double double4, final double double6, final int integer8, final float float9) {
        a(string, double2, double4, double6, integer8, float9, true, 0.0f, false);
    }
    
    public static void a(final String string, final double double2, final double double4, final double double6, final int integer, final float float9, final boolean boolean10, final float float11, final boolean boolean12) {
        final MinecraftClient minecraftClient13 = MinecraftClient.getInstance();
        final Camera camera14 = minecraftClient13.gameRenderer.getCamera();
        if (!camera14.isReady() || minecraftClient13.getEntityRenderManager().gameOptions == null) {
            return;
        }
        final TextRenderer textRenderer15 = minecraftClient13.textRenderer;
        final double double7 = camera14.getPos().x;
        final double double8 = camera14.getPos().y;
        final double double9 = camera14.getPos().z;
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)(double2 - double7), (float)(double4 - double8) + 0.07f, (float)(double6 - double9));
        GlStateManager.normal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.scalef(float9, -float9, float9);
        final EntityRenderDispatcher entityRenderDispatcher22 = minecraftClient13.getEntityRenderManager();
        GlStateManager.rotatef(-entityRenderDispatcher22.cameraYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(-entityRenderDispatcher22.cameraPitch, 1.0f, 0.0f, 0.0f);
        GlStateManager.enableTexture();
        if (boolean12) {
            GlStateManager.disableDepthTest();
        }
        else {
            GlStateManager.enableDepthTest();
        }
        GlStateManager.depthMask(true);
        GlStateManager.scalef(-1.0f, 1.0f, 1.0f);
        float float12 = boolean10 ? (-textRenderer15.getStringWidth(string) / 2.0f) : 0.0f;
        float12 -= float11 / float9;
        textRenderer15.draw(string, float12, 0.0f, integer);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableDepthTest();
        GlStateManager.popMatrix();
    }
    
    @Environment(EnvType.CLIENT)
    public interface Renderer
    {
        void render(final long arg1);
        
        default void a() {
        }
    }
}
