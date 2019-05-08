package net.minecraft.block;

import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class BedrockBlock extends Block
{
    public BedrockBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public boolean allowsSpawning(final BlockState state, final BlockView blockView, final BlockPos blockPos, final EntityType<?> entityType) {
        return false;
    }
}
