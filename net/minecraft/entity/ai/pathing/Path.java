package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Lists;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import javax.annotation.Nullable;
import java.util.List;

public class Path
{
    private final List<PathNode> nodes;
    private PathNode[] b;
    private PathNode[] c;
    private PathNode d;
    private int currentNodeIndex;
    
    public Path(final List<PathNode> list) {
        this.b = new PathNode[0];
        this.c = new PathNode[0];
        this.nodes = list;
    }
    
    public void next() {
        ++this.currentNodeIndex;
    }
    
    public boolean isFinished() {
        return this.currentNodeIndex >= this.nodes.size();
    }
    
    @Nullable
    public PathNode getEnd() {
        if (!this.nodes.isEmpty()) {
            return this.nodes.get(this.nodes.size() - 1);
        }
        return null;
    }
    
    public PathNode getNode(final int index) {
        return this.nodes.get(index);
    }
    
    public List<PathNode> getNodes() {
        return this.nodes;
    }
    
    public void setLength(final int integer) {
        if (this.nodes.size() > integer) {
            this.nodes.subList(integer, this.nodes.size()).clear();
        }
    }
    
    public void setNode(final int index, final PathNode node) {
        this.nodes.set(index, node);
    }
    
    public int getLength() {
        return this.nodes.size();
    }
    
    public int getCurrentNodeIndex() {
        return this.currentNodeIndex;
    }
    
    public void setCurrentNodeIndex(final int index) {
        this.currentNodeIndex = index;
    }
    
    public Vec3d getNodePosition(final Entity entity, final int index) {
        final PathNode pathNode3 = this.nodes.get(index);
        final double double4 = pathNode3.x + (int)(entity.getWidth() + 1.0f) * 0.5;
        final double double5 = pathNode3.y;
        final double double6 = pathNode3.z + (int)(entity.getWidth() + 1.0f) * 0.5;
        return new Vec3d(double4, double5, double6);
    }
    
    public Vec3d getNodePosition(final Entity entity) {
        return this.getNodePosition(entity, this.currentNodeIndex);
    }
    
    public Vec3d getCurrentPosition() {
        final PathNode pathNode1 = this.nodes.get(this.currentNodeIndex);
        return new Vec3d(pathNode1.x, pathNode1.y, pathNode1.z);
    }
    
    public boolean equalsPath(@Nullable final Path path) {
        if (path == null) {
            return false;
        }
        if (path.nodes.size() != this.nodes.size()) {
            return false;
        }
        for (int integer2 = 0; integer2 < this.nodes.size(); ++integer2) {
            final PathNode pathNode3 = this.nodes.get(integer2);
            final PathNode pathNode4 = path.nodes.get(integer2);
            if (pathNode3.x != pathNode4.x || pathNode3.y != pathNode4.y || pathNode3.z != pathNode4.z) {
                return false;
            }
        }
        return true;
    }
    
    public boolean h() {
        final PathNode pathNode1 = this.getEnd();
        return pathNode1 != null && this.a(pathNode1.getPos());
    }
    
    public boolean a(final Vec3d vec3d) {
        final PathNode pathNode2 = this.getEnd();
        return pathNode2 != null && pathNode2.x == (int)vec3d.x && pathNode2.y == (int)vec3d.y && pathNode2.z == (int)vec3d.z;
    }
    
    @Environment(EnvType.CLIENT)
    public PathNode[] i() {
        return this.b;
    }
    
    @Environment(EnvType.CLIENT)
    public PathNode[] j() {
        return this.c;
    }
    
    @Nullable
    public PathNode k() {
        return this.d;
    }
    
    @Environment(EnvType.CLIENT)
    public static Path fromBuffer(final PacketByteBuf buffer) {
        final int integer2 = buffer.readInt();
        final PathNode pathNode3 = PathNode.fromBuffer(buffer);
        final List<PathNode> list4 = Lists.newArrayList();
        for (int integer3 = buffer.readInt(), integer4 = 0; integer4 < integer3; ++integer4) {
            list4.add(PathNode.fromBuffer(buffer));
        }
        final PathNode[] arr6 = new PathNode[buffer.readInt()];
        for (int integer5 = 0; integer5 < arr6.length; ++integer5) {
            arr6[integer5] = PathNode.fromBuffer(buffer);
        }
        final PathNode[] arr7 = new PathNode[buffer.readInt()];
        for (int integer6 = 0; integer6 < arr7.length; ++integer6) {
            arr7[integer6] = PathNode.fromBuffer(buffer);
        }
        final Path path8 = new Path(list4);
        path8.b = arr6;
        path8.c = arr7;
        path8.d = pathNode3;
        path8.currentNodeIndex = integer2;
        return path8;
    }
    
    @Override
    public String toString() {
        return "Path(length=" + this.nodes.size() + ")";
    }
}
