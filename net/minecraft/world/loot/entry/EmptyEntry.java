package net.minecraft.world.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import java.util.function.Consumer;
import net.minecraft.world.loot.function.LootFunction;
import net.minecraft.world.loot.condition.LootCondition;

public class EmptyEntry extends LeafEntry
{
    private EmptyEntry(final int weight, final int quality, final LootCondition[] conditions, final LootFunction[] functions) {
        super(weight, quality, conditions, functions);
    }
    
    public void drop(final Consumer<ItemStack> itemDropper, final LootContext context) {
    }
    
    public static Builder<?> Serializer() {
        return LeafEntry.builder(EmptyEntry::new);
    }
    
    public static class Serializer extends LeafEntry.Serializer<EmptyEntry>
    {
        public Serializer() {
            super(new Identifier("empty"), EmptyEntry.class);
        }
        
        @Override
        protected EmptyEntry fromJson(final JsonObject entryJson, final JsonDeserializationContext context, final int weight, final int quality, final LootCondition[] conditions, final LootFunction[] functions) {
            return new EmptyEntry(weight, quality, conditions, functions, null);
        }
    }
}
