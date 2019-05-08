package net.minecraft.world.loot.entry;

import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import java.util.function.Consumer;
import net.minecraft.world.loot.function.LootFunction;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.util.Identifier;

public class DynamicEntry extends LeafEntry
{
    public static final Identifier instance;
    private final Identifier name;
    
    private DynamicEntry(final Identifier name, final int weight, final int quality, final LootCondition[] conditions, final LootFunction[] functions) {
        super(weight, quality, conditions, functions);
        this.name = name;
    }
    
    public void drop(final Consumer<ItemStack> itemDropper, final LootContext context) {
        context.drop(this.name, itemDropper);
    }
    
    public static Builder<?> builder(final Identifier name) {
        return LeafEntry.builder((weight, quality, conditions, functions) -> new DynamicEntry(name, weight, quality, conditions, functions));
    }
    
    static {
        instance = new Identifier("dynamic");
    }
    
    public static class Serializer extends LeafEntry.Serializer<DynamicEntry>
    {
        public Serializer() {
            super(new Identifier("dynamic"), DynamicEntry.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final DynamicEntry entry, final JsonSerializationContext context) {
            super.toJson(json, entry, context);
            json.addProperty("name", entry.name.toString());
        }
        
        @Override
        protected DynamicEntry fromJson(final JsonObject entryJson, final JsonDeserializationContext context, final int weight, final int quality, final LootCondition[] conditions, final LootFunction[] functions) {
            final Identifier identifier7 = new Identifier(JsonHelper.getString(entryJson, "name"));
            return new DynamicEntry(identifier7, weight, quality, conditions, functions, null);
        }
    }
}
