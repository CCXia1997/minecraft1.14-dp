package net.minecraft.predicate.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.ItemProvider;
import com.google.common.collect.Lists;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.tag.ItemTags;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonElement;
import net.minecraft.enchantment.Enchantment;
import java.util.Map;
import net.minecraft.potion.PotionUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.potion.Potion;
import net.minecraft.util.NumberRange;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

public class ItemPredicate
{
    public static final ItemPredicate ANY;
    @Nullable
    private final Tag<Item> tag;
    @Nullable
    private final Item item;
    private final NumberRange.IntRange count;
    private final NumberRange.IntRange durability;
    private final EnchantmentPredicate[] enchantments;
    @Nullable
    private final Potion potion;
    private final NbtPredicate nbt;
    
    public ItemPredicate() {
        this.tag = null;
        this.item = null;
        this.potion = null;
        this.count = NumberRange.IntRange.ANY;
        this.durability = NumberRange.IntRange.ANY;
        this.enchantments = new EnchantmentPredicate[0];
        this.nbt = NbtPredicate.ANY;
    }
    
    public ItemPredicate(@Nullable final Tag<Item> tag, @Nullable final Item item, final NumberRange.IntRange count, final NumberRange.IntRange durability, final EnchantmentPredicate[] enchantments, @Nullable final Potion potion, final NbtPredicate nbtPredicate) {
        this.tag = tag;
        this.item = item;
        this.count = count;
        this.durability = durability;
        this.enchantments = enchantments;
        this.potion = potion;
        this.nbt = nbtPredicate;
    }
    
    public boolean test(final ItemStack itemStack) {
        if (this == ItemPredicate.ANY) {
            return true;
        }
        if (this.tag != null && !this.tag.contains(itemStack.getItem())) {
            return false;
        }
        if (this.item != null && itemStack.getItem() != this.item) {
            return false;
        }
        if (!this.count.test(itemStack.getAmount())) {
            return false;
        }
        if (!this.durability.isDummy() && !itemStack.hasDurability()) {
            return false;
        }
        if (!this.durability.test(itemStack.getDurability() - itemStack.getDamage())) {
            return false;
        }
        if (!this.nbt.test(itemStack)) {
            return false;
        }
        final Map<Enchantment, Integer> map2 = EnchantmentHelper.getEnchantments(itemStack);
        for (int integer3 = 0; integer3 < this.enchantments.length; ++integer3) {
            if (!this.enchantments[integer3].test(map2)) {
                return false;
            }
        }
        final Potion potion3 = PotionUtil.getPotion(itemStack);
        return this.potion == null || this.potion == potion3;
    }
    
