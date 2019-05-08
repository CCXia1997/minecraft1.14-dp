package net.minecraft.datafixers.fixes;

import java.util.Objects;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;

public class EntityElderGuardianSplitFix extends EntitySimpleTransformFix
{
    public EntityElderGuardianSplitFix(final Schema schema, final boolean boolean2) {
        super("EntityElderGuardianSplitFix", schema, boolean2);
    }
    
    @Override
    protected Pair<String, Dynamic<?>> transform(final String choice, final Dynamic<?> dynamic) {
        return (Pair<String, Dynamic<?>>)Pair.of(((Objects.equals(choice, "Guardian") && dynamic.get("Elder").asBoolean(false)) ? "ElderGuardian" : choice), dynamic);
    }
}
