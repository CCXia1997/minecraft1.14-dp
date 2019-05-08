package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Iterator;
import java.util.stream.Stream;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.OpticFinder;
import java.util.Map;
import java.util.Optional;
import com.google.common.collect.Maps;
import java.util.function.Function;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class BedBlockEntityFix extends DataFix
{
    public BedBlockEntityFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getOutputSchema().getType(TypeReferences.CHUNK);
        final Type<?> type2 = type1.findFieldType("Level");
        final Type<?> type3 = type2.findFieldType("TileEntities");
        if (!(type3 instanceof List.ListType)) {
            throw new IllegalStateException("Tile entity type is not a list type.");
        }
        final List.ListType<?> listType4 = type3;
        return this.a(type2, listType4);
    }
    
    private <TE> TypeRewriteRule a(final Type<?> type, final List.ListType<TE> listType) {
        final Type<TE> type2 = (Type<TE>)listType.getElement();
        final OpticFinder<?> opticFinder4 = DSL.fieldFinder("Level", (Type)type);
        final OpticFinder<java.util.List<TE>> opticFinder5 = (OpticFinder<java.util.List<TE>>)DSL.fieldFinder("TileEntities", (Type)listType);
        final int integer6 = 416;
        final OpticFinder opticFinder6;
        final Typed<?> typed2;
        final Dynamic<?> dynamic2;
        final int integer7;
        final int integer8;
        final OpticFinder opticFinder7;
        final java.util.List<Object> list9;
        java.util.List<? extends Dynamic<?>> list10;
        int integer9;
        Dynamic<?> dynamic3;
        int integer10;
        Stream<Integer> stream14;
        int integer11;
        final Iterator<Integer> iterator;
        int integer12;
        int integer13;
        int integer14;
        int integer15;
        Map<Dynamic<?>, Dynamic<?>> map21;
        final Type type3;
        return TypeRewriteRule.seq(this.fixTypeEverywhere("InjectBedBlockEntityType", (Type)this.getInputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY), (Type)this.getOutputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY), dynamicOps -> pair -> pair), this.fixTypeEverywhereTyped("BedBlockEntityInjecter", this.getOutputSchema().getType(TypeReferences.CHUNK), typed -> {
            typed2 = typed.getTyped(opticFinder6);
            dynamic2 = typed2.get(DSL.remainderFinder());
            integer7 = dynamic2.get("xPos").asInt(0);
            integer8 = dynamic2.get("zPos").asInt(0);
            list9 = Lists.newArrayList(typed2.getOrCreate(opticFinder7));
            for (list10 = dynamic2.get("Sections").asList((Function)Function.identity()), integer9 = 0; integer9 < list10.size(); ++integer9) {
                dynamic3 = list10.get(integer9);
                integer10 = dynamic3.get("Y").asInt(0);
                stream14 = dynamic3.get("Blocks").asStream().<Integer>map(dynamic -> dynamic.asInt(0));
                integer11 = 0;
                ((Iterable<Integer>)stream14::iterator).iterator();
                while (iterator.hasNext()) {
                    integer12 = iterator.next();
                    if (416 == (integer12 & 0xFF) << 4) {
                        integer13 = (integer11 & 0xF);
                        integer14 = (integer11 >> 8 & 0xF);
                        integer15 = (integer11 >> 4 & 0xF);
                        map21 = Maps.newHashMap();
                        map21.put(dynamic3.createString("id"), dynamic3.createString("minecraft:bed"));
                        map21.put(dynamic3.createString("x"), dynamic3.createInt(integer13 + (integer7 << 4)));
                        map21.put(dynamic3.createString("y"), dynamic3.createInt(integer14 + (integer10 << 4)));
                        map21.put(dynamic3.createString("z"), dynamic3.createInt(integer15 + (integer8 << 4)));
                        map21.put(dynamic3.createString("color"), dynamic3.createShort((short)14));
                        list9.add(((Optional)type3.read(dynamic3.createMap((Map)map21)).getSecond()).orElseThrow(() -> new IllegalStateException("Could not parse newly created bed block entity.")));
                    }
                    ++integer11;
                }
            }
            if (!list9.isEmpty()) {
                return typed.set(opticFinder6, typed2.set(opticFinder7, list9));
            }
            else {
                return typed;
            }
        }));
    }
}
