package net.minecraft.recipe;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Collection;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;
import java.util.stream.StreamSupport;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.item.ItemProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.util.PacketByteBuf;
import java.util.Comparator;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import javax.annotation.Nullable;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.stream.Stream;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import java.util.function.Predicate;

public final class Ingredient implements Predicate<ItemStack>
{
    private static final Predicate<? super Entry> NON_EMPTY;
    public static final Ingredient EMPTY;
    private final Entry[] entries;
    private ItemStack[] stackArray;
    private IntList ids;
    
    private Ingredient(final Stream<? extends Entry> entries) {
        this.entries = entries.filter(Ingredient.NON_EMPTY).<Entry>toArray(Entry[]::new);
    }
    
    @Environment(EnvType.CLIENT)
    public ItemStack[] getStackArray() {
        this.createStackArray();
        return this.stackArray;
    }
    
    private void createStackArray() {
        if (this.stackArray == null) {
            this.stackArray = Arrays.<Entry>stream(this.entries).flatMap(entry -> entry.getStacks().stream()).distinct().<ItemStack>toArray(ItemStack[]::new);
        }
    }
    
    public boolean a(@Nullable final ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        if (this.entries.length == 0) {
            return itemStack.isEmpty();
        }
        this.createStackArray();
        for (final ItemStack itemStack2 : this.stackArray) {
            if (itemStack2.getItem() == itemStack.getItem()) {
                return true;
            }
        }
        return false;
    }
    
    public IntList getIds() {
        if (this.ids == null) {
            this.createStackArray();
            this.ids = (IntList)new IntArrayList(this.stackArray.length);
            for (final ItemStack itemStack4 : this.stackArray) {
                this.ids.add(RecipeFinder.getItemId(itemStack4));
            }
            this.ids.sort((Comparator)IntComparators.NATURAL_COMPARATOR);
        }
        return this.ids;
    }
    
    public void write(final PacketByteBuf buf) {
        this.createStackArray();
        buf.writeVarInt(this.stackArray.length);
        for (int integer2 = 0; integer2 < this.stackArray.length; ++integer2) {
            buf.writeItemStack(this.stackArray[integer2]);
        }
    }
    
    public JsonElement toJson() {
        if (this.entries.length == 1) {
            return this.entries[0].toJson();
        }
        final JsonArray jsonArray1 = new JsonArray();
        for (final Entry entry5 : this.entries) {
            jsonArray1.add(entry5.toJson());
        }
        return jsonArray1;
    }
    
    public boolean isEmpty() {
        return this.entries.length == 0 && (this.stackArray == null || this.stackArray.length == 0) && (this.ids == null || this.ids.isEmpty());
    }
    
    private static Ingredient ofEntries(final Stream<? extends Entry> stream) {
        final Ingredient ingredient2 = new Ingredient(stream);
        return (ingredient2.entries.length == 0) ? Ingredient.EMPTY : ingredient2;
    }
    
    public static Ingredient ofItems(final ItemProvider... arr) {
        final StackEntry stackEntry;
        return ofEntries(Arrays.<ItemProvider>stream(arr).map(itemProvider -> {
            new StackEntry(new ItemStack(itemProvider));
            return stackEntry;
        }));
    }
    
    @Environment(EnvType.CLIENT)
    public static Ingredient ofStacks(final ItemStack... arr) {
        return ofEntries(Arrays.<ItemStack>stream(arr).map(itemStack -> new StackEntry(itemStack)));
    }
    
    public static Ingredient fromTag(final Tag<Item> tag) {
        return ofEntries(Stream.<TagEntry>of(new TagEntry((Tag)tag)));
    }
    
    public static Ingredient fromPacket(final PacketByteBuf buf) {
        final int integer2 = buf.readVarInt();
        return ofEntries(Stream.<StackEntry>generate(() -> new StackEntry(buf.readItemStack())).limit(integer2));
    }
    
    public static Ingredient fromJson(@Nullable final JsonElement element) {
        if (element == null || element.isJsonNull()) {
            throw new JsonSyntaxException("Item cannot be null");
        }
        if (element.isJsonObject()) {
            return ofEntries(Stream.<Entry>of(entryFromJson(element.getAsJsonObject())));
        }
        if (!element.isJsonArray()) {
            throw new JsonSyntaxException("Expected item to be object or array of objects");
        }
        final JsonArray jsonArray2 = element.getAsJsonArray();
        if (jsonArray2.size() == 0) {
            throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
        }
        return ofEntries(StreamSupport.<JsonElement>stream(jsonArray2.spliterator(), false).map(jsonElement -> entryFromJson(JsonHelper.asObject(jsonElement, "item"))));
    }
    
    public static Entry entryFromJson(final JsonObject jsonObject) {
        if (jsonObject.has("item") && jsonObject.has("tag")) {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
        }
        if (jsonObject.has("item")) {
            final Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "item"));
            final Object o;
            final Object o2;
            final Item item3 = Registry.ITEM.getOrEmpty(identifier2).<Throwable>orElseThrow(() -> {
                new JsonSyntaxException("Unknown item '" + o2 + "'");
                return o;
            });
            return new StackEntry(new ItemStack(item3));
        }
        if (!jsonObject.has("tag")) {
            throw new JsonParseException("An ingredient entry needs either a tag or an item");
        }
        final Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "tag"));
        final Tag<Item> tag3 = ItemTags.getContainer().get(identifier2);
        if (tag3 == null) {
            throw new JsonSyntaxException("Unknown item tag '" + identifier2 + "'");
        }
        return new TagEntry((Tag)tag3);
    }
    
    static {
        NON_EMPTY = (entry -> !entry.getStacks().stream().allMatch(ItemStack::isEmpty));
        EMPTY = new Ingredient(Stream.empty());
    }
    
    static class StackEntry implements Entry
    {
        private final ItemStack stack;
        
        private StackEntry(final ItemStack itemStack) {
            this.stack = itemStack;
        }
        
        @Override
        public Collection<ItemStack> getStacks() {
            return Collections.<ItemStack>singleton(this.stack);
        }
        
        @Override
        public JsonObject toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.addProperty("item", Registry.ITEM.getId(this.stack.getItem()).toString());
            return jsonObject1;
        }
    }
    
    static class TagEntry implements Entry
    {
        private final Tag<Item> tag;
        
        private TagEntry(final Tag<Item> tag) {
            this.tag = tag;
        }
        
        @Override
        public Collection<ItemStack> getStacks() {
            final List<ItemStack> list1 = Lists.newArrayList();
            for (final Item item3 : this.tag.values()) {
                list1.add(new ItemStack(item3));
            }
            return list1;
        }
        
        @Override
        public JsonObject toJson() {
            final JsonObject jsonObject1 = new JsonObject();
            jsonObject1.addProperty("tag", this.tag.getId().toString());
            return jsonObject1;
        }
    }
    
    interface Entry
    {
        Collection<ItemStack> getStacks();
        
        JsonObject toJson();
    }
}
