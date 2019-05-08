package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Typed;
import java.util.Optional;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.OpticFinder;
import java.util.function.Function;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class HeightmapRenamingFix extends DataFix
{
    public HeightmapRenamingFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    protected TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.CHUNK);
        final OpticFinder<?> opticFinder2 = type1.findField("Level");
        return this.fixTypeEverywhereTyped("HeightmapRenamingFix", (Type)type1, typed -> typed.updateTyped((OpticFinder)opticFinder2, typed -> typed.update(DSL.remainderFinder(), (Function)this::a)));
    }
    
    private Dynamic<?> a(final Dynamic<?> dynamic) {
        final Optional<? extends Dynamic<?>> optional2 = dynamic.get("Heightmaps").get();
        if (!optional2.isPresent()) {
            return dynamic;
        }
        Dynamic<?> dynamic2 = optional2.get();
        final Optional<? extends Dynamic<?>> optional3 = dynamic2.get("LIQUID").get();
        if (optional3.isPresent()) {
            dynamic2 = dynamic2.remove("LIQUID");
            dynamic2 = dynamic2.set("WORLD_SURFACE_WG", (Dynamic)optional3.get());
        }
        final Optional<? extends Dynamic<?>> optional4 = dynamic2.get("SOLID").get();
        if (optional4.isPresent()) {
            dynamic2 = dynamic2.remove("SOLID");
            dynamic2 = dynamic2.set("OCEAN_FLOOR_WG", (Dynamic)optional4.get());
            dynamic2 = dynamic2.set("OCEAN_FLOOR", (Dynamic)optional4.get());
        }
        final Optional<? extends Dynamic<?>> optional5 = dynamic2.get("LIGHT").get();
        if (optional5.isPresent()) {
            dynamic2 = dynamic2.remove("LIGHT");
            dynamic2 = dynamic2.set("LIGHT_BLOCKING", (Dynamic)optional5.get());
        }
        final Optional<? extends Dynamic<?>> optional6 = dynamic2.get("RAIN").get();
        if (optional6.isPresent()) {
            dynamic2 = dynamic2.remove("RAIN");
            dynamic2 = dynamic2.set("MOTION_BLOCKING", (Dynamic)optional6.get());
            dynamic2 = dynamic2.set("MOTION_BLOCKING_NO_LEAVES", (Dynamic)optional6.get());
        }
        return dynamic.set("Heightmaps", (Dynamic)dynamic2);
    }
}
