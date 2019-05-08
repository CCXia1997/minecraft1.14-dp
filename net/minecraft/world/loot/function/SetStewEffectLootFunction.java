package net.minecraft.world.loot.function;

import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.util.Iterator;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.registry.Registry;
import com.google.gson.JsonArray;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import com.google.common.collect.Maps;
import java.util.Random;
import net.minecraft.item.SuspiciousStewItem;
import com.google.common.collect.Iterables;
import net.minecraft.item.Items;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.entity.effect.StatusEffect;
import java.util.Map;

public class SetStewEffectLootFunction extends ConditionalLootFunction
{
    private final Map<StatusEffect, UniformLootTableRange> effects;
    
    private SetStewEffectLootFunction(final LootCondition[] conditions, final Map<StatusEffect, UniformLootTableRange> effects) {
        super(conditions);
        this.effects = ImmutableMap.copyOf(effects);
    }
    
    public ItemStack process(final ItemStack stack, final LootContext context) {
        if (stack.getItem() != Items.pz || this.effects.isEmpty()) {
            return stack;
        }
        final Random random3 = context.getRandom();
        final int integer4 = random3.nextInt(this.effects.size());
        final Map.Entry<StatusEffect, UniformLootTableRange> entry5 = Iterables.<Map.Entry<StatusEffect, UniformLootTableRange>>get(this.effects.entrySet(), integer4);
        final StatusEffect statusEffect6 = entry5.getKey();
        int integer5 = entry5.getValue().next(random3);
        if (!statusEffect6.isInstant()) {
            integer5 *= 20;
        }
        SuspiciousStewItem.addEffectToStew(stack, statusEffect6, integer5);
        return stack;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder extends ConditionalLootFunction.Builder<Builder>
    {
        private final Map<StatusEffect, UniformLootTableRange> map;
        
        public Builder() {
            this.map = Maps.newHashMap();
        }
        
        @Override
        protected Builder getThisBuilder() {
            return this;
        }
        
        public Builder withEffect(final StatusEffect effect, final UniformLootTableRange durationRange) {
            this.map.put(effect, durationRange);
            return this;
        }
        
        @Override
        public LootFunction build() {
            return new SetStewEffectLootFunction(this.getConditions(), this.map, null);
        }
    }
    
    public static class Factory extends ConditionalLootFunction.Factory<SetStewEffectLootFunction>
    {
        public Factory() {
            super(new Identifier("set_stew_effect"), SetStewEffectLootFunction.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final SetStewEffectLootFunction function, final JsonSerializationContext context) {
            super.toJson(json, function, context);
            if (!function.effects.isEmpty()) {
                final JsonArray jsonArray4 = new JsonArray();
                for (final StatusEffect statusEffect6 : function.effects.keySet()) {
                    final JsonObject jsonObject7 = new JsonObject();
                    final Identifier identifier8 = Registry.STATUS_EFFECT.getId(statusEffect6);
                    if (identifier8 == null) {
                        throw new IllegalArgumentException("Don't know how to serialize mob effect " + statusEffect6);
                    }
                    jsonObject7.add("type", new JsonPrimitive(identifier8.toString()));
                    jsonObject7.add("duration", context.serialize(function.effects.get(statusEffect6)));
                    jsonArray4.add(jsonObject7);
                }
                json.add("effects", jsonArray4);
            }
        }
        
        @Override
        public SetStewEffectLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            final Map<StatusEffect, UniformLootTableRange> map4 = Maps.newHashMap();
            if (json.has("effects")) {
                final JsonArray jsonArray5 = JsonHelper.getArray(json, "effects");
                for (final JsonElement jsonElement7 : jsonArray5) {
                    final String string8 = JsonHelper.getString(jsonElement7.getAsJsonObject(), "type");
                    final Object o;
                    final String s;
                    final StatusEffect statusEffect9 = Registry.STATUS_EFFECT.getOrEmpty(new Identifier(string8)).<Throwable>orElseThrow(() -> {
                        new JsonSyntaxException("Unknown mob effect '" + s + "'");
                        return o;
                    });
                    final UniformLootTableRange uniformLootTableRange10 = JsonHelper.<UniformLootTableRange>deserialize(jsonElement7.getAsJsonObject(), "duration", context, UniformLootTableRange.class);
                    map4.put(statusEffect9, uniformLootTableRange10);
                }
            }
            return new SetStewEffectLootFunction(conditions, map4, null);
        }
    }
}
