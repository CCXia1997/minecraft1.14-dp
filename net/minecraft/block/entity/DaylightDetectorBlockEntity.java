package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.util.Tickable;

public class DaylightDetectorBlockEntity extends BlockEntity implements Tickable
{
    public DaylightDetectorBlockEntity() {
        super(BlockEntityType.DAYLIGHT_DETECTOR);
    }
    
    @Override
    public void tick() {
        if (this.world != null && !this.world.isClient && this.world.getTime() % 20L == 0L) {
            final BlockState blockState1 = this.getCachedState();
            final Block block2 = blockState1.getBlock();
            if (block2 instanceof DaylightDetectorBlock) {
                DaylightDetectorBlock.updateState(blockState1, this.world, this.pos);
            }
        }
    }
}
