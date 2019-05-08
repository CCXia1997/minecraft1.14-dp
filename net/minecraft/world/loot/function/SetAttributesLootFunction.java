package net.minecraft.world.loot.function;

import com.google.gson.JsonPrimitive;
import javax.annotation.Nullable;
import net.minecraft.world.loot.UniformLootTableRange;
import com.google.gson.JsonSyntaxException;
import com.google.common.collect.Lists;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.entity.EquipmentSlot;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import java.util.UUID;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import java.util.Collection;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.loot.condition.LootCondition;
import java.util.List;

public class SetAttributesLootFunction extends ConditionalLootFunction
{
    private final List<Attribute> attributes;
    
    private SetAttributesLootFunction(final LootCondition[] conditions, final List<Attribute> attributes) {
        super(conditions);
        this.attributes = ImmutableList.copyOf(attributes);
    }
    
    public ItemStack process(final ItemStack stack, final LootContext context) {
        final Random random3 = context.getRandom();
        for (final Attribute attribute5 : this.attributes) {
            UUID uUID6 = attribute5.id;
            if (uUID6 == null) {
                uUID6 = UUID.randomUUID();
            }
            final EquipmentSlot equipmentSlot7 = attribute5.slots[random3.nextInt(attribute5.slots.length)];
            stack.addAttributeModifier(attribute5.attribute, new EntityAttributeModifier(uUID6, attribute5.name, attribute5.amountRange.nextFloat(random3), attribute5.operation), equipmentSlot7);
        }
        return stack;
    }
    
    public static class Factory extends ConditionalLootFunction.Factory<SetAttributesLootFunction>
    {
        public Factory() {
            super(new Identifier("set_attributes"), SetAttributesLootFunction.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final SetAttributesLootFunction function, final JsonSerializationContext context) {
            super.toJson(json, function, context);
            final JsonArray jsonArray4 = new JsonArray();
            for (final Attribute attribute6 : function.attributes) {
                jsonArray4.add(attribute6.serialize(context));
            }
            json.add("modifiers", jsonArray4);
        }
        
        @Override
        public SetAttributesLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            final JsonArray jsonArray4 = JsonHelper.getArray(json, "modifiers");
            final List<Attribute> list5 = Lists.newArrayListWithExpectedSize(jsonArray4.size());
            for (final JsonElement jsonElement7 : jsonArray4) {
                list5.add(Attribute.deserialize(JsonHelper.asObject(jsonElement7, "modifier"), context));
            }
            if (list5.isEmpty()) {
                throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
            }
            return new SetAttributesLootFunction(conditions, list5, null);
        }
    }
    
    static class Attribute
    {
        private final String name;
        private final String attribute;
        private final EntityAttributeModifier.Operation operation;
        private final UniformLootTableRange amountRange;
        @Nullable
        private final UUID id;
        private final EquipmentSlot[] slots;
        
        private Attribute(final String name, final String attribute, final EntityAttributeModifier.Operation operation, final UniformLootTableRange amountRange, final EquipmentSlot[] slots, @Nullable final UUID id) {
            this.name = name;
            this.attribute = attribute;
            this.operation = operation;
            this.amountRange = amountRange;
            this.id = id;
            this.slots = slots;
        }
        
        public JsonObject serialize(final JsonSerializationContext context) {
            final JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("name", this.name);
            jsonObject2.addProperty("attribute", this.attribute);
            jsonObject2.addProperty("operation", getName(this.operation));
            jsonObject2.add("amount", context.serialize(this.amountRange));
            if (this.id != null) {
                jsonObject2.addProperty("id", this.id.toString());
            }
            if (this.slots.length == 1) {
                jsonObject2.addProperty("slot", this.slots[0].getName());
            }
            else {
                final JsonArray jsonArray3 = new JsonArray();
                for (final EquipmentSlot equipmentSlot7 : this.slots) {
                    jsonArray3.add(new JsonPrimitive(equipmentSlot7.getName()));
                }
                jsonObject2.add("slot", jsonArray3);
            }
            return jsonObject2;
        }
        
        public static Attribute deserialize(final JsonObject json, final JsonDeserializationContext context) {
            final String string3 = JsonHelper.getString(json, "name");
            final String string4 = JsonHelper.getString(json, "attribute");
            final EntityAttributeModifier.Operation operation5 = fromName(JsonHelper.getString(json, "operation"));
            final UniformLootTableRange uniformLootTableRange6 = JsonHelper.<UniformLootTableRange>deserialize(json, "amount", context, UniformLootTableRange.class);
            UUID uUID8 = null;
            EquipmentSlot[] arr7;
            if (JsonHelper.hasString(json, "slot")) {
                arr7 = new EquipmentSlot[] { EquipmentSlot.byName(JsonHelper.getString(json, "slot")) };
            }
            else {
                if (!JsonHelper.hasArray(json, "slot")) {
                    throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
                }
                final JsonArray jsonArray9 = JsonHelper.getArray(json, "slot");
                arr7 = new EquipmentSlot[jsonArray9.size()];
                int integer10 = 0;
                for (final JsonElement jsonElement12 : jsonArray9) {
                    arr7[integer10++] = EquipmentSlot.byName(JsonHelper.asString(jsonElement12, "slot"));
                }
                if (arr7.length == 0) {
                    throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
                }
            }
            if (json.has("id")) {
                final String string5 = JsonHelper.getString(json, "id");
                try {
                    uUID8 = UUID.fromString(string5);
                }
                catch (IllegalArgumentException illegalArgumentException10) {
                    throw new JsonSyntaxException("Invalid attribute modifier id '" + string5 + "' (must be UUID format, with dashes)");
                }
            }
            return new Attribute(string3, string4, operation5, uniformLootTableRange6, arr7, uUID8);
        }
        
        private static String getName(final EntityAttributeModifier.Operation operation) {
            switch (operation) {
                case a: {
                    return "addition";
                }
                case b: {
                    return "multiply_base";
                }
                case c: {
                    return "multiply_total";
                }
                default: {
                    throw new IllegalArgumentException("Unknown operation " + operation);
                }
            }
        }
        
        private static EntityAttributeModifier.Operation fromName(final String name) {
            switch (name) {
                case "addition": {
                    return EntityAttributeModifier.Operation.a;
                }
                case "multiply_base": {
                    return EntityAttributeModifier.Operation.b;
                }
                case "multiply_total": {
                    return EntityAttributeModifier.Operation.c;
                }
                default: {
                    throw new JsonSyntaxException("Unknown attribute modifier operation " + name);
                }
            }
        }
    }
}
