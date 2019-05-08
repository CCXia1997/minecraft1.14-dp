package net.minecraft.world.loot.context;

import net.minecraft.world.loot.LootSupplier;
import net.minecraft.util.Identifier;
import java.util.function.Function;
import net.minecraft.world.loot.LootTableReporter;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public interface ParameterConsumer
{
    default Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of();
    }
    
    default void check(final LootTableReporter reporter, final Function<Identifier, LootSupplier> supplierGetter, final Set<Identifier> parentLootTables, final LootContextType contextType) {
        contextType.check(reporter, this);
    }
}
