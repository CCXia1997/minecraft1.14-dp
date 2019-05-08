package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class DecoratedFlowerFeature extends DecoratedFeature
{
    public DecoratedFlowerFeature(final Function<Dynamic<?>, ? extends DecoratedFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
}
