package net.minecraft.world.loot.entry;

import net.minecraft.world.loot.LootChoice;
import java.util.function.Consumer;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.condition.LootCondition;

public class SequenceEntry extends CombinedEntry
{
    SequenceEntry(final LootEntry[] children, final LootCondition[] conditions) {
        super(children, conditions);
    }
    
    @Override
    protected LootChoiceProvider combine(final LootChoiceProvider[] children) {
        switch (children.length) {
            case 0: {
                return SequenceEntry.ALWAYS_TRUE;
            }
            case 1: {
                return children[0];
            }
            case 2: {
                return children[0].and(children[1]);
            }
            default: {
                final int length;
                int i = 0;
                LootChoiceProvider lootChoiceProvider7;
                return (context, lootChoiceExpander) -> {
                    length = children.length;
                    while (i < length) {
                        lootChoiceProvider7 = children[i];
                        if (!lootChoiceProvider7.expand(context, lootChoiceExpander)) {
                            return false;
                        }
                        else {
                            ++i;
                        }
                    }
                    return true;
                };
            }
        }
    }
}