    public static ItemPredicate deserialize(@Nullable final JsonElement el) {
        if (el == null || el.isJsonNull()) {
            return ItemPredicate.ANY;
        }
        final JsonObject jsonObject2 = JsonHelper.asObject(el, "item");
        final NumberRange.IntRange intRange3 = NumberRange.IntRange.fromJson(jsonObject2.get("count"));
        final NumberRange.IntRange intRange4 = NumberRange.IntRange.fromJson(jsonObject2.get("durability"));
        if (jsonObject2.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        final NbtPredicate nbtPredicate5 = NbtPredicate.deserialize(jsonObject2.get("nbt"));
        Item item6 = null;
        if (jsonObject2.has("item")) {
            final Identifier identifier7 = new Identifier(JsonHelper.getString(jsonObject2, "item"));
            final Object o;
            final Object o2;
            item6 = Registry.ITEM.getOrEmpty(identifier7).<Throwable>orElseThrow(() -> {
                new JsonSyntaxException("Unknown item id '" + o2 + "'");
                return o;
            });
        }
        Tag<Item> tag7 = null;
        if (jsonObject2.has("tag")) {
            final Identifier identifier8 = new Identifier(JsonHelper.getString(jsonObject2, "tag"));
            tag7 = ItemTags.getContainer().get(identifier8);
            if (tag7 == null) {
                throw new JsonSyntaxException("Unknown item tag '" + identifier8 + "'");
            }
        }
        final EnchantmentPredicate[] arr8 = EnchantmentPredicate.deserializeAll(jsonObject2.get("enchantments"));
        Potion potion9 = null;
        if (jsonObject2.has("potion")) {
            final Identifier identifier9 = new Identifier(JsonHelper.getString(jsonObject2, "potion"));
            final Object o3;
            final Object o4;
            potion9 = Registry.POTION.getOrEmpty(identifier9).<Throwable>orElseThrow(() -> {
                new JsonSyntaxException("Unknown potion '" + o4 + "'");
                return o3;
            });
        }
        return new ItemPredicate(tag7, item6, intRange3, intRange4, arr8, potion9, nbtPredicate5);
    }
    
    public JsonElement serialize() {
        if (this == ItemPredicate.ANY) {
            return JsonNull.INSTANCE;
        }
        final JsonObject jsonObject1 = new JsonObject();
        if (this.item != null) {
            jsonObject1.addProperty("item", Registry.ITEM.getId(this.item).toString());
        }
        if (this.tag != null) {
            jsonObject1.addProperty("tag", this.tag.getId().toString());
        }
        jsonObject1.add("count", this.count.serialize());
        jsonObject1.add("durability", this.durability.serialize());
        jsonObject1.add("nbt", this.nbt.serialize());
        if (this.enchantments.length > 0) {
            final JsonArray jsonArray2 = new JsonArray();
            for (final EnchantmentPredicate enchantmentPredicate6 : this.enchantments) {
                jsonArray2.add(enchantmentPredicate6.serialize());
            }
            jsonObject1.add("enchantments", jsonArray2);
        }
        if (this.potion != null) {
            jsonObject1.addProperty("potion", Registry.POTION.getId(this.potion).toString());
        }
        return jsonObject1;
    }
    
    public static ItemPredicate[] deserializeAll(@Nullable final JsonElement el) {
        if (el == null || el.isJsonNull()) {
            return new ItemPredicate[0];
        }
        final JsonArray jsonArray2 = JsonHelper.asArray(el, "items");
        final ItemPredicate[] arr3 = new ItemPredicate[jsonArray2.size()];
        for (int integer4 = 0; integer4 < arr3.length; ++integer4) {
            arr3[integer4] = deserialize(jsonArray2.get(integer4));
        }
        return arr3;
    }
    
    static {
        ANY = new ItemPredicate();
    }
    
    public static class Builder
    {
        private final List<EnchantmentPredicate> enchantments;
        @Nullable
        private Item item;
        @Nullable
        private Tag<Item> tag;
        private NumberRange.IntRange count;
        private NumberRange.IntRange durability;
        @Nullable
        private Potion potion;
        private NbtPredicate nbt;
        
        private Builder() {
            this.enchantments = Lists.newArrayList();
            this.count = NumberRange.IntRange.ANY;
            this.durability = NumberRange.IntRange.ANY;
            this.nbt = NbtPredicate.ANY;
        }
        
        public static Builder create() {
            return new Builder();
        }
        
        public Builder item(final ItemProvider itemProvider) {
            this.item = itemProvider.getItem();
            return this;
        }
        
        public Builder tag(final Tag<Item> tag) {
            this.tag = tag;
            return this;
        }
        
        public Builder count(final NumberRange.IntRange intRange) {
            this.count = intRange;
            return this;
        }
        
        public Builder nbt(final CompoundTag nbt) {
            this.nbt = new NbtPredicate(nbt);
            return this;
        }
        
        public Builder enchantment(final EnchantmentPredicate enchantmentPredicate) {
            this.enchantments.add(enchantmentPredicate);
            return this;
        }
        
        public ItemPredicate build() {
            return new ItemPredicate(this.tag, this.item, this.count, this.durability, this.enchantments.<EnchantmentPredicate>toArray(new EnchantmentPredicate[0]), this.potion, this.nbt);
        }
    }
}
