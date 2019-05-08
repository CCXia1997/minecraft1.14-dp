package net.minecraft.predicate.block;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import java.util.function.Predicate;

public class BlockPredicate implements Predicate<BlockState>
{
    private final Block block;
    
    public BlockPredicate(final Block block) {
        this.block = block;
    }
    
    public static BlockPredicate make(final Block block) {
        return new BlockPredicate(block);
    }
    
    public boolean a(@Nullable final BlockState context) {
        return context != null && context.getBlock() == this.block;
    }
}
