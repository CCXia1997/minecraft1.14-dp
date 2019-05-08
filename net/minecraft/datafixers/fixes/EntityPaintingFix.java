package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Typed;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class EntityPaintingFix extends DataFix
{
    private static final int[][] OFFSETS;
    
    public EntityPaintingFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    private Dynamic<?> a(Dynamic<?> dynamic, final boolean boolean2, final boolean boolean3) {
        if ((boolean2 || boolean3) && !dynamic.get("Facing").asNumber().isPresent()) {
            int integer4;
            if (dynamic.get("Direction").asNumber().isPresent()) {
                integer4 = dynamic.get("Direction").asByte((byte)0) % EntityPaintingFix.OFFSETS.length;
                final int[] arr5 = EntityPaintingFix.OFFSETS[integer4];
                dynamic = dynamic.set("TileX", dynamic.createInt(dynamic.get("TileX").asInt(0) + arr5[0]));
                dynamic = dynamic.set("TileY", dynamic.createInt(dynamic.get("TileY").asInt(0) + arr5[1]));
                dynamic = dynamic.set("TileZ", dynamic.createInt(dynamic.get("TileZ").asInt(0) + arr5[2]));
                dynamic = dynamic.remove("Direction");
                if (boolean3 && dynamic.get("ItemRotation").asNumber().isPresent()) {
                    dynamic = dynamic.set("ItemRotation", dynamic.createByte((byte)(dynamic.get("ItemRotation").asByte((byte)0) * 2)));
                }
            }
            else {
                integer4 = dynamic.get("Dir").asByte((byte)0) % EntityPaintingFix.OFFSETS.length;
                dynamic = dynamic.remove("Dir");
            }
            dynamic = dynamic.set("Facing", dynamic.createByte((byte)integer4));
        }
        return dynamic;
    }
    
    public TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, "Painting");
        final OpticFinder<?> opticFinder2 = DSL.namedChoice("Painting", (Type)type1);
        final Type<?> type2 = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, "ItemFrame");
        final OpticFinder<?> opticFinder3 = DSL.namedChoice("ItemFrame", (Type)type2);
        final Type<?> type3 = this.getInputSchema().getType(TypeReferences.ENTITY);
        final TypeRewriteRule typeRewriteRule6 = this.fixTypeEverywhereTyped("EntityPaintingFix", (Type)type3, typed -> typed.updateTyped((OpticFinder)opticFinder2, (Type)type1, typed -> typed.update(DSL.remainderFinder(), dynamic -> this.a(dynamic, true, false))));
        final TypeRewriteRule typeRewriteRule7 = this.fixTypeEverywhereTyped("EntityItemFrameFix", (Type)type3, typed -> typed.updateTyped((OpticFinder)opticFinder3, (Type)type2, typed -> typed.update(DSL.remainderFinder(), dynamic -> this.a(dynamic, false, true))));
        return TypeRewriteRule.seq(typeRewriteRule6, typeRewriteRule7);
    }
    
    static {
        OFFSETS = new int[][] { { 0, 0, 1 }, { -1, 0, 0 }, { 0, 0, -1 }, { 1, 0, 0 } };
    }
}
