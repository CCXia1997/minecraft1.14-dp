package net.minecraft.util;

public class CuboidBlockIterator
{
    private final int startX;
    private final int startY;
    private final int startZ;
    private final int endX;
    private final int endY;
    private final int endZ;
    private int x;
    private int y;
    private int z;
    private boolean complete;
    
    public CuboidBlockIterator(final int startX, final int startY, final int startZ, final int endX, final int endY, final int endZ) {
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.endX = endX;
        this.endY = endY;
        this.endZ = endZ;
    }
    
    public boolean step() {
        if (!this.complete) {
            this.x = this.startX;
            this.y = this.startY;
            this.z = this.startZ;
            return this.complete = true;
        }
        if (this.x == this.endX && this.y == this.endY && this.z == this.endZ) {
            return false;
        }
        if (this.x < this.endX) {
            ++this.x;
        }
        else if (this.y < this.endY) {
            this.x = this.startX;
            ++this.y;
        }
        else if (this.z < this.endZ) {
            this.x = this.startX;
            this.y = this.startY;
            ++this.z;
        }
        return true;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getZ() {
        return this.z;
    }
}
