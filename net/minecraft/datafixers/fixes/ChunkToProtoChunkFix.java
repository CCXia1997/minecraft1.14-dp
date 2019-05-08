package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.OpticFinder;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.List;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.util.stream.Stream;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.DataFix;

public class ChunkToProtoChunkFix extends DataFix
{
    public ChunkToProtoChunkFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        final Type<?> type1 = this.getInputSchema().getType(TypeReferences.CHUNK);
        final Type<?> type2 = this.getOutputSchema().getType(TypeReferences.CHUNK);
        final Type<?> type3 = type1.findFieldType("Level");
        final Type<?> type4 = type2.findFieldType("Level");
        final Type<?> type5 = type3.findFieldType("TileTicks");
        final OpticFinder<?> opticFinder6 = DSL.fieldFinder("Level", (Type)type3);
        final OpticFinder<?> opticFinder7 = DSL.fieldFinder("TileTicks", (Type)type5);
        final OpticFinder opticFinder8;
        final Type type6;
        final OpticFinder opticFinder9;
        final Optional<? extends Stream<? extends Dynamic<?>>> optional4;
        final Dynamic<?> dynamic4;
        final boolean boolean6;
        final Dynamic<?> dynamic5;
        Dynamic<?> dynamic6;
        Optional<ByteBuffer> optional5;
        ByteBuffer byteBuffer9;
        int[] arr10;
        int integer11;
        Dynamic<?> dynamic7;
        List<Dynamic<?>> list10;
        int integer12;
        int integer13;
        int integer14;
        short short7;
        final List<Dynamic> list11;
        final Dynamic dynamic9;
        Dynamic<?> dynamic8;
        final Type type7;
        return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("ChunkToProtoChunkFix", (Type)type1, this.getOutputSchema().getType(TypeReferences.CHUNK), typed -> typed.updateTyped(opticFinder8, type6, typed -> {
            optional4 = typed.getOptionalTyped(opticFinder9).map(Typed::write).flatMap(Dynamic::asStreamOpt);
            dynamic4 = typed.get(DSL.remainderFinder());
            boolean6 = (dynamic4.get("TerrainPopulated").asBoolean((boolean)(0 != 0)) && (!dynamic4.get("LightPopulated").asNumber().isPresent() || dynamic4.get("LightPopulated").asBoolean((boolean)(0 != 0))));
            dynamic5 = dynamic4.set("Status", dynamic4.createString(boolean6 ? "mobs_spawned" : "empty"));
            dynamic6 = dynamic5.set("hasLegacyStructureData", dynamic5.createBoolean((boolean)(1 != 0)));
            if (boolean6) {
                optional5 = (Optional<ByteBuffer>)dynamic6.get("Biomes").asByteBufferOpt();
                if (optional5.isPresent()) {
                    byteBuffer9 = optional5.get();
                    for (arr10 = new int[256], integer11 = 0; integer11 < arr10.length; ++integer11) {
                        if (integer11 < byteBuffer9.capacity()) {
                            arr10[integer11] = (byteBuffer9.get(integer11) & 0xFF);
                        }
                    }
                    dynamic6 = dynamic6.set("Biomes", dynamic6.createIntList(Arrays.stream(arr10)));
                }
                dynamic7 = dynamic6;
                list10 = IntStream.range(0, 16).mapToObj(integer -> dynamic7.createList((Stream)Stream.empty())).collect(Collectors.toList());
                if (optional4.isPresent()) {
                    ((Stream)optional4.get()).forEach(dynamic3 -> {
                        integer12 = dynamic3.get("x").asInt(0);
                        integer13 = dynamic3.get("y").asInt(0);
                        integer14 = dynamic3.get("z").asInt(0);
                        short7 = a(integer12, integer13, integer14);
                        list11.set(integer13 >> 4, list11.get(integer13 >> 4).merge(dynamic9.createShort(short7)));
                        return;
                    });
                    dynamic6 = dynamic6.set("ToBeTicked", dynamic6.createList((Stream)list10.stream()));
                }
                dynamic8 = typed.set(DSL.remainderFinder(), dynamic6).write();
            }
            else {
                dynamic8 = dynamic6;
            }
            return (Typed)((Optional)type7.readTyped((Dynamic)dynamic8).getSecond()).orElseThrow(() -> new IllegalStateException("Could not read the new chunk"));
        })), this.writeAndRead("Structure biome inject", this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE), this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE)));
    }
    
    private static short a(final int integer1, final int integer2, final int integer3) {
        return (short)((integer1 & 0xF) | (integer2 & 0xF) << 4 | (integer3 & 0xF) << 8);
    }
}
