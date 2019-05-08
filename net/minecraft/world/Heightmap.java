package net.minecraft.world;

import net.minecraft.util.SystemUtil;
import com.google.common.collect.Maps;
import net.minecraft.block.LeavesBlock;
import java.util.HashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Map;
import java.util.Iterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Set;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.util.PackedIntegerArray;
import net.minecraft.block.BlockState;
import java.util.function.Predicate;

public class Heightmap
{
    private static final Predicate<BlockState> ALWAYS_TRUE;
    private static final Predicate<BlockState> SUFFOCATES;
    private final PackedIntegerArray storage;
    private final Predicate<BlockState> blockPredicate;
    private final Chunk chunk;
    
    public Heightmap(final Chunk chunk, final Type type) {
        this.storage = new PackedIntegerArray(9, 256);
        this.blockPredicate = type.getBlockPredicate();
        this.chunk = chunk;
    }
    
    public static void populateHeightmaps(final Chunk chunk, final Set<Type> types) {
        final int integer3 = types.size();
        final ObjectList<Heightmap> objectList4 = (ObjectList<Heightmap>)new ObjectArrayList(integer3);
        final ObjectListIterator<Heightmap> objectListIterator5 = (ObjectListIterator<Heightmap>)objectList4.iterator();
        final int integer4 = chunk.getHighestNonEmptySectionYOffset() + 16;
        try (final BlockPos.PooledMutable pooledMutable7 = BlockPos.PooledMutable.get()) {
            for (int integer5 = 0; integer5 < 16; ++integer5) {
                for (int integer6 = 0; integer6 < 16; ++integer6) {
                    for (final Type type12 : types) {
                        objectList4.add(chunk.getHeightmap(type12));
                    }
                    for (int integer7 = integer4 - 1; integer7 >= 0; --integer7) {
                        pooledMutable7.set(integer5, integer7, integer6);
                        final BlockState blockState12 = chunk.getBlockState(pooledMutable7);
                        if (blockState12.getBlock() != Blocks.AIR) {
                            while (objectListIterator5.hasNext()) {
                                final Heightmap heightmap13 = (Heightmap)objectListIterator5.next();
                                if (heightmap13.blockPredicate.test(blockState12)) {
                                    heightmap13.set(integer5, integer6, integer7 + 1);
                                    objectListIterator5.remove();
                                }
                            }
                            if (objectList4.isEmpty()) {
                                break;
                            }
                            objectListIterator5.back(integer3);
                        }
                    }
                }
            }
        }
    }
    
    public boolean trackUpdate(final int x, final int y, final int z, final BlockState state) {
        final int integer5 = this.get(x, z);
        if (y <= integer5 - 2) {
            return false;
        }
        if (this.blockPredicate.test(state)) {
            if (y >= integer5) {
                this.set(x, z, y + 1);
                return true;
            }
        }
        else if (integer5 - 1 == y) {
            final BlockPos.Mutable mutable6 = new BlockPos.Mutable();
            for (int integer6 = y - 1; integer6 >= 0; --integer6) {
                mutable6.set(x, integer6, z);
                if (this.blockPredicate.test(this.chunk.getBlockState(mutable6))) {
                    this.set(x, z, integer6 + 1);
                    return true;
                }
            }
            this.set(x, z, 0);
            return true;
        }
        return false;
    }
    
    public int get(final int x, final int z) {
        return this.get(toIndex(x, z));
    }
    
    private int get(final int index) {
        return this.storage.get(index);
    }
    
    private void set(final int x, final int z, final int height) {
        this.storage.set(toIndex(x, z), height);
    }
    
    public void setTo(final long[] heightmap) {
        System.arraycopy(heightmap, 0, this.storage.getStorage(), 0, heightmap.length);
    }
    
    public long[] asLongArray() {
        return this.storage.getStorage();
    }
    
    private static int toIndex(final int x, final int z) {
        return x + z * 16;
    }
    
    static {
        ALWAYS_TRUE = (blockState -> !blockState.isAir());
        SUFFOCATES = (blockState -> blockState.getMaterial().blocksMovement());
    }
    
    public enum Purpose
    {
        a, 
        b, 
        c;
    }
    
    public enum Type
    {
        a("WORLD_SURFACE_WG", Purpose.a, (Predicate<BlockState>)Heightmap.ALWAYS_TRUE), 
        b("WORLD_SURFACE", Purpose.c, (Predicate<BlockState>)Heightmap.ALWAYS_TRUE), 
        c("OCEAN_FLOOR_WG", Purpose.a, (Predicate<BlockState>)Heightmap.SUFFOCATES), 
        d("OCEAN_FLOOR", Purpose.b, (Predicate<BlockState>)Heightmap.SUFFOCATES), 
        e("MOTION_BLOCKING", Purpose.c, blockState -> blockState.getMaterial().blocksMovement() || !blockState.getFluidState().isEmpty()), 
        f("MOTION_BLOCKING_NO_LEAVES", Purpose.b, blockState -> (blockState.getMaterial().blocksMovement() || !blockState.getFluidState().isEmpty()) && !(blockState.getBlock() instanceof LeavesBlock));
        
        private final String name;
        private final Purpose purpose;
        private final Predicate<BlockState> blockPredicate;
        private static final Map<String, Type> BY_NAME;
        
        private Type(final String name, final Purpose purpose, final Predicate<BlockState> blockPredicate) {
            this.name = name;
            this.purpose = purpose;
            this.blockPredicate = blockPredicate;
        }
        
        public String getName() {
            return this.name;
        }
        
        public boolean shouldSendToClient() {
            return this.purpose == Purpose.c;
        }
        
        @Environment(EnvType.CLIENT)
        public boolean isStoredServerSide() {
            return this.purpose != Purpose.a;
        }
        
        public static Type byName(final String name) {
            return Type.BY_NAME.get(name);
        }
        
        public Predicate<BlockState> getBlockPredicate() {
            return this.blockPredicate;
        }
        
        static {
            final Type[] array;
            int length;
            int i = 0;
            Type type5;
            BY_NAME = SystemUtil.<Map<String, Type>>consume(Maps.newHashMap(), hashMap -> {
                values();
                for (length = array.length; i < length; ++i) {
                    type5 = array[i];
                    hashMap.put(type5.name, type5);
                }
            });
        }
    }
}
