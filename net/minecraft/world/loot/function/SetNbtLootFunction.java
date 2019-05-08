package net.minecraft.world.loot.function;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.nbt.CompoundTag;

public class SetNbtLootFunction extends ConditionalLootFunction
{
    private final CompoundTag tag;
    
    private SetNbtLootFunction(final LootCondition[] conditions, final CompoundTag tag) {
        super(conditions);
        this.tag = tag;
    }
    
    public ItemStack process(final ItemStack stack, final LootContext context) {
        stack.getOrCreateTag().copyFrom(this.tag);
        return stack;
    }
    
    public static ConditionalLootFunction.Builder<?> builder(final CompoundTag tag) {
        return ConditionalLootFunction.builder(conditions -> new SetNbtLootFunction(conditions, tag));
    }
    
    public static class Builder extends Factory<SetNbtLootFunction>
    {
        public Builder() {
            super(new Identifier("set_nbt"), SetNbtLootFunction.class);
        }
        
        @Override
        public void toJson(final JsonObject json, final SetNbtLootFunction function, final JsonSerializationContext context) {
            super.toJson(json, function, context);
            json.addProperty("tag", function.tag.toString());
        }
        
        @Override
        public SetNbtLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
            try {
                final CompoundTag compoundTag4 = StringNbtReader.parse(JsonHelper.getString(json, "tag"));
                return new SetNbtLootFunction(conditions, compoundTag4, null);
            }
            catch (CommandSyntaxException commandSyntaxException4) {
                throw new JsonSyntaxException(commandSyntaxException4.getMessage());
            }
        }
    }
}
