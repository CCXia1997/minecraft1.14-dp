package net.minecraft.world.gen.feature;

import net.minecraft.state.AbstractPropertyContainer;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.math.BoundingBox;
import com.google.common.cache.CacheLoader;
import java.util.concurrent.TimeUnit;
import com.google.common.cache.CacheBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.state.property.Property;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3i;
import java.util.Iterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import java.util.Random;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import java.util.List;
import com.google.common.cache.LoadingCache;

public class EndSpikeFeature extends Feature<EndSpikeFeatureConfig>
{
    private static final LoadingCache<Long, List<Spike>> CACHE;
    
    public EndSpikeFeature(final Function<Dynamic<?>, ? extends EndSpikeFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    public static List<Spike> getSpikes(final IWorld iWorld) {
        final Random random2 = new Random(iWorld.getSeed());
        final long long3 = random2.nextLong() & 0xFFFFL;
        return EndSpikeFeature.CACHE.getUnchecked(long3);
    }
    
    @Override
    public boolean generate(final IWorld world, final ChunkGenerator<? extends ChunkGeneratorConfig> generator, final Random random, final BlockPos pos, final EndSpikeFeatureConfig config) {
        List<Spike> list6 = config.getSpikes();
        if (list6.isEmpty()) {
            list6 = getSpikes(world);
        }
        for (final Spike spike8 : list6) {
            if (spike8.isInChunk(pos)) {
                this.generateSpike(world, random, config, spike8);
            }
        }
        return true;
    }
    
    private void generateSpike(final IWorld world, final Random random, final EndSpikeFeatureConfig config, final Spike spike) {
        final int integer5 = spike.getRadius();
        for (final BlockPos blockPos7 : BlockPos.iterate(new BlockPos(spike.getCenterX() - integer5, 0, spike.getCenterZ() - integer5), new BlockPos(spike.getCenterX() + integer5, spike.getHeight() + 10, spike.getCenterZ() + integer5))) {
            if (blockPos7.isWithinDistance(new BlockPos(spike.getCenterX(), blockPos7.getY(), spike.getCenterZ()), integer5) && blockPos7.getY() < spike.getHeight()) {
                this.setBlockState(world, blockPos7, Blocks.bJ.getDefaultState());
            }
            else {
                if (blockPos7.getY() <= 65) {
                    continue;
                }
                this.setBlockState(world, blockPos7, Blocks.AIR.getDefaultState());
            }
        }
        if (spike.isGuarded()) {
            final int integer6 = -2;
            final int integer7 = 2;
            final int integer8 = 3;
            final BlockPos.Mutable mutable9 = new BlockPos.Mutable();
            for (int integer9 = -2; integer9 <= 2; ++integer9) {
                for (int integer10 = -2; integer10 <= 2; ++integer10) {
                    for (int integer11 = 0; integer11 <= 3; ++integer11) {
                        final boolean boolean13 = MathHelper.abs(integer9) == 2;
                        final boolean boolean14 = MathHelper.abs(integer10) == 2;
                        final boolean boolean15 = integer11 == 3;
                        if (boolean13 || boolean14 || boolean15) {
                            final boolean boolean16 = integer9 == -2 || integer9 == 2 || boolean15;
                            final boolean boolean17 = integer10 == -2 || integer10 == 2 || boolean15;
                            final BlockState blockState18 = (((((AbstractPropertyContainer<O, BlockState>)Blocks.dA.getDefaultState()).with((Property<Comparable>)PaneBlock.NORTH, boolean16 && integer10 != -2)).with((Property<Comparable>)PaneBlock.SOUTH, boolean16 && integer10 != 2)).with((Property<Comparable>)PaneBlock.WEST, boolean17 && integer9 != -2)).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.EAST, boolean17 && integer9 != 2);
                            this.setBlockState(world, mutable9.set(spike.getCenterX() + integer9, spike.getHeight() + integer11, spike.getCenterZ() + integer10), blockState18);
                        }
                    }
                }
            }
        }
        final EnderCrystalEntity enderCrystalEntity6 = EntityType.END_CRYSTAL.create(world.getWorld());
        enderCrystalEntity6.setBeamTarget(config.getPos());
        enderCrystalEntity6.setInvulnerable(config.isCrystalInvulerable());
        enderCrystalEntity6.setPositionAndAngles(spike.getCenterX() + 0.5f, spike.getHeight() + 1, spike.getCenterZ() + 0.5f, random.nextFloat() * 360.0f, 0.0f);
        world.spawnEntity(enderCrystalEntity6);
        this.setBlockState(world, new BlockPos(spike.getCenterX(), spike.getHeight(), spike.getCenterZ()), Blocks.z.getDefaultState());
    }
    
