package net.minecraft.nbt;

public class PositionTracker
{
    public static final PositionTracker DEFAULT;
    private final long max;
    private long pos;
    
    public PositionTracker(final long long1) {
        this.max = long1;
    }
    
    public void add(final long long1) {
        this.pos += long1 / 8L;
        if (this.pos > this.max) {
            throw new RuntimeException("Tried to read NBT tag that was too big; tried to allocate: " + this.pos + "bytes where max allowed: " + this.max);
        }
    }
    
    static {
        DEFAULT = new PositionTracker(0L) {
            @Override
            public void add(final long long1) {
            }
        };
    }
}
