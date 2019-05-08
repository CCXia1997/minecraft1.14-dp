package net.minecraft.block.dispenser;

import net.minecraft.util.math.BlockPointer;

public abstract class FallibleItemDispenserBehavior extends ItemDispenserBehavior
{
    protected boolean success;
    
    public FallibleItemDispenserBehavior() {
        this.success = true;
    }
    
    @Override
    protected void playSound(final BlockPointer blockPointer) {
        blockPointer.getWorld().playLevelEvent(this.success ? 1000 : 1001, blockPointer.getBlockPos(), 0);
    }
}
