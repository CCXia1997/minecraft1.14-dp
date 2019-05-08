package net.minecraft.world.loot;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.world.loot.context.LootContext;

@FunctionalInterface
interface LootChoiceProvider
{
    public static final LootChoiceProvider ALWAYS_FALSE = (lootContext, consumer) -> false;
    public static final LootChoiceProvider ALWAYS_TRUE = (lootContext, consumer) -> true;
    
    boolean expand(final LootContext arg1, final Consumer<LootChoice> arg2);
    
    default LootChoiceProvider and(final LootChoiceProvider other) {
        Objects.<LootChoiceProvider>requireNonNull(other);
        return (context, lootChoiceExpander) -> this.expand(context, lootChoiceExpander) && other.expand(context, lootChoiceExpander);
    }
    
    default LootChoiceProvider or(final LootChoiceProvider other) {
        Objects.<LootChoiceProvider>requireNonNull(other);
        return (context, lootChoiceExpander) -> this.expand(context, lootChoiceExpander) || other.expand(context, lootChoiceExpander);
    }
}
