package net.minecraft.entity.ai.pathing;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;

public class PathNode
{
    public final int x;
    public final int y;
    public final int z;
    private final int hashCode;
    public int heapIndex;
    public float e;
    public float f;
    public float heapWeight;
    public PathNode h;
    public boolean i;
    public float j;
    public float k;
    public float l;
    public PathNodeType type;
    
    public PathNode(final int x, final int y, final int z) {
        this.heapIndex = -1;
        this.type = PathNodeType.a;
        this.x = x;
        this.y = y;
        this.z = z;
        this.hashCode = calculateHashCode(x, y, z);
    }
    
    public PathNode copyWithNewPosition(final int x, final int y, final int z) {
        final PathNode pathNode4 = new PathNode(x, y, z);
        pathNode4.heapIndex = this.heapIndex;
        pathNode4.e = this.e;
        pathNode4.f = this.f;
        pathNode4.heapWeight = this.heapWeight;
        pathNode4.h = this.h;
        pathNode4.i = this.i;
        pathNode4.j = this.j;
        pathNode4.k = this.k;
        pathNode4.l = this.l;
        pathNode4.type = this.type;
        return pathNode4;
    }
    
    public static int calculateHashCode(final int x, final int y, final int z) {
        return (y & 0xFF) | (x & 0x7FFF) << 8 | (z & 0x7FFF) << 24 | ((x < 0) ? Integer.MIN_VALUE : 0) | ((z < 0) ? 32768 : 0);
    }
    
    public float distance(final PathNode pathNode) {
        final float float2 = (float)(pathNode.x - this.x);
        final float float3 = (float)(pathNode.y - this.y);
        final float float4 = (float)(pathNode.z - this.z);
        return MathHelper.sqrt(float2 * float2 + float3 * float3 + float4 * float4);
    }
    
    public float distanceSquared(final PathNode pathNode) {
        final float float2 = (float)(pathNode.x - this.x);
        final float float3 = (float)(pathNode.y - this.y);
        final float float4 = (float)(pathNode.z - this.z);
        return float2 * float2 + float3 * float3 + float4 * float4;
    }
    
    public float manhattanDistance(final PathNode pathNode) {
        final float float2 = (float)Math.abs(pathNode.x - this.x);
        final float float3 = (float)Math.abs(pathNode.y - this.y);
        final float float4 = (float)Math.abs(pathNode.z - this.z);
        return float2 + float3 + float4;
    }
    
    public Vec3d getPos() {
        return new Vec3d(this.x, this.y, this.z);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof PathNode) {
            final PathNode pathNode2 = (PathNode)o;
            return this.hashCode == pathNode2.hashCode && this.x == pathNode2.x && this.y == pathNode2.y && this.z == pathNode2.z;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
    public boolean isInHeap() {
        return this.heapIndex >= 0;
    }
    
    @Override
    public String toString() {
        return this.x + ", " + this.y + ", " + this.z;
    }
    
    @Environment(EnvType.CLIENT)
    public static PathNode fromBuffer(final PacketByteBuf buffer) {
        final PathNode pathNode2 = new PathNode(buffer.readInt(), buffer.readInt(), buffer.readInt());
        pathNode2.j = buffer.readFloat();
        pathNode2.k = buffer.readFloat();
        pathNode2.l = buffer.readFloat();
        pathNode2.i = buffer.readBoolean();
        pathNode2.type = PathNodeType.values()[buffer.readInt()];
        pathNode2.heapWeight = buffer.readFloat();
        return pathNode2;
    }
}
