package net.minecraft.entity.ai.pathing;

public class PathMinHeap
{
    private PathNode[] pathNodes;
    private int count;
    
    public PathMinHeap() {
        this.pathNodes = new PathNode[128];
    }
    
    public PathNode push(final PathNode pathNode) {
        if (pathNode.heapIndex >= 0) {
            throw new IllegalStateException("OW KNOWS!");
        }
        if (this.count == this.pathNodes.length) {
            final PathNode[] arr2 = new PathNode[this.count << 1];
            System.arraycopy(this.pathNodes, 0, arr2, 0, this.count);
            this.pathNodes = arr2;
        }
        this.pathNodes[this.count] = pathNode;
        pathNode.heapIndex = this.count;
        this.shiftUp(this.count++);
        return pathNode;
    }
    
    public void clear() {
        this.count = 0;
    }
    
    public PathNode pop() {
        final PathNode pathNode1 = this.pathNodes[0];
        final PathNode[] pathNodes = this.pathNodes;
        final int n = 0;
        final PathNode[] pathNodes2 = this.pathNodes;
        final int count = this.count - 1;
        this.count = count;
        pathNodes[n] = pathNodes2[count];
        this.pathNodes[this.count] = null;
        if (this.count > 0) {
            this.shiftDown(0);
        }
        pathNode1.heapIndex = -1;
        return pathNode1;
    }
    
    public void setNodeWeight(final PathNode node, final float float2) {
        final float float3 = node.heapWeight;
        node.heapWeight = float2;
        if (float2 < float3) {
            this.shiftUp(node.heapIndex);
        }
        else {
            this.shiftDown(node.heapIndex);
        }
    }
    
    private void shiftUp(int integer) {
        final PathNode pathNode2 = this.pathNodes[integer];
        final float float3 = pathNode2.heapWeight;
        while (integer > 0) {
            final int integer2 = integer - 1 >> 1;
            final PathNode pathNode3 = this.pathNodes[integer2];
            if (float3 >= pathNode3.heapWeight) {
                break;
            }
            this.pathNodes[integer] = pathNode3;
            pathNode3.heapIndex = integer;
            integer = integer2;
        }
        this.pathNodes[integer] = pathNode2;
        pathNode2.heapIndex = integer;
    }
    
    private void shiftDown(int integer) {
        final PathNode pathNode2 = this.pathNodes[integer];
        final float float3 = pathNode2.heapWeight;
        while (true) {
            final int integer2 = 1 + (integer << 1);
            final int integer3 = integer2 + 1;
            if (integer2 >= this.count) {
                break;
            }
            final PathNode pathNode3 = this.pathNodes[integer2];
            final float float4 = pathNode3.heapWeight;
            PathNode pathNode4;
            float float5;
            if (integer3 >= this.count) {
                pathNode4 = null;
                float5 = Float.POSITIVE_INFINITY;
            }
            else {
                pathNode4 = this.pathNodes[integer3];
                float5 = pathNode4.heapWeight;
            }
            if (float4 < float5) {
                if (float4 >= float3) {
                    break;
                }
                this.pathNodes[integer] = pathNode3;
                pathNode3.heapIndex = integer;
                integer = integer2;
            }
            else {
                if (float5 >= float3) {
                    break;
                }
                this.pathNodes[integer] = pathNode4;
                pathNode4.heapIndex = integer;
                integer = integer3;
            }
        }
        this.pathNodes[integer] = pathNode2;
        pathNode2.heapIndex = integer;
    }
    
    public boolean isEmpty() {
        return this.count == 0;
    }
}
