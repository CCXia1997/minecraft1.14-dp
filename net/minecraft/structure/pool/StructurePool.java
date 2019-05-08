package net.minecraft.structure.pool;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Arrays;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.world.Heightmap;
import net.minecraft.structure.processor.StructureProcessor;
import java.util.Map;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Random;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.structure.StructureManager;
import java.util.Iterator;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import com.mojang.datafixers.util.Pair;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Identifier;

public class StructurePool
{
    public static final StructurePool EMPTY;
    public static final StructurePool INVALID;
    private final Identifier id;
    private final ImmutableList<Pair<StructurePoolElement, Integer>> elementCounts;
    private final List<StructurePoolElement> elements;
    private final Identifier terminatorsId;
    private final Projection projection;
    private int h;
    
    public StructurePool(final Identifier id, final Identifier terminatorsId, final List<Pair<StructurePoolElement, Integer>> elementCounts, final Projection projection) {
        this.h = Integer.MIN_VALUE;
        this.id = id;
        this.elementCounts = ImmutableList.<Pair<StructurePoolElement, Integer>>copyOf(elementCounts);
        this.elements = Lists.newArrayList();
        for (final Pair<StructurePoolElement, Integer> pair6 : elementCounts) {
            for (Integer integer7 = 0; integer7 < (int)pair6.getSecond(); ++integer7) {
                this.elements.add(((StructurePoolElement)pair6.getFirst()).setProjection(projection));
            }
        }
        this.terminatorsId = terminatorsId;
        this.projection = projection;
    }
    
    public int a(final StructureManager structureManager) {
        if (this.h == Integer.MIN_VALUE) {
            this.h = this.elements.stream().mapToInt(structurePoolElement -> structurePoolElement.getBoundingBox(structureManager, BlockPos.ORIGIN, BlockRotation.ROT_0).getBlockCountY()).max().orElse(0);
        }
        return this.h;
    }
    
    public Identifier getTerminatorsId() {
        return this.terminatorsId;
    }
    
    public StructurePoolElement getRandomElement(final Random random) {
        return this.elements.get(random.nextInt(this.elements.size()));
    }
    
    public List<StructurePoolElement> getElementIndicesInRandomOrder(final Random random) {
        return ImmutableList.copyOf(ObjectArrays.shuffle((Object[])this.elements.<StructurePoolElement>toArray(new StructurePoolElement[0]), random));
    }
    
    public Identifier getId() {
        return this.id;
    }
    
    public int getElementCount() {
        return this.elements.size();
    }
    
    static {
        EMPTY = new StructurePool(new Identifier("empty"), new Identifier("empty"), ImmutableList.of(), Projection.RIGID);
        INVALID = new StructurePool(new Identifier("invalid"), new Identifier("invalid"), ImmutableList.of(), Projection.RIGID);
    }
    
    public enum Projection
    {
        TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new GravityStructureProcessor(Heightmap.Type.a, -1))), 
        RIGID("rigid", ImmutableList.<StructureProcessor>of());
        
        private static final Map<String, Projection> PROJECTIONS_BY_ID;
        private final String id;
        private final ImmutableList<StructureProcessor> processors;
        
        private Projection(final String string1, final ImmutableList<StructureProcessor> immutableList) {
            this.id = string1;
            this.processors = immutableList;
        }
        
        public String getId() {
            return this.id;
        }
        
        public static Projection getById(final String id) {
            return Projection.PROJECTIONS_BY_ID.get(id);
        }
        
        public ImmutableList<StructureProcessor> getProcessors() {
            return this.processors;
        }
        
        static {
            PROJECTIONS_BY_ID = Arrays.<Projection>stream(values()).collect(Collectors.toMap(Projection::getId, projection -> projection));
        }
    }
}
