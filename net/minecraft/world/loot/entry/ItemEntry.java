package net.minecraft.world.loot.entry;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import net.minecraft.util.registry.Registry;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.item.ItemProvider;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import java.util.function.Consumer;
import net.minecraft.world.loot.function.LootFunction;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.item.Item;

public class ItemEntry extends LeafEntry
{
    private final Item item;
    
    private ItemEntry(final Item item, final int weight, final int quality, final LootCondition[] conditions, final LootFunction[] functions) {
        super(weight, quality, conditions, functions);
        this.item = item;
    }
    
    public void drop(final Consumer<ItemStack> itemDropper, final LootContext context) {
        itemDropper.accept(new ItemStack(this.item));
    }
    
    public static Builder<?> builder(final ItemProvider itemProvider) {
        return LeafEntry.builder((weight, quality, conditions, functions) -> new ItemEntry(itemProvider.getItem(), weight, quality, conditions, functions));
    }
    
    public static class Serializer extends LeafEntry.Serializer<ItemEntry>
    {
        public Serializer() {
            super(new Identifier("item"), ItemEntry.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final ItemEntry entry, final JsonSerializationContext context) {
            super.toJson(json, entry, context);
            final Identifier identifier4 = Registry.ITEM.getId(entry.item);
            if (identifier4 == null) {
                throw new IllegalArgumentException("Can't serialize unknown item " + entry.item);
            }
            json.addProperty("name", identifier4.toString());
        }
        
        @Override
        protected ItemEntry fromJson(final JsonObject entryJson, final JsonDeserializationContext context, final int weight, final int quality, final LootCondition[] conditions, final LootFunction[] functions) {
            final Item item7 = JsonHelper.getItem(entryJson, "name");
            return new ItemEntry(item7, weight, quality, conditions, functions, null);
        }
    }
}
