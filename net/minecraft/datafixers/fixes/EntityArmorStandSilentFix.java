package net.minecraft.datafixers.fixes;

import java.util.function.Function;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.Dynamic;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.schemas.Schema;

public class EntityArmorStandSilentFix extends ChoiceFix
{
    public EntityArmorStandSilentFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2, "EntityArmorStandSilentFix", TypeReferences.ENTITY, "ArmorStand");
    }
    
    public Dynamic<?> a(final Dynamic<?> dynamic) {
        if (dynamic.get("Silent").asBoolean(false) && !dynamic.get("Marker").asBoolean(false)) {
            return dynamic.remove("Silent");
        }
        return dynamic;
    }
    
    @Override
    protected Typed<?> transform(final Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), (Function)this::a);
    }
}
