package net.minecraft.datafixers.fixes;

import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.types.templates.TaggedChoice;
import java.util.Iterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Optional;
import com.mojang.datafixers.OpticFinder;
import java.util.Objects;
import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.templates.List;
import net.minecraft.datafixers.TypeReferences;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import org.apache.logging.log4j.Logger;
import com.mojang.datafixers.DataFix;

public class AddTrappedChestFix extends DataFix
{
    private static final Logger LOGGER;
    
    public AddTrappedChestFix(final Schema schema, final boolean boolean2) {
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
        final OpticFinder<? extends java.util.List<?>> opticFinder5 = DSL.fieldFinder("TileEntities", (Type)listType4);
        final Type<?> type4 = this.getInputSchema().getType(TypeReferences.CHUNK);
        final OpticFinder<?> opticFinder6 = type4.findField("Level");
        final OpticFinder<?> opticFinder7 = opticFinder6.type().findField("Sections");
        final Type<?> type5 = opticFinder7.type();
        if (!(type5 instanceof List.ListType)) {
            throw new IllegalStateException("Expecting sections to be a list.");
        }
        final Type<?> type6 = ((List.ListType)type5).getElement();
        final OpticFinder<?> opticFinder8 = DSL.typeFinder((Type)type6);
        final OpticFinder opticFinder9;
        final Optional<? extends Typed<?>> optional5;
        final OpticFinder opticFinder10;
        java.util.List<? extends Typed<?>> list6;
        IntSet intSet7;
        final Iterator<? extends Typed<?>> iterator;
        Typed<?> typed2;
        a a10;
        int integer11;
        int integer12;
        Dynamic<?> dynamic8;
        int integer13;
        int integer14;
        TaggedChoice.TaggedChoiceType<String> taggedChoiceType11;
        final OpticFinder opticFinder11;
        final TaggedChoice.TaggedChoiceType taggedChoiceType12;
        Dynamic<?> dynamic9;
        final int n;
        int integer15;
        int integer16;
        final int n2;
        int integer17;
        final IntSet set;
        final TaggedChoice.TaggedChoiceType taggedChoiceType13;
        return TypeRewriteRule.seq(new FixChoiceTypes(this.getOutputSchema(), "AddTrappedChestFix", TypeReferences.BLOCK_ENTITY).makeRule(), this.fixTypeEverywhereTyped("Trapped Chest fix", (Type)type4, typed -> typed.updateTyped((OpticFinder)opticFinder6, typed -> {
            optional5 = typed.getOptionalTyped(opticFinder9);
            if (!optional5.isPresent()) {
                return typed;
            }
            else {
                list6 = ((Typed)optional5.get()).getAllTyped(opticFinder10);
                intSet7 = (IntSet)new IntOpenHashSet();
                list6.iterator();
                while (iterator.hasNext()) {
                    typed2 = iterator.next();
                    a10 = new a(typed2, this.getInputSchema());
                    if (a10.b()) {
                        continue;
                    }
                    else {
                        for (integer11 = 0; integer11 < 4096; ++integer11) {
                            integer12 = a10.c(integer11);
                            if (a10.a(integer12)) {
                                intSet7.add(a10.c() << 12 | integer11);
                            }
                        }
                    }
                }
                dynamic8 = typed.get(DSL.remainderFinder());
                integer13 = dynamic8.get("xPos").asInt(0);
                integer14 = dynamic8.get("zPos").asInt(0);
                taggedChoiceType11 = (TaggedChoice.TaggedChoiceType<String>)this.getInputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY);
                return typed.updateTyped(opticFinder11, typed -> typed.updateTyped(taggedChoiceType12.finder(), typed -> {
                    dynamic9 = ((Typed)typed).getOrCreate(DSL.remainderFinder());
                    integer15 = dynamic9.get("x").asInt(0) - (n << 4);
                    integer16 = dynamic9.get("y").asInt(0);
                    integer17 = dynamic9.get("z").asInt(0) - (n2 << 4);
                    if (set.contains(LeavesFix.a(integer15, integer16, integer17))) {
                        return ((Typed)typed).update(taggedChoiceType13.finder(), pair -> pair.mapFirst(string -> {
                            if (!Objects.equals(string, "minecraft:chest")) {
                                AddTrappedChestFix.LOGGER.warn("Block Entity was expected to be a chest");
                            }
                            return "minecraft:trapped_chest";
                        }));
                    }
                    else {
                        return (Typed)typed;
                    }
                }));
            }
        })));
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static final class a extends LeavesFix.b
    {
        @Nullable
        private IntSet e;
        
        public a(final Typed<?> typed, final Schema schema) {
            super(typed, schema);
        }
        
        @Override
        protected boolean a() {
            this.e = (IntSet)new IntOpenHashSet();
            for (int integer1 = 0; integer1 < this.b.size(); ++integer1) {
                final Dynamic<?> dynamic2 = this.b.get(integer1);
                final String string3 = dynamic2.get("Name").asString("");
                if (Objects.equals(string3, "minecraft:trapped_chest")) {
                    this.e.add(integer1);
                }
            }
            return this.e.isEmpty();
        }
        
        public boolean a(final int integer) {
            return this.e.contains(integer);
        }
    }
}
