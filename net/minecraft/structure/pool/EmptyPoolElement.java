package net.minecraft.structure.pool;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.MutableIntBoundingBox;
import java.util.Collections;
import net.minecraft.structure.Structure;
import java.util.List;
import java.util.Random;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.structure.StructureManager;

public class EmptyPoolElement extends StructurePoolElement
{
    public static final EmptyPoolElement INSTANCE;
    
    private EmptyPoolElement() {
        super(StructurePool.Projection.TERRAIN_MATCHING);
    }
    
    @Override
    public List<Structure.StructureBlockInfo> getStructureBlockInfos(final StructureManager structureManager, final BlockPos pos, final BlockRotation rotation, final Random random) {
        return Collections.<Structure.StructureBlockInfo>emptyList();
    }
    
    @Override
    public MutableIntBoundingBox getBoundingBox(final StructureManager structureManager, final BlockPos pos, final BlockRotation rotation) {
        return MutableIntBoundingBox.empty();
    }
    
    @Override
    public boolean generate(final StructureManager structureManager, final IWorld world, final BlockPos pos, final BlockRotation rotation, final MutableIntBoundingBox boundingBox, final Random random) {
        return true;
    }
    
    @Override
    public StructurePoolElementType getType() {
        return StructurePoolElementType.EMPTY_POOL_ELEMENT;
    }
    
    public <T> Dynamic<T> a(final DynamicOps<T> dynamicOps) {
        return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.emptyMap());
    }
    
    @Override
    public String toString() {
        return "Empty";
    }
    
    static {
        INSTANCE = new EmptyPoolElement();
    }
}
