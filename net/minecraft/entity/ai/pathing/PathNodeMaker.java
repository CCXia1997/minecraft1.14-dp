package net.minecraft.entity.ai.pathing;

import net.minecraft.util.math.MathHelper;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.BlockView;

public abstract class PathNodeMaker
{
    protected BlockView blockView;
    protected MobEntity entity;
    protected final Int2ObjectMap<PathNode> pathNodeCache;
    protected int d;
    protected int e;
    protected int f;
    protected boolean entersOpenDoors;
    protected boolean pathsThroughDoors;
    protected boolean swims;
    
    public PathNodeMaker() {
        this.pathNodeCache = (Int2ObjectMap<PathNode>)new Int2ObjectOpenHashMap();
    }
    
    public void init(final BlockView blockView, final MobEntity mobEntity) {
        this.blockView = blockView;
        this.entity = mobEntity;
        this.pathNodeCache.clear();
        this.d = MathHelper.floor(mobEntity.getWidth() + 1.0f);
        this.e = MathHelper.floor(mobEntity.getHeight() + 1.0f);
        this.f = MathHelper.floor(mobEntity.getWidth() + 1.0f);
    }
    
    public void clear() {
        this.blockView = null;
        this.entity = null;
    }
    
    protected PathNode getPathNode(final int x, final int y, final int integer3) {
        return (PathNode)this.pathNodeCache.computeIfAbsent(PathNode.calculateHashCode(x, y, integer3), integer4 -> new PathNode(x, y, integer3));
    }
    
    public abstract PathNode getStart();
    
    public abstract PathNode getPathNode(final double arg1, final double arg2, final double arg3);
    
    public abstract int getPathNodes(final PathNode[] arg1, final PathNode arg2, final PathNode arg3, final float arg4);
    
    public abstract PathNodeType getPathNodeType(final BlockView arg1, final int arg2, final int arg3, final int arg4, final MobEntity arg5, final int arg6, final int arg7, final int arg8, final boolean arg9, final boolean arg10);
    
    public abstract PathNodeType getPathNodeType(final BlockView arg1, final int arg2, final int arg3, final int arg4);
    
    public void setCanEnterOpenDoors(final boolean boolean1) {
        this.entersOpenDoors = boolean1;
    }
    
    public void setCanPathThroughDoors(final boolean boolean1) {
        this.pathsThroughDoors = boolean1;
    }
    
    public void setCanSwim(final boolean boolean1) {
        this.swims = boolean1;
    }
    
    public boolean canEnterOpenDoors() {
        return this.entersOpenDoors;
    }
    
    public boolean canPathThroughDoors() {
        return this.pathsThroughDoors;
    }
    
    public boolean canSwim() {
        return this.swims;
    }
}
