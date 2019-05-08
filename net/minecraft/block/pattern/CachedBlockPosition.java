package net.minecraft.block.pattern;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;

public class CachedBlockPosition
{
    private final ViewableWorld world;
    private final BlockPos pos;
    private final boolean forceLoad;
    private BlockState state;
    private BlockEntity blockEntity;
    private boolean cachedEntity;
    
    public CachedBlockPosition(final ViewableWorld viewableWorld, final BlockPos pos, final boolean boolean3) {
        this.world = viewableWorld;
        this.pos = pos.toImmutable();
        this.forceLoad = boolean3;
    }
    
    public BlockState getBlockState() {
        if (this.state == null && (this.forceLoad || this.world.isBlockLoaded(this.pos))) {
            this.state = this.world.getBlockState(this.pos);
        }
        return this.state;
    }
    
    @Nullable
    public BlockEntity getBlockEntity() {
        if (this.blockEntity == null && !this.cachedEntity) {
            this.blockEntity = this.world.getBlockEntity(this.pos);
            this.cachedEntity = true;
        }
        return this.blockEntity;
    }
    
    public ViewableWorld getWorld() {
        return this.world;
    }
    
    public BlockPos getBlockPos() {
        return this.pos;
    }
    
    public static Predicate<CachedBlockPosition> matchesBlockState(final Predicate<BlockState> predicate) {
        return cachedBlockPosition -> cachedBlockPosition != null && predicate.test(cachedBlockPosition.getBlockState());
    }
}
