package net.minecraft.world.loot.entry;

import net.minecraft.world.loot.LootChoice;
import java.util.function.Consumer;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.condition.LootCondition;

public class GroupEntry extends CombinedEntry
{
    GroupEntry(final LootEntry[] children, final LootCondition[] conditions) {
        super(children, conditions);
    }
    
    @Override
    protected LootChoiceProvider combine(final LootChoiceProvider[] children) {
        switch (children.length) {
            case 0: {
                return GroupEntry.ALWAYS_TRUE;
            }
            case 1: {
                return children[0];
            }
            case 2: {
                final LootChoiceProvider lootChoiceProvider2 = children[0];
                final LootChoiceProvider lootChoiceProvider3 = children[1];
                final LootChoiceProvider lootChoiceProvider5;
                final LootChoiceProvider lootChoiceProvider6;
                return (context, lootChoiceExpander) -> {
                    lootChoiceProvider5.expand(context, lootChoiceExpander);
                    lootChoiceProvider6.expand(context, lootChoiceExpander);
                    return true;
                };
            }
            default: {
                int length;
                int i = 0;
                LootChoiceProvider lootChoiceProvider4;
                return (context, lootChoiceExpander) -> {
                    for (length = children.length; i < length; ++i) {
                        lootChoiceProvider4 = children[i];
                        lootChoiceProvider4.expand(context, lootChoiceExpander);
                    }
                    return true;
                };
            }
        }
    }
}
