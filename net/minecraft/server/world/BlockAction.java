package net.minecraft.server.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class BlockAction
{
    private final BlockPos pos;
    private final Block block;
    private final int type;
    private final int data;
    
    public BlockAction(final BlockPos pos, final Block block, final int type, final int data) {
        this.pos = pos;
        this.block = block;
        this.type = type;
        this.data = data;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public Block getBlock() {
        return this.block;
    }
    
    public int getType() {
        return this.type;
    }
    
    public int getData() {
        return this.data;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof BlockAction) {
            final BlockAction blockAction2 = (BlockAction)o;
            return this.pos.equals(blockAction2.pos) && this.type == blockAction2.type && this.data == blockAction2.data && this.block == blockAction2.block;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "TE(" + this.pos + ")," + this.type + "," + this.data + "," + this.block;
    }
}
