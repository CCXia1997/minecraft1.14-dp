package net.minecraft.world.level.storage;

public class AlphaChunkDataArray
{
    public final byte[] data;
    private final int zOffset;
    private final int xOffset;
    
    public AlphaChunkDataArray(final byte[] data, final int yCoordinateBits) {
        this.data = data;
        this.zOffset = yCoordinateBits;
        this.xOffset = yCoordinateBits + 4;
    }
    
    public int get(final int x, final int y, final int z) {
        final int integer4 = x << this.xOffset | z << this.zOffset | y;
        final int integer5 = integer4 >> 1;
        final int integer6 = integer4 & 0x1;
        if (integer6 == 0) {
            return this.data[integer5] & 0xF;
        }
        return this.data[integer5] >> 4 & 0xF;
    }
}
