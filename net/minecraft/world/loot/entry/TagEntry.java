package net.minecraft.world.loot.entry;

import com.google.gson.JsonParseException;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import java.util.Iterator;
import net.minecraft.world.loot.LootChoice;
import net.minecraft.item.ItemProvider;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import java.util.function.Consumer;
import net.minecraft.world.loot.function.LootFunction;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

public class TagEntry extends LeafEntry
{
    private final Tag<Item> name;
    private final boolean expand;
    
    private TagEntry(final Tag<Item> name, final boolean expand, final int weight, final int quality, final LootCondition[] conditions, final LootFunction[] functions) {
        super(weight, quality, conditions, functions);
        this.name = name;
        this.expand = expand;
    }
    
    public void drop(final Consumer<ItemStack> itemDropper, final LootContext context) {
        this.name.values().forEach(item -> itemDropper.accept(new ItemStack(item)));
    }
    
    private boolean grow(final LootContext context, final Consumer<LootChoice> lootChoiceExpander) {
        if (this.test(context)) {
            for (final Item item4 : this.name.values()) {
                lootChoiceExpander.accept(new Choice() {
                    @Override
                    public void drop(final Consumer<ItemStack> itemDropper, final LootContext context) {
                        itemDropper.accept(new ItemStack(item4));
                    }
                });
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean expand(final LootContext lootContext, final Consumer<LootChoice> consumer) {
        if (this.expand) {
            return this.grow(lootContext, consumer);
        }
        return super.expand(lootContext, consumer);
    }
    
    public static Builder<?> builder(final Tag<Item> name) {
        return LeafEntry.builder((weight, quality, conditions, functions) -> new TagEntry(name, true, weight, quality, conditions, functions));
    }
    
    public static class Serializer extends LeafEntry.Serializer<TagEntry>
    {
        public Serializer() {
            super(new Identifier("tag"), TagEntry.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final TagEntry entry, final JsonSerializationContext context) {
            super.toJson(json, entry, context);
            json.addProperty("name", entry.name.getId().toString());
            json.addProperty("expand", entry.expand);
        }
        
        @Override
        protected TagEntry fromJson(final JsonObject entryJson, final JsonDeserializationContext context, final int weight, final int quality, final LootCondition[] conditions, final LootFunction[] functions) {
            final Identifier identifier7 = new Identifier(JsonHelper.getString(entryJson, "name"));
            final Tag<Item> tag8 = ItemTags.getContainer().get(identifier7);
            if (tag8 == null) {
                throw new JsonParseException("Can't find tag: " + identifier7);
            }
            final boolean boolean9 = JsonHelper.getBoolean(entryJson, "expand");
            return new TagEntry(tag8, boolean9, weight, quality, conditions, functions, null);
        }
    }
}
