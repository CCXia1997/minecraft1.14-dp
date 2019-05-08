package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.Typed;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.Iterator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.math.NumberUtils;
import java.util.Optional;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import com.mojang.datafixers.DSL;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.google.common.base.Splitter;
import com.mojang.datafixers.DataFix;

public class LevelFlatGeneratorInfoFix extends DataFix
{
    private static final Splitter SPLIT_ON_SEMICOLON;
    private static final Splitter SPLIT_ON_COMMA;
    private static final Splitter SPLIT_ON_LOWER_X;
    private static final Splitter SPLIT_ON_ASTERISK;
    private static final Splitter SPLIT_ON_COLON;
    
    public LevelFlatGeneratorInfoFix(final Schema schema, final boolean boolean2) {
        super(schema, boolean2);
    }
    
    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("LevelFlatGeneratorInfoFix", this.getInputSchema().getType(TypeReferences.LEVEL), typed -> typed.update(DSL.remainderFinder(), (Function)this::a));
    }
    
    private Dynamic<?> a(final Dynamic<?> dynamic) {
        if (dynamic.get("generatorName").asString("").equalsIgnoreCase("flat")) {
            return dynamic.update("generatorOptions", dynamic -> (Dynamic)DataFixUtils.orElse((Optional)((Dynamic)dynamic).asString().map(this::transform).map(dynamic::createString), dynamic));
        }
        return dynamic;
    }
    
    @VisibleForTesting
    String transform(final String string) {
        if (string.isEmpty()) {
            return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
        }
        final Iterator<String> iterator2 = LevelFlatGeneratorInfoFix.SPLIT_ON_SEMICOLON.split(string).iterator();
        final String string2 = iterator2.next();
        int integer4;
        String string3;
        if (iterator2.hasNext()) {
            integer4 = NumberUtils.toInt(string2, 0);
            string3 = iterator2.next();
        }
        else {
            integer4 = 0;
            string3 = string2;
        }
        if (integer4 < 0 || integer4 > 3) {
            return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
        }
        final StringBuilder stringBuilder6 = new StringBuilder();
        final Splitter splitter7 = (integer4 < 3) ? LevelFlatGeneratorInfoFix.SPLIT_ON_LOWER_X : LevelFlatGeneratorInfoFix.SPLIT_ON_ASTERISK;
        final Splitter splitter8;
        final List<String> list6;
        int integer5;
        String string4;
        final List<String> list7;
        final int integer6;
        final String string5;
        final int n;
        int n2;
        final int integer7;
        final int integer8;
        final int integer9;
        final StringBuilder sb;
        String string6;
        stringBuilder6.append(StreamSupport.<String>stream(LevelFlatGeneratorInfoFix.SPLIT_ON_COMMA.split(string3).spliterator(), false).map(string -> {
            list6 = splitter8.splitToList(string);
            if (list6.size() == 2) {
                integer5 = NumberUtils.toInt((String)list6.get(0));
                string4 = list6.get(1);
            }
            else {
                integer5 = 1;
                string4 = list6.get(0);
            }
            list7 = LevelFlatGeneratorInfoFix.SPLIT_ON_COLON.splitToList(string4);
            integer6 = (list7.get(0).equals("minecraft") ? 1 : 0);
            string5 = list7.get(integer6);
            if (n == 3) {
                n2 = EntityBlockStateFix.getNumericalBlockId("minecraft:" + string5);
            }
            else {
                n2 = NumberUtils.toInt(string5, 0);
            }
            integer7 = n2;
            integer8 = integer6 + 1;
            integer9 = ((list7.size() > integer8) ? NumberUtils.toInt((String)list7.get(integer8), 0) : 0);
            sb = new StringBuilder();
            if (integer5 == 1) {
                string6 = "";
            }
            else {
                string6 = integer5 + "*";
            }
            return sb.append(string6).append(BlockStateFlattening.lookupState(integer7 << 4 | integer9).get("Name").asString("")).toString();
        }).collect(Collectors.joining(",")));
        while (iterator2.hasNext()) {
            stringBuilder6.append(';').append(iterator2.next());
        }
        return stringBuilder6.toString();
    }
    
    static {
        SPLIT_ON_SEMICOLON = Splitter.on(';').limit(5);
        SPLIT_ON_COMMA = Splitter.on(',');
        SPLIT_ON_LOWER_X = Splitter.on('x').limit(2);
        SPLIT_ON_ASTERISK = Splitter.on('*').limit(2);
        SPLIT_ON_COLON = Splitter.on(':').limit(3);
    }
}
