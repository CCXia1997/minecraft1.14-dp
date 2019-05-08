package net.minecraft.world.loot.function;

import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonArray;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.enchantment.InfoEnchantment;
import net.minecraft.item.ItemProvider;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import com.google.common.collect.Lists;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.enchantment.Enchantment;
import java.util.List;
import org.apache.logging.log4j.Logger;

public class EnchantRandomlyLootFunction extends ConditionalLootFunction
{
    private static final Logger LOGGER;
    private final List<Enchantment> enchantments;
    
    private EnchantRandomlyLootFunction(final LootCondition[] conditions, final Collection<Enchantment> enchantments) {
        super(conditions);
        this.enchantments = ImmutableList.copyOf(enchantments);
    }
    
    public ItemStack process(ItemStack stack, final LootContext context) {
        final Random random4 = context.getRandom();
        Enchantment enchantment8;
        if (this.enchantments.isEmpty()) {
            final List<Enchantment> list5 = Lists.newArrayList();
            for (final Enchantment enchantment7 : Registry.ENCHANTMENT) {
                if (stack.getItem() == Items.kS || enchantment7.isAcceptableItem(stack)) {
                    list5.add(enchantment7);
                }
            }
            if (list5.isEmpty()) {
                EnchantRandomlyLootFunction.LOGGER.warn("Couldn't find a compatible enchantment for {}", stack);
                return stack;
            }
            enchantment8 = list5.get(random4.nextInt(list5.size()));
        }
        else {
            enchantment8 = this.enchantments.get(random4.nextInt(this.enchantments.size()));
        }
        final int integer5 = MathHelper.nextInt(random4, enchantment8.getMinimumLevel(), enchantment8.getMaximumLevel());
        if (stack.getItem() == Items.kS) {
            stack = new ItemStack(Items.nZ);
            EnchantedBookItem.addEnchantment(stack, new InfoEnchantment(enchantment8, integer5));
        }
        else {
            stack.addEnchantment(enchantment8, integer5);
        }
        return stack;
    }
    
    public static Builder<?> builder() {
        return ConditionalLootFunction.builder(conditions -> new EnchantRandomlyLootFunction(conditions, ImmutableList.of()));
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class Factory extends ConditionalLootFunction.Factory<EnchantRandomlyLootFunction>
    {
        public Factory() {
            super(new Identifier("enchant_randomly"), EnchantRandomlyLootFunction.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final EnchantRandomlyLootFunction function, final JsonSerializationContext context) {
            super.toJson(json, function, context);
            if (!function.enchantments.isEmpty()) {
                final JsonArray jsonArray4 = new JsonArray();
                for (final Enchantment enchantment6 : function.enchantments) {
                    final Identifier identifier7 = Registry.ENCHANTMENT.getId(enchantment6);
                    if (identifier7 == null) {
                        throw new IllegalArgumentException("Don't know how to serialize enchantment " + enchantment6);
                    }
                    jsonArray4.add(new JsonPrimitive(identifier7.toString()));
                }
                json.add("enchantments", jsonArray4);
            }
        }
        
        @Override
        public EnchantRandomlyLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            final List<Enchantment> list4 = Lists.newArrayList();
            if (json.has("enchantments")) {
                final JsonArray jsonArray5 = JsonHelper.getArray(json, "enchantments");
                for (final JsonElement jsonElement7 : jsonArray5) {
                    final String string8 = JsonHelper.asString(jsonElement7, "enchantment");
                    final Object o;
                    final String s;
                    final Enchantment enchantment9 = Registry.ENCHANTMENT.getOrEmpty(new Identifier(string8)).<Throwable>orElseThrow(() -> {
                        new JsonSyntaxException("Unknown enchantment '" + s + "'");
                        return o;
                    });
                    list4.add(enchantment9);
                }
            }
            return new EnchantRandomlyLootFunction(conditions, list4, null);
        }
    }
}
