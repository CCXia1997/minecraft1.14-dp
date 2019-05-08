package net.minecraft.command.arguments;

import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.block.entity.BlockEntity;
import java.util.Iterator;
import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Property;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import java.util.function.Predicate;

public class BlockStateArgument implements Predicate<CachedBlockPosition>
{
    private final BlockState state;
    private final Set<Property<?>> properties;
    @Nullable
    private final CompoundTag data;
    
    public BlockStateArgument(final BlockState blockState, final Set<Property<?>> set, @Nullable final CompoundTag compoundTag) {
        this.state = blockState;
        this.properties = set;
        this.data = compoundTag;
    }
    
    public BlockState getBlockState() {
        return this.state;
    }
    
    public boolean a(final CachedBlockPosition context) {
        final BlockState blockState2 = context.getBlockState();
        if (blockState2.getBlock() != this.state.getBlock()) {
            return false;
        }
        for (final Property<?> property4 : this.properties) {
            if (blockState2.get(property4) != this.state.get(property4)) {
                return false;
            }
        }
        if (this.data != null) {
            final BlockEntity blockEntity3 = context.getBlockEntity();
            return blockEntity3 != null && TagHelper.areTagsEqual(this.data, blockEntity3.toTag(new CompoundTag()), true);
        }
        return true;
    }
    
    public boolean setBlockState(final ServerWorld serverWorld, final BlockPos blockPos, final int integer) {
        if (!serverWorld.setBlockState(blockPos, this.state, integer)) {
            return false;
        }
        if (this.data != null) {
            final BlockEntity blockEntity4 = serverWorld.getBlockEntity(blockPos);
            if (blockEntity4 != null) {
                final CompoundTag compoundTag5 = this.data.copy();
                compoundTag5.putInt("x", blockPos.getX());
                compoundTag5.putInt("y", blockPos.getY());
                compoundTag5.putInt("z", blockPos.getZ());
                blockEntity4.fromTag(compoundTag5);
            }
        }
        return true;
    }
}
