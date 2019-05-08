package net.minecraft.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;

public class EndPortalBlockEntity extends BlockEntity
{
    public EndPortalBlockEntity(final BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }
    
    public EndPortalBlockEntity() {
        this(BlockEntityType.END_PORTAL);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean shouldDrawSide(final Direction direction) {
        return direction == Direction.UP;
    }
}
