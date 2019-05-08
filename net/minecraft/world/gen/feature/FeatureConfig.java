package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public interface FeatureConfig
{
    public static final DefaultFeatureConfig DEFAULT = new DefaultFeatureConfig();
    
     <T> Dynamic<T> serialize(final DynamicOps<T> arg1);
}