    static {
        CACHE = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).<Long, List<Spike>>build(new SpikeCache());
    }
    
    public static class Spike
    {
        private final int centerX;
        private final int centerZ;
        private final int radius;
        private final int height;
        private final boolean guarded;
        private final BoundingBox boundingBox;
        
        public Spike(final int centerX, final int centerZ, final int radius, final int height, final boolean boolean5) {
            this.centerX = centerX;
            this.centerZ = centerZ;
            this.radius = radius;
            this.height = height;
            this.guarded = boolean5;
            this.boundingBox = new BoundingBox(centerX - radius, 0.0, centerZ - radius, centerX + radius, 256.0, centerZ + radius);
        }
        
        public boolean isInChunk(final BlockPos pos) {
            return pos.getX() >> 4 == this.centerX >> 4 && pos.getZ() >> 4 == this.centerZ >> 4;
        }
        
        public int getCenterX() {
            return this.centerX;
        }
        
        public int getCenterZ() {
            return this.centerZ;
        }
        
        public int getRadius() {
            return this.radius;
        }
        
        public int getHeight() {
            return this.height;
        }
        
        public boolean isGuarded() {
            return this.guarded;
        }
        
        public BoundingBox getBoundingBox() {
            return this.boundingBox;
        }
        
         <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
            final ImmutableMap.Builder<T, T> builder2 = ImmutableMap.<T, T>builder();
            builder2.put((T)dynamicOps.createString("centerX"), (T)dynamicOps.createInt(this.centerX));
            builder2.put((T)dynamicOps.createString("centerZ"), (T)dynamicOps.createInt(this.centerZ));
            builder2.put((T)dynamicOps.createString("radius"), (T)dynamicOps.createInt(this.radius));
            builder2.put((T)dynamicOps.createString("height"), (T)dynamicOps.createInt(this.height));
            builder2.put((T)dynamicOps.createString("guarded"), (T)dynamicOps.createBoolean(this.guarded));
            return (Dynamic<T>)new Dynamic((DynamicOps)dynamicOps, dynamicOps.createMap((Map)builder2.build()));
        }
        
        public static <T> Spike deserialize(final Dynamic<T> dynamic) {
            return new Spike(dynamic.get("centerX").asInt(0), dynamic.get("centerZ").asInt(0), dynamic.get("radius").asInt(0), dynamic.get("height").asInt(0), dynamic.get("guarded").asBoolean(false));
        }
    }
    
    static class SpikeCache extends CacheLoader<Long, List<Spike>>
    {
        private SpikeCache() {
        }
        
        public List<Spike> a(final Long long1) {
            final List<Integer> list2 = IntStream.range(0, 10).boxed().collect(Collectors.toList());
            Collections.shuffle(list2, new Random(long1));
            final List<Spike> list3 = Lists.newArrayList();
            for (int integer4 = 0; integer4 < 10; ++integer4) {
                final int integer5 = (int)(42.0 * Math.cos(2.0 * (-3.141592653589793 + 0.3141592653589793 * integer4)));
                final int integer6 = (int)(42.0 * Math.sin(2.0 * (-3.141592653589793 + 0.3141592653589793 * integer4)));
                final int integer7 = list2.get(integer4);
                final int integer8 = 2 + integer7 / 3;
                final int integer9 = 76 + integer7 * 3;
                final boolean boolean10 = integer7 == 1 || integer7 == 2;
                list3.add(new Spike(integer5, integer6, integer8, integer9, boolean10));
            }
            return list3;
        }
    }
}
