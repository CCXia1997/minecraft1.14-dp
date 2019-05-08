package net.minecraft.client.render;

import net.minecraft.util.math.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PartiallyBrokenBlockEntry
{
    private final int breakingEntityId;
    private final BlockPos pos;
    private int stage;
    private int lastUpdateTicks;
    
    public PartiallyBrokenBlockEntry(final int integer, final BlockPos blockPos) {
        this.breakingEntityId = integer;
        this.pos = blockPos;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public void setStage(int value) {
        if (value > 10) {
            value = 10;
        }
        this.stage = value;
    }
    
    public int getStage() {
        return this.stage;
    }
    
    public void setLastUpdateTicks(final int value) {
        this.lastUpdateTicks = value;
    }
    
    public int getLastUpdateTicks() {
        return this.lastUpdateTicks;
    }
}
