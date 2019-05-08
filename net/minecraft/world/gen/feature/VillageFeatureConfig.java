package net.minecraft.world.gen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.Identifier;

public class VillageFeatureConfig implements FeatureConfig
{
    public final Identifier startPool;
    public final int size;
    
    public VillageFeatureConfig(final String startPool, final int size) {
        this.startPool = new Identifier(startPool);
        this.size = size;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("start_pool"), ops.createString(this.startPool.toString()), ops.createString("size"), ops.createInt(this.size))));
    }
    
    public static <T> VillageFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final String string2 = dynamic.get("start_pool").asString("");
        final int integer3 = dynamic.get("size").asInt(6);
        return new VillageFeatureConfig(string2, integer3);
    }
}
