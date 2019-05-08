package net.minecraft.datafixers.fixes;

import java.util.Objects;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;

public class EntitySkeletonSplitFix extends EntitySimpleTransformFix
{
    public EntitySkeletonSplitFix(final Schema schema, final boolean boolean2) {
        super("EntitySkeletonSplitFix", schema, boolean2);
    }
    
    @Override
    protected Pair<String, Dynamic<?>> transform(String choice, final Dynamic<?> dynamic) {
        if (Objects.equals(choice, "Skeleton")) {
            final int integer3 = dynamic.get("SkeletonType").asInt(0);
            if (integer3 == 1) {
                choice = "WitherSkeleton";
            }
            else if (integer3 == 2) {
                choice = "Stray";
            }
        }
        return (Pair<String, Dynamic<?>>)Pair.of(choice, dynamic);
    }
}
