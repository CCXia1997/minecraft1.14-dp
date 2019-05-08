package net.minecraft.structure;

import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class VillageStructureStart extends StructureStart
{
    public VillageStructureStart(final StructureFeature<?> structureFeature, final int chunkX, final int chunkZ, final Biome biome, final MutableIntBoundingBox mutableIntBoundingBox, final int integer6, final long long7) {
        super(structureFeature, chunkX, chunkZ, biome, mutableIntBoundingBox, integer6, long7);
    }
    
    @Override
    protected void setBoundingBoxFromChildren() {
        super.setBoundingBoxFromChildren();
        final int integer1 = 12;
        final MutableIntBoundingBox boundingBox = this.boundingBox;
        boundingBox.minX -= 12;
        final MutableIntBoundingBox boundingBox2 = this.boundingBox;
        boundingBox2.minY -= 12;
        final MutableIntBoundingBox boundingBox3 = this.boundingBox;
        boundingBox3.minZ -= 12;
        final MutableIntBoundingBox boundingBox4 = this.boundingBox;
        boundingBox4.maxX += 12;
        final MutableIntBoundingBox boundingBox5 = this.boundingBox;
        boundingBox5.maxY += 12;
        final MutableIntBoundingBox boundingBox6 = this.boundingBox;
        boundingBox6.maxZ += 12;
    }
}
