package net.minecraft.entity.ai.pathing;

import java.util.List;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.BlockView;
import com.google.common.collect.Sets;
import java.util.Set;

public class PathNodeNavigator
{
    private final PathMinHeap minHeap;
    private final Set<PathNode> b;
    private final PathNode[] c;
    private final int d;
    private PathNodeMaker pathNodeMaker;
    
    public PathNodeNavigator(final PathNodeMaker pathNodeMaker, final int integer) {
        this.minHeap = new PathMinHeap();
        this.b = Sets.newHashSet();
        this.c = new PathNode[32];
        this.pathNodeMaker = pathNodeMaker;
        this.d = integer;
    }
    
    @Nullable
    public Path pathfind(final BlockView world, final MobEntity entity, final double x, final double y, final double z, final float followRange) {
        this.minHeap.clear();
        this.pathNodeMaker.init(world, entity);
        final PathNode pathNode10 = this.pathNodeMaker.getStart();
        final PathNode pathNode11 = this.pathNodeMaker.getPathNode(x, y, z);
        final Path path12 = this.pathfind(pathNode10, pathNode11, followRange);
        this.pathNodeMaker.clear();
        return path12;
    }
    
    @Nullable
    private Path pathfind(final PathNode start, final PathNode end, final float followRange) {
        start.e = 0.0f;
        start.f = start.manhattanDistance(end);
        start.heapWeight = start.f;
        this.minHeap.clear();
        this.b.clear();
        this.minHeap.push(start);
        PathNode pathNode4 = start;
        int integer5 = 0;
        while (!this.minHeap.isEmpty() && ++integer5 < this.d) {
            final PathNode pathNode5 = this.minHeap.pop();
            if (pathNode5.equals(end)) {
                pathNode4 = end;
                break;
            }
            if (pathNode5.manhattanDistance(end) < pathNode4.manhattanDistance(end)) {
                pathNode4 = pathNode5;
            }
            pathNode5.i = true;
            for (int integer6 = this.pathNodeMaker.getPathNodes(this.c, pathNode5, end, followRange), integer7 = 0; integer7 < integer6; ++integer7) {
                final PathNode pathNode6 = this.c[integer7];
                final float float10 = pathNode5.manhattanDistance(pathNode6);
                pathNode6.j = pathNode5.j + float10;
                pathNode6.k = float10 + pathNode6.l;
                final float float11 = pathNode5.e + pathNode6.k;
                if (pathNode6.j < followRange && (!pathNode6.isInHeap() || float11 < pathNode6.e)) {
                    pathNode6.h = pathNode5;
                    pathNode6.e = float11;
                    pathNode6.f = pathNode6.manhattanDistance(end) + pathNode6.l;
                    if (pathNode6.isInHeap()) {
                        this.minHeap.setNodeWeight(pathNode6, pathNode6.e + pathNode6.f);
                    }
                    else {
                        pathNode6.heapWeight = pathNode6.e + pathNode6.f;
                        this.minHeap.push(pathNode6);
                    }
                }
            }
        }
        if (pathNode4 == start) {
            return null;
        }
        final Path path6 = this.a(pathNode4);
        return path6;
    }
    
    private Path a(final PathNode pathNode) {
        final List<PathNode> list2 = Lists.newArrayList();
        PathNode pathNode2 = pathNode;
        list2.add(0, pathNode2);
        while (pathNode2.h != null) {
            pathNode2 = pathNode2.h;
            list2.add(0, pathNode2);
        }
        return new Path(list2);
    }
}
