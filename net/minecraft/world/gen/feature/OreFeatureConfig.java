package net.minecraft.world.gen.feature;

import java.util.stream.Collectors;
import java.util.Arrays;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.block.Block;
import java.util.function.Predicate;
import net.minecraft.block.Blocks;
import java.util.function.Function;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;

public class OreFeatureConfig implements FeatureConfig
{
    public final Target target;
    public final int size;
    public final BlockState state;
    
    public OreFeatureConfig(final Target target, final BlockState state, final int size) {
        this.size = size;
        this.state = state;
        this.target = target;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("size"), ops.createInt(this.size), ops.createString("target"), ops.createString(this.target.getName()), ops.createString("state"), BlockState.<T>serialize(ops, this.state).getValue())));
    }
    
    public static OreFeatureConfig deserialize(final Dynamic<?> dynamic) {
        final int integer2 = dynamic.get("size").asInt(0);
        final Target target3 = Target.byName(dynamic.get("target").asString(""));
        final BlockState blockState4 = dynamic.get("state").map((Function)BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        return new OreFeatureConfig(target3, blockState4, integer2);
    }
    
    public enum Target
    {
        NATURAL_STONE("natural_stone", blockState -> {
            if (blockState != null) {
                block2 = blockState.getBlock();
                return block2 == Blocks.b || block2 == Blocks.c || block2 == Blocks.e || block2 == Blocks.g;
            }
            else {
                return false;
            }
        }), 
        NETHERRACK("netherrack", (Predicate<BlockState>)new BlockPredicate(Blocks.cJ));
        
        private static final Map<String, Target> nameMap;
        private final String name;
        private final Predicate<BlockState> predicate;
        
        private Target(final String name, final Predicate<BlockState> predicate) {
            this.name = name;
            this.predicate = predicate;
        }
        
        public String getName() {
            return this.name;
        }
        
        public static Target byName(final String name) {
            return Target.nameMap.get(name);
        }
        
        public Predicate<BlockState> getCondition() {
            return this.predicate;
        }
        
        static {
            nameMap = Arrays.<Target>stream(values()).collect(Collectors.toMap(Target::getName, target -> target));
        }
    }
}
