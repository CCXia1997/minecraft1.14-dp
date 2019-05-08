package net.minecraft.world.chunk;

import net.minecraft.util.SystemUtil;
import java.util.Arrays;
import net.minecraft.world.biome.Biomes;
import net.minecraft.server.world.ChunkHolder;
import java.util.function.Predicate;
import java.util.List;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.FluidState;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EmptyChunk extends WorldChunk
{
    private static final Biome[] BIOMES;
    
    public EmptyChunk(final World world, final ChunkPos chunkPos) {
        super(world, chunkPos, EmptyChunk.BIOMES);
    }
    
    @Override
    public BlockState getBlockState(final BlockPos pos) {
        return Blocks.kS.getDefaultState();
    }
    
    @Nullable
    @Override
    public BlockState setBlockState(final BlockPos pos, final BlockState state, final boolean boolean3) {
        return null;
    }
    
    @Override
    public FluidState getFluidState(final BlockPos pos) {
        return Fluids.EMPTY.getDefaultState();
    }
    
    @Nullable
    @Override
    public LightingProvider getLightingProvider() {
        return null;
    }
    
    @Override
    public int getLuminance(final BlockPos pos) {
        return 0;
    }
    
    @Override
    public void addEntity(final Entity entity) {
    }
    
    @Override
    public void remove(final Entity entity) {
    }
    
    @Override
    public void remove(final Entity entity, final int integer) {
    }
    
    @Nullable
    @Override
    public BlockEntity getBlockEntity(final BlockPos pos, final CreationType creationType) {
        return null;
    }
    
    @Override
    public void addBlockEntity(final BlockEntity blockEntity) {
    }
    
    @Override
    public void setBlockEntity(final BlockPos pos, final BlockEntity blockEntity) {
    }
    
    @Override
    public void removeBlockEntity(final BlockPos blockPos) {
    }
    
    @Override
    public void markDirty() {
    }
    
    @Override
    public void appendEntities(@Nullable final Entity except, final BoundingBox box, final List<Entity> entityList, final Predicate<? super Entity> predicate) {
    }
    
    @Override
    public <T extends Entity> void appendEntities(final Class<? extends T> entityClass, final BoundingBox box, final List<T> entityList, final Predicate<? super T> predicate) {
    }
    
    @Override
    public boolean isEmpty() {
        return true;
    }
    
    @Override
    public boolean a(final int integer1, final int integer2) {
        return true;
    }
    
    @Override
    public ChunkHolder.LevelType getLevelType() {
        return ChunkHolder.LevelType.BORDER;
    }
    
    static {
        BIOMES = SystemUtil.<Biome[]>consume(new Biome[256], arr -> Arrays.fill(arr, Biomes.c));
    }
}
