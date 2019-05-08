package net.minecraft.world.gen.feature;

import java.util.function.Function;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import java.util.List;

public class EndSpikeFeatureConfig implements FeatureConfig
{
    private final boolean crystalInvulnerable;
    private final List<EndSpikeFeature.Spike> spikes;
    @Nullable
    private final BlockPos crystalBeamTarget;
    
    public EndSpikeFeatureConfig(final boolean crystalInvulnerable, final List<EndSpikeFeature.Spike> spikes, @Nullable final BlockPos crystalBeamTarget) {
        this.crystalInvulnerable = crystalInvulnerable;
        this.spikes = spikes;
        this.crystalBeamTarget = crystalBeamTarget;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("crystalInvulnerable"), ops.createBoolean(this.crystalInvulnerable), ops.createString("spikes"), ops.createList((Stream)this.spikes.stream().map(spike -> spike.<T>serialize(ops).getValue())), ops.createString("crystalBeamTarget"), (this.crystalBeamTarget == null) ? ops.createList((Stream)Stream.empty()) : ops.createList((Stream)IntStream.of(this.crystalBeamTarget.getX(), this.crystalBeamTarget.getY(), this.crystalBeamTarget.getZ()).mapToObj(ops::createInt)))));
    }
    
    public static <T> EndSpikeFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final List<EndSpikeFeature.Spike> list2 = (List<EndSpikeFeature.Spike>)dynamic.get("spikes").asList((Function)EndSpikeFeature.Spike::deserialize);
        final List<Integer> list3 = (List<Integer>)dynamic.get("crystalBeamTarget").asList(dynamic -> dynamic.asInt(0));
        BlockPos blockPos4;
        if (list3.size() == 3) {
            blockPos4 = new BlockPos(list3.get(0), list3.get(1), list3.get(2));
        }
        else {
            blockPos4 = null;
        }
        return new EndSpikeFeatureConfig(dynamic.get("crystalInvulnerable").asBoolean(false), list2, blockPos4);
    }
    
    public boolean isCrystalInvulerable() {
        return this.crystalInvulnerable;
    }
    
    public List<EndSpikeFeature.Spike> getSpikes() {
        return this.spikes;
    }
    
    @Nullable
    public BlockPos getPos() {
        return this.crystalBeamTarget;
    }
}
