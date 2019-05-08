package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DataFixUtils;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.function.Function;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import java.util.Optional;
import net.minecraft.util.Identifier;
import java.util.Locale;
import com.mojang.datafixers.Dynamic;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class EntityPaintingMotiveFix extends ChoiceFix
{
    private static final Map<String, String> RENAMED_MOTIVES;
    
    public EntityPaintingMotiveFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2, "EntityPaintingMotiveFix", TypeReferences.ENTITY, "minecraft:painting");
    }
    
    public Dynamic<?> a(final Dynamic<?> dynamic) {
        final Optional<String> optional2 = (Optional<String>)dynamic.get("Motive").asString();
        if (optional2.isPresent()) {
            final String string3 = optional2.get().toLowerCase(Locale.ROOT);
            return dynamic.set("Motive", dynamic.createString(new Identifier(EntityPaintingMotiveFix.RENAMED_MOTIVES.getOrDefault(string3, string3)).toString()));
        }
        return dynamic;
    }
    
    @Override
    protected Typed<?> transform(final Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), (Function)this::a);
    }
    
    static {
        RENAMED_MOTIVES = (Map)DataFixUtils.make(Maps.newHashMap(), hashMap -> {
            hashMap.put("donkeykong", "donkey_kong");
            hashMap.put("burningskull", "burning_skull");
            hashMap.put("skullandroses", "skull_and_roses");
        });
    }
}
