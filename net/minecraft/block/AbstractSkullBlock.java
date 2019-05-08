package net.minecraft.block;

import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class AbstractSkullBlock extends BlockWithEntity
{
    private final SkullBlock.SkullType type;
    
    public AbstractSkullBlock(final SkullBlock.SkullType skullType, final Settings settings) {
        super(settings);
        this.type = skullType;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean hasBlockEntityBreakingRender(final BlockState state) {
        return true;
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new SkullBlockEntity();
    }
    
    @Environment(EnvType.CLIENT)
    public SkullBlock.SkullType getSkullType() {
        return this.type;
    }
}
