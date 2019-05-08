package net.minecraft.world.loot.entry;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.loot.LootChoice;
import java.util.function.Consumer;
import net.minecraft.world.loot.context.LootContext;
import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.world.loot.context.LootContextType;
import java.util.Set;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.util.Identifier;
import java.util.function.Function;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.condition.LootCondition;

public class AlternativeEntry extends CombinedEntry
{
    AlternativeEntry(final LootEntry[] children, final LootCondition[] conditions) {
        super(children, conditions);
    }
    
    @Override
    protected LootChoiceProvider combine(final LootChoiceProvider[] children) {
        switch (children.length) {
            case 0: {
                return AlternativeEntry.ALWAYS_FALSE;
            }
            case 1: {
                return children[0];
            }
            case 2: {
                return children[0].or(children[1]);
            }
            default: {
                final int length;
                int i = 0;
                LootChoiceProvider lootChoiceProvider7;
                return (context, lootChoiceExpander) -> {
                    length = children.length;
                    while (i < length) {
                        lootChoiceProvider7 = children[i];
                        if (lootChoiceProvider7.expand(context, lootChoiceExpander)) {
                            return true;
                        }
                        else {
                            ++i;
                        }
                    }
                    return false;
                };
            }
        }
    }
    
    @Override
    public void check(final LootTableReporter reporter, final Function<Identifier, LootSupplier> supplierGetter, final Set<Identifier> parentLootTables, final LootContextType contextType) {
        super.check(reporter, supplierGetter, parentLootTables, contextType);
        for (int integer5 = 0; integer5 < this.children.length - 1; ++integer5) {
            if (ArrayUtils.isEmpty((Object[])this.children[integer5].conditions)) {
                reporter.report("Unreachable entry!");
            }
        }
    }
    
    public static Builder builder(final LootEntry.Builder<?>... children) {
        return new Builder(children);
    }
    
    public static class Builder extends LootEntry.Builder<Builder>
    {
        private final List<LootEntry> children;
        
        public Builder(final LootEntry.Builder<?>... children) {
            this.children = Lists.newArrayList();
            for (final LootEntry.Builder<?> builder5 : children) {
                this.children.add(builder5.build());
            }
        }
        
        @Override
        protected Builder getThisBuilder() {
            return this;
        }
        
        @Override
        public Builder withChild(final LootEntry.Builder<?> builder) {
            this.children.add(builder.build());
            return this;
        }
        
        @Override
        public LootEntry build() {
            return new AlternativeEntry(this.children.<LootEntry>toArray(new LootEntry[0]), this.getConditions());
        }
    }
}
