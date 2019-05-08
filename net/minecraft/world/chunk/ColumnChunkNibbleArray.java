package net.minecraft.world.chunk;

public class ColumnChunkNibbleArray extends ChunkNibbleArray
{
    public ColumnChunkNibbleArray() {
        super(128);
    }
    
    public ColumnChunkNibbleArray(final ChunkNibbleArray chunkNibbleArray, final int integer) {
        super(128);
        System.arraycopy(chunkNibbleArray.asByteArray(), integer * 128, this.byteArray, 0, 128);
    }
    
    @Override
    protected int getIndex(final int x, final int y, final int z) {
        return z << 4 | x;
    }
    
    @Override
    public byte[] asByteArray() {
        final byte[] arr1 = new byte[2048];
        for (int integer2 = 0; integer2 < 16; ++integer2) {
            System.arraycopy(this.byteArray, 0, arr1, integer2 * 128, 128);
        }
        return arr1;
    }
}
