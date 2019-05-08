package net.minecraft.client.render.chunk;

import net.minecraft.client.render.Camera;
import javax.annotation.Nullable;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.fluid.FluidState;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import java.util.Iterator;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.world.SafeWorldView;
import java.util.Collection;
import net.minecraft.block.BlockRenderType;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.BlockView;
import java.util.Random;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.entity.Entity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexFormats;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.util.SystemUtil;
import net.minecraft.block.BlockRenderLayer;
import com.google.common.collect.Sets;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.client.gl.GlBuffer;
import net.minecraft.block.entity.BlockEntity;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChunkRenderer
{
    private volatile World world;
    private final WorldRenderer renderer;
    public static int chunkUpdateCount;
    public ChunkRenderData chunkRenderData;
    private final ReentrantLock chunkRenderLock;
    private final ReentrantLock chunkRenderDataLock;
    private ChunkRenderTask chunkRenderDataTask;
    private final Set<BlockEntity> blockEntities;
    private final GlBuffer[] buffers;
    public BoundingBox boundingBox;
    private int k;
    private boolean renderScheduled;
    private final BlockPos.Mutable origin;
    private final BlockPos.Mutable[] n;
    private boolean o;
    
    public ChunkRenderer(final World world, final WorldRenderer worldRenderer) {
        this.chunkRenderData = ChunkRenderData.EMPTY;
        this.chunkRenderLock = new ReentrantLock();
        this.chunkRenderDataLock = new ReentrantLock();
        this.blockEntities = Sets.newHashSet();
        this.buffers = new GlBuffer[BlockRenderLayer.values().length];
        this.k = -1;
        this.renderScheduled = true;
        this.origin = new BlockPos.Mutable(-1, -1, -1);
        int integer2;
        this.n = SystemUtil.<BlockPos.Mutable[]>consume(new BlockPos.Mutable[6], arr -> {
            for (integer2 = 0; integer2 < arr.length; ++integer2) {
                arr[integer2] = new BlockPos.Mutable();
            }
            return;
        });
        this.world = world;
        this.renderer = worldRenderer;
        if (GLX.useVbo()) {
            for (int integer3 = 0; integer3 < BlockRenderLayer.values().length; ++integer3) {
                this.buffers[integer3] = new GlBuffer(VertexFormats.POSITION_COLOR_UV_LMAP);
            }
        }
    }
    
    private static boolean a(final BlockPos blockPos, final World world) {
        return !world.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4).isEmpty();
    }
    
    public boolean b() {
        final BlockPos blockPos1 = new BlockPos(MinecraftClient.getInstance().player);
        final BlockPos blockPos2 = this.getOrigin();
        final int integer3 = 16;
        final int integer4 = 8;
        final int integer5 = 24;
        if (blockPos2.add(8, 8, 8).getSquaredDistance(blockPos1) > 576.0) {
            final World world6 = this.getWorld();
            final BlockPos.Mutable mutable7 = new BlockPos.Mutable(blockPos2);
            return a(mutable7.set(blockPos2).setOffset(Direction.WEST, 16), world6) && a(mutable7.set(blockPos2).setOffset(Direction.NORTH, 16), world6) && a(mutable7.set(blockPos2).setOffset(Direction.EAST, 16), world6) && a(mutable7.set(blockPos2).setOffset(Direction.SOUTH, 16), world6);
        }
        return true;
    }
    
    public boolean a(final int integer) {
        if (this.k == integer) {
            return false;
        }
        this.k = integer;
        return true;
    }
    
    public GlBuffer getGlBuffer(final int integer) {
        return this.buffers[integer];
    }
    
    public void a(final int integer1, final int integer2, final int integer3) {
        if (integer1 == this.origin.getX() && integer2 == this.origin.getY() && integer3 == this.origin.getZ()) {
            return;
        }
        this.clear();
        this.origin.set(integer1, integer2, integer3);
        this.boundingBox = new BoundingBox(integer1, integer2, integer3, integer1 + 16, integer2 + 16, integer3 + 16);
        for (final Direction direction7 : Direction.values()) {
            this.n[direction7.ordinal()].set(this.origin).setOffset(direction7, 16);
        }
    }
    
    public void resortTransparency(final float float1, final float float2, final float float3, final ChunkRenderTask chunkRenderTask) {
        final ChunkRenderData chunkRenderData5 = chunkRenderTask.getRenderData();
        if (chunkRenderData5.getBufferState() == null || chunkRenderData5.b(BlockRenderLayer.TRANSLUCENT)) {
            return;
        }
        this.beginBufferBuilding(chunkRenderTask.getBufferBuilders().get(BlockRenderLayer.TRANSLUCENT), this.origin);
        chunkRenderTask.getBufferBuilders().get(BlockRenderLayer.TRANSLUCENT).restoreState(chunkRenderData5.getBufferState());
        this.endBufferBuilding(BlockRenderLayer.TRANSLUCENT, float1, float2, float3, chunkRenderTask.getBufferBuilders().get(BlockRenderLayer.TRANSLUCENT), chunkRenderData5);
    }
    
    public void rebuildChunk(final float float1, final float float2, final float float3, final ChunkRenderTask chunkRenderTask) {
        final ChunkRenderData chunkRenderData5 = new ChunkRenderData();
        final int integer6 = 1;
        final BlockPos blockPos7 = this.origin.toImmutable();
        final BlockPos blockPos8 = blockPos7.add(15, 15, 15);
        final World world9 = this.world;
        if (world9 == null) {
            return;
        }
        chunkRenderTask.getLock().lock();
        try {
            if (chunkRenderTask.getStage() != ChunkRenderTask.Stage.b) {
                return;
            }
            chunkRenderTask.setRenderData(chunkRenderData5);
        }
        finally {
            chunkRenderTask.getLock().unlock();
        }
        final ChunkOcclusionGraphBuilder chunkOcclusionGraphBuilder10 = new ChunkOcclusionGraphBuilder();
        final Set<BlockEntity> set11 = Sets.newHashSet();
        final SafeWorldView safeWorldView12 = chunkRenderTask.getAndInvalidateWorldView();
        if (safeWorldView12 != null) {
            ++ChunkRenderer.chunkUpdateCount;
            final boolean[] arr13 = new boolean[BlockRenderLayer.values().length];
            BlockModelRenderer.enableBrightnessCache();
            final Random random14 = new Random();
            final BlockRenderManager blockRenderManager15 = MinecraftClient.getInstance().getBlockRenderManager();
            for (final BlockPos blockPos9 : BlockPos.iterate(blockPos7, blockPos8)) {
                final BlockState blockState18 = safeWorldView12.getBlockState(blockPos9);
                final Block block19 = blockState18.getBlock();
                if (blockState18.isFullOpaque(safeWorldView12, blockPos9)) {
                    chunkOcclusionGraphBuilder10.markClosed(blockPos9);
                }
                if (block19.hasBlockEntity()) {
                    final BlockEntity blockEntity20 = safeWorldView12.getBlockEntity(blockPos9, WorldChunk.CreationType.c);
                    if (blockEntity20 != null) {
                        final BlockEntityRenderer<BlockEntity> blockEntityRenderer21 = BlockEntityRenderDispatcher.INSTANCE.<BlockEntity>get(blockEntity20);
                        if (blockEntityRenderer21 != null) {
                            chunkRenderData5.addBlockEntity(blockEntity20);
                            if (blockEntityRenderer21.a(blockEntity20)) {
                                set11.add(blockEntity20);
                            }
                        }
                    }
                }
                final FluidState fluidState20 = safeWorldView12.getFluidState(blockPos9);
                if (!fluidState20.isEmpty()) {
                    final BlockRenderLayer blockRenderLayer21 = fluidState20.getRenderLayer();
                    final int integer7 = blockRenderLayer21.ordinal();
                    final BufferBuilder bufferBuilder23 = chunkRenderTask.getBufferBuilders().get(integer7);
                    if (!chunkRenderData5.isBufferInitialized(blockRenderLayer21)) {
                        chunkRenderData5.markBufferInitialized(blockRenderLayer21);
                        this.beginBufferBuilding(bufferBuilder23, blockPos7);
                    }
                    final boolean[] array = arr13;
                    final int n = integer7;
                    array[n] |= blockRenderManager15.tesselateFluid(blockPos9, safeWorldView12, bufferBuilder23, fluidState20);
                }
                if (blockState18.getRenderType() != BlockRenderType.a) {
                    final BlockRenderLayer blockRenderLayer21 = block19.getRenderLayer();
                    final int integer7 = blockRenderLayer21.ordinal();
                    final BufferBuilder bufferBuilder23 = chunkRenderTask.getBufferBuilders().get(integer7);
                    if (!chunkRenderData5.isBufferInitialized(blockRenderLayer21)) {
                        chunkRenderData5.markBufferInitialized(blockRenderLayer21);
                        this.beginBufferBuilding(bufferBuilder23, blockPos7);
                    }
                    final boolean[] array2 = arr13;
                    final int n2 = integer7;
                    array2[n2] |= blockRenderManager15.tesselateBlock(blockState18, blockPos9, safeWorldView12, bufferBuilder23, random14);
                }
            }
            for (final BlockRenderLayer blockRenderLayer22 : BlockRenderLayer.values()) {
                if (arr13[blockRenderLayer22.ordinal()]) {
                    chunkRenderData5.a(blockRenderLayer22);
                }
                if (chunkRenderData5.isBufferInitialized(blockRenderLayer22)) {
                    this.endBufferBuilding(blockRenderLayer22, float1, float2, float3, chunkRenderTask.getBufferBuilders().get(blockRenderLayer22), chunkRenderData5);
                }
            }
            BlockModelRenderer.disableBrightnessCache();
        }
        chunkRenderData5.a(chunkOcclusionGraphBuilder10.build());
        this.chunkRenderLock.lock();
        try {
            final Set<BlockEntity> set12 = Sets.newHashSet(set11);
            final Set<BlockEntity> set13 = Sets.newHashSet(this.blockEntities);
            set12.removeAll(this.blockEntities);
            set13.removeAll(set11);
            this.blockEntities.clear();
            this.blockEntities.addAll(set11);
            this.renderer.updateBlockEntities(set13, set12);
        }
        finally {
            this.chunkRenderLock.unlock();
        }
    }
    
    protected void cancel() {
        this.chunkRenderLock.lock();
        try {
            if (this.chunkRenderDataTask != null && this.chunkRenderDataTask.getStage() != ChunkRenderTask.Stage.d) {
                this.chunkRenderDataTask.cancel();
                this.chunkRenderDataTask = null;
            }
        }
        finally {
            this.chunkRenderLock.unlock();
        }
    }
    
    public ReentrantLock getChunkRenderLock() {
        return this.chunkRenderLock;
    }
    
    public ChunkRenderTask e() {
        this.chunkRenderLock.lock();
        try {
            this.cancel();
            final BlockPos blockPos1 = this.origin.toImmutable();
            final int integer2 = 1;
            final SafeWorldView safeWorldView3 = SafeWorldView.create(this.world, blockPos1.add(-1, -1, -1), blockPos1.add(16, 16, 16), 1);
            return this.chunkRenderDataTask = new ChunkRenderTask(this, ChunkRenderTask.Mode.a, this.getDistanceToPlayerSquared(), safeWorldView3);
        }
        finally {
            this.chunkRenderLock.unlock();
        }
    }
    
    @Nullable
    public ChunkRenderTask getResortTransparencyTask() {
        this.chunkRenderLock.lock();
        try {
            if (this.chunkRenderDataTask != null && this.chunkRenderDataTask.getStage() == ChunkRenderTask.Stage.a) {
                return null;
            }
            if (this.chunkRenderDataTask != null && this.chunkRenderDataTask.getStage() != ChunkRenderTask.Stage.d) {
                this.chunkRenderDataTask.cancel();
                this.chunkRenderDataTask = null;
            }
            (this.chunkRenderDataTask = new ChunkRenderTask(this, ChunkRenderTask.Mode.b, this.getDistanceToPlayerSquared(), null)).setRenderData(this.chunkRenderData);
            return this.chunkRenderDataTask;
        }
        finally {
            this.chunkRenderLock.unlock();
        }
    }
    
    protected double getDistanceToPlayerSquared() {
        final Camera camera1 = MinecraftClient.getInstance().gameRenderer.getCamera();
        final double double2 = this.boundingBox.minX + 8.0 - camera1.getPos().x;
        final double double3 = this.boundingBox.minY + 8.0 - camera1.getPos().y;
        final double double4 = this.boundingBox.minZ + 8.0 - camera1.getPos().z;
        return double2 * double2 + double3 * double3 + double4 * double4;
    }
    
    private void beginBufferBuilding(final BufferBuilder bufferBuilder, final BlockPos blockPos) {
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_UV_LMAP);
        bufferBuilder.setOffset(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());
    }
    
    private void endBufferBuilding(final BlockRenderLayer blockRenderLayer, final float float2, final float float3, final float float4, final BufferBuilder bufferBuilder, final ChunkRenderData chunkRenderData) {
        if (blockRenderLayer == BlockRenderLayer.TRANSLUCENT && !chunkRenderData.b(blockRenderLayer)) {
            bufferBuilder.sortQuads(float2, float3, float4);
            chunkRenderData.setBufferState(bufferBuilder.toBufferState());
        }
        bufferBuilder.end();
    }
    
    public ChunkRenderData getChunkRenderData() {
        return this.chunkRenderData;
    }
    
    public void setChunkRenderData(final ChunkRenderData chunkRenderData) {
        this.chunkRenderDataLock.lock();
        try {
            this.chunkRenderData = chunkRenderData;
        }
        finally {
            this.chunkRenderDataLock.unlock();
        }
    }
    
    public void clear() {
        this.cancel();
        this.chunkRenderData = ChunkRenderData.EMPTY;
        this.renderScheduled = true;
    }
    
    public void delete() {
        this.clear();
        this.world = null;
        for (int integer1 = 0; integer1 < BlockRenderLayer.values().length; ++integer1) {
            if (this.buffers[integer1] != null) {
                this.buffers[integer1].delete();
            }
        }
    }
    
    public BlockPos getOrigin() {
        return this.origin;
    }
    
    public void scheduleRender(boolean boolean1) {
        if (this.renderScheduled) {
            boolean1 |= this.o;
        }
        this.renderScheduled = true;
        this.o = boolean1;
    }
    
    public void l() {
        this.renderScheduled = false;
        this.o = false;
    }
    
    public boolean m() {
        return this.renderScheduled;
    }
    
    public boolean n() {
        return this.renderScheduled && this.o;
    }
    
    public BlockPos a(final Direction direction) {
        return this.n[direction.ordinal()];
    }
    
    public World getWorld() {
        return this.world;
    }
}
