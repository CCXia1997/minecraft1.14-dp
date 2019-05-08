package net.minecraft.predicate.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import com.google.gson.JsonObject;
import com.google.gson.JsonNull;
import com.google.gson.JsonElement;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.NumberRange;
import net.minecraft.enchantment.Enchantment;

public class EnchantmentPredicate
{
    public static final EnchantmentPredicate ANY;
    private final Enchantment enchantment;
    private final NumberRange.IntRange levels;
    
    public EnchantmentPredicate() {
        this.enchantment = null;
        this.levels = NumberRange.IntRange.ANY;
    }
    
    public EnchantmentPredicate(@Nullable final Enchantment enchantment, final NumberRange.IntRange intRange) {
        this.enchantment = enchantment;
        this.levels = intRange;
    }
    
    public boolean test(final Map<Enchantment, Integer> map) {
        if (this.enchantment != null) {
            if (!map.containsKey(this.enchantment)) {
                return false;
            }
            final int integer2 = map.get(this.enchantment);
            if (this.levels != null && !this.levels.test(integer2)) {
                return false;
            }
        }
        else if (this.levels != null) {
            for (final Integer integer3 : map.values()) {
                if (this.levels.test(integer3)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public JsonElement serialize() {
        if (this == EnchantmentPredicate.ANY) {
            return JsonNull.INSTANCE;
        }
        final JsonObject jsonObject1 = new JsonObject();
        if (this.enchantment != null) {
            jsonObject1.addProperty("enchantment", Registry.ENCHANTMENT.getId(this.enchantment).toString());
        }
        jsonObject1.add("levels", this.levels.serialize());
        return jsonObject1;
    }
    
    public static EnchantmentPredicate deserialize(@Nullable final JsonElement el) {
        if (el == null || el.isJsonNull()) {
            return EnchantmentPredicate.ANY;
        }
        final JsonObject jsonObject2 = JsonHelper.asObject(el, "enchantment");
        Enchantment enchantment3 = null;
        if (jsonObject2.has("enchantment")) {
            final Identifier identifier4 = new Identifier(JsonHelper.getString(jsonObject2, "enchantment"));
            final Object o;
            final Object o2;
            enchantment3 = Registry.ENCHANTMENT.getOrEmpty(identifier4).<Throwable>orElseThrow(() -> {
                new JsonSyntaxException("Unknown enchantment '" + o2 + "'");
                return o;
            });
        }
        final NumberRange.IntRange intRange4 = NumberRange.IntRange.fromJson(jsonObject2.get("levels"));
        return new EnchantmentPredicate(enchantment3, intRange4);
    }
    
    public static EnchantmentPredicate[] deserializeAll(@Nullable final JsonElement el) {
        if (el == null || el.isJsonNull()) {
            return new EnchantmentPredicate[0];
        }
        final JsonArray jsonArray2 = JsonHelper.asArray(el, "enchantments");
        final EnchantmentPredicate[] arr3 = new EnchantmentPredicate[jsonArray2.size()];
        for (int integer4 = 0; integer4 < arr3.length; ++integer4) {
            arr3[integer4] = deserialize(jsonArray2.get(integer4));
        }
        return arr3;
    }
    
    static {
        ANY = new EnchantmentPredicate();
    }
}
